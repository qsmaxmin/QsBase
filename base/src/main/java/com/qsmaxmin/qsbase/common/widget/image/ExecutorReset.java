package com.qsmaxmin.qsbase.common.widget.image;

import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import androidx.annotation.NonNull;

/**
 * @CreateBy qsmaxmin
 * @Date 2021/5/14 14:59
 * @Description
 */
class ExecutorReset extends ExecutorBase {
    private final float        stepValue;
    private final float[]      beginValues;
    private final Interpolator interpolator;
    private       float        progress;

    /**
     * @param data 数据实体
     */
    ExecutorReset(@NonNull ImageData data) {
        super(data);
        stepValue = 0.04f;
        beginValues = new float[8];
        interpolator = new DecelerateInterpolator(2f);
    }

    @Override public void run() {
        if (progress <= 1f) {
            progress += stepValue;
            float value = interpolator.getInterpolation(progress);
            float[] endValues = data.getMatrix().getInitCoordinate().getValues();
            transform(beginValues, endValues, value);
            invalidate();
            postAnimation(this);
        } else {
            resetFinal();
        }
    }

    void startReset(boolean anim) {
        if (data.getMatrix().hasBeenReset()) return;
        if (anim) {
            removeCallbacks(this);
            progress = 0f;
            data.getMatrix().copyValues(beginValues);
            post(this);
        } else {
            resetFinal();
        }
    }

    private void resetFinal() {
        data.getMatrix().reset();
        onExecuteComplete();
        invalidate();
    }
}
