package com.qsmaxmin.qsbase.mvp.fragment;

import android.view.View;

import com.qsmaxmin.qsbase.mvp.presenter.QsPresenter;
import com.qsmaxmin.qsbase.mvvm.fragment.MvHeaderViewpagerFragment;
import com.qsmaxmin.qsbase.plugin.bind.QsIBindView;
import com.qsmaxmin.qsbase.plugin.presenter.QsIPresenter;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;

/**
 * @CreateBy qsmaxmin
 * @Date 2016/11/21 16:06
 * @Description
 */

public abstract class QsHeaderViewpagerFragment<P extends QsPresenter> extends MvHeaderViewpagerFragment implements QsIBindView, QsIPresenter {
    @SuppressWarnings("unchecked") private final P presenter = (P) createPresenter();

    @Override public void onViewCreated(@NonNull View rootView) {
        bindViewByQsPlugin(rootView);
    }

    @CallSuper @Override public void bindViewByQsPlugin(View view) {
    }

    @Override public Object createPresenter() {
        return null;
    }

    public final P getPresenter() {
        return presenter;
    }
}
