package com.qsmaxmin.qsbase.mvp.presenter;

import android.content.Context;

import com.qsmaxmin.qsbase.common.exception.QsException;
import com.qsmaxmin.qsbase.common.exception.QsExceptionType;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.utils.QsHelper;
import com.qsmaxmin.qsbase.mvp.QsIView;
import com.qsmaxmin.qsbase.mvp.model.QsConstants;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/6/21 16:27
 * @Description
 */
public class QsPresenter<V extends QsIView> {
    private boolean isAttach;
    private V       mView;

    protected String initTag() {
        return getClass().getSimpleName();
    }

    public Context getContext() {
        if (!isViewDetach()) return mView.getContext();
        return null;
    }

    public void initPresenter(V view) {
        isAttach = true;
        mView = view;
    }

    public V getView() {
        if (isViewDetach()) {
            String threadName = Thread.currentThread().getName();
            switch (threadName) {
                case QsConstants.NAME_HTTP_THREAD:
                case QsConstants.NAME_WORK_THREAD:
                case QsConstants.NAME_SINGLE_THREAD:
                    throw new QsException(QsExceptionType.CANCEL, null, "current thread:" + threadName + " execute " + getClass().getSimpleName() + ".getView() return null, maybe view" + (mView == null ? "" : "(" + mView.getClass().getSimpleName() + ")") + "is destroy...");
                default:
                    throw new QsException(QsExceptionType.CANCEL, null, "当前线程:" + threadName + "执行" + getClass().getSimpleName() + ".getView()方法返回null, 因为View层" + (mView == null ? "" : "(" + mView.getClass().getSimpleName() + ")") + "销毁了，为了规避这种风险，请不要在Presenter里面通过非@ThreadPoint注解的方式创建线程并在该线程中调用getView()方法...");
            }
        }
        return mView;
    }

    public void setDetach() {
        isAttach = false;
        cancelHttpRequest();
    }


    public boolean isViewDetach() {
        return !isAttach || mView == null;
    }

    /**
     * 发起http请求
     */
    protected <T> T createHttpRequest(Class<T> clazz) {
        return createHttpRequest(clazz, getClass().getSimpleName());
    }

    protected <T> T createHttpRequest(Class<T> clazz, String requestTag) {
        return QsHelper.getInstance().getHttpHelper().create(clazz, requestTag);
    }

    /**
     * 取消由当前presenter发起的http请求
     */
    protected void cancelHttpRequest() {
        try {
            QsHelper.getInstance().getHttpHelper().cancelRequest(getClass().getSimpleName());
        } catch (Exception e) {
            L.e(initTag(), "cancel http request failed :" + e.getMessage());
        }
    }

    /**
     * 自定义异常处理
     */
    public void methodError(QsException exception) {
        L.e(initTag(), "methodError ：" + exception.getMessage());
        if (!isViewDetach()) {
            if (mView.isOpenViewState()) {
                mView.showErrorView();
            } else {
                mView.loadingClose();
            }
        }
    }
}
