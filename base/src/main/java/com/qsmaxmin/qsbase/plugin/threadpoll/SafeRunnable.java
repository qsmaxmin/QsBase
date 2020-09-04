package com.qsmaxmin.qsbase.plugin.threadpoll;

/**
 * @CreateBy qsmaxmin
 * @Date 2020/6/16 14:09
 * @Description 安全的runnable
 */
public abstract class SafeRunnable implements Runnable {

    @Override public final void run() {
        try {
            safeRun();
        } catch (Exception e) {
            onError(e);
            e.printStackTrace();
        }
    }

    protected abstract void safeRun();

    protected void onError(Exception e) {
        //custom your logic
    }
}
