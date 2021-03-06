package com.qsmaxmin.qsbase.mvp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qsmaxmin.qsbase.mvvm.adapter.MvRecycleAdapterItem;
import com.qsmaxmin.qsbase.plugin.bind.QsIBindView;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;


/**
 * @CreateBy qsmaxmin
 * @Date 16/8/5
 * @Description RecyclerView holder
 */
public abstract class QsRecycleAdapterItem<D> extends MvRecycleAdapterItem<D> implements QsIBindView {

    /**
     * @deprecated
     */
    public QsRecycleAdapterItem(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
    }

    public QsRecycleAdapterItem() {
    }

    /**
     * for QsTransform
     */
    @CallSuper @Override public void bindViewByQsPlugin(View view) {
    }

    @CallSuper @Override protected void init(View itemView) {
        bindViewByQsPlugin(getItemView());
    }

    @Override protected View onCreateItemView(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return itemViewLayoutId() == 0 ? null : inflater.inflate(itemViewLayoutId(), parent, false);
    }

    protected int itemViewLayoutId() {
        return 0;
    }
}