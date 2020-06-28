package com.qsmaxmin.qsbase.mvp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.qsmaxmin.qsbase.mvp.fragment.QsIRecyclerView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @CreateBy administrator
 * @Date 2020/4/9 16:39
 * @Description recyclerView适配器
 */
public class QsRecyclerAdapter<D> extends RecyclerView.Adapter<MyRecycleViewHolder<D>> {
    private final LayoutInflater     mInflater;
    private final QsIRecyclerView<D> recyclerLayer;
    private final List<D>            mList;

    public QsRecyclerAdapter(QsIRecyclerView<D> recyclerLayer, List<D> list, LayoutInflater inflater) {
        this.recyclerLayer = recyclerLayer;
        this.mInflater = inflater;
        this.mList = list;
    }

    @NonNull @Override public MyRecycleViewHolder<D> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        QsRecycleAdapterItem<D> recycleAdapterItem = recyclerLayer.getRecycleAdapterItem(mInflater, parent, viewType);
        MyRecycleViewHolder<D> holder = new MyRecycleViewHolder<>(recycleAdapterItem);

        holder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                recyclerLayer.onItemClick(parent, view, position, id);
            }
        });
        holder.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return recyclerLayer.onItemLongClick(parent, view, position, id);
            }
        });
        return holder;
    }

    @Override public void onBindViewHolder(@NonNull MyRecycleViewHolder<D> holder, int position) {
        recyclerLayer.onAdapterGetView(position, getItemCount());
        holder.onBindData(mList.get(position), position, mList.size());
    }

    @Override public int getItemViewType(int position) {
        return recyclerLayer.getItemViewType(position);
    }

    @Override public int getItemCount() {
        return mList.size();
    }
}
