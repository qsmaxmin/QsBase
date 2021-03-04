package com.qsmaxmin.qsbase.mvvm.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.widget.listview.LoadingFooter;
import com.qsmaxmin.qsbase.common.widget.ptr.PtrDefaultHandler;
import com.qsmaxmin.qsbase.common.widget.ptr.PtrFrameLayout;
import com.qsmaxmin.qsbase.common.widget.ptr.PtrUIHandler;
import com.qsmaxmin.qsbase.common.widget.ptr.header.BeautyCircleRefreshHeader;
import com.qsmaxmin.qsbase.mvvm.MvIPullToRefreshView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @CreateBy qsmaxmin
 * @Date 17/7/4  下午3:13
 * @Description
 */
public abstract class MvPullListFragment<D> extends MvListFragment<D> implements MvIPullToRefreshView {
    private   boolean        canLoadingMore = true;
    private   PtrFrameLayout mPtrFrameLayout;
    protected LoadingFooter  loadingFooter;

    @Override public int layoutId() {
        return canPullRefreshing() ? R.layout.qs_pull_listview : super.layoutId();
    }

    @Override public int getFooterLayout() {
        return R.layout.qs_loading_footer;
    }

    @NonNull @Override public PtrUIHandler getPtrUIHandlerView() {
        return new BeautyCircleRefreshHeader(getContext());
    }

    @Override protected View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        View view = super.initView(inflater, container);
        if (canPullRefreshing()) {
            initPtrFrameLayout(view);
        }

        View footerView = getFooterView();
        if (footerView instanceof LoadingFooter) {
            loadingFooter = (LoadingFooter) footerView;
        } else if (footerView != null) {
            loadingFooter = footerView.findViewById(R.id.loading_footer);
        }
        if (!canPullLoading()) {
            setLoadingState(LoadingFooter.State.TheEnd);
        }
        return view;
    }

    private void initPtrFrameLayout(View view) {
        if (view instanceof PtrFrameLayout) {
            mPtrFrameLayout = (PtrFrameLayout) view;
        } else {
            mPtrFrameLayout = view.findViewById(R.id.swipe_container);
        }
        if (mPtrFrameLayout == null) throw new RuntimeException("PtrFrameLayout is not exist or its id not 'R.id.swipe_container' in current layout!!");
        PtrUIHandler handlerView = getPtrUIHandlerView();
        mPtrFrameLayout.setHeaderView((View) handlerView);
        mPtrFrameLayout.addPtrUIHandler(handlerView);
        mPtrFrameLayout.setPtrHandler(new PtrDefaultHandler(this));
    }

    /**
     * 获取下拉刷新控件
     */
    @Override public PtrFrameLayout getPtrFrameLayout() {
        return mPtrFrameLayout;
    }

    @Override public void startRefreshing() {
        if (mPtrFrameLayout != null && mPtrFrameLayout.isEnabled()) {
            mPtrFrameLayout.post(new Runnable() {
                @Override public void run() {
                    mPtrFrameLayout.autoRefresh();
                }
            });
        }
    }

    @Override public void stopRefreshing() {
        if (mPtrFrameLayout != null) {
            mPtrFrameLayout.post(new Runnable() {
                @Override public void run() {
                    mPtrFrameLayout.refreshComplete();
                }
            });
        }
    }

    @Override public void setLoadingState(final LoadingFooter.State state) {
        L.i(initTag(), "setLoadingState:" + state);
        if (loadingFooter != null) loadingFooter.setState(state);
    }

    @Override public LoadingFooter.State getLoadingState() {
        return loadingFooter == null ? null : loadingFooter.getState();
    }

    @Override public void setData(List<D> list, boolean showEmptyView) {
        super.setData(list, showEmptyView);
        if (mPtrFrameLayout != null) mPtrFrameLayout.post(new Runnable() {
            @Override public void run() {
                mPtrFrameLayout.refreshComplete();
            }
        });
        setFooterStateByData(list);
    }

    @Override public void addData(List<D> list, int position) {
        super.addData(list, position);
        setFooterStateByData(list);
    }

    protected void setFooterStateByData(List<D> list) {
        if (!canPullLoading() || list == null || list.isEmpty()) {
            setLoadingState(LoadingFooter.State.TheEnd);
        } else if (canLoadingMore && getLoadingState() != LoadingFooter.State.Normal) {
            setLoadingState(LoadingFooter.State.Normal);
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

    private void loadingMoreData() {
        if (canPullLoading() && loadingFooter != null) {
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
        return true;
    }

    @Override public boolean canPullRefreshing() {
        return true;
    }

    @Override public final void openPullRefreshing() {
        if (mPtrFrameLayout != null) mPtrFrameLayout.post(new Runnable() {
            @Override public void run() {
                mPtrFrameLayout.setEnabled(true);
            }
        });
    }

    @Override public final void closePullRefreshing() {
        if (mPtrFrameLayout != null) mPtrFrameLayout.post(new Runnable() {
            @Override public void run() {
                mPtrFrameLayout.setEnabled(false);
            }
        });
    }

    @Override public final void openPullLoading() {
        canLoadingMore = true;
    }

    @Override public final void closePullLoading() {
        canLoadingMore = false;
    }

    @Override public void smoothScrollToTop(boolean autoRefresh) {
        super.smoothScrollToTop(autoRefresh);
        if (autoRefresh) startRefreshing();
    }

    protected int onLoadTriggerCondition() {
        return LOAD_WHEN_SCROLL_TO_BOTTOM;
    }
}
