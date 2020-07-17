package com.qsmaxmin.qsbase.mvp.fragment;

import android.view.LayoutInflater;
import android.view.View;

import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.widget.listview.LoadingFooter;
import com.qsmaxmin.qsbase.common.widget.ptr.PtrDefaultHandler;
import com.qsmaxmin.qsbase.common.widget.ptr.PtrFrameLayout;
import com.qsmaxmin.qsbase.common.widget.ptr.PtrHandler;
import com.qsmaxmin.qsbase.common.widget.ptr.PtrUIHandler;
import com.qsmaxmin.qsbase.common.widget.ptr.header.BeautyCircleRefreshHeader;
import com.qsmaxmin.qsbase.common.widget.recyclerview.EndlessRecyclerOnScrollListener;
import com.qsmaxmin.qsbase.mvp.QsIPullToRefreshView;
import com.qsmaxmin.qsbase.mvp.presenter.QsPresenter;

import java.util.List;

/**
 * @CreateBy qsmaxmin
 * @Date 17/7/2  下午4:23
 * @Description
 */
public abstract class QsPullRecyclerFragment<P extends QsPresenter, D> extends QsRecyclerFragment<P, D> implements QsIPullToRefreshView {
    private   boolean        canLoadingMore = true;
    private   PtrFrameLayout ptrFrameLayout;
    protected LoadingFooter  loadingFooter;

    @Override public int getFooterLayout() {
        return R.layout.qs_loading_footer;
    }

    @Override public int layoutId() {
        return R.layout.qs_pull_recyclerview;
    }

    @Override public PtrUIHandler getPtrUIHandlerView() {
        return new BeautyCircleRefreshHeader(getContext());
    }

    @Override protected View initView(LayoutInflater inflater) {
        View view = super.initView(inflater);
        initPtrFrameLayout(view);

        View footerView = getFooterView();
        if (footerView instanceof LoadingFooter) {
            loadingFooter = (LoadingFooter) footerView;
        } else if (footerView != null) {
            loadingFooter = footerView.findViewById(R.id.loading_footer);
        }
        getRecyclerView().addOnScrollListener(mOnScrollListener);
        return view;
    }

    private void initPtrFrameLayout(View view) {
        if (view instanceof PtrFrameLayout) {
            ptrFrameLayout = (PtrFrameLayout) view;
        } else {
            ptrFrameLayout = view.findViewById(R.id.swipe_container);
        }
        if (ptrFrameLayout == null) throw new RuntimeException("PtrFrameLayout is not exist or its id not 'R.id.swipe_container' in current layout!!");
        PtrUIHandler handlerView = getPtrUIHandlerView();
        ptrFrameLayout.setHeaderView((View) handlerView);
        ptrFrameLayout.addPtrUIHandler(handlerView);
        ptrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }

            @Override public void onRefreshBegin(PtrFrameLayout frame) {
                onRefresh();
            }
        });
    }

    @Override public void startRefreshing() {
        if (ptrFrameLayout != null) ptrFrameLayout.post(new Runnable() {
            @Override public void run() {
                ptrFrameLayout.autoRefresh();
            }
        });
    }

    @Override public void stopRefreshing() {
        if (ptrFrameLayout != null) ptrFrameLayout.post(new Runnable() {
            @Override public void run() {
                ptrFrameLayout.refreshComplete();
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
        ptrFrameLayout.setEnabled(true);
        if (ptrFrameLayout != null) ptrFrameLayout.post(new Runnable() {
            @Override public void run() {
                ptrFrameLayout.setEnabled(true);
            }
        });
    }

    @Override public void closePullRefreshing() {
        if (ptrFrameLayout != null) ptrFrameLayout.post(new Runnable() {
            @Override public void run() {
                ptrFrameLayout.setEnabled(false);
            }
        });
    }

    @Override public void openPullLoading() {
        canLoadingMore = true;
    }

    @Override public void closePullLoading() {
        canLoadingMore = false;
    }

    @Override public PtrFrameLayout getPtrFrameLayout() {
        return ptrFrameLayout;
    }

    @Override public void setData(List<D> list, boolean showEmptyView) {
        if (ptrFrameLayout != null) ptrFrameLayout.post(new Runnable() {
            @Override public void run() {
                ptrFrameLayout.refreshComplete();
            }
        });
        super.setData(list, showEmptyView);
    }

    private EndlessRecyclerOnScrollListener mOnScrollListener = new EndlessRecyclerOnScrollListener() {
        @Override public void onLoadNextPage(View view) {
            if (onLoadTriggerCondition() == LOAD_WHEN_SCROLL_TO_BOTTOM) {
                loadingMoreData();
            }
        }
    };

    @Override public void onAdapterGetView(int position, int totalCount) {
        if (onLoadTriggerCondition() == LOAD_WHEN_SECOND_TO_LAST && (position == totalCount - 2 || totalCount <= 1)) {
            loadingMoreData();
        }
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

    @Override public boolean canPullLoading() {
        return canLoadingMore;
    }

    @Override public boolean canPullRefreshing() {
        return ptrFrameLayout.isEnabled();
    }

    @Override public void smoothScrollToTop(boolean autoRefresh) {
        super.smoothScrollToTop(autoRefresh);
        if (autoRefresh) startRefreshing();
    }

    protected int onLoadTriggerCondition() {
        return LOAD_WHEN_SCROLL_TO_BOTTOM;
    }
}
