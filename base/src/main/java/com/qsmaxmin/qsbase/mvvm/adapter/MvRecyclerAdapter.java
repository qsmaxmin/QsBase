package com.qsmaxmin.qsbase.mvvm.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.qsmaxmin.qsbase.mvvm.MvIRecyclerView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @CreateBy qsmaxmin
 * @Date 2020/4/9 16:39
 * @Description recyclerView adapter
 */
public class MvRecyclerAdapter<D> extends RecyclerView.Adapter<MvRecycleViewHolder<D>> {
    private final LayoutInflater     mInflater;
    private final MvIRecyclerView<D> recyclerLayer;
    private final List<D>            mList;

    public MvRecyclerAdapter(MvIRecyclerView<D> recyclerLayer, List<D> list, @NonNull LayoutInflater inflater) {
        this.recyclerLayer = recyclerLayer;
        this.mInflater = inflater;
        this.mList = list;
    }

    @NonNull @Override public MvRecycleViewHolder<D> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MvRecycleAdapterItem<D> adapterItem = recyclerLayer.getRecycleAdapterItemInner(mInflater, parent, viewType);
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
}
