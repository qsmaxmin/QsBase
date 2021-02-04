package com.qsmaxmin.qsbase.mvp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qsmaxmin.qsbase.common.widget.headerview.HeaderScrollListener;
import com.qsmaxmin.qsbase.common.widget.headerview.HeaderScrollView;
import com.qsmaxmin.qsbase.common.widget.headerview.ScrollerProvider;

import androidx.annotation.NonNull;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/7/4 11:08
 * @Description header view
 */

public interface QsIHeaderView extends ScrollerProvider, HeaderScrollListener {
    View onCreateHeaderView(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent);

    HeaderScrollView getHeaderScrollView();
}
