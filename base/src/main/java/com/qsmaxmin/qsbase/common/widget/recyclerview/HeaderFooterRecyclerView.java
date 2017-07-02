package com.qsmaxmin.qsbase.common.widget.recyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * @CreateBy qsmaxmin
 * @Date 2016/11/24 11:18
 * @Description
 */
public class HeaderFooterRecyclerView extends RecyclerView {

    private List<View> mHeaderViews = new ArrayList<>();
    private List<View> mFooterViews = new ArrayList<>();
    private HeaderFooterRecyclerAdapter mAdapter;

    public HeaderFooterRecyclerView(Context context) {
        super(context);
    }

    public HeaderFooterRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HeaderFooterRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public boolean removeHeaderView(View view) {
        boolean success = false;
        if (mHeaderViews.contains(view)) {
            success = mHeaderViews.remove(view);
        }
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
        return success;
    }

    public boolean removeFooterView(View view) {
        boolean success = false;
        if (mFooterViews.contains(view)) {
            success = mFooterViews.remove(view);
        }
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
        return success;
    }

    public int getHeaderViewSize() {
        return mHeaderViews.size();
    }

    public int getFooterViewSize() {
        return mFooterViews.size();
    }

    public void addHeaderView(View view) {
        mHeaderViews.add(view);
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    public void addFooterView(View view) {
        mFooterViews.add(view);
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override public void setAdapter(Adapter adapter) {
        if (mAdapter != null) {
            mAdapter.unregisterDataSetObserver(mAdapterDataObserver);
        }
        mAdapter = new HeaderFooterRecyclerAdapter(adapter, mHeaderViews, mFooterViews);
        //重新注册一个新的观察者
        mAdapter.registerDataSetObserver(mAdapterDataObserver);
        super.setAdapter(mAdapter);
    }

    //数据观察者
    private AdapterDataObserver mAdapterDataObserver = new AdapterDataObserver() {
        @Override public void onChanged() {
            mAdapter.notifyDataSetChanged();
        }

        @Override public void onItemRangeChanged(int positionStart, int itemCount) {
            mAdapter.notifyItemRangeChanged(positionStart + mHeaderViews.size(), itemCount);
        }

        @Override public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            mAdapter.notifyItemRangeChanged(positionStart + mHeaderViews.size(), itemCount, payload);
        }

        @Override public void onItemRangeInserted(int positionStart, int itemCount) {
            mAdapter.notifyItemRangeInserted(positionStart + mHeaderViews.size(), itemCount);
        }

        @Override public void onItemRangeRemoved(int positionStart, int itemCount) {
            mAdapter.notifyItemRangeRemoved(positionStart + mHeaderViews.size(), itemCount);
        }
    };
}
