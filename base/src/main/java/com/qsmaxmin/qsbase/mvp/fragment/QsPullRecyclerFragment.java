package com.qsmaxmin.qsbase.mvp.fragment;

import android.view.LayoutInflater;
import android.view.View;

import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.widget.listview.LoadingFooter;
import com.qsmaxmin.qsbase.common.widget.ptr.PtrDefaultHandler;
import com.qsmaxmin.qsbase.common.widget.ptr.PtrFrameLayout;
import com.qsmaxmin.qsbase.common.widget.ptr.PtrHandler;
import com.qsmaxmin.qsbase.common.widget.recyclerview.EndlessRecyclerOnScrollListener;
import com.qsmaxmin.qsbase.mvp.presenter.QsPresenter;

import java.util.List;

/**
 * @CreateBy qsmaxmin
 * @Date 17/7/2  下午4:23
 * @Description
 */

public abstract class QsPullRecyclerFragment<P extends QsPresenter, D> extends QsRecyclerFragment<P, D> implements QsIPullRecyclerFragment<D> {

    private boolean canLoadingMore = true;
    private PtrFrameLayout mPtrFrameLayout;

    @Override public int getFooterLayout() {
        return R.layout.qs_loading_footer;
    }

    @Override public int layoutId() {
        return R.layout.qs_fragment_pull_recyclerview;
    }

    @Override protected View initView(LayoutInflater inflater) {
        View view = super.initView(inflater);
        initPtrFrameLayout(view);
        getRecyclerView().addOnScrollListener(mOnScrollListener);
        return view;
    }

    private void initPtrFrameLayout(View view) {
        if (view instanceof PtrFrameLayout) {
            mPtrFrameLayout = (PtrFrameLayout) view;
        } else {
            mPtrFrameLayout = (PtrFrameLayout) view.findViewById(R.id.swipe_container);
        }
        if (mPtrFrameLayout == null) throw new RuntimeException("PtrFrameLayout is not exit or its id not 'R.id.swipe_container' in current layout!!");
        mPtrFrameLayout.setHeaderView((View) getPtrUIHandlerView());
        mPtrFrameLayout.addPtrUIHandler(getPtrUIHandlerView());
        mPtrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }

            @Override public void onRefreshBegin(PtrFrameLayout frame) {
                onRefresh();
            }
        });
    }


    @Override public void requestRefresh() {
        mPtrFrameLayout.autoRefresh();
    }

    @Override public void closeRefresh() {
        mPtrFrameLayout.refreshComplete();
    }

    @Override public void setLoadingState(LoadingFooter.State state) {
        if (mLoadingFooter != null) {
            L.i(initTag(), "设置刷新尾部状态：" + state);
            mLoadingFooter.setState(state);
        }
    }

    @Override public void openPullRefreshing() {
        mPtrFrameLayout.setEnabled(true);
    }

    @Override public void closePullRefreshing() {
        mPtrFrameLayout.setEnabled(false);
    }

    @Override public void openPullLoading() {
        canLoadingMore = true;
    }

    @Override public void closePullLoading() {
        canLoadingMore = false;
    }

    @Override public PtrFrameLayout getPtrFrameLayout() {
        return mPtrFrameLayout;
    }

    @Override public void setData(List<D> list) {
        mPtrFrameLayout.refreshComplete();
        super.setData(list);
    }

    private EndlessRecyclerOnScrollListener mOnScrollListener = new EndlessRecyclerOnScrollListener() {
        @Override public void onLoadNextPage(View view) {
            super.onLoadNextPage(view);
            loadingMoreData();
        }
    };

    private void loadingMoreData() {
        if (mLoadingFooter != null) {
            LoadingFooter.State state = mLoadingFooter.getState();
            if (!canLoadingMore) {
                return;
            } else if (state == LoadingFooter.State.Loading) {
                L.i(initTag(), "正在加载中,无需再次加载..........");
                return;
            } else if (state == LoadingFooter.State.TheEnd) {
                L.i(initTag(), "加载完了,没有更多数据了...........");
                return;
            }
            setLoadingState(LoadingFooter.State.Loading);
            onLoad();
        }
    }
}
