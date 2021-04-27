package com.qsmaxmin.qsbase.mvvm;

import com.qsmaxmin.qsbase.common.widget.viewpager.PagerSlidingTabStrip;
import com.qsmaxmin.qsbase.mvvm.adapter.MvIPagerAdapter;
import com.qsmaxmin.qsbase.mvvm.adapter.MvTabAdapter;
import com.qsmaxmin.qsbase.mvvm.adapter.MvTabAdapterItem;
import com.qsmaxmin.qsbase.mvvm.model.MvModelPager;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/7/3 15:26
 * @Description
 */

public interface MvIViewPager extends MvIView {

    void onPageScrollStateChanged(int state);

    void onPageSelected(int position, int oldPosition);

    void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

    void initViewPager(@Nullable MvModelPager[] modelPagers);

    void initViewPager(@Nullable MvModelPager[] modelPagers, int offScreenPageLimit);

    void initViewPager(@Nullable List<MvModelPager> modelPagers);

    void initViewPager(@Nullable List<MvModelPager> modelPagers, int offScreenPageLimit);

    @Nullable MvModelPager[] createModelPagers();

    void setIndex(int index);

    void setIndex(int index, boolean bool);

    PagerSlidingTabStrip getTab();

    ViewPager getViewPager();

    MvIPagerAdapter getViewPagerAdapter();

    MvTabAdapterItem createTabAdapterItemInner(int position);

    MvTabAdapterItem createTabAdapterItem(int position);

    MvTabAdapter getTabAdapter();

    void initTab(@NonNull PagerSlidingTabStrip tabStrip);

    Fragment getCurrentFragment();

    FragmentManager getViewPagerFragmentManager();
}
