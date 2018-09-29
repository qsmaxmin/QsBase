package com.qsmaxmin.qsbase.mvp.fragment;

import android.view.LayoutInflater;
import android.view.View;

import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.common.aspect.ThreadPoint;
import com.qsmaxmin.qsbase.common.aspect.ThreadType;
import com.qsmaxmin.qsbase.common.widget.listview.LoadingFooter;
import com.qsmaxmin.qsbase.common.widget.ptr.PtrFrameLayout;
import com.qsmaxmin.qsbase.common.widget.ptr.PtrHandler;
import com.qsmaxmin.qsbase.common.widget.ptr.PtrUIHandler;
import com.qsmaxmin.qsbase.common.widget.ptr.header.BeautyCircleRefreshHeader;
import com.qsmaxmin.qsbase.common.widget.viewpager.headerpager.HeaderViewPager;
import com.qsmaxmin.qsbase.mvp.presenter.QsPresenter;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/9/19 13:43
 * @Description
 */

public abstract class QsPullHeaderViewpagerFragment<P extends QsPresenter> extends QsHeaderViewpagerFragment<P> implements QsIPullToRefresh {

    private PtrFrameLayout mPtrFrameLayout;

    @Override public int layoutId() {
        return R.layout.qs_fragment_pull_header_viewpager;
    }

    @Override public PtrUIHandler getPtrUIHandlerView() {
        return new BeautyCircleRefreshHeader(getContext());
    }

    @ThreadPoint(ThreadType.MAIN) @Override public void startRefreshing() {
        if (mPtrFrameLayout != null) mPtrFrameLayout.autoRefresh();
    }

    @Override public void stopRefreshing() {
        mPtrFrameLayout.refreshComplete();
    }

    @Override public void openPullRefreshing() {
        mPtrFrameLayout.setEnabled(true);
    }

    @Override public void closePullRefreshing() {
        mPtrFrameLayout.setEnabled(false);
    }

    @Override public PtrFrameLayout getPtrFrameLayout() {
        return mPtrFrameLayout;
    }

    @Override protected View initView(LayoutInflater inflater) {
        View view = super.initView(inflater);
        initPtrFrameLayout(view);
        return view;
    }

    private void initPtrFrameLayout(View view) {
        if (view instanceof PtrFrameLayout) {
            mPtrFrameLayout = (PtrFrameLayout) view;
        } else {
            mPtrFrameLayout = view.findViewById(R.id.swipe_container);
        }
        if (mPtrFrameLayout == null) throw new RuntimeException("PtrFrameLayout is not exit or its id not 'R.id.swipe_container' in current layout!!");
        PtrUIHandler handlerView = getPtrUIHandlerView();
        mPtrFrameLayout.setHeaderView((View) handlerView);
        mPtrFrameLayout.addPtrUIHandler(handlerView);
        mPtrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return !canChildScrollUp((HeaderViewPager) content);
            }

            @Override public void onRefreshBegin(PtrFrameLayout frame) {
                onRefresh();
            }
        });
    }

    @Override public boolean canPullRefreshing() {
        return mPtrFrameLayout.isEnabled();
    }

    @Override public final void onLoad() {
        // do nothing
    }

    @Override public boolean canPullLoading() {
        return false;
    }

    @Override public final void setLoadingState(LoadingFooter.State state) {
        // do nothing
    }

    @Override public LoadingFooter.State getLoadingState() {
        return null;
    }

    @Override public final void openPullLoading() {
        // do nothing
    }

    @Override public final void closePullLoading() {
        // do nothing
    }

    public boolean canChildScrollUp(HeaderViewPager view) {
        return view != null
                && view.getCurrentInnerScroller() != null
                && view.getCurrentInnerScroller().get() != null
                && view.getCurrentInnerScroller().get().canScrollVertically(-1);
    }

    @Override public void smoothScrollToTop(boolean autoRefresh) {
        super.smoothScrollToTop(autoRefresh);
        if (autoRefresh) startRefreshing();
    }
}
