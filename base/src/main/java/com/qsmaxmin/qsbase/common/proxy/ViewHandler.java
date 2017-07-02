package com.qsmaxmin.qsbase.common.proxy;

import android.os.Looper;

import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.utils.QsHelper;
import com.qsmaxmin.qsbase.mvp.QsIView;
import com.qsmaxmin.qsbase.mvp.presenter.QsPresenter;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;

/**
 * @CreateBy qsmaxmin
 * @Date 17/7/2  上午10:26
 * @Description
 */

public class ViewHandler<V extends QsIView> implements InvocationHandler {

    private QsPresenter    presenter;
    private V              mView;
    private CountDownLatch countDownLatch;

    public ViewHandler(V view, QsPresenter vQsPresenter) {
        this.mView = view;
        this.presenter = vQsPresenter;
    }

    @Override public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            return method.invoke(proxy, args);
        } else {
            final Object[] result = new Object[1];
            countDownLatch = new CountDownLatch(1);
            QsHelper.getInstance().getThreadHelper().getMainThread().execute(new Runnable() {
                @Override public void run() {
                    try {
                        result[0] = executeMethod(method, args);
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    } finally {
                        countDownLatch.countDown();
                    }
                }
            });
            countDownLatch.await();
            return result[0];
        }
    }

    private Object executeMethod(Method method, Object[] args) throws Throwable {
        if (presenter.isViewDetach()) {
            L.i("ViewHandler", "getView()...View is Detach, so stop here!!");
        } else {
            try {
                L.i("ViewHandler", "getView()...run in main thread!!");
                return method.invoke(mView, args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
