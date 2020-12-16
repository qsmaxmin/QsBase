package com.qsmaxmin.qsbase.mvp.fragment;

import com.qsmaxmin.qsbase.mvp.QsIView;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/6/21 15:00
 * @Description
 */

public interface QsIFragment extends QsIView {

    boolean interceptTouchEvent();

    boolean interceptBackPressed();

    void onFragmentSelectedInViewPager(boolean isSelected, int currentPosition, int totalCount);

    void setActivityTitle(CharSequence title);

    void setActivityTitle(CharSequence title, int type);

    boolean isDelayData();

    void initDataWhenDelay();
}
