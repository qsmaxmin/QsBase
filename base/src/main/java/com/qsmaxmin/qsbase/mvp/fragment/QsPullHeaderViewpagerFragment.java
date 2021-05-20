package com.qsmaxmin.qsbase.mvp.fragment;

import android.content.Context;
import android.view.View;

import com.qsmaxmin.qsbase.mvp.presenter.QsPresenter;
import com.qsmaxmin.qsbase.mvvm.fragment.MvPullHeaderViewpagerFragment;
import com.qsmaxmin.qsbase.plugin.bind.QsIBindView;
import com.qsmaxmin.qsbase.plugin.presenter.QsIPresenter;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/9/19 13:43
 * @Description
 */

public abstract class QsPullHeaderViewpagerFragment<P extends QsPresenter> extends MvPullHeaderViewpagerFragment implements QsIBindView, QsIPresenter<P> {
    private final P presenter = createPresenter();

    @CallSuper @Override public void onViewCreated(@NonNull View rootView) {
        bindViewByQsPlugin(rootView);
    }

    @CallSuper @Override public void bindViewByQsPlugin(View view) {
    }

    @Override public P createPresenter() {
        return null;
    }

    protected final P getPresenter() {
        return presenter;
    }
}
