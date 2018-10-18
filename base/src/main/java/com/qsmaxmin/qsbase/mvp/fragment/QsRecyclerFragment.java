package com.qsmaxmin.qsbase.mvp.fragment;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.common.aspect.ThreadPoint;
import com.qsmaxmin.qsbase.common.aspect.ThreadType;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.viewbind.ViewBindData;
import com.qsmaxmin.qsbase.common.viewbind.ViewBindHelper;
import com.qsmaxmin.qsbase.common.widget.recyclerview.HeaderFooterRecyclerView;
import com.qsmaxmin.qsbase.mvp.adapter.MyRecycleViewHolder;
import com.qsmaxmin.qsbase.mvp.adapter.QsRecycleAdapterItem;
import com.qsmaxmin.qsbase.mvp.presenter.QsPresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * @CreateBy qsmaxmin
 * @Date 17/7/1
 * @Description RecyclerView视图
 */
public abstract class QsRecyclerFragment<P extends QsPresenter, D> extends QsFragment<P> implements QsIRecyclerFragment<D>, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    public static final byte    TYPE_LIST          = 1 << 2;
    public static final byte    TYPE_GRID          = 2 << 2;
    public static final byte    TYPE_STAGGEREDGRID = 3 << 2;
    private final       List<D> mList              = new ArrayList<>();

    private HeaderFooterRecyclerView mRecyclerView;
    private RecyclerView.Adapter     mRecyclerViewAdapter;
    private View                     headerView;
    private View                     footerView;

    @Override public int layoutId() {
        return R.layout.qs_fragment_recycleview;
    }

    @Override public int getHeaderLayout() {
        return 0;
    }

    @Override public int getFooterLayout() {
        return 0;
    }

    @Override public int getTopLayout() {
        return 0;
    }

    @Override public int getBottomLayout() {
        return 0;
    }

    @Override public RecyclerView.Adapter onCreateAdapter() {
        return null;
    }

    @Override public HeaderFooterRecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    @Override protected View initView(LayoutInflater inflater) {
        View rootView = super.initView(inflater);
        if (getTopLayout() > 0 || getBottomLayout() > 0) initTopBottomView(rootView, inflater);
        initRecycleView(inflater, rootView);
        return rootView;
    }

    @Override protected int rootViewLayoutId() {
        return (getTopLayout() > 0 || getBottomLayout() > 0) ? R.layout.qs_fragment_state_with_top_bottom : super.rootViewLayoutId();
    }

    protected void initTopBottomView(View rootView, LayoutInflater inflater) {
        if (rootView instanceof LinearLayout) {
            if (getTopLayout() > 0) {
                ((LinearLayout) rootView).addView(inflater.inflate(getTopLayout(), null), 0);
            }
            if (getBottomLayout() > 0) {
                int childCount = ((LinearLayout) rootView).getChildCount();
                ((LinearLayout) rootView).addView(inflater.inflate(getBottomLayout(), null), childCount);
            }
        } else {
            L.e(initTag(), "rootViewLayoutId() root view must be LinearLayout when getTopLayout() or getBottomLayout() is overwrite, but now is:" + rootView.getClass().getSimpleName());
        }
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
        if (getHeaderLayout() > 0) {
            headerView = inflater.inflate(getHeaderLayout(), null);
            mRecyclerView.addHeaderView(headerView);
            ViewBindHelper.bindView(this, headerView);
        }
        if (getFooterLayout() > 0) {
            footerView = inflater.inflate(getFooterLayout(), null);
            mRecyclerView.addFooterView(footerView);
            ViewBindHelper.bindView(this, footerView);
        }

        mRecyclerView.addItemDecoration(new CustomItemDecoration());
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                //防止滚动列表时item位置互换及滑动到顶部时对齐顶部
                if (newState == RecyclerView.SCROLL_STATE_IDLE && getRecyclerViewType() == TYPE_STAGGEREDGRID && getSpanCount() > 1) {
                    int[] spanArr = new int[getSpanCount()];
                    StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) getRecyclerView().getLayoutManager();
                    layoutManager.findFirstCompletelyVisibleItemPositions(spanArr);
                    for (int index : spanArr) {
                        if (index == 1 || index == 0) {
                            getAdapter().notifyDataSetChanged();
                            break;
                        }
                    }
                }
                QsRecyclerFragment.this.onScrollStateChanged(recyclerView, newState);
            }

            @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                QsRecyclerFragment.this.onScrolled(recyclerView, dx, dy);
            }
        });
        mRecyclerViewAdapter = onCreateAdapter();
        if (mRecyclerViewAdapter == null) {
            mRecyclerViewAdapter = new MyRecycleAdapter(inflater);
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
                        if (getHeaderLayout() > 0 && position == 0) {
                            return getSpanCount();
                        } else if (getHeaderLayout() > 0 && getFooterLayout() > 0 && position == mList.size() + 1) {
                            return getSpanCount();
                        } else if (getHeaderLayout() == 0 && getFooterLayout() > 0 && position == mList.size()) {
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

    /**
     * 适配器
     */
    public class MyRecycleAdapter extends RecyclerView.Adapter<MyRecycleViewHolder<D>> {
        private final LayoutInflater mInflater;

        MyRecycleAdapter(LayoutInflater inflater) {
            this.mInflater = inflater;
        }

        @NonNull @Override public MyRecycleViewHolder<D> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            QsRecycleAdapterItem<D> recycleAdapterItem = getRecycleAdapterItem(mInflater, parent, viewType);
            MyRecycleViewHolder<D> holder = new MyRecycleViewHolder<>(recycleAdapterItem);

            holder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    QsRecyclerFragment.this.onItemClick(parent, view, position, id);
                }
            });
            holder.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    return QsRecyclerFragment.this.onItemLongClick(parent, view, position, id);
                }
            });
            return holder;
        }

        @Override public void onBindViewHolder(@NonNull MyRecycleViewHolder<D> holder, int position) {
            onAdapterGetView(position, getItemCount());
            holder.onBindData(mList.get(position), position, mList.size());
        }

        @Override public int getItemViewType(int position) {
            return QsRecyclerFragment.this.getItemViewType(position);
        }

        @Override public int getItemCount() {
            return mList.size();
        }
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
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            setItemOffset(outRect, view, parent, state);
        }
    }

    protected void setItemOffset(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        //for custom logic
    }

}
