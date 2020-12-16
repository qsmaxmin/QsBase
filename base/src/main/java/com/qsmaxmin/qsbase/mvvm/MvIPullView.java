package com.qsmaxmin.qsbase.mvvm;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @CreateBy qsmaxmin
 * @Date 17/7/2  下午12:54
 * @Description
 */

public interface MvIPullView extends MvIPullToRefreshView {
    View onCreateChildView(LayoutInflater inflater, ViewGroup parent);
}
