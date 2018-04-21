package com.qsmaxmin.qsbase.mvp.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;

import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.common.aspect.ThreadPoint;
import com.qsmaxmin.qsbase.common.aspect.ThreadType;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.widget.listview.LoadingFooter;
import com.qsmaxmin.qsbase.common.widget.ptr.PtrDefaultHandler;
import com.qsmaxmin.qsbase.common.widget.ptr.PtrFrameLayout;
import com.qsmaxmin.qsbase.common.widget.ptr.PtrHandler;
import com.qsmaxmin.qsbase.common.widget.ptr.PtrUIHandler;
import com.qsmaxmin.qsbase.common.widget.ptr.header.StoreHouseHeader;
import com.qsmaxmin.qsbase.common.widget.viewpager.headerpager.base.InnerScroller;
import com.qsmaxmin.qsbase.common.widget.viewpager.headerpager.base.OuterScroller;
import com.qsmaxmin.qsbase.mvp.presenter.QsPresenter;

import java.util.List;

/**
 * @CreateBy qsmaxmin
 * @Date 17/7/4  下午3:13
 * @Description
 */

public abstract class QsPullListFragment<T extends QsPresenter, D> extends QsListFragment<T, D> implements QsIPullListFragment<D> {

    private   PtrFrameLayout mPtrFrameLayout;
    protected LoadingFooter  mLoadingFooter;
    private boolean canLoadingMore = true;

    @Override public int layoutId() {
        return (!isOpenViewState() && (getTopLayout() > 0 || getBottomLayout() > 0)) ? R.layout.qs_fragment_pull_listview_with_top_bottom : R.layout.qs_fragment_pull_listview;
    }

    @Override public int getFooterLayout() {
        return R.layout.qs_loading_footer;
    }

    @Override public PtrUIHandler getPtrUIHandlerView() {
        return new StoreHouseHeader(getContext());
    }

    @Override protected View initView(LayoutInflater inflater) {
        View view = super.initView(inflater);
        initPtrFrameLayout(view);
        return view;
    }

    @Override protected void initListView(LayoutInflater inflater, View view) {
        super.initListView(inflater, view);
        View footerView = getFooterView();
        if (footerView instanceof LoadingFooter) {
            mLoadingFooter = (LoadingFooter) footerView;
        } else if (footerView != null) {
            mLoadingFooter = (LoadingFooter) footerView.findViewById(R.id.loading_footer);
        }
    }

    private void initPtrFrameLayout(View view) {
        if (view instanceof PtrFrameLayout) {
            mPtrFrameLayout = (PtrFrameLayout) view;
        } else {
            mPtrFrameLayout = (PtrFrameLayout) view.findViewById(R.id.swipe_container);
        }
        if (mPtrFrameLayout == null) throw new RuntimeException("PtrFrameLayout is not exit or its id not 'R.id.swipe_container' in current layout!!");
        mPtrFrameLayout.setHeaderView((View) getPtrUIHandlerView());
        mPtrFrameLayout.addPtrUIHandler(getPtrUIHandlerView());
        mPtrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }

            @Override public void onRefreshBegin(PtrFrameLayout frame) {
                onRefresh();
            }
        });
    }

    /**
     * 获取下拉刷新控件
     */
    @Override public PtrFrameLayout getPtrFrameLayout() {
        return mPtrFrameLayout;
    }

    @Override @ThreadPoint(ThreadType.MAIN) public void startRefreshing() {
        mPtrFrameLayout.autoRefresh();
    }

    @Override @ThreadPoint(ThreadType.MAIN) public void stopRefreshing() {
        mPtrFrameLayout.refreshComplete();
    }

    @Override @ThreadPoint(ThreadType.MAIN) public void setLoadingState(LoadingFooter.State state) {
        if (mLoadingFooter != null) {
            L.i(initTag(), "设置刷新尾部状态：" + state);
            mLoadingFooter.setState(state);
        }
    }

    @Override @ThreadPoint(ThreadType.MAIN) public void openPullRefreshing() {
        mPtrFrameLayout.setEnabled(true);
    }

    @Override @ThreadPoint(ThreadType.MAIN) public void closePullRefreshing() {
        mPtrFrameLayout.setEnabled(false);
    }

    @Override public void openPullLoading() {
        canLoadingMore = true;
    }

    @Override public void closePullLoading() {
        canLoadingMore = false;
    }

    @Override public void setData(List<D> list) {
        setData(list, true);
    }

    @ThreadPoint(ThreadType.MAIN) @Override public void setData(List<D> list, boolean showEmptyView) {
        mPtrFrameLayout.refreshComplete();
        super.setData(list, showEmptyView);
    }

    private void loadingMoreData() {
        if (mLoadingFooter != null) {
            LoadingFooter.State state = mLoadingFooter.getState();
            if (!canLoadingMore) {
                return;
            } else if (state == LoadingFooter.State.Loading) {
                L.i(initTag(), "Under loading..........");
                return;
            } else if (state == LoadingFooter.State.TheEnd) {
                L.i(initTag(), "no more data...........");
                return;
            }
            setLoadingState(LoadingFooter.State.Loading);
            onLoad();
        }
    }

    @Override public void onScrollStateChanged(AbsListView view, int scrollState) {
        super.onScrollStateChanged(view, scrollState);
        if (!canListScrollUp() && scrollState == SCROLL_STATE_IDLE) {
            loadingMoreData();
        }
    }

    @Override public void setMyOuterScroller(OuterScroller outerScroller, int myPosition) {
        super.setMyOuterScroller(outerScroller, myPosition);
        if (getListView() instanceof InnerScroller) {
            int headerHeight = outerScroller.getHeaderHeight();
            int tabHeight = outerScroller.getTabHeight();
            L.i(initTag(), "The current Fragment is detected in the QsHeaderViewpagerFragment, so the header will be Downward migration... headerHeight:" + headerHeight + "  tabHeight:" + tabHeight);
            getPtrFrameLayout().setHeaderOffsetY(headerHeight + tabHeight);
        }
    }
}
