package com.qsmaxmin.qsbase.mvp.fragment;

import android.support.v4.view.ViewCompat;
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
import com.qsmaxmin.qsbase.common.utils.QsHelper;
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

    public static final int     TYPE_LIST          = 1 << 2;
    public static final int     TYPE_GRID          = 2 << 2;
    public static final int     TYPE_STAGGEREDGRID = 3 << 2;
    private final       List<D> mList              = new ArrayList<>();

    private   HeaderFooterRecyclerView   mRecyclerView;
    private   RecyclerView.Adapter       mRecyclerViewAdapter;
    private   View                       headerView;
    private   View                       footerView;
    protected StaggeredGridLayoutManager staggeredGridLayoutManager;

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
            mRecyclerView = (HeaderFooterRecyclerView) view.findViewById(android.R.id.list);
        }
        if (mRecyclerView == null) throw new RuntimeException("HeaderFooterRecyclerView is not exit or its id not 'android.R.id.list' in current layout!!");
        if (getHeaderLayout() > 0) {
            headerView = inflater.inflate(getHeaderLayout(), null);
            mRecyclerView.addHeaderView(headerView);
            QsHelper.getInstance().getViewBindHelper().bind(this, headerView);
        }
        if (getFooterLayout() > 0) {
            footerView = inflater.inflate(getFooterLayout(), null);
            mRecyclerView.addFooterView(footerView);
            QsHelper.getInstance().getViewBindHelper().bind(this, footerView);
        }

        mRecyclerViewAdapter = onCreateAdapter();
        if (mRecyclerViewAdapter == null) {
            mRecyclerViewAdapter = new MyRecycleAdapter(inflater);
        }
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

        switch (getRecyclerViewType()) {
            case TYPE_LIST:
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                break;

            case TYPE_GRID:
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

            case TYPE_STAGGEREDGRID:
                staggeredGridLayoutManager = new StaggeredGridLayoutManager(getSpanCount(), StaggeredGridLayoutManager.VERTICAL);
                /*顶部不留白*/
                staggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
                mRecyclerView.setLayoutManager(staggeredGridLayoutManager);
                break;
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

    @Override public void setData(List<D> list, boolean showEmptyView) {
        synchronized (mList) {
            mList.clear();
            if (list != null && !list.isEmpty()) mList.addAll(list);
            updateAdapter(showEmptyView);
        }
    }

    @Override public void addData(D d) {
        if (d != null) {
            synchronized (mList) {
                mList.add(d);
                updateAdapter(true);
            }
        }
    }

    @Override public void addData(List<D> list) {
        if (list != null && !list.isEmpty()) {
            synchronized (mList) {
                mList.addAll(list);
                updateAdapter(true);
            }
        }
    }

    @Override @ThreadPoint(ThreadType.MAIN) public void addData(List<D> list, int position) {
        if (list != null && !list.isEmpty() && position >= 0) {
            synchronized (mList) {
                position = (position < mList.size()) ? position : mList.size();
                if (mRecyclerViewAdapter != null) mRecyclerViewAdapter.notifyItemRangeInserted(position, list.size());
                mList.addAll(position, list);
                updateAdapter(true);
            }
        }
    }

    @Override @ThreadPoint(ThreadType.MAIN) public void delete(int position) {
        synchronized (mList) {
            if (position >= 0 && position < mList.size()) {
                if (mRecyclerViewAdapter != null) mRecyclerViewAdapter.notifyItemRemoved(position);
                mList.remove(position);
                updateAdapter(true);
            }
        }
    }

    @Override public void delete(D d) {
        if (d == null) return;
        synchronized (mList) {
            mList.remove(d);
            updateAdapter(true);
        }
    }

    @Override public void deleteAll() {
        synchronized (mList) {
            mList.clear();
            updateAdapter(true);
        }
    }

    @Override public D getData(int position) {
        if (position >= 0 && position < mList.size()) {
            return mList.get(position);
        }
        return null;
    }

    @Override public final List<D> getData() {
        return mList;
    }

    @ThreadPoint(ThreadType.MAIN) @Override public void updateAdapter(boolean showEmptyView) {
        if (mRecyclerViewAdapter != null) {
            mRecyclerViewAdapter.notifyDataSetChanged();
            if (mViewAnimator != null) {
                if (mList.isEmpty() && showEmptyView) {
                    showEmptyView();
                } else {
                    showContentView();
                }
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
        return ViewCompat.canScrollVertically(getRecyclerView(), -1);
    }

    /**
     * recyclerView是否滑动到底部
     */
    @Override public boolean canListScrollUp() {
        return ViewCompat.canScrollVertically(getRecyclerView(), 1);
    }

    /**
     * 适配器
     */
    public class MyRecycleAdapter extends RecyclerView.Adapter<MyRecycleViewHolder<D>> {
        private final LayoutInflater mInflater;

        MyRecycleAdapter(LayoutInflater inflater) {
            this.mInflater = inflater;
        }

        @Override public MyRecycleViewHolder<D> onCreateViewHolder(ViewGroup parent, int viewType) {
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

        @Override public void onBindViewHolder(MyRecycleViewHolder<D> holder, int position) {
            holder.onBindData(mList.get(position), position, mList.size());
        }

        @Override public int getItemViewType(int position) {
            return QsRecyclerFragment.this.getItemViewType(position);
        }

        @Override public int getItemCount() {
            return mList.size();
        }
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
}
