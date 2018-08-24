package com.qsmaxmin.qsbase.mvp.fragment;

import android.widget.BaseAdapter;
import android.widget.ListView;

import com.qsmaxmin.qsbase.mvp.adapter.QsListAdapterItem;

import java.util.List;

/**
 * @CreateBy qsmaxmin
 * @Date 17/7/2  上午10:58
 * @Description
 */

public interface QsIListFragment<D> extends QsIFragment {

    QsListAdapterItem<D> getListAdapterItem(int type);

    int getHeaderLayout();

    int getFooterLayout();

    int getTopLayout();

    int getBottomLayout();

    ListView getListView();

    int getViewTypeCount();

    int getItemViewType(int position);

    void setData(List<D> list);

    void setData(List<D> list, boolean showEmptyView);

    void addData(List<D> list);

    void addData(D d);

    void delete(int position);

    void delete(D d);

    void deleteAll();

    List<D> getData();

    D getData(int position);

    void updateAdapter(boolean showEmptyView);

    BaseAdapter onCreateAdapter();

    BaseAdapter getAdapter();

    boolean canListScrollUp();

    boolean canListScrollDown();

    boolean showContentViewWhenDataLoadingComplete();

    void onAdapterGetView(int position, int totalCount);
}
