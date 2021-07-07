package com.qsmaxmin.qsbase.common.widget.recyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @CreateBy qsmaxmin
 * @Date 2016/11/24 11:18
 * @Description
 */
public class HeaderFooterRecyclerView extends RecyclerView {
    private HeaderFooterRecyclerAdapter mAdapter;

    public HeaderFooterRecyclerView(Context context) {
        super(context);
        init();
    }

    public HeaderFooterRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HeaderFooterRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mAdapter = new HeaderFooterRecyclerAdapter();
    }

    /**
     * @see #removeFooterView()
     */
    public boolean removeHeaderView(View view) {
        return mAdapter.removeHeaderView();
    }

    public boolean removeHeaderView() {
        return mAdapter.removeHeaderView();
    }

    /**
     * @see #removeFooterView()
     */
    public boolean removeFooterView(View view) {
        return mAdapter.removeFooterView();
    }

    public boolean removeFooterView() {
        return mAdapter.removeFooterView();
    }

    public int getHeaderViewSize() {
        return mAdapter.getHeaderViewSize();
    }

    public int getFooterViewSize() {
        return mAdapter.getFooterViewSize();
    }

    public View getHeaderView() {
        return mAdapter.getHeaderView();
    }

    public View getFooterView() {
        return mAdapter.getFooterView();
    }

    public void addHeaderView(View view) {
        if (view != null) {
            if (view.getLayoutParams() == null) {
                view.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            mAdapter.setHeaderView(view);
        }
    }

    public void addFooterView(View view) {
        if (view != null) {
            if (view.getLayoutParams() == null) {
                view.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            mAdapter.setFooterView(view);
        }
    }

    @Override public void setAdapter(Adapter adapter) {
        mAdapter.bindAdapter(adapter);
        super.setAdapter(adapter == null ? null : mAdapter);
    }

    @Nullable @Override public Adapter getAdapter() {
        Adapter adapter = super.getAdapter();
        return adapter == null ? null : mAdapter.getInnerAdapter();
    }
}
