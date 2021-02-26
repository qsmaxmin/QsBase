package com.qsmaxmin.qsbase.mvp;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.qsmaxmin.qsbase.mvp.adapter.QsRecycleAdapterItem;
import com.qsmaxmin.qsbase.mvvm.MvIRecyclerView;

import androidx.annotation.NonNull;

/**
 * @CreateBy qsmaxmin
 * @Date 17/7/2  上午10:58
 * @Description
 */

public interface QsIRecyclerView<D> extends MvIRecyclerView<D>, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    @NonNull QsRecycleAdapterItem<D> getRecycleAdapterItemInner(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent, int type);

    @NonNull QsRecycleAdapterItem<D> getRecycleAdapterItem(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent, int type);

    int getHeaderLayout();

    int getFooterLayout();
}
