package com.qsmaxmin.qsbase.mvvm;

import android.view.LayoutInflater;
import android.view.View;

import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.common.utils.QsHelper;
import com.qsmaxmin.qsbase.common.widget.listview.LoadingState;
import com.qsmaxmin.qsbase.common.widget.ptr.PtrDefaultHandler;
import com.qsmaxmin.qsbase.common.widget.ptr.PtrFrameLayout;
import com.qsmaxmin.qsbase.common.widget.ptr.PtrUIHandler;
import com.qsmaxmin.qsbase.common.widget.ptr.header.BeautyCircleRefreshHeader;

import androidx.annotation.NonNull;

/**
 * @CreateBy qsmaxmin
 * @Date 2020/9/9 11:14
 * @Description
 */
public abstract class MvPullHeaderViewPagerActivity extends MvHeaderViewPagerActivity implements MvIPullToRefreshView {
    private PtrFrameLayout mPtrFrameLayout;

    @Override public int layoutId() {
        return R.layout.qs_pull_header_viewpager;
    }

    @NonNull @Override public PtrUIHandler getPtrUIHandlerView() {
        return new BeautyCircleRefreshHeader(getContext());
    }

    @Override public void startRefreshing() {
        if (mPtrFrameLayout == null) return;
        if (QsHelper.isMainThread()) {
            mPtrFrameLayout.autoRefresh();
        } else {
            mPtrFrameLayout.post(new Runnable() {
                @Override public void run() {
                    mPtrFrameLayout.autoRefresh();
                }
            });
        }
    }

    @Override public void stopRefreshing() {
        if (mPtrFrameLayout == null) return;
        if (QsHelper.isMainThread()) {
            mPtrFrameLayout.refreshComplete();
        } else {
            mPtrFrameLayout.post(new Runnable() {
                @Override public void run() {
                    mPtrFrameLayout.refreshComplete();
                }
            });
        }
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

    @NonNull @Override protected View initView(@NonNull LayoutInflater inflater) {
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
        if (mPtrFrameLayout == null) throw new IllegalStateException("PtrFrameLayout is not exist or its id not 'R.id.swipe_container' in current layout!!");
        PtrUIHandler handlerView = getPtrUIHandlerView();
        mPtrFrameLayout.setHeaderView((View) handlerView);
        mPtrFrameLayout.addPtrUIHandler(handlerView);
        mPtrFrameLayout.setPtrHandler(new PtrDefaultHandler(this));
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

    @Override public final void setLoadingState(LoadingState state) {
        // do nothing
    }

    @Override public LoadingState getLoadingState() {
        return null;
    }

    @Override public final void openPullLoading() {
        // do nothing
    }

    @Override public final void closePullLoading() {
        // do nothing
    }

    @Override public void smoothScrollToTop(boolean autoRefresh) {
        super.smoothScrollToTop(autoRefresh);
        if (autoRefresh) startRefreshing();
    }
}
