package com.qsmaxmin.qsbase.mvp.fragment;

import android.support.v4.app.Fragment;
import android.view.View;

import com.qsmaxmin.qsbase.common.widget.viewpager.PagerSlidingTabStrip;
import com.qsmaxmin.qsbase.common.widget.viewpager.QsViewPager;
import com.qsmaxmin.qsbase.mvp.adapter.QsViewPagerAdapter;
import com.qsmaxmin.qsbase.mvp.model.QsModelPager;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/7/3 13:15
 * @Description
 */

public interface QsIViewPagerFragment extends QsIFragment {

    void onPageScrollStateChanged(int state);

    void onPageSelected(View currentTabItem, View oldTabItem, int position, int oldPosition);

    void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

    void initViewPager(QsModelPager[] modelPagers, int offScreenPageLimit);

    QsModelPager[] getModelPagers();

    void replaceViewPageItem(QsModelPager... modelPagers);

    void setIndex(int index, boolean bool);

    PagerSlidingTabStrip getTabs();

    QsViewPager getViewPager();

    QsViewPagerAdapter getViewPagerAdapter();

    int getTabItemLayout();

    void initTab(View tabItem, QsModelPager modelPager);

    Fragment getCurrentFragment();
}
