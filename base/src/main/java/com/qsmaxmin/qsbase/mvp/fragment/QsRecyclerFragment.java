package com.qsmaxmin.qsbase.mvp.fragment;

import android.view.View;

import com.qsmaxmin.qsbase.mvp.presenter.QsPresenter;
import com.qsmaxmin.qsbase.mvvm.fragment.MvRecyclerFragment;
import com.qsmaxmin.qsbase.plugin.bind.QsIBindView;
import com.qsmaxmin.qsbase.plugin.presenter.QsIPresenter;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;

/**
 * @CreateBy qsmaxmin
 * @Date 17/7/1
 * @Description RecyclerView视图
 */
public abstract class QsRecyclerFragment<P extends QsPresenter, D> extends MvRecyclerFragment<D> implements QsIBindView, QsIPresenter {
    @SuppressWarnings("unchecked") private final P presenter = (P) createPresenter();

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

    public final P getPresenter() {
        return presenter;
    }
}
