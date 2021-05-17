package com.qsmaxmin.qsbase.common.widget.image;

import android.graphics.Matrix;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * @CreateBy qsmaxmin
 * @Date 2021/5/14 15:03
 * @Description
 */
class TapScaleExecutor extends BaseExecutor {
    private final Matrix       tempMatrix;
    private final float        stepValue;
    private final Interpolator interpolator;
    private final float[]      sv;
    private final float[]      ev;
    private final float[]      cv;
    private       boolean      isRunning;
    private       float        progress;

    TapScaleExecutor(ImageData data) {
        super(data);
        tempMatrix = new Matrix();
        stepValue = 0.04f;
        sv = new float[9];
        ev = new float[9];
        cv = new float[9];
        interpolator = new DecelerateInterpolator(2f);
    }

    @Override public void run() {
        if (progress <= 1f) {
            progress += stepValue;
            float value = interpolator.getInterpolation(progress);
            for (int i = 0; i < 6; i++) {
                cv[i] = sv[i] + (ev[i] - sv[i]) * value;
            }
            data.getCurrentMatrix().setValues(cv);
            mapCurrentRect();
            invalidate();
            postAnimation(this);
        } else {
            isRunning = false;
            data.startRecover();
        }
    }

    void tapScale(float scale, float px, float py) {
        data.setIdle(false);
        isRunning = true;
        removeCallbacks(this);
        progress = 0;
        tempMatrix.set(data.getCurrentMatrix());
        tempMatrix.getValues(sv);
        System.arraycopy(sv, 0, cv, 0, cv.length);
        tempMatrix.postScale(scale, scale, px, py);
        tempMatrix.getValues(ev);
        postAnimation(this);
    }

    boolean isScaling() {
        return isRunning;
    }
}
