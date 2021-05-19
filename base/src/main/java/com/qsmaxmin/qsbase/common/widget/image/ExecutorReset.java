package com.qsmaxmin.qsbase.common.widget.image;

import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import androidx.annotation.NonNull;

/**
 * @CreateBy qsmaxmin
 * @Date 2021/5/14 14:59
 * @Description
 */
class ExecutorReset extends ExecutorAnimated {
    private final float[]      beginValues;
    private final Interpolator interpolator;

    /**
     * @param data 数据实体
     */
    ExecutorReset(@NonNull ImageData data) {
        super(data);
        beginValues = new float[8];
        interpolator = new DecelerateInterpolator(2f);
    }

    @Override protected void onAnimating(final float progress, boolean ended) {
        if (ended) {
            resetFinal();
        } else {
            float value = interpolator.getInterpolation(progress);
            float[] endValues = data.getMatrix().getInitCoordinate().getValues();
            transform(beginValues, endValues, value);
            invalidate();
        }
    }

    void startReset(boolean anim) {
        if (data.getMatrix().hasBeenReset()) return;
        if (anim) {
            removeCallbacks(this);
            data.getMatrix().copyValues(beginValues);
            startAnimation();
        } else {
            resetFinal();
        }
    }

    private void resetFinal() {
        data.getMatrix().reset();
        invalidate();
    }
}
