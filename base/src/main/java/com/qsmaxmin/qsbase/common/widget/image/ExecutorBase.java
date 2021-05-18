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

    protected String initTag() {
        return getClass().getSimpleName();
    }

    protected final void postAnimation(Runnable action) {
        data.postAnimation(action);
    }

    protected final void invalidate() {
        data.invalidate();
    }

    protected final void post(Runnable action) {
        running = true;
        data.post(action);
    }

    final boolean isRunning() {
        return running;
    }

    protected final void onExecuteComplete() {
        this.running = false;
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
        transform(begin, end, data.getMatrix().getValues(), progress);
    }

    protected final void transform(float[] begin, float[] end, float[] current, float progress) {
        for (int i = 0; i < current.length; i++) {
            current[i] = begin[i] + (end[i] - begin[i]) * progress;
        }
    }
}
