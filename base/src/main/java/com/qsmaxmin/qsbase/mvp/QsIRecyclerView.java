package com.qsmaxmin.qsbase.mvp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.qsmaxmin.qsbase.common.widget.recyclerview.HeaderFooterRecyclerView;
import com.qsmaxmin.qsbase.common.widget.recyclerview.QsItemDecoration;
import com.qsmaxmin.qsbase.mvp.adapter.QsRecycleAdapterItem;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @CreateBy qsmaxmin
 * @Date 17/7/2  上午10:58
 * @Description
 */

public interface QsIRecyclerView<D> extends AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    @NonNull QsRecycleAdapterItem<D> getRecycleAdapterItemInner(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent, int type);

    @NonNull QsRecycleAdapterItem<D> getRecycleAdapterItem(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent, int type);

    int getHeaderLayout();

    int getFooterLayout();

    View onCreateListHeaderView(@NonNull LayoutInflater inflater);

    View onCreateListFooterView(@NonNull LayoutInflater inflater);

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

    @NonNull List<D> getData();

    @NonNull List<D> copyData();

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

    void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState);

    void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy);

    void onReceiveAdapterItemEvent(int eventType, D data, int position);

    @NonNull RecyclerView.LayoutManager getLayoutManager();

    QsItemDecoration getItemDecoration();

    Context getContext();

    FragmentActivity getActivity();
}
