package com.qsmaxmin.qsbase.common.widget.image;

import android.graphics.Matrix;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

/**
 * @CreateBy qsmaxmin
 * @Date 2021/5/17 10:51
 * @Description
 */
class SimpleOnGestureListenerImpl extends GestureDetector.SimpleOnGestureListener {
    private final ImageData        data;
    private final float[]          matrixValues;
    private final Runnable         singleTapCallback;
    private       TapScaleExecutor tapScaleExecutor;

    SimpleOnGestureListenerImpl(final ImageData data) {
        this.data = data;
        this.matrixValues = new float[9];
        this.singleTapCallback = new Runnable() {
            @Override public void run() {
                if (data.getGestureListener() != null) data.getGestureListener().onSingleTap();
            }
        };
    }

    @Override public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        boolean active = e2.getPointerCount() == 1;
        if (active) {
            data.getCurrentMatrix().postTranslate(-distanceX, -distanceY);
            if (data.isPreviewFunction() && data.getCurrentRect().top > data.getInitRect().top) {
                data.getCurrentMatrix().getValues(matrixValues);
                float scaleCurrent = matrixValues[Matrix.MSCALE_X];
                if (data.getTouchBeginScale() == 0f) {
                    data.setTouchBeginScale(scaleCurrent);
                } else {
                    float scaleBegin = data.getTouchBeginScale();
                    float scaleEnd = 0.3f;
                    float scaleTarget = scaleBegin + (scaleEnd - scaleBegin) * data.calculateTouchScale();
                    float ps = scaleTarget / scaleCurrent;
                    data.getCurrentMatrix().postScale(ps, ps, e2.getX(), e2.getY());
                    data.callbackTouchScaleChanged();
                }
            }
            data.mapCurrentRect();
            data.invalidate();
        }
        return active;
    }

    @Override public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (data.canFling() && (velocityX != 0 || velocityY != 0)) {
            data.startFling(velocityX, velocityY);
        } else {
            data.startRecover();
        }
        return true;
    }

    @Override public boolean onDown(MotionEvent e) {
        data.setIdle(false);
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
        float scale = data.getCurrentRect().width() / data.getInitRect().width();
        float maxScale = 4f;
        float x = e.getX();
        float y = e.getY();
        if (data.getCurrentRect().contains(x, y)) {
            if (scale < maxScale) {
                scale *= 1.5f;
                if (scale > maxScale) scale = maxScale;
                startTapScale(scale, x, y);
            } else {
                float scaleX = data.getCutRect().width() / data.getCurrentRect().width();
                float scaleY = data.getCutRect().height() / data.getCurrentRect().height();
                startTapScale(Math.min(scaleX, scaleY), x, y);
            }
        }
        return true;
    }

    final boolean isInTapScaling() {
        return tapScaleExecutor != null && tapScaleExecutor.isScaling();
    }

    final void startTapScale(float scaleFactor, float px, float py) {
        if (tapScaleExecutor == null) tapScaleExecutor = new TapScaleExecutor(data);
        tapScaleExecutor.tapScale(scaleFactor, px, py);
    }
}
