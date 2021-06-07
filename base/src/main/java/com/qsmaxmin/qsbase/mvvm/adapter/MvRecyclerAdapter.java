package com.qsmaxmin.qsbase.mvvm.adapter;

import android.view.ViewGroup;

import com.qsmaxmin.qsbase.common.utils.QsHelper;
import com.qsmaxmin.qsbase.mvvm.MvIRecyclerView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @CreateBy qsmaxmin
 * @Date 2020/4/9 16:39
 * @Description recyclerView adapter
 */
public class MvRecyclerAdapter<D> extends RecyclerView.Adapter<MvRecycleViewHolder<D>> {
    private final MvIRecyclerView<D> recyclerLayer;
    private final List<D>            mList;

    public MvRecyclerAdapter(MvIRecyclerView<D> recyclerLayer) {
        this.recyclerLayer = recyclerLayer;
        this.mList = new ArrayList<>();
    }

    @NonNull @Override public MvRecycleViewHolder<D> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MvRecycleAdapterItem<D> adapterItem = recyclerLayer.getRecycleAdapterItemInner(parent, viewType);
        return new MvRecycleViewHolder<>(adapterItem);
    }

    @Override public void onBindViewHolder(@NonNull MvRecycleViewHolder<D> holder, int position) {
        recyclerLayer.onAdapterGetView(position, getItemCount());
        holder.onBindViewHolder(mList.get(position), position, mList.size());
    }

    @Override public int getItemViewType(int position) {
        return recyclerLayer.getItemViewType(position);
    }

    @Override public int getItemCount() {
        return mList.size();
    }

    public void setData(final List<D> list, final boolean showEmptyView) {
        if (QsHelper.isMainThread()) {
            if (list != mList) {
                mList.clear();
                if (list != null && !list.isEmpty()) mList.addAll(list);
            }
            updateAdapter(showEmptyView);
        } else {
            recyclerLayer.post(new Runnable() {
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

    public void addData(final D d) {
        if (d != null) {
            if (QsHelper.isMainThread()) {
                mList.add(d);
                updateAdapter(true);
            } else {
                recyclerLayer.post(new Runnable() {
                    @Override public void run() {
                        mList.add(d);
                        updateAdapter(true);
                    }
                });
            }
        }
    }

    public void addData(final int position, final D d) {
        if (d != null) {
            if (QsHelper.isMainThread()) {
                mList.add(position, d);
                updateAdapter(true);
            } else {
                recyclerLayer.post(new Runnable() {
                    @Override public void run() {
                        mList.add(position, d);
                        updateAdapter(true);
                    }
                });
            }
        }
    }

    public void addData(final List<D> list, int position) {
        if (list != null && !list.isEmpty() && position >= 0) {
            if (QsHelper.isMainThread()) {
                position = Math.min(position, mList.size());
                notifyItemRangeInserted(position, list.size());
                mList.addAll(position, list);
                updateAdapter(true);
            } else {
                final int finalPosition = Math.min(position, mList.size());
                recyclerLayer.post(new Runnable() {
                    @Override public void run() {
                        notifyItemRangeInserted(finalPosition, list.size());
                        mList.addAll(finalPosition, list);
                        updateAdapter(true);
                    }
                });
            }
        }
    }

    public final void delete(final int position) {
        if (position >= 0 && position < mList.size()) {
            if (QsHelper.isMainThread()) {
                notifyItemRemoved(position);
                mList.remove(position);
                updateAdapter(true);
            } else {
                recyclerLayer.post(new Runnable() {
                    @Override public void run() {
                        notifyItemRemoved(position);
                        mList.remove(position);
                        updateAdapter(true);
                    }
                });
            }
        }
    }

    public void delete(final D d) {
        if (d != null) {
            if (QsHelper.isMainThread()) {
                boolean success;
                success = mList.remove(d);
                if (success) updateAdapter(true);
            } else {
                recyclerLayer.post(new Runnable() {
                    @Override public void run() {
                        boolean success;
                        success = mList.remove(d);
                        if (success) updateAdapter(true);
                    }
                });
            }
        }
    }

    public final void deleteAll() {
        if (!mList.isEmpty()) {
            if (QsHelper.isMainThread()) {
                mList.clear();
                updateAdapter(true);
            } else {
                recyclerLayer.post(new Runnable() {
                    @Override public void run() {
                        mList.clear();
                        updateAdapter(true);
                    }
                });
            }
        }
    }

    public List<D> getData() {
        return mList;
    }

    public final List<D> copyData() {
        ArrayList<D> list = new ArrayList<>();
        if (!mList.isEmpty()) list.addAll(mList);
        return list;
    }

    public final D getData(int position) {
        if (position >= 0 && position < mList.size()) {
            return mList.get(position);
        }
        return null;
    }

    public void updateAdapter(boolean showEmptyView) {
        notifyDataSetChanged();
        if (mList.isEmpty() && showEmptyView) {
            recyclerLayer.showEmptyView();
        } else {
            recyclerLayer.showContentView();
        }
    }
}
