package com.qsmaxmin.qsbase.mvp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.qsmaxmin.qsbase.mvp.fragment.QsIListView;

import java.util.List;

/**
 * @CreateBy administrator
 * @Date 2020/4/9 15:23
 * @Description listView适配器
 */
public class QsListAdapter<D> extends BaseAdapter {
    private final QsIListView<D> listLayer;
    private       List<D>        mList;

    public QsListAdapter(QsIListView<D> listLayer, List<D> list) {
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
        QsListAdapterItem<D> item = null;
        if (convertView == null) {
            int count = getViewTypeCount();
            if (count > 1) {
                int type = getItemViewType(position);
                item = listLayer.getListAdapterItem(type);
            } else {
                item = listLayer.getListAdapterItem(0);
            }
            convertView = LayoutInflater.from(parent.getContext()).inflate(item.getItemLayout(), parent, false);
            item.init(convertView);
            convertView.setTag(item);
        }
        if (item == null) {
            item = (QsListAdapterItem) convertView.getTag();
        }
        if (item != null) item.bindData(getItem(position), position, getCount());
        return convertView;
    }
}
