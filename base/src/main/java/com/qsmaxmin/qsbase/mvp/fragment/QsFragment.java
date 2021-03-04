package com.qsmaxmin.qsbase.mvp.fragment;

import android.view.View;

import com.qsmaxmin.qsbase.mvp.presenter.QsPresenter;
import com.qsmaxmin.qsbase.mvvm.fragment.MvFragment;
import com.qsmaxmin.qsbase.plugin.bind.QsIBindView;
import com.qsmaxmin.qsbase.plugin.presenter.QsIPresenter;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/6/21 11:40
 * @Description
 */
public abstract class QsFragment<P extends QsPresenter> extends MvFragment implements QsIBindView, QsIPresenter {
    private P presenter;

    @Override public void onViewCreated(@NonNull View rootView) {
        bindViewByQsPlugin(rootView);
    }

    @CallSuper @Override public void bindViewByQsPlugin(View view) {
    }

    @Override public Object createPresenter() {
        return null;
    }

    @CallSuper @Override public void onDestroyView() {
        super.onDestroyView();
        if (presenter != null) presenter.setDetach();
    }

    @SuppressWarnings("unchecked")
    public final P getPresenter() {
        if (presenter == null) {
            presenter = (P) createPresenter();
            presenter.initPresenter(this);
        }
        return presenter;
    }
}
