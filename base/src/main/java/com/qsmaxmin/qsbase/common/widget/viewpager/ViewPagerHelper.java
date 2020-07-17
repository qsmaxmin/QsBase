package com.qsmaxmin.qsbase.common.widget.viewpager;

import android.view.View;

import com.qsmaxmin.qsbase.mvp.QsIViewPager;
import com.qsmaxmin.qsbase.mvp.fragment.QsIFragment;
import com.qsmaxmin.qsbase.mvp.model.QsModelPager;

import androidx.viewpager.widget.ViewPager;

/**
 * @CreateBy qsmaxmin
 * @Date 2018/4/16 9:51
 * @Description
 */

public class ViewPagerHelper {
    private View                 oldView          = null;
    private int                  oldPosition      = 0;
    private int                  currentPageIndex = -1;        // 当前page索引（切换之前）
    private QsIViewPager         viewPagerLayer;
    private QsViewPager          pager;
    private PagerSlidingTabStrip tabs;
    private QsModelPager[]       viewPagerData;


    public ViewPagerHelper(QsIViewPager viewPagerLayer, QsViewPager pager, PagerSlidingTabStrip tabs, QsModelPager[] modelPagers) {
        this.viewPagerLayer = viewPagerLayer;
        this.viewPagerData = modelPagers;
        this.tabs = tabs;
        this.pager = pager;
        pager.addOnPageChangeListener(new MyPageChangeListener());
    }

    public QsModelPager[] getViewPagerData() {
        return viewPagerData;
    }

    public void setViewPagerData(QsModelPager[] data) {
        this.viewPagerData = data;
    }

    public void resetPageIndex() {
        currentPageIndex = -1;
    }

    public QsViewPager getViewPager() {
        return pager;
    }

    public QsIViewPager getViewPagerLayer() {
        return viewPagerLayer;
    }

    private class MyPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (viewPagerLayer != null) viewPagerLayer.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }

        @Override public void onPageSelected(int position) {
            if (currentPageIndex == -1) {
                currentPageIndex = position;
                if (tabs != null) oldView = tabs.tabsContainer.getChildAt(0);
            }
            if (currentPageIndex < viewPagerData.length) viewPagerData[currentPageIndex].fragment.onPause();
            if (position < viewPagerData.length && viewPagerData[position].fragment.isAdded()) {
                if (viewPagerData[position].fragment instanceof QsIFragment) {
                    ((QsIFragment) viewPagerData[position].fragment).initDataWhenDelay(); // 调用延迟加载
                }
                viewPagerData[position].fragment.onResume();
            }
            currentPageIndex = position;
            if (viewPagerLayer != null) viewPagerLayer.onPageSelected(tabs == null ? null : tabs.tabsContainer.getChildAt(position), oldView, position, oldPosition);
            if (tabs != null) oldView = tabs.tabsContainer.getChildAt(position);
            oldPosition = position;
        }

        @Override public void onPageScrollStateChanged(int state) {
            if (viewPagerLayer != null) viewPagerLayer.onPageScrollStateChanged(state);
        }
    }
}
