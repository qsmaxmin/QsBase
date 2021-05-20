package com.qsmaxmin.qsbase.mvp.fragment;

import android.content.Context;
import android.view.View;

import com.qsmaxmin.qsbase.mvp.presenter.QsPresenter;
import com.qsmaxmin.qsbase.mvvm.fragment.MvViewPagerFragment;
import com.qsmaxmin.qsbase.plugin.bind.QsIBindView;
import com.qsmaxmin.qsbase.plugin.presenter.QsIPresenter;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/7/3 13:14
 * @Description
 */

public abstract class QsViewPagerFragment<P extends QsPresenter> extends MvViewPagerFragment implements QsIBindView, QsIPresenter<P> {
    private final P presenter = createPresenter();

    @Override public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override public void onViewCreated(@NonNull View rootView) {
        bindViewByQsPlugin(rootView);
    }

    @CallSuper @Override public void bindViewByQsPlugin(View view) {
    }

    @Override public P createPresenter() {
        return null;
    }

    public final P getPresenter() {
        return presenter;
    }
}
