package com.qsmaxmin.qsbase.mvp;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;

import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.common.aspect.ThreadPoint;
import com.qsmaxmin.qsbase.common.aspect.ThreadType;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.viewbind.ViewBindHelper;
import com.qsmaxmin.qsbase.common.widget.recyclerview.HeaderFooterRecyclerView;
import com.qsmaxmin.qsbase.mvp.adapter.QsRecyclerAdapter;
import com.qsmaxmin.qsbase.mvp.fragment.QsIRecyclerView;
import com.qsmaxmin.qsbase.mvp.presenter.QsPresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * @CreateBy administrator
 * @Date 2020/4/9 15:06
 * @Description list activity with actionbar
 */
public abstract class QsRecyclerActivity<P extends QsPresenter, D> extends QsActivity<P> implements QsIRecyclerView<D> {
    public static final byte TYPE_LIST          = 1;
    public static final byte TYPE_GRID          = 2;
    public static final byte TYPE_STAGGEREDGRID = 3;

    private final List<D>                  mList = new ArrayList<>();
    private       HeaderFooterRecyclerView mRecyclerView;
    private       RecyclerView.Adapter     mRecyclerViewAdapter;
    private       View                     headerView;
    private       View                     footerView;

    @Override public int layoutId() {
        return R.layout.qs_activity_recyclerview;
    }

    @Override public int getHeaderLayout() {
        return 0;
    }

    @Override public int getFooterLayout() {
        return 0;
    }

    @Override public RecyclerView.Adapter onCreateAdapter() {
        return null;
    }

    @Override public HeaderFooterRecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    @Override protected View initView() {
        View rootView = super.initView();
        initRecycleView(getLayoutInflater(), rootView);
        return rootView;
    }

    /**
     * 初始化RecycleView
     */
    protected void initRecycleView(LayoutInflater inflater, View view) {
        if (view instanceof HeaderFooterRecyclerView) {
            mRecyclerView = (HeaderFooterRecyclerView) view;
        } else {
            mRecyclerView = view.findViewById(android.R.id.list);
        }
        if (mRecyclerView == null) throw new RuntimeException("HeaderFooterRecyclerView is not exit or its id not 'android.R.id.list' in current layout!!");
        if (getHeaderLayout() != 0) {
            headerView = inflater.inflate(getHeaderLayout(), null);
            mRecyclerView.addHeaderView(headerView);
            ViewBindHelper.bindView(this, headerView);
        }
        if (getFooterLayout() != 0) {
            footerView = inflater.inflate(getFooterLayout(), null);
            mRecyclerView.addFooterView(footerView);
            ViewBindHelper.bindView(this, footerView);
        }

        mRecyclerView.addItemDecoration(new CustomItemDecoration());
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                //防止滚动列表时item位置互换及滑动到顶部时对齐顶部
                if (newState == RecyclerView.SCROLL_STATE_IDLE && getRecyclerViewType() == TYPE_STAGGEREDGRID && getSpanCount() > 1) {
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
                QsRecyclerActivity.this.onScrollStateChanged(recyclerView, newState);
            }

            @Override public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                QsRecyclerActivity.this.onScrolled(recyclerView, dx, dy);
            }
        });
        mRecyclerViewAdapter = onCreateAdapter();
        if (mRecyclerViewAdapter == null) {
            mRecyclerViewAdapter = new QsRecyclerAdapter<>(this, mList, inflater);
        }
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
                        /*当有footer或者header时特殊处理，让它占满整个一条*/
                        L.i(initTag(), "getSpanSize   position:" + position);
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
            case TYPE_STAGGEREDGRID: {
                StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(getSpanCount(), StaggeredGridLayoutManager.VERTICAL);
                manager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
                mRecyclerView.setLayoutManager(manager);
                break;
            }
        }
    }

    @Override public RecyclerView.Adapter getAdapter() {
        return mRecyclerViewAdapter;
    }

    public View getHeaderView() {
        return headerView;
    }

    public View getFooterView() {
        return footerView;
    }

    @Override public void setData(List<D> list) {
        setData(list, true);
    }

    @ThreadPoint(ThreadType.MAIN) @Override public void setData(List<D> list, boolean showEmptyView) {
        mList.clear();
        if (list != null && !list.isEmpty()) mList.addAll(list);
        updateAdapter(showEmptyView);
    }

    @ThreadPoint(ThreadType.MAIN) @Override public void addData(D d) {
        if (d != null) {
            mList.add(d);
            updateAdapter(true);
        }
    }

    @ThreadPoint(ThreadType.MAIN) @Override public void addData(List<D> list) {
        if (list != null && !list.isEmpty()) {
            mList.addAll(list);
            updateAdapter(true);
        }
    }

    @ThreadPoint(ThreadType.MAIN) @Override public void addData(List<D> list, int position) {
        if (list != null && !list.isEmpty() && position >= 0) {
            position = (position < mList.size()) ? position : mList.size();
            if (mRecyclerViewAdapter != null) mRecyclerViewAdapter.notifyItemRangeInserted(position, list.size());
            mList.addAll(position, list);
            updateAdapter(true);
        }
    }

    @ThreadPoint(ThreadType.MAIN) @Override public void delete(int position) {
        if (position >= 0 && position < mList.size()) {
            if (mRecyclerViewAdapter != null) mRecyclerViewAdapter.notifyItemRemoved(position);
            mList.remove(position);
            updateAdapter(true);
        }
    }

    @ThreadPoint(ThreadType.MAIN) @Override public void delete(D d) {
        if (d != null) {
            boolean success;
            success = mList.remove(d);
            if (success) updateAdapter(true);
        }
    }

    @ThreadPoint(ThreadType.MAIN) @Override public void deleteAll() {
        if (!mList.isEmpty()) {
            mList.clear();
            updateAdapter(true);
        }
    }

    @Override public final List<D> getData() {
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
            } else if (showContentViewWhenDataLoadingComplete()) {
                showContentView();
            }
        }
    }


    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }

    /**
     * recyclerView是否滑动到顶部
     */
    @Override public boolean canListScrollDown() {
        return getRecyclerView().canScrollVertically(-1);
    }

    @Override public boolean showContentViewWhenDataLoadingComplete() {
        return true;
    }

    /**
     * recyclerView是否滑动到底部
     */
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
