package com.qsmaxmin.qsbase.mvvm;

import com.qsmaxmin.qsbase.common.widget.listview.LoadingFooter;
import com.qsmaxmin.qsbase.common.widget.ptr.PtrFrameLayout;
import com.qsmaxmin.qsbase.common.widget.ptr.PtrUIHandler;

/**
 * @CreateBy qsmaxmin
 * @Date 17/7/2  下午12:54
 * @Description 可下拉刷新和上拉加载布局
 */

public interface MvIPullToRefreshView {
    byte LOAD_WHEN_SCROLL_TO_BOTTOM = 0;
    byte LOAD_WHEN_SECOND_TO_LAST   = 1;

    PtrUIHandler getPtrUIHandlerView();

    void onRefresh();

    void onLoad();

    void startRefreshing();

    void stopRefreshing();

    void setLoadingState(LoadingFooter.State state);

    LoadingFooter.State getLoadingState();

    boolean canPullRefreshing();

    boolean canPullLoading();

    void openPullRefreshing();

    void closePullRefreshing();

    void openPullLoading();

    void closePullLoading();

    PtrFrameLayout getPtrFrameLayout();
}
