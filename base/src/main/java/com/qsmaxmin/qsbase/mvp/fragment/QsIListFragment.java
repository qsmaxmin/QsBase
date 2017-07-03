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

    int getHeaderLayout();

    int getFooterLayout();

    ListView getListView();

    int getViewTypeCount();

    int getItemViewType(int position);

    QsListAdapterItem<D> getListAdapterItem(int type);

    void setData(List<D> list);

    void addData(List<D> list);

    void delete(int position);

    void deleteAll();

    List<D> getData();

    void updateAdapter();

    BaseAdapter onCreateAdapter();

    BaseAdapter getAdapter();
}
