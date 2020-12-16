package com.qsmaxmin.qsbase.mvvm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.qsmaxmin.qsbase.common.widget.recyclerview.HeaderFooterRecyclerView;
import com.qsmaxmin.qsbase.common.widget.recyclerview.QsItemDecoration;
import com.qsmaxmin.qsbase.mvvm.adapter.MvRecycleAdapterItem;

import java.util.List;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @CreateBy qsmaxmin
 * @Date 17/7/2  上午10:58
 * @Description
 */

public interface MvIRecyclerView<D> extends AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    MvRecycleAdapterItem<D> getRecycleAdapterItemInner(LayoutInflater inflater, ViewGroup parent, int type);

    MvRecycleAdapterItem<D> getRecycleAdapterItem(LayoutInflater inflater, ViewGroup parent, int type);

    View onCreateListHeaderView(LayoutInflater inflater);

    View onCreateListFooterView(LayoutInflater inflater);

    View getHeaderView();

    View getFooterView();

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

    List<D> copyData();

    D getData(int position);

    void updateAdapter(boolean showEmptyView);

    RecyclerView.Adapter getAdapter();

    /**
     * top or left
     */
    boolean canRecyclerScrollStart();

    /**
     * bottom or right
     */
    boolean canRecyclerScrollEnd();

    void onAdapterGetView(int position, int totalCount);

    void onScrollStateChanged(RecyclerView recyclerView, int newState);

    void onScrolled(RecyclerView recyclerView, int dx, int dy);

    void onReceiveAdapterItemEvent(int eventType, D data, int position);

    RecyclerView.LayoutManager getLayoutManager();

    QsItemDecoration getItemDecoration();

    Context getContext();

    FragmentActivity getActivity();
}
