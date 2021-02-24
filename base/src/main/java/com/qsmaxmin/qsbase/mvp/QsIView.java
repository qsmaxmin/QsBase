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
    int VIEW_STATE_LOADING = 0;
    int VIEW_STATE_CONTENT = 1;
    int VIEW_STATE_EMPTY   = 2;
    int VIEW_STATE_ERROR   = 3;

    @LayoutRes int layoutId();

    @LayoutRes int emptyLayoutId();

    @LayoutRes int loadingLayoutId();

    @LayoutRes int errorLayoutId();

    P getPresenter();
}
