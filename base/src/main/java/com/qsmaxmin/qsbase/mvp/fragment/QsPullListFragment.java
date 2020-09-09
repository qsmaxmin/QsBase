package com.qsmaxmin.qsbase.mvp.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;

import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.widget.listview.LoadingFooter;
import com.qsmaxmin.qsbase.common.widget.ptr.PtrDefaultHandler;
import com.qsmaxmin.qsbase.common.widget.ptr.PtrFrameLayout;
import com.qsmaxmin.qsbase.common.widget.ptr.PtrUIHandler;
import com.qsmaxmin.qsbase.common.widget.ptr.header.BeautyCircleRefreshHeader;
import com.qsmaxmin.qsbase.mvp.QsIPullToRefreshView;
import com.qsmaxmin.qsbase.mvp.presenter.QsPresenter;

import java.util.List;

/**
 * @CreateBy qsmaxmin
 * @Date 17/7/4  下午3:13
 * @Description
 */
public abstract class QsPullListFragment<T extends QsPresenter, D> extends QsListFragment<T, D> implements QsIPullToRefreshView {
    private   boolean        canLoadingMore = true;
    private   PtrFrameLayout mPtrFrameLayout;
    protected LoadingFooter  loadingFooter;

    @Override public int layoutId() {
        return R.layout.qs_pull_listview;
    }

    @Override public int getFooterLayout() {
        return R.layout.qs_loading_footer;
    }

    @Override public PtrUIHandler getPtrUIHandlerView() {
        return new BeautyCircleRefreshHeader(getContext());
    }

    @Override protected View initView(LayoutInflater inflater) {
        View view = super.initView(inflater);

        mPtrFrameLayout = view.findViewById(R.id.swipe_container);
        if (mPtrFrameLayout == null) throw new RuntimeException("PtrFrameLayout is not exist or its id not 'R.id.swipe_container' in current layout!!");
        PtrUIHandler handlerView = getPtrUIHandlerView();
        mPtrFrameLayout.setHeaderView((View) handlerView);
        mPtrFrameLayout.addPtrUIHandler(handlerView);
        mPtrFrameLayout.setPtrHandler(new PtrDefaultHandler(this));

        View footerView = getFooterView();
        if (footerView instanceof LoadingFooter) {
            loadingFooter = (LoadingFooter) footerView;
        } else if (footerView != null) {
            loadingFooter = footerView.findViewById(R.id.loading_footer);
        }
        return view;
    }


    /**
     * 获取下拉刷新控件
     */
    @Override public PtrFrameLayout getPtrFrameLayout() {
        return mPtrFrameLayout;
    }

    @Override public void startRefreshing() {
        if (mPtrFrameLayout != null) mPtrFrameLayout.post(new Runnable() {
            @Override public void run() {
                mPtrFrameLayout.autoRefresh();
            }
        });
    }

    public void stopRefreshing() {
        if (mPtrFrameLayout != null) mPtrFrameLayout.post(new Runnable() {
            @Override public void run() {
                mPtrFrameLayout.refreshComplete();
            }
        });
    }

    @Override public void setLoadingState(final LoadingFooter.State state) {
        L.i(initTag(), "setLoadingState:" + state);
        if (loadingFooter != null) loadingFooter.setState(state);
    }

    @Override public LoadingFooter.State getLoadingState() {
        return loadingFooter == null ? null : loadingFooter.getState();
    }

    @Override public void openPullRefreshing() {
        if (mPtrFrameLayout != null) mPtrFrameLayout.post(new Runnable() {
            @Override public void run() {
                mPtrFrameLayout.setEnabled(true);
            }
        });
    }

    @Override public void closePullRefreshing() {
        if (mPtrFrameLayout != null) mPtrFrameLayout.post(new Runnable() {
            @Override public void run() {
                mPtrFrameLayout.setEnabled(false);
            }
        });
    }

    @Override public void openPullLoading() {
        canLoadingMore = true;
    }

    @Override public void closePullLoading() {
        canLoadingMore = false;
    }

    @Override public void setData(List<D> list, boolean showEmptyView) {
        mPtrFrameLayout.post(new Runnable() {
            @Override public void run() {
                mPtrFrameLayout.refreshComplete();
            }
        });
        super.setData(list, showEmptyView);
    }

    private void loadingMoreData() {
        if (loadingFooter != null) {
            LoadingFooter.State state = loadingFooter.getState();
            if (!canLoadingMore) {
                return;
            } else if (state == LoadingFooter.State.Loading) {
                if (L.isEnable()) L.i(initTag(), "Under loading..........");
                return;
            } else if (state == LoadingFooter.State.TheEnd) {
                if (L.isEnable()) L.i(initTag(), "no more data...........");
                return;
            }
            setLoadingState(LoadingFooter.State.Loading);
            onLoad();
        }
    }

    @Override public void onAdapterGetView(int position, int totalCount) {
        if (onLoadTriggerCondition() == LOAD_WHEN_SECOND_TO_LAST && (position == totalCount - 2 || totalCount == 1)) {
            loadingMoreData();
        }
    }

    @Override public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (onLoadTriggerCondition() == LOAD_WHEN_SCROLL_TO_BOTTOM && scrollState == SCROLL_STATE_IDLE && !canListScrollUp()) {
            loadingMoreData();
        }
    }

    @Override public boolean canPullLoading() {
        return canLoadingMore;
    }

    @Override public boolean canPullRefreshing() {
        return mPtrFrameLayout.isEnabled();
    }

    @Override public void smoothScrollToTop(boolean autoRefresh) {
        super.smoothScrollToTop(autoRefresh);
        if (autoRefresh) startRefreshing();
    }

    protected int onLoadTriggerCondition() {
        return LOAD_WHEN_SCROLL_TO_BOTTOM;
    }
}
