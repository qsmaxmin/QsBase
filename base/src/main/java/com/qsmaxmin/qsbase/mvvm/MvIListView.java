package com.qsmaxmin.qsbase.mvvm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.qsmaxmin.qsbase.mvvm.adapter.MvListAdapterItem;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

/**
 * @CreateBy qsmaxmin
 * @Date 17/7/2  上午10:58
 * @Description list视图超类
 */

public interface MvIListView<D> extends MvIView, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, AbsListView.OnScrollListener {

    @NonNull MvListAdapterItem<D> getListAdapterItemInner(int type);

    @NonNull MvListAdapterItem<D> getListAdapterItem(int type);

    int getAdapterItemPreloadSize();

    int getHeaderLayout();

    int getFooterLayout();

    View onCreateListHeaderView(@NonNull LayoutInflater inflater);

    View onCreateListFooterView(@NonNull LayoutInflater inflater);

    View getHeaderView();

    View getFooterView();

    ListView getListView();

    int getViewTypeCount();

    int getItemViewType(int position);

    void setData(List<D> list);

    void setData(List<D> list, boolean showEmptyView);

    void addData(List<D> list);

    void addData(List<D> list, int position);

    void addData(D d);

    void addData(int position, D d);

    void delete(int position);

    void delete(D d);

    void deleteAll();

    @NonNull List<D> getData();

    @NonNull List<D> copyData();

    D getData(int position);

    void updateAdapter(boolean showEmptyView);

    BaseAdapter getAdapter();

    boolean canListScrollUp();

    boolean canListScrollDown();

    void onAdapterGetView(int position, int totalCount);

    Context getContext();

    FragmentActivity getActivity();

    void onReceiveAdapterItemEvent(int eventType, D data, int position);

    LayoutInflater getLayoutInflater();

    void setOnScrollListener(AbsListView.OnScrollListener listener);
}
