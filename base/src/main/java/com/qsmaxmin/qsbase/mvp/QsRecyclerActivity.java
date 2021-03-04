package com.qsmaxmin.qsbase.mvp;

import android.view.View;

import com.qsmaxmin.qsbase.mvp.presenter.QsPresenter;
import com.qsmaxmin.qsbase.mvvm.MvRecyclerActivity;
import com.qsmaxmin.qsbase.plugin.bind.QsIBindView;
import com.qsmaxmin.qsbase.plugin.presenter.QsIPresenter;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;

/**
 * @CreateBy qsmaxmin
 * @Date 2020/4/9 15:06
 * @Description recycler activity
 */
public abstract class QsRecyclerActivity<P extends QsPresenter, D> extends MvRecyclerActivity<D> implements QsIBindView, QsIPresenter {
    private P presenter;

    @CallSuper @Override public void onViewCreated(@NonNull View rootView) {
        if (getHeaderView() != null) bindViewByQsPlugin(getHeaderView());
        if (getFooterView() != null) bindViewByQsPlugin(getFooterView());
        bindViewByQsPlugin(rootView);
    }

    @CallSuper @Override public void bindViewByQsPlugin(View view) {
    }

    @Override public Object createPresenter() {
        return null;
    }

    @CallSuper @Override protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) presenter.setDetach();
    }

    @SuppressWarnings("unchecked")
    protected final P getPresenter() {
        if (presenter == null) {
            presenter = (P) createPresenter();
            presenter.initPresenter(this);
        }
        return presenter;
    }
}
