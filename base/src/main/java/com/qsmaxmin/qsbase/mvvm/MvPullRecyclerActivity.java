package com.qsmaxmin.qsbase.mvvm;

import android.view.LayoutInflater;
import android.view.View;

import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.widget.listview.BaseLoadingFooter;
import com.qsmaxmin.qsbase.common.widget.listview.LoadingState;
import com.qsmaxmin.qsbase.common.widget.ptr.PtrDefaultHandler;
import com.qsmaxmin.qsbase.common.widget.ptr.PtrFrameLayout;
import com.qsmaxmin.qsbase.common.widget.ptr.PtrUIHandler;
import com.qsmaxmin.qsbase.common.widget.ptr.header.BeautyCircleRefreshHeader;

import java.util.List;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @CreateBy qsmaxmin
 * @Date 2020/4/9 16:21
 * @Description pull recycler activity
 */
public abstract class MvPullRecyclerActivity<D> extends MvRecyclerActivity<D> implements MvIPullToRefreshView {
    private   boolean           canLoadingMore = true;
    private   PtrFrameLayout    mPtrFrameLayout;
    protected BaseLoadingFooter mLoadingFooter;

    @Override public int layoutId() {
        return canPullRefreshing() ? R.layout.qs_pull_recyclerview : super.layoutId();
    }

    @Override public int getFooterLayout() {
        return R.layout.qs_loading_footer;
    }

    @NonNull @Override public PtrUIHandler getPtrUIHandlerView() {
        return new BeautyCircleRefreshHeader(getContext());
    }

    @NonNull @Override protected View initView(@NonNull LayoutInflater inflater) {
        View view = super.initView(inflater);
        if (canPullRefreshing()) {
            initPtrFrameLayout(view);
        }
        View footerView = getFooterView();
        if (footerView instanceof BaseLoadingFooter) {
            mLoadingFooter = (BaseLoadingFooter) footerView;
        } else if (footerView != null) {
            mLoadingFooter = footerView.findViewById(R.id.loading_footer);
        }

        if (!canPullLoading()) {
            setLoadingState(LoadingState.TheEnd);
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

    @Override public void setLoadingState(final LoadingState state) {
        L.i(initTag(), "setLoadingState:" + state);
        if (mLoadingFooter != null) mLoadingFooter.setState(state);
    }

    @Override public LoadingState getLoadingState() {
        return mLoadingFooter == null ? null : mLoadingFooter.getState();
    }

    @Override public PtrFrameLayout getPtrFrameLayout() {
        return mPtrFrameLayout;
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
            setLoadingState(LoadingState.TheEnd);
        } else if (canLoadingMore && getLoadingState() != LoadingState.Normal) {
            setLoadingState(LoadingState.Normal);
        }
    }

    @CallSuper @Override public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        if (onLoadTriggerCondition() == LOAD_WHEN_SCROLL_TO_BOTTOM && newState == RecyclerView.SCROLL_STATE_IDLE && !canRecyclerScrollEnd()) {
            loadingMoreData();
        }
    }

    @Override public void onAdapterGetView(int position, int totalCount) {
        super.onAdapterGetView(position, totalCount);
        if (onLoadTriggerCondition() == LOAD_WHEN_SECOND_TO_LAST && (position == totalCount - 2 || totalCount <= 1)) {
            loadingMoreData();
        }
    }

    private void loadingMoreData() {
        if (canPullLoading() && mLoadingFooter != null) {
            LoadingState state = mLoadingFooter.getState();
            if (!canLoadingMore) {
                return;
            } else if (state == LoadingState.Loading) {
                if (L.isEnable()) L.i(initTag(), "Under loading..........");
                return;
            } else if (state == LoadingState.TheEnd) {
                if (L.isEnable()) L.i(initTag(), "no more data...........");
                return;
            }
            setLoadingState(LoadingState.Loading);
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
