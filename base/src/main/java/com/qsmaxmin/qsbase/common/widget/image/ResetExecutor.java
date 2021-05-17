package com.qsmaxmin.qsbase.common.widget.image;

import android.graphics.Matrix;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import androidx.annotation.NonNull;

/**
 * @CreateBy qsmaxmin
 * @Date 2021/5/14 14:59
 * @Description
 */
class ResetExecutor extends BaseExecutor {
    private final Matrix       tempMatrix;
    private final float        stepValue;
    private final float[]      sv;
    private final float[]      cv;
    private final Interpolator interpolator;
    private       float        progress;

    /**
     * @param data 数据实体
     */
    ResetExecutor(@NonNull ImageData data) {
        super(data);
        stepValue = 0.04f;
        sv = new float[9];
        cv = new float[9];
        tempMatrix = new Matrix();
        interpolator = new DecelerateInterpolator(2f);
    }

    @Override public void run() {
        if (progress <= 1f) {
            progress += stepValue;
            float value = interpolator.getInterpolation(progress);
            float[] ev = getInitValues();
            for (int i = 0; i < 6; i++) {
                cv[i] = sv[i] + (ev[i] - sv[i]) * value;
            }
            data.getCurrentMatrix().setValues(cv);
            mapCurrentRect();
            invalidate();
            postAnimation(this);
        } else {
            resetFinal();
        }
    }

    void resetMatrix(boolean anim) {
        if (anim) {
            removeCallbacks(this);
            progress = 0f;
            data.setIdle(false);
            tempMatrix.set(data.getCurrentMatrix());
            tempMatrix.getValues(sv);
            System.arraycopy(sv, 0, cv, 0, cv.length);
            postAnimation(this);
        } else {
            resetFinal();
        }
    }

    private void resetFinal() {
        data.setIdle(true);
        data.resetLastAngle();
        data.getCurrentMatrix().setValues(getInitValues());
        mapCurrentRect();
        invalidate();
    }
}
