package com.qsmaxmin.qsbase.common.widget.listview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qsmaxmin.qsbase.R;

/**
 * ListView/GridView/RecyclerView 分页加载时使用到的FooterView
 */
public class LoadingFooter extends BaseLoadingFooter {

    public LoadingFooter(Context context) {
        super(context);
    }

    public LoadingFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LoadingFooter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override protected View onCreateStateView(LoadingState state, LayoutInflater inflater, ViewGroup parent) {
        if (state == LoadingState.Normal) {
            return inflater.inflate(R.layout.qs_layout_footer_init, parent, false);
        } else if (state == LoadingState.Loading) {
            return inflater.inflate(R.layout.qs_layout_footer_loading, parent, false);
        } else if (state == LoadingState.TheEnd) {
            return inflater.inflate(R.layout.qs_layout_footer_end, parent, false);
        } else if (state == LoadingState.NetWorkError) {
            return inflater.inflate(R.layout.qs_layout_footer_error, parent, false);
        }
        return null;
    }
}