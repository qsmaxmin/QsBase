package com.qsmaxmin.qsbase.mvvm.fragment;

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
import com.qsmaxmin.qsbase.mvvm.MvIPullView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @CreateBy qsmaxmin
 * @Date 17/7/4
 * @Description
 */

public abstract class MvPullFragment extends MvFragment implements MvIPullView {
    private PtrFrameLayout mPtrFrameLayout;
    private View           childView;

    @Override public View onCreateContentView(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return inflater.inflate(R.layout.qs_pulll_view, parent, false);
    }

    @NonNull @Override public PtrUIHandler getPtrUIHandlerView() {
        return new BeautyCircleRefreshHeader(getContext());
    }

    @Override protected View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        View view = super.initView(inflater, container);
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

    @Override public void openPullRefreshing() {
        if (QsHelper.isMainThread()) {
            mPtrFrameLayout.setEnabled(true);
        } else {
            mPtrFrameLayout.post(new Runnable() {
                @Override public void run() {
                    mPtrFrameLayout.setEnabled(true);
                }
            });
        }
    }

    @Override public void closePullRefreshing() {
        closePullRefreshing(false);
    }

    @Override public boolean canPullRefreshing() {
        return mPtrFrameLayout.isEnabled();
    }

    public void closePullRefreshing(final boolean enableOverDrag) {
        if (QsHelper.isMainThread()) {
            if (enableOverDrag) {
                mPtrFrameLayout.setEnabled(true);
                View headerView = mPtrFrameLayout.getHeaderView();
                if (headerView != null) headerView.setVisibility(View.GONE);
                mPtrFrameLayout.setKeepHeaderWhenRefresh(false);
            } else {
                mPtrFrameLayout.setEnabled(false);
            }
        } else {
            mPtrFrameLayout.post(new Runnable() {
                @Override public void run() {
                    if (enableOverDrag) {
                        mPtrFrameLayout.setEnabled(true);
                        View headerView = mPtrFrameLayout.getHeaderView();
                        if (headerView != null) headerView.setVisibility(View.GONE);
                        mPtrFrameLayout.setKeepHeaderWhenRefresh(false);
                    } else {
                        mPtrFrameLayout.setEnabled(false);
                    }
                }
            });
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

    @Override public View getScrollableView() {
        return childView;
    }
}
