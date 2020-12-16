package com.qsmaxmin.qsbase.mvvm.fragment;

import com.qsmaxmin.qsbase.mvvm.MvIView;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/6/21 15:00
 * @Description
 */

public interface MvIFragment extends MvIView {

    boolean interceptTouchEvent();

    boolean interceptBackPressed();

    void onFragmentSelectedInViewPager(boolean isSelected, int currentPosition, int totalCount);

    void setActivityTitle(CharSequence title);

    void setActivityTitle(CharSequence title, int type);

    boolean isDelayData();

    void initDataWhenDelay();
}
