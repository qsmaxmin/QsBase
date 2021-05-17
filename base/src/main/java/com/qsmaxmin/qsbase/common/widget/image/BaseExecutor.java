package com.qsmaxmin.qsbase.common.widget.image;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;

import androidx.annotation.NonNull;

/**
 * @CreateBy qsmaxmin
 * @Date 2021/5/14 13:33
 * @Description
 */
abstract class BaseExecutor implements Runnable {
    protected final ImageData data;

    /**
     * @param data 数据实体
     */
    BaseExecutor(@NonNull ImageData data) {
        this.data = data;
    }

    protected void postAnimation(Runnable action) {
        data.postAnimation(action);
    }

    protected void invalidate() {
        data.invalidate();
    }

    protected void post(Runnable action) {
        data.post(action);
    }

    protected void removeCallbacks(Runnable action) {
        data.removeCallbacks(action);
    }

    protected void mapWithOriginalRect(Matrix matrix, RectF rectF) {
        data.mapWithOriginalRect(matrix, rectF);
    }

    protected Context getContext() {
        return data.getContext();
    }

    protected boolean isPreviewFunction() {
        return data.isPreviewFunction();
    }
}
