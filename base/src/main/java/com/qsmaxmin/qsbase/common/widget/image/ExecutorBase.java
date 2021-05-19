package com.qsmaxmin.qsbase.common.widget.image;

import android.content.Context;

import androidx.annotation.NonNull;

/**
 * @CreateBy qsmaxmin
 * @Date 2021/5/14 13:33
 * @Description
 */
abstract class ExecutorBase implements Runnable {
    protected final ImageData data;
    private         boolean   running;

    /**
     * @param data 数据实体
     */
    ExecutorBase(@NonNull ImageData data) {
        this.data = data;
    }

    protected final String initTag() {
        return getClass().getSimpleName();
    }

    protected final void postAnimation(Runnable action) {
        data.postAnimation(action);
    }

    protected final void invalidate() {
        data.invalidate();
    }

    protected final void post(Runnable action) {
        data.post(action);
    }

    final boolean isAnimating() {
        return running;
    }

    protected final void setAnimating(boolean running) {
        this.running = running;
    }

    protected final void removeCallbacks(Runnable action) {
        data.removeCallbacks(action);
    }

    protected final Context getContext() {
        return data.getContext();
    }

    protected final boolean isPreviewFunction() {
        return data.isPreviewFunction();
    }

    protected final void transform(float[] begin, float[] end, float progress) {
        float[] current = data.getMatrix().getValues();
        for (int i = 0; i < current.length; i++) {
            current[i] = begin[i] + (end[i] - begin[i]) * progress;
        }
    }
}
