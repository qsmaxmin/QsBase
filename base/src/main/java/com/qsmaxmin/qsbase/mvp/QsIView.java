package com.qsmaxmin.qsbase.mvp;

import com.qsmaxmin.qsbase.mvvm.MvIView;
import com.qsmaxmin.qsbase.plugin.bind.QsIBindView;
import com.qsmaxmin.qsbase.plugin.presenter.QsIPresenter;

import androidx.annotation.LayoutRes;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/6/21 14:02
 * @Description MVP架构，View层基类
 */
public interface QsIView<P> extends MvIView, QsIBindView, QsIPresenter {
    @LayoutRes int layoutId();

    @LayoutRes int emptyLayoutId();

    @LayoutRes int loadingLayoutId();

    @LayoutRes int errorLayoutId();

    P getPresenter();
}
