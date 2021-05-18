package com.qsmaxmin.qsbase.common.widget.image;

import android.graphics.RectF;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import androidx.annotation.NonNull;

/**
 * @CreateBy qsmaxmin
 * @Date 2021/5/18 18:27
 * @Description
 */
public class ExecutorTransform extends ExecutorBase {
    private final float[]      endValues;
    private final float[]      beginValues;
    private final float        stepValue;
    private final Interpolator interpolator;
    private       float        progress;

    /**
     * @param data 数据实体
     */
    ExecutorTransform(@NonNull ImageData data) {
        super(data);
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
            data.callbackTransformChanged(progress, progress == 1f);
            postAnimation(this);
        } else {
            onExecuteComplete();
        }
    }

    void startTransform(RectF rectF, boolean anim) {
        removeCallbacks(this);
        progress = 0f;
        data.getMatrix().getCurrentCoordinate().copyValues(beginValues);
        Coordinate original = data.getMatrix().getOriginalCoordinate();
        float originalWidth = original.getWidth();
        float originalHeight = original.getHeight();

        float width = rectF.width();
        float height = rectF.height();
        float sw = originalWidth / width;
        float sh = originalHeight / height;
        float scale = Math.min(sw, sh);

        float finalWidth = originalWidth / scale;
        float finalHeight = originalHeight / scale;
        setValue(endValues, rectF.centerX(), rectF.centerY(), finalWidth, finalHeight);
        if (anim) {
            post(this);
        } else {
            data.getMatrix().setValues(endValues);
        }
    }

    private void setValue(float[] pos, float cx, float cy, float width, float height) {
        float dx = width / 2f;
        float dy = height / 2f;
        pos[0] = pos[6] = cx - dx;
        pos[1] = pos[3] = cy - dy;
        pos[2] = pos[4] = cx + dx;
        pos[5] = pos[7] = cy + dy;
    }
}
