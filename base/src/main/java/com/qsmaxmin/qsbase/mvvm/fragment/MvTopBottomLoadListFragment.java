package com.qsmaxmin.qsbase.mvvm.fragment;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.utils.QsHelper;
import com.qsmaxmin.qsbase.common.widget.listview.LoadingFooter;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @CreateBy qsmaxmin
 * @Date 2019/3/21 13:55
 * @Description listView滑动到顶部和底部都能加载更多数据
 */
public abstract class MvTopBottomLoadListFragment<D> extends MvListFragment<D> implements MvITopBottomLoadView<D> {
    public static final byte          LOAD_WHEN_SCROLL_TO_BOTTOM = 0;
    public static final byte          LOAD_WHEN_SECOND_TO_LAST   = 1;
    private             boolean       isTopLoadingOpen           = true;
    private             boolean       isBottomLoadingOpen        = true;
    private             LoadingFooter topLoadingView;
    private             LoadingFooter bottomLoadingView;

    @Override protected View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        View view = super.initView(inflater, container);
        View headerView = getHeaderView();
        if (headerView instanceof LoadingFooter) {
            this.topLoadingView = (LoadingFooter) headerView;
        }
        View footerView = getFooterView();
        if (footerView instanceof LoadingFooter) {
            this.bottomLoadingView = (LoadingFooter) footerView;
        }
        if (!canTopLoading()) {
            setTopLoadingState(LoadingFooter.State.TheEnd);
        }
        if (!canBottomLoading()) {
            setBottomLoadingState(LoadingFooter.State.TheEnd);
        }
        return view;
    }

    @SuppressLint("InflateParams")
    @Override public View onCreateListHeaderView(LayoutInflater inflater) {
        return inflater.inflate(R.layout.qs_loading_footer, null);
    }

    @SuppressLint("InflateParams")
    @Override public View onCreateListFooterView(LayoutInflater inflater) {
        return inflater.inflate(R.layout.qs_loading_footer, null);
    }

    @Override public void onScrollStateChanged(AbsListView view, int scrollState) {
        super.onScrollStateChanged(view, scrollState);
        if (onLoadTriggerCondition() == LOAD_WHEN_SCROLL_TO_BOTTOM && scrollState == SCROLL_STATE_IDLE) {
            if (isTopLoadingOpen && !canListScrollDown()) {
                loadingTopData();
            }

            if (isBottomLoadingOpen && !canListScrollUp()) {
                loadingBottomData();
            }
        }
    }

    @Override public void onAdapterGetView(int position, int totalCount) {
        super.onAdapterGetView(position, totalCount);
        if (onLoadTriggerCondition() == LOAD_WHEN_SECOND_TO_LAST) {
            if (isTopLoadingOpen && position == 0) {
                loadingTopData();
            }

            if (isBottomLoadingOpen && position == totalCount - 2 || totalCount == 1) {
                loadingBottomData();
            }
        }
    }

    @Override public void setTopLoadingState(LoadingFooter.State state) {
        L.i(initTag(), "setTopLoadingState:" + state);
        if (topLoadingView != null) {
            topLoadingView.setState(state);
        }
    }

    @Override public void setBottomLoadingState(LoadingFooter.State state) {
        L.i(initTag(), "setBottomLoadingState:" + state);
        if (bottomLoadingView != null) {
            bottomLoadingView.setState(state);
        }
    }

    @Override public LoadingFooter.State getTopLoadingState() {
        return topLoadingView == null ? null : topLoadingView.getState();
    }

    @Override public LoadingFooter.State getBottomLoadingState() {
        return bottomLoadingView == null ? null : bottomLoadingView.getState();
    }

    @Override public final void openTopLoading() {
        isTopLoadingOpen = true;
    }

    @Override public final void closeTopLoading() {
        isTopLoadingOpen = false;
    }

    @Override public final void openBottomLoading() {
        isBottomLoadingOpen = true;
    }

    @Override public final void closeBottomLoading() {
        isBottomLoadingOpen = false;
    }

    @Override public boolean canTopLoading() {
        return true;
    }

    @Override public boolean canBottomLoading() {
        return true;
    }

    @Override public void addTopData(final List<D> list) {
        if (list != null && !list.isEmpty()) {
            final int index = getListView().getFirstVisiblePosition();
            View childAt = getListView().getChildAt(0);
            final int topMargin = (childAt == null) ? 0 : childAt.getHeight() + childAt.getTop();

            if (QsHelper.isMainThread()) {
                mList.addAll(0, list);
                updateAdapter(true);
                getListView().setSelectionFromTop(list.size() + index + 1, topMargin);
                setTopLoadingStateByData(list);
            } else {
                post(new Runnable() {
                    @Override public void run() {
                        mList.addAll(0, list);
                        updateAdapter(true);
                        getListView().setSelectionFromTop(list.size() + index + 1, topMargin);
                        setTopLoadingStateByData(list);
                    }
                });
            }
        }
    }

    @Override public void addBottomData(List<D> list) {
        addData(list);
    }

    @Override public void addData(List<D> list, int position) {
        super.addData(list, position);
        setBottomLoadingStateByData(list);
    }

    protected void setBottomLoadingStateByData(List<D> list) {
        if (list == null || list.isEmpty()) {
            setBottomLoadingState(LoadingFooter.State.TheEnd);
        } else if (isBottomLoadingOpen && getBottomLoadingState() != LoadingFooter.State.Normal) {
            setBottomLoadingState(LoadingFooter.State.Normal);
        }
    }

    protected void setTopLoadingStateByData(List<D> list) {
        if (list == null || list.isEmpty()) {
            setTopLoadingState(LoadingFooter.State.TheEnd);
        } else if (isTopLoadingOpen && getTopLoadingState() != LoadingFooter.State.Normal) {
            setTopLoadingState(LoadingFooter.State.Normal);
        }
    }

    protected int onLoadTriggerCondition() {
        return LOAD_WHEN_SECOND_TO_LAST;
    }

    private void loadingBottomData() {
        if (isBottomLoadingOpen && bottomLoadingView != null) {
            LoadingFooter.State state = bottomLoadingView.getState();
            if (state == LoadingFooter.State.Loading) {
                L.i(initTag(), "Under bottom loading..........");
                return;
            } else if (state == LoadingFooter.State.TheEnd) {
                L.i(initTag(), "no more data...........");
                return;
            }
            setBottomLoadingState(LoadingFooter.State.Loading);
            onBottomLoading();
        }
    }

    private void loadingTopData() {
        if (isTopLoadingOpen && topLoadingView != null) {
            LoadingFooter.State state = topLoadingView.getState();
            if (state == LoadingFooter.State.Loading) {
                L.i(initTag(), "Under top loading..........");
                return;
            } else if (state == LoadingFooter.State.TheEnd) {
                L.i(initTag(), "no more data...........");
                return;
            }
            setTopLoadingState(LoadingFooter.State.Loading);
            onTopLoading();
        }
    }
}
