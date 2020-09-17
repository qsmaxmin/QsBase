package com.qsmaxmin.qsbase.plugin.threadpoll;

import com.qsmaxmin.qsbase.common.exception.QsException;
import com.qsmaxmin.qsbase.common.http.NetworkErrorReceiver;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.mvp.presenter.QsPresenter;

/**
 * @CreateBy qsmaxmin
 * @Date 2020/6/16 14:09
 * @Description safely runnable
 * @see QsPresenter#methodError(QsException)
 */
public abstract class SafeRunnable implements Runnable {

    @Override public final void run() {
        try {
            safeRun();
        } catch (QsException e) {
            if (L.isEnable()) e.printStackTrace();
            NetworkErrorReceiver receiver = e.getErrorReceiver();
            if (receiver != null) receiver.methodError(e);
        } catch (Exception e) {
            if (L.isEnable()) e.printStackTrace();
            onError(e);
        }
    }

    protected abstract void safeRun();

    protected void onError(Exception e) {
        //custom your logic
    }
}
