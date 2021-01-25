package com.qsmaxmin.qsbase.mvp;

import com.qsmaxmin.qsbase.common.widget.viewpager.PagerSlidingTabStrip;
import com.qsmaxmin.qsbase.mvp.adapter.QsIPagerAdapter;
import com.qsmaxmin.qsbase.mvp.adapter.QsTabAdapter;
import com.qsmaxmin.qsbase.mvp.adapter.QsTabAdapterItem;
import com.qsmaxmin.qsbase.mvp.model.QsModelPager;
import com.qsmaxmin.qsbase.mvvm.adapter.MvTabAdapterItem;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/7/3 15:26
 * @Description
 */

public interface QsIViewPager {

    void onPageScrollStateChanged(int state);

    void onPageSelected(int position, int oldPosition);

    void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

    void initViewPager(@Nullable QsModelPager[] modelPagers);

    void initViewPager(@Nullable QsModelPager[] modelPagers, int offScreenPageLimit);

    @Nullable QsModelPager[] createModelPagers();

    void setIndex(int index, boolean bool);

    PagerSlidingTabStrip getTab();

    ViewPager getViewPager();

    QsIPagerAdapter getViewPagerAdapter();

    QsTabAdapterItem createTabAdapterItemInner(int position);

    QsTabAdapterItem createTabAdapterItem(int position);

    QsTabAdapter getTabAdapter();

    void initTab(PagerSlidingTabStrip tabStrip);

    Fragment getCurrentFragment();
}
