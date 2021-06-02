package com.qsmaxmin.qsbase.mvvm.adapter;

import android.view.View;
import android.widget.AdapterView;

import androidx.recyclerview.widget.RecyclerView;

/**
 * @CreateBy qsmaxmin
 * @Date 16/8/5
 * @Description RecyclerView holder
 */
public class MvRecycleViewHolder<D> extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
    private final MvRecycleAdapterItem<D>             mAdapterItem;
    private       int                                 mPosition;
    public        int                                 mTotalCount;
    private       AdapterView.OnItemClickListener     mClickListener;
    private       AdapterView.OnItemLongClickListener mLongClickListener;

    public MvRecycleViewHolder(MvRecycleAdapterItem<D> adapterItem) {
        super(adapterItem.getItemView());
        this.mAdapterItem = adapterItem;
        adapterItem.getItemView().setOnClickListener(this);
        adapterItem.getItemView().setOnLongClickListener(this);
    }

    public final void onBindData(D d, int position, int totalCount) {
        this.mPosition = position;
        this.mTotalCount = totalCount;
        this.mAdapterItem.onBindItemDataInner(d, position, totalCount);
    }

    public final void onScrollStateChanged(int scrollState) {
        this.mAdapterItem.onScrollStateChangedInner(scrollState);
    }

    @Override public final void onClick(View v) {
        if (mClickListener != null) mClickListener.onItemClick(null, v, mPosition, -1);
    }

    @Override public final boolean onLongClick(View v) {
        return mLongClickListener != null && mLongClickListener.onItemLongClick(null, v, mPosition, -1);
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.mClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener onItemLongClickListener) {
        this.mLongClickListener = onItemLongClickListener;
    }
}