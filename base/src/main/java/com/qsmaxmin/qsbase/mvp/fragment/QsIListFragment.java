package com.qsmaxmin.qsbase.mvp.fragment;

import android.widget.BaseAdapter;
import android.widget.ListView;

import com.qsmaxmin.qsbase.mvp.adapter.QsAdapterItem;

import java.util.List;

/**
 * @CreateBy qsmaxmin
 * @Date 17/7/2  上午10:58
 * @Description
 */

interface QsIListFragment extends QsIFragment {

    int getHeaderLayout();

    int getFooterLayout();

    ListView getListView();

    int getViewTypeCount();

    int getItemViewType(int position);

    QsAdapterItem getQsAdapterItem(int type);

    void setData(List list);

    void setData(List list, boolean bool);

    void resetAdapter();

    void updateAdapter();

    void updateAdapter(boolean shouldShowEmptyView);

    BaseAdapter onCreateAdapter();

    BaseAdapter getAdapter();
}
