package com.qsmaxmin.qsbase.mvp;

import android.view.View;

import com.qsmaxmin.qsbase.mvp.presenter.QsPresenter;
import com.qsmaxmin.qsbase.mvvm.MvPullHeaderViewPagerActivity;
import com.qsmaxmin.qsbase.plugin.bind.QsIBindView;
import com.qsmaxmin.qsbase.plugin.presenter.QsIPresenter;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;

/**
 * @CreateBy administrator
 * @Date 2020/9/9 11:14
 * @Description
 */
public abstract class QsPullHeaderViewPagerActivity<P extends QsPresenter> extends MvPullHeaderViewPagerActivity implements QsIBindView, QsIPresenter {
    private P presenter;

    @CallSuper @Override public void onViewCreated(@NonNull View rootView) {
        bindViewByQsPlugin(rootView);
    }

    @CallSuper @Override public void bindViewByQsPlugin(View view) {
    }

    @Override public Object createPresenter() {
        return null;
    }

    @CallSuper @Override protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) presenter.onViewDestroy();
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
