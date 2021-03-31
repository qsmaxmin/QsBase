package com.qsmaxmin.qsbase.plugin.threadpoll;

import com.qsmaxmin.qsbase.common.exception.QsException;
import com.qsmaxmin.qsbase.common.http.NetworkErrorReceiver;
import com.qsmaxmin.qsbase.common.log.L;

/**
 * @CreateBy qsmaxmin
 * @Date 2020/6/16 14:09
 * @Description safely runnable
 * @see NetworkErrorReceiver#methodError(QsException)
 */
public abstract class SafeRunnable implements Runnable {

    @Override public void run() {
        try {
            safeRun();
        } catch (Throwable t) {
            onError(t);
        }
    }

    protected abstract void safeRun() throws Exception;

    protected void onError(Throwable t) {
        if (L.isEnable()) L.e("SafeRunnable", t);
        //custom your logic
    }
}
