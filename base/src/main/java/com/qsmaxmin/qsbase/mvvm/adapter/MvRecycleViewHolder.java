package com.qsmaxmin.qsbase.mvvm.adapter;

import androidx.recyclerview.widget.RecyclerView;

/**
 * @CreateBy qsmaxmin
 * @Date 16/8/5
 * @Description RecyclerView holder
 */
public class MvRecycleViewHolder<D> extends RecyclerView.ViewHolder {
    private final MvRecycleAdapterItem<D> mAdapterItem;

    public MvRecycleViewHolder(MvRecycleAdapterItem<D> adapterItem) {
        super(adapterItem.getItemView());
        this.mAdapterItem = adapterItem;
    }

    public final void onBindViewHolder(D d, int position, int totalCount) {
        this.mAdapterItem.onBindItemDataInner(d, position, totalCount);
    }

    public final void onScrollStateChanged(int scrollState) {
        this.mAdapterItem.onScrollStateChangedInner(scrollState);
    }
}