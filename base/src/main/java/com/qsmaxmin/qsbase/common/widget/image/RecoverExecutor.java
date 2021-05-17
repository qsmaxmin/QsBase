package com.qsmaxmin.qsbase.common.widget.image;

import android.graphics.Matrix;
import android.graphics.RectF;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import androidx.annotation.NonNull;

/**
 * @CreateBy qsmaxmin
 * @Date 2021/5/14 13:28
 * @Description
 */
class RecoverExecutor extends BaseExecutor {
    private final Interpolator interpolator;
    private final Matrix       tempMatrix;
    private final RectF        tempRect;
    private final float        stepValue;
    private final float[]      values;
    private       float        progress;
    private       float        translateX;
    private       float        translateY;
    private       float        scaleStart;
    private       float        scaleEnd;

    RecoverExecutor(@NonNull ImageData imageData) {
        super(imageData);
        stepValue = 0.04f;
        values = new float[9];
        interpolator = new DecelerateInterpolator(2f);
        tempMatrix = new Matrix();
        tempRect = new RectF();
    }

    @Override public void run() {
        if (progress <= 1f) {
            progress += stepValue;
            float value = interpolator.getInterpolation(progress);
            data.getCurrentMatrix().set(tempMatrix);
            if (scaleEnd != 0) {
                float scale = 1f + (scaleEnd / scaleStart - 1f) * value;
                data.getCurrentMatrix().postScale(scale, scale, tempRect.centerX(), tempRect.centerY());
            }
            data.getCurrentMatrix().postTranslate(translateX * value, translateY * value);
            mapCurrentRect();
            invalidate();

            data.callbackTouchScaleChanged();

            postAnimation(this);
        } else {
            data.setIdle(true);
        }
    }

    void recover() {
        removeCallbacks(this);
        data.stopFling();
        progress = 0f;
        scaleEnd = 0;
        translateX = 0;
        translateY = 0;
        tempMatrix.set(data.getCurrentMatrix());
        tempRect.set(data.getCurrentRect());

        tempMatrix.getValues(values);
        scaleStart = values[Matrix.MSCALE_X];

        if (isPreviewFunction()) {
            recoverPreview();
        } else {
            recoverMatrix();
        }
    }

    private void recoverMatrix() {
        if (tempRect.width() < data.getCutRect().width() || tempRect.height() < data.getCutRect().height()) {
            float scaleX = data.getCutRect().width() / tempRect.width();
            float scaleY = data.getCutRect().height() / tempRect.height();
            float maxScale = Math.max(scaleX, scaleY);
            tempMatrix.postScale(maxScale, maxScale, tempRect.centerX(), tempRect.centerY());
            mapWithOriginalRect(tempMatrix, tempRect);
            tempMatrix.getValues(values);
            scaleEnd = values[Matrix.MSCALE_X];
            tempMatrix.set(data.getCurrentMatrix());
        }

        if (tempRect.left > data.getCutRect().left) {
            translateX = data.getCutRect().left - tempRect.left;
        } else if (tempRect.right < data.getCutRect().right) {
            translateX = data.getCutRect().right - tempRect.right;
        }

        if (tempRect.top > data.getCutRect().top) {
            translateY = data.getCutRect().top - tempRect.top;
        } else if (tempRect.bottom < data.getCutRect().bottom) {
            translateY = data.getCutRect().bottom - tempRect.bottom;
        }
        if (scaleEnd != 0 || translateX != 0 || translateY != 0) {
            post(this);
        } else {
            data.setIdle(true);
        }
    }


    private void recoverPreview() {
        if (tempRect.width() < data.getCutRect().width() && tempRect.height() < data.getCutRect().height()) {
            float scaleX = data.getCutRect().width() / tempRect.width();
            float scaleY = data.getCutRect().height() / tempRect.height();
            float minScale = Math.min(scaleX, scaleY);
            tempMatrix.postScale(minScale, minScale, tempRect.centerX(), tempRect.centerY());
            mapWithOriginalRect(tempMatrix, tempRect);
            tempMatrix.getValues(values);
            scaleEnd = values[Matrix.MSCALE_X];
            tempMatrix.set(data.getCurrentMatrix());
        }

        if (tempRect.left > data.getCutRect().left || tempRect.right < data.getCutRect().right) {
            if (tempRect.width() < data.getCutRect().width()) {
                float left = (data.getCutRect().width() - tempRect.width()) / 2f;
                translateX = left - tempRect.left;
            } else if (tempRect.left > data.getCutRect().left) {
                translateX = data.getCutRect().left - tempRect.left;
            } else if (tempRect.right < data.getCutRect().right) {
                translateX = data.getCutRect().right - tempRect.right;
            }
        }

        if (tempRect.top > data.getCutRect().top || tempRect.bottom < data.getCutRect().bottom) {
            if (tempRect.height() < data.getCutRect().height()) {
                float top = (data.getCutRect().height() - tempRect.height()) / 2f;
                translateY = top - tempRect.top;
            } else if (tempRect.top > data.getCutRect().top) {
                translateY = data.getCutRect().top - tempRect.top;
            } else if (tempRect.bottom < data.getCutRect().bottom) {
                translateY = data.getCutRect().bottom - tempRect.bottom;
            }
        }
        if (scaleEnd != 0 || translateX != 0 || translateY != 0) {
            post(this);
        } else {
            data.setIdle(true);
        }
    }
}
