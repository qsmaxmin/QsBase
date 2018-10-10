package com.qsmaxmin.qsbase.mvp.fragment;

import android.view.KeyEvent;

import com.qsmaxmin.qsbase.mvp.QsIView;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/6/21 15:00
 * @Description
 */

public interface QsIFragment extends QsIView {

    void onActionBar();

    void setActivityTitle(Object value);

    void setActivityTitle(Object value, int code);

    boolean shouldInterceptTouchEvent();

    void smoothScrollToTop(boolean autoRefresh);

    boolean onKeyDown(int keyCode, KeyEvent event);

    void onBackPressed();

    void onFragmentSelectedInViewPager(boolean isSelected, int currentPosition, int totalCount);
}
