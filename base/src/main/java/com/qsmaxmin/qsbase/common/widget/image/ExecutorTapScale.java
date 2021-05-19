package com.qsmaxmin.qsbase.common.widget.image;

import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * @CreateBy qsmaxmin
 * @Date 2021/5/17 15:54
 * @Description
 */
public class ExecutorTapScale extends ExecutorAnimated {
    private final Interpolator interpolator;
    private final float[]      beginValues;
    private final float[]      endValues;

    ExecutorTapScale(ImageData data) {
        super(data);
        beginValues = new float[8];
        endValues = new float[8];
        interpolator = new DecelerateInterpolator(2f);
    }

    @Override protected void onAnimating(float progress, boolean ended) {
        if (ended) {
            data.startRecover();
        } else {
            float value = interpolator.getInterpolation(progress);
            transform(beginValues, endValues, value);
            invalidate();
        }
    }

    void startTapScale(float scaleFactor, float px, float py) {
        removeCallbacks(this);
        TransformMatrix matrix = data.getMatrix().copy();
        matrix.copyValues(beginValues);
        matrix.postScale(scaleFactor, scaleFactor, px, py);
        matrix.copyValues(endValues);
        startAnimation();
    }
}
