package com.qsmaxmin.qsbase.mvvm.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

import com.qsmaxmin.qsbase.mvvm.MvIListView;

import java.util.List;

/**
 * @CreateBy qsmaxmin
 * @Date 2020/12/9 11:57
 * @Description
 */
public class MvListAdapter<D> extends BaseAdapter {
    private final MvIListView<D> listLayer;
    private final List<D>        mList;

    public MvListAdapter(MvIListView<D> listLayer, List<D> list) {
        this.listLayer = listLayer;
        this.mList = list;
    }

    @Override public int getCount() {
        return mList.size();
    }

    @Override public D getItem(int position) {
        if (position > getCount() - 1) return null;
        return mList.get(position);
    }

    @Override public long getItemId(int position) {
        return position;
    }

    @Override public int getItemViewType(int position) {
        return listLayer.getItemViewType(position);
    }

    @Override public int getViewTypeCount() {
        return listLayer.getViewTypeCount();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override public View getView(int position, View convertView, ViewGroup parent) {
        listLayer.onAdapterGetView(position, getCount());
        MvListAdapterItem<D> item;
        if (convertView == null) {
            int count = getViewTypeCount();
            if (count > 1) {
                int type = getItemViewType(position);
                item = listLayer.getListAdapterItemInner(type);
            } else {
                item = listLayer.getListAdapterItemInner(0);
            }
            convertView = item.onCreateItemView(LayoutInflater.from(parent.getContext()), parent);
            item.init(convertView);
            convertView.setTag(item);
        } else {
            item = (MvListAdapterItem) convertView.getTag();
        }

        if (item != null) {
            item.bindDataInner(getItem(position), position, getCount());
        }
        return convertView;
    }

    public final void onScrollStateChanged(AbsListView view, int scrollState) {
        int childCount = view.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = view.getChildAt(i);
            Object tag = childView.getTag();
            if (tag instanceof MvListAdapterItem) {
                MvListAdapterItem item = (MvListAdapterItem) tag;
                item.onScrollStateChangedInner(scrollState);
            }
        }
    }
}
