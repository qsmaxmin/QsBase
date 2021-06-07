package com.qsmaxmin.qsbase.mvvm.fragment;

import com.qsmaxmin.qsbase.common.widget.listview.LoadingState;

import java.util.List;

/**
 * @CreateBy qsmaxmin
 * @Date 2019/3/21 13:57
 * @Description
 */
public interface MvITopBottomLoadView<D> {
    byte LOAD_WHEN_SCROLL_TO_BOTTOM = 0;
    byte LOAD_WHEN_SECOND_TO_LAST   = 1;

    void onTopLoading();

    void onBottomLoading();

    void setTopLoadingState(LoadingState state);

    void setBottomLoadingState(LoadingState state);

    LoadingState getTopLoadingState();

    LoadingState getBottomLoadingState();

    void openTopLoading();

    void closeTopLoading();

    void openBottomLoading();

    void closeBottomLoading();

    boolean canTopLoading();

    boolean canBottomLoading();

    void addTopData(List<D> list);

    void addBottomData(List<D> list);
}
