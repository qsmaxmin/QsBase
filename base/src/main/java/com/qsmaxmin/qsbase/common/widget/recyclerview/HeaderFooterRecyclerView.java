package com.qsmaxmin.qsbase.common.widget.recyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @CreateBy qsmaxmin
 * @Date 2016/11/24 11:18
 * @Description
 */
public class HeaderFooterRecyclerView extends RecyclerView {
    private List<View>                  mHeaderViews = new ArrayList<>();
    private List<View>                  mFooterViews = new ArrayList<>();
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
            if (success && mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        }
        return success;
    }

    public boolean removeFooterView(View view) {
        boolean success = false;
        if (mFooterViews.contains(view)) {
            success = mFooterViews.remove(view);
            if (success && mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
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
        if (!mHeaderViews.contains(view)) {
            mHeaderViews.add(view);
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    public void addFooterView(View view) {
        if (!mFooterViews.contains(view)) {
            mFooterViews.add(view);
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override public void setAdapter(Adapter adapter) {
        if (mAdapter != null) {
            mAdapter.release();
        }
        mAdapter = new HeaderFooterRecyclerAdapter(adapter, mHeaderViews, mFooterViews);
        super.setAdapter(mAdapter);
    }

}
