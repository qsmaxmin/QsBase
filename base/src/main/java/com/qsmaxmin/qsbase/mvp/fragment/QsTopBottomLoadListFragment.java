package com.qsmaxmin.qsbase.mvp.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.utils.QsHelper;
import com.qsmaxmin.qsbase.common.widget.listview.LoadingFooter;
import com.qsmaxmin.qsbase.mvp.presenter.QsPresenter;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @CreateBy qsmaxmin
 * @Date 2019/3/21 13:55
 * @Description listView滑动到顶部和底部都能加载更多数据
 */
public abstract class QsTopBottomLoadListFragment<P extends QsPresenter, D> extends QsListFragment<P, D> implements QsITopBottomLoadView<D> {
    public static final byte          LOAD_WHEN_SCROLL_TO_BOTTOM = 0;
    public static final byte          LOAD_WHEN_SECOND_TO_LAST   = 1;
    private             boolean       canTopLoading              = true;
    private             boolean       canBottomLoading           = true;
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
        return view;
    }

    @Override public int getHeaderLayout() {
        return R.layout.qs_loading_footer;
    }

    @Override public int getFooterLayout() {
        return R.layout.qs_loading_footer;
    }

    @Override public void onScrollStateChanged(AbsListView view, int scrollState) {
        super.onScrollStateChanged(view, scrollState);
        if (onLoadTriggerCondition() == LOAD_WHEN_SCROLL_TO_BOTTOM && scrollState == SCROLL_STATE_IDLE) {
            if (canTopLoading && !canListScrollDown()) {
                loadingTopData();
            }

            if (canBottomLoading && !canListScrollUp()) {
                loadingBottomData();
            }
        }
    }

    @Override public void onAdapterGetView(int position, int totalCount) {
        super.onAdapterGetView(position, totalCount);
        if (onLoadTriggerCondition() == LOAD_WHEN_SECOND_TO_LAST) {
            if (canTopLoading && position == 0) {
                loadingTopData();
            }

            if (canBottomLoading && position == totalCount - 2 || totalCount == 1) {
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

    @Override public void openTopLoading() {
        canTopLoading = true;
    }

    @Override public void closeTopLoading() {
        canTopLoading = false;
    }

    @Override public void openBottomLoading() {
        canBottomLoading = true;
    }

    @Override public void closeBottomLoading() {
        canBottomLoading = false;
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
            } else {
                post(new Runnable() {
                    @Override public void run() {
                        mList.addAll(0, list);
                        updateAdapter(true);
                        getListView().setSelectionFromTop(list.size() + index + 1, topMargin);
                    }
                });
            }
        }
    }

    @Override public void addBottomData(List<D> list) {
        addData(list);
    }

    protected int onLoadTriggerCondition() {
        return LOAD_WHEN_SECOND_TO_LAST;
    }

    private void loadingBottomData() {
        if (canBottomLoading && bottomLoadingView != null) {
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
        if (canTopLoading && topLoadingView != null) {
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
