package com.qsmaxmin.qsbase.mvp.fragment;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.qsmaxmin.qsbase.common.widget.recyclerview.HeaderFooterRecyclerView;
import com.qsmaxmin.qsbase.mvp.adapter.QsRecycleAdapterItem;

import java.util.List;

/**
 * @CreateBy qsmaxmin
 * @Date 17/7/2  上午10:58
 * @Description
 */

public interface QsIRecyclerView<D> extends AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    QsRecycleAdapterItem<D> getRecycleAdapterItem(LayoutInflater mInflater, ViewGroup parent, int type);

    int getHeaderLayout();

    int getFooterLayout();

    HeaderFooterRecyclerView getRecyclerView();

    int getItemViewType(int position);

    void setData(List<D> list);

    void setData(List<D> list, boolean showEmptyView);

    void addData(List<D> list);

    void addData(D d);

    void addData(List<D> data, int position);

    void delete(int position);

    void delete(D d);

    void deleteAll();

    List<D> getData();

    D getData(int position);

    void updateAdapter(boolean showEmptyView);

    RecyclerView.Adapter onCreateAdapter();

    RecyclerView.Adapter getAdapter();

    boolean canListScrollUp();

    boolean canListScrollDown();

    boolean showContentViewWhenDataLoadingComplete();

    void onAdapterGetView(int position, int totalCount);

    void onScrollStateChanged(RecyclerView recyclerView, int newState);

    void onScrolled(RecyclerView recyclerView, int dx, int dy);
}
