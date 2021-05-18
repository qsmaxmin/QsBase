package com.qsmaxmin.qsbase.common.widget.image;

import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import androidx.annotation.NonNull;

/**
 * @CreateBy qsmaxmin
 * @Date 2021/5/14 13:28
 * @Description
 */
class ExecutorRecover extends ExecutorBase {
    private final Interpolator interpolator;
    private final float        stepValue;
    private final float[]      beginValues;
    private final float[]      endValues;
    private       float        progress;

    ExecutorRecover(@NonNull ImageData imageData) {
        super(imageData);
        stepValue = 0.04f;
        beginValues = new float[8];
        endValues = new float[8];
        interpolator = new DecelerateInterpolator(2f);
    }

    @Override public void run() {
        if (progress <= 1f) {
            progress += stepValue;
            float value = interpolator.getInterpolation(progress);
            transform(beginValues, endValues, value);
            invalidate();
            if (data.canTouchScaleDown()) {
                data.callbackTouchScaleChanged(data.getMatrix().calculateTouchScale());
            }
            postAnimation(this);
        } else {
            onExecuteComplete();
        }
    }

    void recover() {
        removeCallbacks(this);
        data.stopFling();
        progress = 0f;
        TransformMatrix tempMatrix = data.getMatrix().copy();
        tempMatrix.copyValues(beginValues);

        if (isPreviewFunction()) {
            recoverPreview(tempMatrix);
        } else {
            recoverMatrix(tempMatrix);
        }
    }

    private void recoverMatrix(TransformMatrix tempMatrix) {
        Coordinate out = tempMatrix.getViewCoordinate();
        Coordinate current = tempMatrix.getCurrentCoordinate();

        float currentWidth = current.getRectWidth();
        float currentHeight = current.getRectHeight();
        float outWidth = out.getRectWidth();
        float outHeight = out.getRectHeight();

        boolean scaled = false;
        if (outWidth > currentWidth || outHeight > currentHeight) {
            scaled = true;
            float scaleX = outWidth / currentWidth;
            float scaleY = outHeight / currentHeight;
            float scale = Math.max(scaleX, scaleY);
            tempMatrix.postScale(scale, scale);
        }

        float outLeft = out.getLeft();
        float outTop = out.getTop();
        float outRight = out.getRight();
        float outBottom = out.getBottom();

        float currentLeft = current.getLeft();
        float currentTop = current.getTop();
        float currentRight = current.getRight();
        float currentBottom = current.getBottom();

        float translateX = 0, translateY = 0;
        if (currentLeft > outLeft) {
            translateX = outLeft - currentLeft;
        } else if (currentRight < outRight) {
            translateX = outRight - currentRight;
        }

        if (currentTop > outTop) {
            translateY = outTop - currentTop;
        } else if (currentBottom < outBottom) {
            translateY = outBottom - currentBottom;
        }
        tempMatrix.postTranslate(translateX, translateY);

        if (scaled || translateX != 0 || translateY != 0) {
            tempMatrix.copyValues(endValues);
            post(this);
        }
    }

    private void recoverPreview(TransformMatrix tempMatrix) {
        Coordinate out = tempMatrix.getViewCoordinate();
        Coordinate current = tempMatrix.getCurrentCoordinate();

        float currentWidth = current.getRectWidth();
        float currentHeight = current.getRectHeight();
        float outWidth = out.getRectWidth();
        float outHeight = out.getRectHeight();

        boolean scaled = false;
        if (outWidth > currentWidth && outHeight > currentHeight) {
            scaled = true;
            float scaleX = outWidth / currentWidth;
            float scaleY = outHeight / currentHeight;
            float scale = Math.min(scaleX, scaleY);
            tempMatrix.postScale(scale, scale);
        }
        float translateX = 0, translateY = 0;
        if (outWidth > currentWidth) {
            translateX = out.getCenterX() - current.getCenterX();
        } else if (current.getLeft() > out.getLeft()) {
            translateX = out.getLeft() - current.getLeft();
        } else if (current.getRight() < out.getRight()) {
            translateX = out.getRight() - current.getRight();
        }

        if (outHeight > currentHeight) {
            translateY = out.getCenterY() - current.getCenterY();
        } else if (current.getTop() > out.getTop()) {
            translateY = out.getTop() - current.getTop();
        } else if (current.getBottom() < out.getBottom()) {
            translateY = out.getBottom() - current.getBottom();
        }
        tempMatrix.postTranslate(translateX, translateY);

        if (scaled || translateX != 0 || translateY != 0) {
            tempMatrix.copyValues(endValues);
            post(this);
        }
    }
}
