package com.qsmaxmin.qsbase.common.threadpoll;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/6/20 17:43
 * @Description
 */

public class MainExecutor {
    private static MainExecutor mainExecutor = new MainExecutor();
    private final  Handler      handler      = new Handler(Looper.getMainLooper());

    static MainExecutor getInstance() {
        return mainExecutor;
    }

    public void execute(@NonNull Runnable runnable) {
        handler.post(runnable);
    }

    public void executeDelayed(@NonNull Runnable runnable, long delayMillis) {
        handler.postDelayed(runnable, delayMillis);
    }

    public Handler getHandler() {
        return handler;
    }
}
