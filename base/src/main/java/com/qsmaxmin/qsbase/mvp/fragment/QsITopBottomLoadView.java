package com.qsmaxmin.qsbase.mvp.fragment;

import com.qsmaxmin.qsbase.common.widget.listview.LoadingFooter;

import java.util.List;

/**
 * @CreateBy qsmaxmin
 * @Date 2019/3/21 13:57
 * @Description
 */
public interface QsITopBottomLoadView<D> {

    void onTopLoading();

    void onBottomLoading();

    void setTopLoadingState(LoadingFooter.State state);

    void setBottomLoadingState(LoadingFooter.State state);

    LoadingFooter.State getTopLoadingState();

    LoadingFooter.State getBottomLoadingState();

    void openTopLoading();

    void closeTopLoading();

    void openBottomLoading();

    void closeBottomLoading();

    void addTopData(List<D> list);

    void addBottomData(List<D> list);
}
