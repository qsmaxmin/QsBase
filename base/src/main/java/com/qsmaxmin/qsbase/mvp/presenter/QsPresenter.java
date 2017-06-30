package com.qsmaxmin.qsbase.mvp.presenter;

import android.content.Context;

import com.qsmaxmin.qsbase.common.exception.QsException;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.mvp.QsIView;

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
        if (mView != null) return mView.getContext();
        return null;
    }

    public void initPresenter(V view) {
        isAttach = true;
        mView = view;
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
