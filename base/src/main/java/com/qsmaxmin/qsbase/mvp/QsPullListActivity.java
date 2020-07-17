package com.qsmaxmin.qsbase.mvp;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;

import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.widget.listview.LoadingFooter;
import com.qsmaxmin.qsbase.common.widget.ptr.PtrDefaultHandler;
import com.qsmaxmin.qsbase.common.widget.ptr.PtrFrameLayout;
import com.qsmaxmin.qsbase.common.widget.ptr.PtrHandler;
import com.qsmaxmin.qsbase.common.widget.ptr.PtrUIHandler;
import com.qsmaxmin.qsbase.common.widget.ptr.header.BeautyCircleRefreshHeader;
import com.qsmaxmin.qsbase.mvp.presenter.QsPresenter;

import java.util.List;

/**
 * @CreateBy qsmaxmin
 * @Date 2020/4/9 16:21
 * @Description pull list activity
 */
public abstract class QsPullListActivity<P extends QsPresenter, D> extends QsListActivity<P, D> implements QsIPullToRefreshView {
    private   boolean        canLoadingMore = true;
    private   PtrFrameLayout ptrLayout;
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

        ptrLayout = view.findViewById(R.id.swipe_container);
        if (ptrLayout == null) throw new RuntimeException("PtrFrameLayout is not exist or its id not 'R.id.swipe_container' in current layout!!");
        PtrUIHandler handlerView = getPtrUIHandlerView();
        ptrLayout.setHeaderView((View) handlerView);
        ptrLayout.addPtrUIHandler(handlerView);
        ptrLayout.setPtrHandler(new PtrHandler() {
            @Override public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }

            @Override public void onRefreshBegin(PtrFrameLayout frame) {
                onRefresh();
            }
        });

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
        return ptrLayout;
    }

    @Override public void startRefreshing() {
        if (ptrLayout != null) ptrLayout.post(new Runnable() {
            @Override public void run() {
                ptrLayout.autoRefresh();
            }
        });
    }

    public void stopRefreshing() {
        if (ptrLayout != null) ptrLayout.post(new Runnable() {
            @Override public void run() {
                ptrLayout.refreshComplete();
            }
        });
    }

    @Override public void setLoadingState(final LoadingFooter.State state) {
        L.i(initTag(), "setLoadingState：" + state);
        if (loadingFooter != null) loadingFooter.setState(state);
    }

    @Override public LoadingFooter.State getLoadingState() {
        return loadingFooter == null ? null : loadingFooter.getState();
    }

    @Override public void openPullRefreshing() {
        if (ptrLayout != null) ptrLayout.post(new Runnable() {
            @Override public void run() {
                ptrLayout.setEnabled(true);
            }
        });
    }

    @Override public void closePullRefreshing() {
        if (ptrLayout != null) ptrLayout.post(new Runnable() {
            @Override public void run() {
                ptrLayout.setEnabled(false);
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
        ptrLayout.post(new Runnable() {
            @Override public void run() {
                ptrLayout.refreshComplete();
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
        return ptrLayout.isEnabled();
    }

    @Override public void smoothScrollToTop(boolean autoRefresh) {
        super.smoothScrollToTop(autoRefresh);
        if (autoRefresh) startRefreshing();
    }

    protected int onLoadTriggerCondition() {
        return LOAD_WHEN_SCROLL_TO_BOTTOM;
    }
}
