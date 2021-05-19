package com.qsmaxmin.qsbase.common.widget.image;

import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import androidx.annotation.NonNull;

/**
 * @CreateBy qsmaxmin
 * @Date 2021/5/18 18:27
 * @Description
 */
public class ExecutorTransform extends ExecutorAnimated {
    private final float[]      endValues;
    private final float[]      beginValues;
    private final Interpolator interpolator;
    private       boolean      isInitTransform;

    /**
     * @param data 数据实体
     */
    ExecutorTransform(@NonNull ImageData data) {
        super(data);
        beginValues = new float[8];
        endValues = new float[8];
        interpolator = new DecelerateInterpolator(2f);
    }

    @Override protected void onAnimating(float progress, boolean ended) {
        float value = interpolator.getInterpolation(progress);
        if (isInitTransform) {
            float[] initEndValues = data.getMatrix().getInitCoordinate().getValues();
            transform(beginValues, initEndValues, value);
        } else {
            transform(beginValues, endValues, value);
        }
        invalidate();
        data.callbackTransformChanged(progress, progress == 1f);
    }

    void transformTo(float[] coordinate, boolean anim) {
        removeCallbacks(this);
        isInitTransform = false;
        data.getMatrix().getCurrentCoordinate().copyValues(beginValues);
        System.arraycopy(coordinate, 0, endValues, 0, endValues.length);
        if (anim) {
            startAnimation();
        } else {
            data.getMatrix().setValues(endValues);
            invalidate();
        }
    }

    void transformFrom(float[] coordinate, int duration) {
        removeCallbacks(this);
        isInitTransform = true;
        System.arraycopy(coordinate, 0, beginValues, 0, beginValues.length);
        startAnimation(duration);
    }
}
