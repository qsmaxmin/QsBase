package com.qsmaxmin.qsbase.mvp;

import android.widget.AbsListView;
import android.widget.AdapterView;

import com.qsmaxmin.qsbase.mvp.adapter.QsListAdapterItem;
import com.qsmaxmin.qsbase.mvvm.MvIListView;

import androidx.annotation.NonNull;

/**
 * @CreateBy qsmaxmin
 * @Date 17/7/2  上午10:58
 * @Description list视图超类
 */

public interface QsIListView<D> extends MvIListView<D>, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, AbsListView.OnScrollListener {

    @NonNull QsListAdapterItem<D> getListAdapterItemInner(int type);

    @NonNull QsListAdapterItem<D> getListAdapterItem(int type);

    int getHeaderLayout();

    int getFooterLayout();
}
