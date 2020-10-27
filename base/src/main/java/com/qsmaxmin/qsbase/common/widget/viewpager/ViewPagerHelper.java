package com.qsmaxmin.qsbase.common.widget.viewpager;

import com.qsmaxmin.qsbase.mvp.QsIViewPager;
import com.qsmaxmin.qsbase.mvp.adapter.QsTabAdapter;
import com.qsmaxmin.qsbase.mvp.fragment.QsIFragment;
import com.qsmaxmin.qsbase.mvp.model.QsModelPager;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

/**
 * @CreateBy qsmaxmin
 * @Date 2018/4/16 9:51
 * @Description
 */

public class ViewPagerHelper {
    private final QsIViewPager   viewPagerLayer;
    private final ViewPager      pager;
    private final QsModelPager[] viewPagerData;
    private       int            oldPosition = 0;

    public ViewPagerHelper(QsIViewPager viewPagerLayer, ViewPager pager, PagerSlidingTabStrip tabs, QsModelPager[] modelPagers) {
        this.viewPagerLayer = viewPagerLayer;
        this.viewPagerData = modelPagers;
        this.pager = pager;
        pager.addOnPageChangeListener(new MyPageChangeListener());
    }

    public QsModelPager[] getModelPagers() {
        return viewPagerData;
    }

    public QsModelPager getModelPager(int position) {
        return viewPagerData[position];
    }

    public QsModelPager getCurrentPager() {
        return viewPagerData[pager.getCurrentItem()];
    }

    public Fragment getCurrentFragment() {
        return viewPagerData[pager.getCurrentItem()].fragment;
    }

    public QsTabAdapter getTabAdapter() {
        return viewPagerLayer.getTabAdapter();
    }

    public ViewPager getViewPager() {
        return pager;
    }

    public QsIViewPager getViewPagerLayer() {
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
                QsModelPager qsModelPager = viewPagerData[i];
                if (qsModelPager.fragment instanceof QsIFragment) {
                    ((QsIFragment) qsModelPager.fragment).onFragmentSelectedInViewPager(position == i, position, viewPagerData.length);
                }
            }

            if (position < viewPagerData.length && viewPagerData[position].fragment.isAdded()) {
                if (viewPagerData[position].fragment instanceof QsIFragment) {
                    ((QsIFragment) viewPagerData[position].fragment).initDataWhenDelay(); // 调用延迟加载
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
