package com.qsmaxmin.qsbase.mvp.fragment;

import com.qsmaxmin.qsbase.common.widget.ptr.PtrFrameLayout;
import com.qsmaxmin.qsbase.common.widget.ptr.PtrUIHandler;

/**
 * @CreateBy qsmaxmin
 * @Date 17/7/2  下午12:54
 * @Description
 */

public interface QsIPullFragment extends QsIFragment {

    PtrUIHandler getPtrUIHandlerView();

    int viewLayoutId();

    void onRefresh();

    void startRefreshing();

    void stopRefreshing();

    void openPullRefreshing();

    void closePullRefreshing();

    PtrFrameLayout getPtrFrameLayout();
}
