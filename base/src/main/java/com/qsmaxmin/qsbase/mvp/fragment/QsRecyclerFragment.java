package com.qsmaxmin.qsbase.mvp.fragment;

import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.utils.QsHelper;
import com.qsmaxmin.qsbase.common.widget.recyclerview.HeaderFooterRecyclerView;
import com.qsmaxmin.qsbase.mvp.QsIRecyclerView;
import com.qsmaxmin.qsbase.mvp.adapter.QsRecycleAdapterItem;
import com.qsmaxmin.qsbase.mvp.adapter.QsRecyclerAdapter;
import com.qsmaxmin.qsbase.mvp.presenter.QsPresenter;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/**
 * @CreateBy qsmaxmin
 * @Date 17/7/1
 * @Description RecyclerView视图
 */
public abstract class QsRecyclerFragment<P extends QsPresenter, D> extends QsFragment<P> implements QsIRecyclerView<D> {
    private final List<D>                  mList = new ArrayList<>();
    private       HeaderFooterRecyclerView mRecyclerView;
    private       RecyclerView.Adapter     mRecyclerViewAdapter;
    private       View                     headerView;
    private       View                     footerView;

    @Override public int layoutId() {
        return R.layout.qs_recyclerview;
    }

    @Override public int getHeaderLayout() {
        return 0;
    }

    @Override public int getFooterLayout() {
        return 0;
    }

    @Override public final View getHeaderView() {
        return headerView;
    }

    @Override public final View getFooterView() {
        return footerView;
    }

    @Override public final HeaderFooterRecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    @Override protected View initView(LayoutInflater inflater) {
        View rootView = super.initView(inflater);
        initRecycleView(inflater, rootView);
        return rootView;
    }

    /**
     * 初始化RecycleView
     */
    private void initRecycleView(LayoutInflater inflater, View view) {
        if (view instanceof HeaderFooterRecyclerView) {
            mRecyclerView = (HeaderFooterRecyclerView) view;
        } else {
            mRecyclerView = view.findViewById(android.R.id.list);
        }
        if (mRecyclerView == null) throw new RuntimeException("HeaderFooterRecyclerView is not exit or its id not 'android.R.id.list' in current layout!!");
        if (getHeaderLayout() != 0) {
            headerView = inflater.inflate(getHeaderLayout(), null);
            mRecyclerView.addHeaderView(headerView);
            bindViewByQsPlugin(headerView);
        }
        if (getFooterLayout() != 0) {
            footerView = inflater.inflate(getFooterLayout(), null);
            mRecyclerView.addFooterView(footerView);
            bindViewByQsPlugin(footerView);
        }

        mRecyclerView.addItemDecoration(new CustomItemDecoration());
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE && getRecyclerViewType() == TYPE_STAGGERED_GRID && getSpanCount() > 1) {
                    int[] spanArr = new int[getSpanCount()];
                    StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) getRecyclerView().getLayoutManager();
                    if (layoutManager != null) layoutManager.findFirstCompletelyVisibleItemPositions(spanArr);
                    for (int index : spanArr) {
                        if (index == 1 || index == 0) {
                            getAdapter().notifyDataSetChanged();
                            break;
                        }
                    }
                }
                QsRecyclerFragment.this.onScrollStateChanged(recyclerView, newState);
            }

            @Override public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                QsRecyclerFragment.this.onScrolled(recyclerView, dx, dy);
            }
        });
        mRecyclerViewAdapter = new QsRecyclerAdapter<>(this, mList, inflater);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

        switch (getRecyclerViewType()) {
            case TYPE_LIST: {
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                break;
            }
            case TYPE_GRID: {
                final GridLayoutManager manager = new GridLayoutManager(getContext(), getSpanCount());
                manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override public int getSpanSize(int position) {
                        L.i(initTag(), "getSpanSize, position:" + position);
                        if (getHeaderLayout() != 0 && position == 0) {
                            return getSpanCount();
                        } else if (getHeaderLayout() != 0 && getFooterLayout() != 0 && position == mList.size() + 1) {
                            return getSpanCount();
                        } else if (getHeaderLayout() == 0 && getFooterLayout() != 0 && position == mList.size()) {
                            return getSpanCount();
                        } else {
                            return 1;
                        }
                    }
                });
                mRecyclerView.setLayoutManager(manager);
                break;
            }
            case TYPE_STAGGERED_GRID: {
                StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(getSpanCount(), StaggeredGridLayoutManager.VERTICAL);
                manager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
                mRecyclerView.setLayoutManager(manager);
                break;
            }
        }
    }

    @Override public final RecyclerView.Adapter getAdapter() {
        return mRecyclerViewAdapter;
    }

    @Override public final void setData(List<D> list) {
        setData(list, true);
    }

    @Override public void setData(final List<D> list, final boolean showEmptyView) {
        if (QsHelper.isMainThread()) {
            mList.clear();
            if (list != null && !list.isEmpty()) mList.addAll(list);
            updateAdapter(showEmptyView);
        } else {
            post(new Runnable() {
                @Override public void run() {
                    mList.clear();
                    if (list != null && !list.isEmpty()) mList.addAll(list);
                    updateAdapter(showEmptyView);
                }
            });
        }
    }

    @Override public void addData(final D d) {
        if (d != null) {
            if (QsHelper.isMainThread()) {
                mList.add(d);
                updateAdapter(true);
            } else {
                post(new Runnable() {
                    @Override public void run() {
                        mList.add(d);
                        updateAdapter(true);
                    }
                });
            }
        }
    }

    @Override public void addData(final List<D> list) {
        if (list != null && !list.isEmpty()) {
            if (QsHelper.isMainThread()) {
                mList.addAll(list);
                updateAdapter(true);
            } else {
                post(new Runnable() {
                    @Override public void run() {
                        mList.addAll(list);
                        updateAdapter(true);
                    }
                });
            }
        }
    }

    @Override public void addData(final List<D> list, int position) {
        if (list != null && !list.isEmpty() && position >= 0) {
            if (QsHelper.isMainThread()) {
                position = Math.min(position, mList.size());
                if (mRecyclerViewAdapter != null) mRecyclerViewAdapter.notifyItemRangeInserted(position, list.size());
                mList.addAll(position, list);
                updateAdapter(true);
            } else {
                final int finalPosition = Math.min(position, mList.size());
                post(new Runnable() {
                    @Override public void run() {
                        if (mRecyclerViewAdapter != null) mRecyclerViewAdapter.notifyItemRangeInserted(finalPosition, list.size());
                        mList.addAll(finalPosition, list);
                        updateAdapter(true);
                    }
                });
            }
        }
    }

    @Override public void delete(final int position) {
        if (position >= 0 && position < mList.size()) {
            if (QsHelper.isMainThread()) {
                if (mRecyclerViewAdapter != null) mRecyclerViewAdapter.notifyItemRemoved(position);
                mList.remove(position);
                updateAdapter(true);
            } else {
                post(new Runnable() {
                    @Override public void run() {
                        if (mRecyclerViewAdapter != null) mRecyclerViewAdapter.notifyItemRemoved(position);
                        mList.remove(position);
                        updateAdapter(true);
                    }
                });
            }
        }
    }

    @Override public void delete(final D d) {
        if (d != null) {
            if (QsHelper.isMainThread()) {
                boolean success;
                success = mList.remove(d);
                if (success) updateAdapter(true);
            } else {
                post(new Runnable() {
                    @Override public void run() {
                        boolean success;
                        success = mList.remove(d);
                        if (success) updateAdapter(true);
                    }
                });
            }
        }
    }

    @Override public void deleteAll() {
        if (!mList.isEmpty()) {
            if (QsHelper.isMainThread()) {
                mList.clear();
                updateAdapter(true);
            } else {
                post(new Runnable() {
                    @Override public void run() {
                        mList.clear();
                        updateAdapter(true);
                    }
                });
            }
        }
    }

    @Override public final List<D> getData() {
        return mList;
    }

    @Override public List<D> copyData() {
        ArrayList<D> list = new ArrayList<>();
        if (!mList.isEmpty()) list.addAll(mList);
        return list;
    }

    @Override public D getData(int position) {
        if (position >= 0 && position < mList.size()) {
            return mList.get(position);
        }
        return null;
    }

    /**
     * 该方法必须在主线程中执行
     */
    @Override public void updateAdapter(boolean showEmptyView) {
        if (mRecyclerViewAdapter != null) {
            mRecyclerViewAdapter.notifyDataSetChanged();
            if (mList.isEmpty() && showEmptyView) {
                showEmptyView();
            } else {
                showContentView();
            }
        }
    }

    @Override public QsRecycleAdapterItem<D> getRecycleAdapterItemInner(LayoutInflater mInflater, ViewGroup parent, int type) {
        QsRecycleAdapterItem<D> adapterItem = getRecycleAdapterItem(mInflater, parent, type);
        adapterItem.setViewLayer(this);
        return adapterItem;
    }

    @Override public void onReceiveAdapterItemEvent(int eventType, D data, int position) {
        L.i(initTag(), "onReceiveAdapterItemEvent......eventType:" + eventType + ", position:" + position);
    }

    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }

    @Override public boolean canListScrollDown() {
        return getRecyclerView().canScrollVertically(-1);
    }

    @Override public boolean canListScrollUp() {
        return getRecyclerView().canScrollVertically(1);
    }

    @Override public void smoothScrollToTop(boolean autoRefresh) {
        getRecyclerView().post(new Runnable() {
            @Override public void run() {
                getRecyclerView().smoothScrollToPosition(0);
            }
        });
    }

    @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        //for custom logic
    }

    @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        //for custom logic
    }

    protected int getSpanCount() {
        return 2;
    }

    protected int getRecyclerViewType() {
        return TYPE_LIST;
    }

    @Override public int getItemViewType(int position) {
        return 0;
    }

    @Override public void onAdapterGetView(int position, int totalCount) {
        //for custom logic
    }

    private class CustomItemDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            setItemOffset(outRect, view, parent, state);
        }
    }

    protected void setItemOffset(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        //for custom logic
    }

}
