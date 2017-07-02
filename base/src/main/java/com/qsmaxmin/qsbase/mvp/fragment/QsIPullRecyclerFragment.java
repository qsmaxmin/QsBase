package com.qsmaxmin.qsbase.mvp.fragment;

import com.qsmaxmin.qsbase.common.widget.listview.LoadingFooter;
import com.qsmaxmin.qsbase.common.widget.ptr.PtrFrameLayout;
import com.qsmaxmin.qsbase.common.widget.ptr.PtrUIHandler;

/**
 * @CreateBy qsmaxmin
 * @Date 17/7/2  下午4:24
 * @Description
 */

public interface QsIPullRecyclerFragment<D> extends QsIRecyclerFragment<D> {

    PtrUIHandler getPtrUIHandlerView();

    void onRefresh();

    void onLoad();

    void startRefreshing();

    void stopRefreshing();

    void setLoadingState(LoadingFooter.State state);

    void openPullRefreshing();

    void closePullRefreshing();

    void openPullLoading();

    void closePullLoading();

    PtrFrameLayout getPtrFrameLayout();
}
