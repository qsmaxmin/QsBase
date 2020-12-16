package com.qsmaxmin.qsbase.mvvm;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.common.utils.QsHelper;
import com.qsmaxmin.qsbase.common.widget.listview.LoadingFooter;
import com.qsmaxmin.qsbase.common.widget.ptr.PtrDefaultHandler;
import com.qsmaxmin.qsbase.common.widget.ptr.PtrFrameLayout;
import com.qsmaxmin.qsbase.common.widget.ptr.PtrUIHandler;
import com.qsmaxmin.qsbase.common.widget.ptr.header.BeautyCircleRefreshHeader;

/**
 * @CreateBy qsmaxmin
 * @Date 17/7/4
 * @Description
 */

public abstract class MvPullActivity extends MvActivity implements MvIPullView {
    private PtrFrameLayout mPtrFrameLayout;
    private View           childView;

    @Override public View onCreateContentView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(R.layout.qs_pulll_view, parent, false);
    }

    @Override public PtrUIHandler getPtrUIHandlerView() {
        return new BeautyCircleRefreshHeader(getContext());
    }

    @Override protected View initView(LayoutInflater inflater) {
        View view = super.initView(inflater);
        initPtrFrameLayout(view);
        childView = onCreateChildView(inflater, mPtrFrameLayout);
        if (childView.getParent() == null) {
            mPtrFrameLayout.addView(childView);
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
        mPtrFrameLayout.setEnabled(canPullRefreshing());
    }

    /**
     * 获取下拉刷新控件
     */
    @Override public PtrFrameLayout getPtrFrameLayout() {
        return mPtrFrameLayout;
    }

    @Override public void startRefreshing() {
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

    @Override public final void onLoad() {
        // do nothing
    }

    @Override public boolean canPullRefreshing() {
        return true;
    }

    @Override public boolean canPullLoading() {
        return false;
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
    }

    @Override public final void closePullLoading() {
    }

    @Override public final void setLoadingState(LoadingFooter.State state) {
        // do nothing
    }

    @Override public LoadingFooter.State getLoadingState() {
        return null;
    }


    @Override public void smoothScrollToTop(boolean autoRefresh) {
        if (childView != null && childView instanceof ScrollView) {
            ScrollView scrollView = (ScrollView) childView;
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
