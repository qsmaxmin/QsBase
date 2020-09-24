package com.qsmaxmin.qsbase.mvp;

import android.view.View;

import com.qsmaxmin.qsbase.common.widget.viewpager.PagerSlidingTabStrip;
import com.qsmaxmin.qsbase.mvp.adapter.QsIPagerAdapter;
import com.qsmaxmin.qsbase.mvp.model.QsModelPager;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/7/3 15:26
 * @Description
 */

public interface QsIViewPager {

    void onPageScrollStateChanged(int state);

    void onPageSelected(View currentTabItem, View oldTabItem, int position, int oldPosition);

    void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

    void initViewPager(QsModelPager[] modelPagers);

    void initViewPager(QsModelPager[] modelPagers, int offScreenPageLimit);

    QsModelPager[] getModelPagers();

    void setIndex(int index, boolean bool);

    PagerSlidingTabStrip getTab();

    ViewPager getViewPager();

    QsIPagerAdapter getViewPagerAdapter();

    int getTabItemLayout();

    void initTab(PagerSlidingTabStrip tabStrip);

    void initTabItem(View tabItem, QsModelPager modelPager);

    Fragment getCurrentFragment();
}
