package com.qsmaxmin.qsbase.mvvm;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.utils.QsHelper;
import com.qsmaxmin.qsbase.common.widget.recyclerview.HeaderFooterRecyclerView;
import com.qsmaxmin.qsbase.common.widget.recyclerview.QsItemDecoration;
import com.qsmaxmin.qsbase.mvvm.adapter.MvRecycleAdapterItem;
import com.qsmaxmin.qsbase.mvvm.adapter.MvRecyclerAdapter;

import java.util.ArrayList;
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
    private final List<D>                  mList = new ArrayList<>();
    private       HeaderFooterRecyclerView recyclerView;
    private       RecyclerView.Adapter     recyclerViewAdapter;
    private       View                     headerView;
    private       View                     footerView;

    @Override public View onCreateContentView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(R.layout.qs_recyclerview, parent, false);
    }

    @Override public View onCreateListHeaderView(LayoutInflater inflater) {
        return null;
    }

    @Override public View onCreateListFooterView(LayoutInflater inflater) {
        return null;
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

    @Override protected View initView(LayoutInflater inflater) {
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
                MvRecyclerActivity.this.onScrollStateChanged(recyclerView, newState);
            }

            @Override public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                MvRecyclerActivity.this.onScrolled(recyclerView, dx, dy);
            }
        });
        recyclerView.setLayoutManager(getLayoutManager());

        recyclerViewAdapter = new MvRecyclerAdapter<>(this, mList, inflater);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    @Override public RecyclerView.Adapter getAdapter() {
        return recyclerViewAdapter;
    }

    @Override public final void setData(List<D> list) {
        setData(list, true);
    }

    @Override public void setData(final List<D> list, final boolean showEmptyView) {
        if (QsHelper.isMainThread()) {
            if (list != mList) {
                mList.clear();
                if (list != null && !list.isEmpty()) mList.addAll(list);
            }
            updateAdapter(showEmptyView);
        } else {
            post(new Runnable() {
                @Override public void run() {
                    if (list != mList) {
                        mList.clear();
                        if (list != null && !list.isEmpty()) mList.addAll(list);
                    }
                    updateAdapter(showEmptyView);
                }
            });
        }
    }

    @Override public final void addData(final D d) {
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

    @Override public final void addData(final List<D> list) {
        addData(list, mList.size());
    }

    @Override public void addData(final List<D> list, int position) {
        if (list != null && !list.isEmpty() && position >= 0) {
            if (QsHelper.isMainThread()) {
                position = Math.min(position, mList.size());
                if (recyclerViewAdapter != null) recyclerViewAdapter.notifyItemRangeInserted(position, list.size());
                mList.addAll(position, list);
                updateAdapter(true);
            } else {
                final int finalPosition = Math.min(position, mList.size());
                post(new Runnable() {
                    @Override public void run() {
                        if (recyclerViewAdapter != null) recyclerViewAdapter.notifyItemRangeInserted(finalPosition, list.size());
                        mList.addAll(finalPosition, list);
                        updateAdapter(true);
                    }
                });
            }
        }
    }

    @Override public final void delete(final int position) {
        if (position >= 0 && position < mList.size()) {
            if (QsHelper.isMainThread()) {
                if (recyclerViewAdapter != null) recyclerViewAdapter.notifyItemRemoved(position);
                mList.remove(position);
                updateAdapter(true);
            } else {
                post(new Runnable() {
                    @Override public void run() {
                        if (recyclerViewAdapter != null) recyclerViewAdapter.notifyItemRemoved(position);
                        mList.remove(position);
                        updateAdapter(true);
                    }
                });
            }
        }
    }

    @Override public final void delete(final D d) {
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

    @Override public final void deleteAll() {
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

    @Override public final List<D> copyData() {
        ArrayList<D> list = new ArrayList<>();
        if (!mList.isEmpty()) list.addAll(mList);
        return list;
    }

    @Override public final D getData(int position) {
        if (position >= 0 && position < mList.size()) {
            return mList.get(position);
        }
        return null;
    }

    /**
     * 该方法必须在主线程中执行
     */
    @Override public void updateAdapter(boolean showEmptyView) {
        if (recyclerViewAdapter != null) {
            recyclerViewAdapter.notifyDataSetChanged();
            if (mList.isEmpty() && showEmptyView) {
                showEmptyView();
            } else {
                showContentView();
            }
        }
    }

    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
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

    @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        //for custom logic
    }

    @Override public void onScrollStateChanged(RecyclerView view, int newState) {
        //for custom logic
    }

    @Override public final MvRecycleAdapterItem<D> getRecycleAdapterItemInner(LayoutInflater mInflater, ViewGroup parent, int type) {
        MvRecycleAdapterItem<D> adapterItem = getRecycleAdapterItem(mInflater, parent, type);
        adapterItem.setViewLayer(this);
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

    @Override public RecyclerView.LayoutManager getLayoutManager() {
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