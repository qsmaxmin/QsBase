package com.qsmaxmin.qsbase.mvvm.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.utils.QsHelper;
import com.qsmaxmin.qsbase.common.widget.listview.BaseLoadingFooter;
import com.qsmaxmin.qsbase.common.widget.listview.LoadingState;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @CreateBy qsmaxmin
 * @Date 2019/3/21 13:55
 * @Description listView滑动到顶部和底部都能加载更多数据
 */
public abstract class MvTopBottomLoadListFragment<D> extends MvListFragment<D> implements MvITopBottomLoadView<D> {
    private boolean           isTopLoadingOpen    = true;
    private boolean           isBottomLoadingOpen = true;
    private BaseLoadingFooter topLoadingView;
    private BaseLoadingFooter bottomLoadingView;

    @Override public int getHeaderLayout() {
        return R.layout.qs_loading_footer;
    }

    @Override public int getFooterLayout() {
        return R.layout.qs_loading_footer;
    }

    @Override protected View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        View view = super.initView(inflater, container);
        View headerView = getHeaderView();
        if (headerView instanceof BaseLoadingFooter) {
            this.topLoadingView = (BaseLoadingFooter) headerView;
        }
        View footerView = getFooterView();
        if (footerView instanceof BaseLoadingFooter) {
            this.bottomLoadingView = (BaseLoadingFooter) footerView;
        }
        if (!canTopLoading()) {
            setTopLoadingState(LoadingState.TheEnd);
        }
        if (!canBottomLoading()) {
            setBottomLoadingState(LoadingState.TheEnd);
        }
        return view;
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

    @Override public void setTopLoadingState(LoadingState state) {
        L.i(initTag(), "setTopLoadingState:" + state);
        if (topLoadingView != null) {
            topLoadingView.setState(state);
        }
    }

    @Override public void setBottomLoadingState(LoadingState state) {
        L.i(initTag(), "setBottomLoadingState:" + state);
        if (bottomLoadingView != null) {
            bottomLoadingView.setState(state);
        }
    }

    @Override public LoadingState getTopLoadingState() {
        return topLoadingView == null ? null : topLoadingView.getState();
    }

    @Override public LoadingState getBottomLoadingState() {
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

    @Override public final void addTopData(final List<D> list) {
        if (list != null && !list.isEmpty()) {
            final int index = getListView().getFirstVisiblePosition();
            View childAt = getListView().getChildAt(0);
            final int topMargin = (childAt == null) ? 0 : childAt.getHeight() + childAt.getTop();

            if (QsHelper.isMainThread()) {
                getData().addAll(0, list);
                updateAdapter(true);
                getListView().setSelectionFromTop(list.size() + index + 1, topMargin);
                setTopLoadingStateByData(list);
            } else {
                post(new Runnable() {
                    @Override public void run() {
                        getData().addAll(0, list);
                        updateAdapter(true);
                        getListView().setSelectionFromTop(list.size() + index + 1, topMargin);
                        setTopLoadingStateByData(list);
                    }
                });
            }
        }
    }

    @Override public final void addBottomData(List<D> list) {
        addData(list);
    }

    @Override public final void addData(List<D> list, int position) {
        super.addData(list, position);
        setBottomLoadingStateByData(list);
    }

    protected void setBottomLoadingStateByData(List<D> list) {
        if (list == null || list.isEmpty()) {
            setBottomLoadingState(LoadingState.TheEnd);
        } else if (isBottomLoadingOpen && getBottomLoadingState() != LoadingState.Normal) {
            setBottomLoadingState(LoadingState.Normal);
        }
    }

    protected void setTopLoadingStateByData(List<D> list) {
        if (list == null || list.isEmpty()) {
            setTopLoadingState(LoadingState.TheEnd);
        } else if (isTopLoadingOpen && getTopLoadingState() != LoadingState.Normal) {
            setTopLoadingState(LoadingState.Normal);
        }
    }

    protected int onLoadTriggerCondition() {
        return LOAD_WHEN_SECOND_TO_LAST;
    }

    private void loadingBottomData() {
        if (isBottomLoadingOpen && bottomLoadingView != null) {
            LoadingState state = bottomLoadingView.getState();
            if (state == LoadingState.Loading) {
                L.i(initTag(), "Under bottom loading..........");
                return;
            } else if (state == LoadingState.TheEnd) {
                L.i(initTag(), "no more data...........");
                return;
            }
            setBottomLoadingState(LoadingState.Loading);
            onBottomLoading();
        }
    }

    private void loadingTopData() {
        if (isTopLoadingOpen && topLoadingView != null) {
            LoadingState state = topLoadingView.getState();
            if (state == LoadingState.Loading) {
                L.i(initTag(), "Under top loading..........");
                return;
            } else if (state == LoadingState.TheEnd) {
                L.i(initTag(), "no more data...........");
                return;
            }
            setTopLoadingState(LoadingState.Loading);
            onTopLoading();
        }
    }
}
