package com.qsmaxmin.qsbase.mvp.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.common.aspect.ThreadPoint;
import com.qsmaxmin.qsbase.common.aspect.ThreadType;
import com.qsmaxmin.qsbase.common.widget.listview.LoadingFooter;
import com.qsmaxmin.qsbase.common.widget.ptr.PtrDefaultHandler;
import com.qsmaxmin.qsbase.common.widget.ptr.PtrFrameLayout;
import com.qsmaxmin.qsbase.common.widget.ptr.PtrHandler;
import com.qsmaxmin.qsbase.common.widget.ptr.PtrUIHandler;
import com.qsmaxmin.qsbase.common.widget.ptr.header.BeautyCircleRefreshHeader;
import com.qsmaxmin.qsbase.mvp.presenter.QsPresenter;

/**
 * @CreateBy qsmaxmin
 * @Date 17/7/4  下午3:13
 * @Description
 */

public abstract class QsPullFragment<T extends QsPresenter> extends QsFragment<T> implements QsIPullFragment {

    private PtrFrameLayout mPtrFrameLayout;
    private ViewGroup      childView;

    @Override public int layoutId() {
        return R.layout.qs_fragment_pull_view;
    }

    @Override public PtrUIHandler getPtrUIHandlerView() {
        return new BeautyCircleRefreshHeader(getContext());
    }

    @Override protected View initView(LayoutInflater inflater) {
        View view = super.initView(inflater);
        initPtrFrameLayout(view, inflater);
        return view;
    }

    private void initPtrFrameLayout(View view, LayoutInflater inflater) {
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
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }

            @Override public void onRefreshBegin(PtrFrameLayout frame) {
                onRefresh();
            }
        });
        childView = view.findViewById(R.id.swipe_child);
        if (viewLayoutId() > 0) {
            if (childView != null) {
                inflater.inflate(viewLayoutId(), childView);
            } else {
                inflater.inflate(viewLayoutId(), mPtrFrameLayout);
            }
        }
    }

    /**
     * 获取下拉刷新控件
     */
    @Override public PtrFrameLayout getPtrFrameLayout() {
        return mPtrFrameLayout;
    }

    @ThreadPoint(ThreadType.MAIN) @Override public void startRefreshing() {
        mPtrFrameLayout.autoRefresh();
    }

    @Override @ThreadPoint(ThreadType.MAIN) public void stopRefreshing() {
        mPtrFrameLayout.refreshComplete();
    }

    @Override @ThreadPoint(ThreadType.MAIN) public void openPullRefreshing() {
        mPtrFrameLayout.setEnabled(true);
    }

    @Override @ThreadPoint(ThreadType.MAIN) public void closePullRefreshing() {
        closePullRefreshing(false);
    }

    @Override public boolean canPullRefreshing() {
        return mPtrFrameLayout.isEnabled();
    }

    public void closePullRefreshing(boolean enableOverDrag) {
        if (enableOverDrag) {
            mPtrFrameLayout.setEnabled(true);
            View headerView = mPtrFrameLayout.getHeaderView();
            if (headerView != null) headerView.setVisibility(View.GONE);
            mPtrFrameLayout.setKeepHeaderWhenRefresh(false);
        } else {
            mPtrFrameLayout.setEnabled(false);
        }
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

    @Override public void smoothScrollToTop(boolean autoRefresh) {
        if (childView != null && childView instanceof ScrollView) {
            ScrollView scrollView = (ScrollView) this.childView;
            scrollView.smoothScrollTo(0, 0);
            if (autoRefresh) scrollView.post(new Runnable() {
                @Override public void run() {
                    startRefreshing();
                }
            });
        } else {
            super.smoothScrollToTop(autoRefresh);
        }
    }
}
