package com.qsmaxmin.qsbase.mvp.fragment;

import com.qsmaxmin.qsbase.mvp.QsIView;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/6/21 15:00
 * @Description
 */

public interface QsIFragment extends QsIView {

    boolean shouldInterceptTouchEvent();

    void onBackPressed();

    void onFragmentSelectedInViewPager(boolean isSelected, int currentPosition, int totalCount);
}
