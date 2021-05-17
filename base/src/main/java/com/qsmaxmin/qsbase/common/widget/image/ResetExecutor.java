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
public class ResetExecutor extends BaseExecutor {
    private final Matrix       tempMatrix;
    private final float        stepValue;
    private final float[]      sv;
    private final float[]      ev;
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
        ev = new float[9];
        cv = new float[9];
        tempMatrix = new Matrix();
        interpolator = new DecelerateInterpolator(2f);
    }

    @Override public void run() {
        if (progress <= 1f) {
            progress += stepValue;
            float value = interpolator.getInterpolation(progress);
            for (int i = 0; i < 6; i++) {
                cv[i] = sv[i] + (ev[i] - sv[i]) * value;
            }
            data.currentMatrix.setValues(cv);
            mapWithOriginalRect(data.currentMatrix, data.currentRect);
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
            data.isIdle = false;
            tempMatrix.set(data.currentMatrix);
            tempMatrix.getValues(sv);
            data.originalMatrix.getValues(ev);
            System.arraycopy(sv, 0, cv, 0, cv.length);
            postAnimation(this);
        } else {
            resetFinal();
        }
    }

    private void resetFinal() {
        data.isIdle = true;
        data.lastAngle = 0;
        data.currentMatrix.set(data.originalMatrix);
        mapWithOriginalRect(data.currentMatrix, data.currentRect);
        invalidate();
    }
}
