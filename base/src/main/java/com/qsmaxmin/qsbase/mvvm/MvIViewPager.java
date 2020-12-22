package com.qsmaxmin.qsbase.mvvm;

import com.qsmaxmin.qsbase.common.widget.viewpager.PagerSlidingTabStrip;
import com.qsmaxmin.qsbase.mvvm.adapter.MvIPagerAdapter;
import com.qsmaxmin.qsbase.mvvm.adapter.MvTabAdapter;
import com.qsmaxmin.qsbase.mvvm.adapter.MvTabAdapterItem;
import com.qsmaxmin.qsbase.mvvm.model.MvModelPager;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/7/3 15:26
 * @Description
 */

public interface MvIViewPager {

    void onPageScrollStateChanged(int state);

    void onPageSelected(int position, int oldPosition);

    void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

    void initViewPager(MvModelPager[] modelPagers);

    void initViewPager(MvModelPager[] modelPagers, int offScreenPageLimit);

    MvModelPager[] createModelPagers();

    void setIndex(int index, boolean bool);

    PagerSlidingTabStrip getTab();

    ViewPager getViewPager();

    MvIPagerAdapter getViewPagerAdapter();

    MvTabAdapterItem createTabAdapterItemInner(int position);

    MvTabAdapterItem createTabAdapterItem(int position);

    MvTabAdapter getTabAdapter();

    void initTab(PagerSlidingTabStrip tabStrip);

    Fragment getCurrentFragment();
}
