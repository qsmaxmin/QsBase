package com.qsmaxmin.qsbase.mvp;

import android.view.View;

import com.qsmaxmin.qsbase.mvp.presenter.QsPresenter;
import com.qsmaxmin.qsbase.mvvm.MvListActivity;
import com.qsmaxmin.qsbase.plugin.bind.QsIBindView;
import com.qsmaxmin.qsbase.plugin.presenter.QsIPresenter;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;

/**
 * @CreateBy qsmaxmin
 * @Date 2020/4/9 15:06
 * @Description list activity
 */
public abstract class QsListActivity<P extends QsPresenter, D> extends MvListActivity<D> implements QsIBindView, QsIPresenter {
    private P presenter;

    @CallSuper @Override public void onViewCreated(@NonNull View rootView) {
        bindViewByQsPlugin(rootView);
    }

    @CallSuper @Override public void bindViewByQsPlugin(View view) {
    }

    @Override public Object createPresenter() {
        return null;
    }

    @Override protected void onResume() {
        super.onResume();
        if (presenter != null) presenter.onResume();
    }

    @Override protected void onPause() {
        super.onPause();
        if (presenter != null) presenter.onPause();
    }

    @CallSuper @Override protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) presenter.onDestroy();
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
