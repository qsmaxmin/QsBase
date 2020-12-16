package com.qsmaxmin.qsbase.mvvm;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qsmaxmin.qsbase.common.widget.headerview.HeaderScrollListener;
import com.qsmaxmin.qsbase.common.widget.headerview.HeaderScrollView;
import com.qsmaxmin.qsbase.common.widget.headerview.ScrollerProvider;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/7/4 11:08
 * @Description header view
 */

public interface MvIHeaderView extends ScrollerProvider, HeaderScrollListener {
    View onCreateHeaderView(LayoutInflater inflater, ViewGroup parent);

    HeaderScrollView getHeaderScrollView();
}
