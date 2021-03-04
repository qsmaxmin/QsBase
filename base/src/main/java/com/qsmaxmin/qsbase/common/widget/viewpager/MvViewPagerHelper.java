package com.qsmaxmin.qsbase.common.widget.viewpager;

import com.qsmaxmin.qsbase.mvvm.MvIViewPager;
import com.qsmaxmin.qsbase.mvvm.adapter.MvTabAdapter;
import com.qsmaxmin.qsbase.mvvm.fragment.MvIFragment;
import com.qsmaxmin.qsbase.mvvm.model.MvModelPager;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

/**
 * @CreateBy qsmaxmin
 * @Date 2018/4/16 9:51
 * @Description
 */

public class MvViewPagerHelper {
    private final MvIViewPager   viewPagerLayer;
    private final ViewPager      pager;
    private final MvModelPager[] viewPagerData;
    private       int            oldPosition = 0;

    public MvViewPagerHelper(MvIViewPager viewPagerLayer, ViewPager pager, MvModelPager[] modelPagers) {
        this.viewPagerLayer = viewPagerLayer;
        this.viewPagerData = modelPagers;
        this.pager = pager;
        pager.addOnPageChangeListener(new MyPageChangeListener());
    }

    public MvModelPager[] getModelPagers() {
        return viewPagerData;
    }

    public MvModelPager getModelPager(int position) {
        return viewPagerData[position];
    }

    public MvModelPager getCurrentPager() {
        return viewPagerData[pager.getCurrentItem()];
    }

    public Fragment getCurrentFragment() {
        return viewPagerData[pager.getCurrentItem()].fragment;
    }

    public MvTabAdapter getTabAdapter() {
        return viewPagerLayer.getTabAdapter();
    }

    public ViewPager getViewPager() {
        return pager;
    }

    public MvIViewPager getViewPagerLayer() {
        return viewPagerLayer;
    }

    private class MyPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (getTabAdapter() != null) {
                getTabAdapter().onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
            if (viewPagerLayer != null) {
                viewPagerLayer.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        }

        @Override public void onPageSelected(int position) {
            for (int i = 0; i < viewPagerData.length; i++) {
                MvModelPager qsModelPager = viewPagerData[i];
                if (qsModelPager.fragment instanceof MvIFragment) {
                    ((MvIFragment) qsModelPager.fragment).onFragmentSelectedInViewPager(position == i, position, viewPagerData.length);
                }
            }

            if (position < viewPagerData.length && viewPagerData[position].fragment.isAdded()) {
                if (viewPagerData[position].fragment instanceof MvIFragment) {
                    ((MvIFragment) viewPagerData[position].fragment).initDataWhenDelay(); // 调用延迟加载
                }
            }

            if (getTabAdapter() != null) {
                getTabAdapter().onPageSelected(position, oldPosition);
            }
            if (viewPagerLayer != null) {
                viewPagerLayer.onPageSelected(position, oldPosition);
            }
            oldPosition = position;
        }

        @Override public void onPageScrollStateChanged(int state) {
            if (getTabAdapter() != null) {
                getTabAdapter().onPageScrollStateChanged(state);
            }
            if (viewPagerLayer != null) {
                viewPagerLayer.onPageScrollStateChanged(state);
            }
        }
    }
}
