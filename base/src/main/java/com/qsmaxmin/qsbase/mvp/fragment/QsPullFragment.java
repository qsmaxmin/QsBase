package com.qsmaxmin.qsbase.mvp.fragment;

import android.view.View;

import com.qsmaxmin.qsbase.mvp.presenter.QsPresenter;
import com.qsmaxmin.qsbase.mvvm.fragment.MvPullFragment;
import com.qsmaxmin.qsbase.plugin.bind.QsIBindView;
import com.qsmaxmin.qsbase.plugin.presenter.QsIPresenter;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;

/**
 * @CreateBy qsmaxmin
 * @Date 17/7/4
 * @Description
 */

public abstract class QsPullFragment<P extends QsPresenter> extends MvPullFragment implements QsIBindView, QsIPresenter {
    private P presenter;

    @Override public void onViewCreated(@NonNull View rootView) {
        bindViewByQsPlugin(rootView);
    }

    @CallSuper @Override public void bindViewByQsPlugin(View view) {
    }

    @Override public Object createPresenter() {
        return null;
    }

    @Override public void onResume() {
        super.onResume();
        if (presenter != null) presenter.onResume();
    }

    @Override public void onPause() {
        super.onPause();
        if (presenter != null) presenter.onPause();
    }

    @CallSuper @Override public void onDestroy() {
        super.onDestroy();
        if (presenter != null) presenter.onDestroy();
    }

    @SuppressWarnings("unchecked")
    public final P getPresenter() {
        if (presenter == null) {
            presenter = (P) createPresenter();
            presenter.initPresenter(this);
            presenter.onCreate();
        }
        return presenter;
    }
}
