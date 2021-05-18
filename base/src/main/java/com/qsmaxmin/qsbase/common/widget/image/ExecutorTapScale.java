package com.qsmaxmin.qsbase.common.widget.image;

import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * @CreateBy qsmaxmin
 * @Date 2021/5/17 15:54
 * @Description
 */
public class ExecutorTapScale extends ExecutorBase {
    private final float        stepValue;
    private final Interpolator interpolator;
    private final float[]      beginValues;
    private final float[]      endValues;
    private       float        progress;

    ExecutorTapScale(ImageData data) {
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
            postAnimation(this);
        } else {
            onExecuteComplete();
            data.startRecover();
        }
    }

    void startTapScale(float scaleFactor, float px, float py) {
        removeCallbacks(this);
        progress = 0;
        TransformMatrix matrix = data.getMatrix().copy();
        matrix.copyValues(beginValues);
        matrix.postScale(scaleFactor, scaleFactor, px, py);
        matrix.copyValues(endValues);
        post(this);
    }
}
