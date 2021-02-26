package com.qsmaxmin.qsbase.mvp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.qsmaxmin.qsbase.mvp.adapter.QsListAdapterItem;
import com.qsmaxmin.qsbase.mvvm.MvIListView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

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
