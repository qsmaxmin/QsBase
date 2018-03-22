package com.qsmaxmin.qsbase.mvp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;

/**
 * @CreateBy qsmaxmin
 * @Date 16/8/5
 * @Description RecyclerView holder
 */
public class MyRecycleViewHolder<T> extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
    private final QsRecycleAdapterItem<T>             mAdapterItem;
    private       int                                 mPosition;
    public        int                                 mTotalCount;
    private       AdapterView.OnItemClickListener     mClickListener;
    private       AdapterView.OnItemLongClickListener mLongClickListener;

    public MyRecycleViewHolder(QsRecycleAdapterItem<T> adapterItem) {
        super(adapterItem.getItemView());
        this.mAdapterItem = adapterItem;
        adapterItem.getItemView().setOnClickListener(this);
        adapterItem.getItemView().setOnLongClickListener(this);
    }

    public void onBindData(T t, int position, int totalCount) {
        this.mPosition = position;
        this.mTotalCount = totalCount;
        mAdapterItem.onBindItemData(t, position, totalCount);
    }

    @Override public void onClick(View v) {
        if (mClickListener != null) mClickListener.onItemClick(null, v, mPosition, -1);
    }

    @Override public boolean onLongClick(View v) {
        return mClickListener != null && mLongClickListener.onItemLongClick(null, v, mPosition, -1);
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.mClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener onItemLongClickListener) {
        this.mLongClickListener = onItemLongClickListener;
    }
}