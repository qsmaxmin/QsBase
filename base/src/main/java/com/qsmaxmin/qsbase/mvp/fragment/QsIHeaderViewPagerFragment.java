package com.qsmaxmin.qsbase.mvp.fragment;

import android.view.ViewGroup;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/7/4 11:08
 * @Description
 */

public interface QsIHeaderViewPagerFragment {
    int getHeaderLayout();

    ViewGroup createTabView();
}
