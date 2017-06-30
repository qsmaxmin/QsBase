package com.qsmaxmin.qsbase.common.threadpoll;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/6/20 17:43
 * @Description
 */

public class MainExecutor implements Executor {
    private static MainExecutor mainExecutor = new MainExecutor();

    private final Handler handler = new Handler(Looper.getMainLooper());

    static MainExecutor getInstance() {
        return mainExecutor;
    }

    @Override public void execute(@NonNull Runnable runnable) {
        handler.post(runnable);
    }
}
