package com.qsmaxmin.qsbase.mvp.fragment;

import com.qsmaxmin.qsbase.common.widget.listview.LoadingFooter;
import com.qsmaxmin.qsbase.common.widget.ptr.PtrFrameLayout;
import com.qsmaxmin.qsbase.common.widget.ptr.PtrUIHandler;

/**
 * @CreateBy qsmaxmin
 * @Date 17/7/2  下午12:54
 * @Description
 */

public interface QsIPullToRefresh {

    PtrUIHandler getPtrUIHandlerView();

    void onRefresh();

    void onLoad();

    void startRefreshing();

    void stopRefreshing();

    void setLoadingState(LoadingFooter.State state);

    LoadingFooter.State getLoadingState();

    void openPullRefreshing();

    void closePullRefreshing();

    boolean canPullRefreshing();

    void openPullLoading();

    void closePullLoading();

    boolean canPullLoading();

    PtrFrameLayout getPtrFrameLayout();
}
