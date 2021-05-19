package com.qsmaxmin.qsbase.common.widget.image;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

/**
 * @CreateBy qsmaxmin
 * @Date 2021/5/17 10:51
 * @Description
 */
class SimpleGestureListener extends GestureDetector.SimpleOnGestureListener {
    private final ImageData data;
    private final Runnable  singleTapCallback;
    private       float     touchBeginScale;

    SimpleGestureListener(final ImageData data) {
        this.data = data;
        this.singleTapCallback = new Runnable() {
            @Override public void run() {
                if (data.getGestureListener() != null) data.getGestureListener().onSingleTap();
            }
        };
    }

    void resetTouchBeginScale() {
        touchBeginScale = 0f;
    }

    boolean isTriggeredTouchScale() {
        return touchBeginScale != 0;
    }

    @Override public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        boolean active = e2.getPointerCount() == 1;
        if (active) {
            data.getMatrix().postTranslate(-distanceX, -distanceY);
            if (data.isEnableTouchScaleDown()) {
                if (data.getMatrix().canTouchScaleDown()) {
                    float scaleCurrent = data.getMatrix().getScale();
                    if (touchBeginScale == 0f) {
                        touchBeginScale = scaleCurrent;
                    } else {
                        float ratio = data.getMatrix().calculateTouchProgress();
                        float scaleBegin = touchBeginScale;
                        float scaleEnd = 0.3f;
                        float scaleTarget = scaleBegin + (scaleEnd - scaleBegin) * ratio;
                        float ps = scaleTarget / scaleCurrent;
                        data.getMatrix().postScale(ps, ps, e2.getX(), e2.getY());
                        data.callbackTouchScaleChanged(ratio);
                    }
                } else {
                    touchBeginScale = 0;
                    float ratio = data.getMatrix().calculateTouchProgress();
                    data.callbackTouchScaleChanged(ratio);
                }
            }
            data.invalidate();
        }
        return active;
    }

    @Override public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        int vx = (int) velocityX;
        int vy = (int) velocityY;
        if ((vx != 0 || vy != 0) && data.getMatrix().canFling()) {
            data.startFling(vx, vy);
        } else {
            data.startRecover("SimpleGestureListener-onFling");
        }
        return true;
    }

    @Override public boolean onDown(MotionEvent e) {
        data.stopFling();
        return true;
    }

    @Override public boolean onSingleTapUp(MotionEvent e) {
        data.removeCallbacks(singleTapCallback);
        data.postDelayed(singleTapCallback, ViewConfiguration.getDoubleTapTimeout());
        return false;
    }

    @Override public boolean onDoubleTap(MotionEvent e) {
        data.removeCallbacks(singleTapCallback);
        float maxScale = 4f;
        float x = e.getX();
        float y = e.getY();
        if (data.getMatrix().contains(x, y)) {
            float scale = data.getMatrix().getScale();
            if (scale < maxScale) {
                float scale1 = scale * 1.5f;
                if (scale1 > maxScale) scale1 = maxScale;
                data.startTapScale(scale1, x, y);
            } else {
                data.startTapScale(1f / scale, x, y);
            }
        }
        return true;
    }
}
