package com.qsmaxmin.qsbase.mvvm;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.widget.recyclerview.HeaderFooterRecyclerView;
import com.qsmaxmin.qsbase.common.widget.recyclerview.QsItemDecoration;
import com.qsmaxmin.qsbase.mvvm.adapter.MvRecycleAdapterItem;
import com.qsmaxmin.qsbase.mvvm.adapter.MvRecycleViewHolder;
import com.qsmaxmin.qsbase.mvvm.adapter.MvRecyclerAdapter;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/**
 * @CreateBy qsmaxmin
 * @Date 2020/4/9 15:06
 * @Description recycler activity
 */
public abstract class MvRecyclerActivity<D> extends MvActivity implements MvIRecyclerView<D> {
    private final MvRecyclerAdapter<D>     recyclerViewAdapter = new MvRecyclerAdapter<>(this);
    private       HeaderFooterRecyclerView recyclerView;
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

    @Override public View onCreateListHeaderView(@NonNull LayoutInflater inflater) {
        return getHeaderLayout() == 0 ? null : inflater.inflate(getHeaderLayout(), null);
    }

    @Override public View onCreateListFooterView(@NonNull LayoutInflater inflater) {
        return getFooterLayout() == 0 ? null : inflater.inflate(getFooterLayout(), null);
    }

    @Override public final View getHeaderView() {
        return headerView;
    }

    @Override public final View getFooterView() {
        return footerView;
    }

    @Override public HeaderFooterRecyclerView getRecyclerView() {
        return recyclerView;
    }

    @NonNull @Override protected View initView(@NonNull LayoutInflater inflater) {
        View rootView = super.initView(inflater);
        initRecycleView(getLayoutInflater(), rootView);
        return rootView;
    }

    /**
     * 初始化RecycleView
     */
    private void initRecycleView(LayoutInflater inflater, View rootView) {
        recyclerView = rootView.findViewById(android.R.id.list);
        if (recyclerView == null) throw new RuntimeException("HeaderFooterRecyclerView is not exit or its id not 'android.R.id.list' in current layout!!");

        headerView = onCreateListHeaderView(inflater);
        if (headerView != null) {
            recyclerView.addHeaderView(headerView);
        }

        footerView = onCreateListFooterView(inflater);
        if (footerView != null) {
            recyclerView.addFooterView(footerView);
        }

        RecyclerView.ItemDecoration itemDecoration = getItemDecoration();
        if (itemDecoration != null) {
            recyclerView.addItemDecoration(itemDecoration);
        }
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                int childCount = recyclerView.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View childView = recyclerView.getChildAt(i);
                    RecyclerView.ViewHolder holder = recyclerView.getChildViewHolder(childView);
                    if (holder instanceof MvRecycleViewHolder) {
                        ((MvRecycleViewHolder) holder).onScrollStateChanged(newState);
                    }
                }

                MvRecyclerActivity.this.onScrollStateChanged(recyclerView, newState);
            }

            @Override public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                MvRecyclerActivity.this.onScrolled(recyclerView, dx, dy);
            }
        });
        recyclerView.setLayoutManager(getLayoutManager());
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    @Override public RecyclerView.Adapter getAdapter() {
        return recyclerViewAdapter;
    }

    @Override public final void setData(List<D> list) {
        setData(list, true);
    }

    @Override public void setData(final List<D> list, final boolean showEmptyView) {
        recyclerViewAdapter.setData(list, showEmptyView);
    }

    @Override public final void addData(final D d) {
        recyclerViewAdapter.addData(d);
    }

    @Override public final void addData(final int position, final D d) {
        recyclerViewAdapter.addData(position, d);
    }

    @Override public final void addData(final List<D> list) {
        addData(list, getData().size());
    }

    @Override public void addData(final List<D> list, int position) {
        recyclerViewAdapter.addData(list, position);
    }

    @Override public final void delete(final int position) {
        recyclerViewAdapter.delete(position);
    }

    @Override public final void delete(final D d) {
        recyclerViewAdapter.delete(d);
    }

    @Override public final void deleteAll() {
        recyclerViewAdapter.deleteAll();
    }

    @NonNull @Override public final List<D> getData() {
        return recyclerViewAdapter.getData();
    }

    @NonNull @Override public final List<D> copyData() {
        return recyclerViewAdapter.copyData();
    }

    @Override public final D getData(int position) {
        return recyclerViewAdapter.getData(position);
    }

    /**
     * 该方法必须在主线程中执行
     */
    @Override public void updateAdapter(boolean showEmptyView) {
        recyclerViewAdapter.updateAdapter(showEmptyView);
    }

    @Override public boolean canRecyclerScrollStart() {
        return canRecyclerScrollInner(-1);
    }

    @Override public boolean canRecyclerScrollEnd() {
        return canRecyclerScrollInner(1);
    }

    @Override public void smoothScrollToTop(boolean autoRefresh) {
        if (getRecyclerView() != null) {
            getRecyclerView().post(new Runnable() {
                @Override public void run() {
                    getRecyclerView().smoothScrollToPosition(0);
                }
            });
        }
    }

    @Override public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        //for custom logic
    }

    @Override public void onScrollStateChanged(@NonNull RecyclerView view, int newState) {
        //for custom logic
    }

    @Override public final MvRecycleAdapterItem<D> getRecycleAdapterItemInner(@NonNull ViewGroup parent, int type) {
        MvRecycleAdapterItem<D> adapterItem = getRecycleAdapterItem(getLayoutInflater(), parent, type);
        adapterItem.setViewLayer(this, getLayoutInflater(), parent);
        return adapterItem;
    }

    @Override public void onReceiveAdapterItemEvent(int eventType, D data, int position) {
        L.i(initTag(), "onReceiveAdapterItemEvent......eventType:" + eventType + ", position:" + position);
    }

    @Override public int getItemViewType(int position) {
        return 0;
    }

    @Override public void onAdapterGetView(int position, int totalCount) {
        //for custom logic
    }

    @NonNull @Override public RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(this);
    }

    @Override public QsItemDecoration getItemDecoration() {
        return null;
    }

    private boolean canRecyclerScrollInner(int direction) {
        HeaderFooterRecyclerView view = getRecyclerView();
        if (view == null) return false;
        RecyclerView.LayoutManager manager = view.getLayoutManager();
        int orientation;
        if (manager instanceof LinearLayoutManager) {
            orientation = ((LinearLayoutManager) manager).getOrientation();
        } else if (manager instanceof StaggeredGridLayoutManager) {
            orientation = ((StaggeredGridLayoutManager) manager).getOrientation();
        } else {
            return false;
        }
        return (orientation == RecyclerView.VERTICAL && view.canScrollVertically(direction))
                || (orientation == RecyclerView.HORIZONTAL && view.canScrollHorizontally(direction));
    }
}
