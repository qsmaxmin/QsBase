package com.qsmaxmin.qsbase.mvp.presenter;

import android.content.Context;

import com.qsmaxmin.qsbase.common.exception.QsException;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.proxy.ViewHandler;
import com.qsmaxmin.qsbase.mvp.QsIView;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/6/21 16:27
 * @Description
 */
public class QsPresenter<V extends QsIView> {
    private boolean isAttach;
    private V       mView;

    public String initTag() {
        return getClass().getSimpleName();
    }

    public Context getContext() {
        if (mView != null && !isViewDetach()) return mView.getContext();
        return null;
    }

    public void initPresenter(V view) {
        isAttach = true;
        ClassLoader loader = view.getClass().getClassLoader();
        Class<?>[] interfaces = view.getClass().getInterfaces();
        if (interfaces.length == 0) {
            interfaces = view.getClass().getSuperclass().getInterfaces();
        }
        InvocationHandler invocationHandler = new ViewHandler<>(view, this);
        mView = (V) Proxy.newProxyInstance(loader, interfaces, invocationHandler);
    }

    public V getView() {
        return mView;
    }

    public void setDetach() {
        isAttach = false;
    }

    public boolean isViewDetach() {
        return !isAttach;
    }


    /**
     * 自定义异常处理
     */
    public final void methodError(QsException exception) {
        L.e(initTag(), "methodError ：" + exception.getMessage());
        if (mView != null) {
            if (mView.isOpenViewState()) {
                mView.showErrorView();
            } else {
                mView.loadingClose();
            }
        }
    }
}
