package com.qsmaxmin.qsbase.common.widget.viewpager;

import android.support.v4.view.ViewPager;
import android.view.View;

import com.qsmaxmin.qsbase.mvp.QsIViewPagerABActivity;
import com.qsmaxmin.qsbase.mvp.QsIViewPagerActivity;
import com.qsmaxmin.qsbase.mvp.fragment.QsIFragment;
import com.qsmaxmin.qsbase.mvp.fragment.QsIViewPagerFragment;
import com.qsmaxmin.qsbase.mvp.model.QsModelPager;

/**
 * @CreateBy qsmaxmin
 * @Date 2018/4/16 9:51
 * @Description
 */

public class ViewPagerHelper {
    private View oldView          = null;
    private int  oldPosition      = 0;
    private int  currentPageIndex = -1;        // 当前page索引（切换之前）

    private QsIViewPagerFragment   viewPagerFragment;
    private QsIViewPagerABActivity viewPagerABActivity;
    private QsIViewPagerActivity   viewPagerActivity;
    private QsViewPager            pager;
    private PagerSlidingTabStrip   tabs;
    private QsModelPager[]         viewPagerData;

    public ViewPagerHelper(QsIViewPagerFragment fragment, QsViewPager viewPager, PagerSlidingTabStrip slidingTabStrip, final QsModelPager[] modelPagers) {
        this.viewPagerFragment = fragment;
        this.viewPagerData = modelPagers;
        this.tabs = slidingTabStrip;
        this.pager = viewPager;
        final MyPageChangeListener listener = new MyPageChangeListener();
        pager.addOnPageChangeListener(listener);
    }

    public ViewPagerHelper(QsIViewPagerABActivity activity, QsViewPager pager, PagerSlidingTabStrip tabs, QsModelPager[] modelPagers) {
        this.viewPagerABActivity = activity;
        this.viewPagerData = modelPagers;
        this.tabs = tabs;
        this.pager = pager;
        pager.addOnPageChangeListener(new MyPageChangeListener());
    }


    public ViewPagerHelper(QsIViewPagerActivity activity, QsViewPager pager, PagerSlidingTabStrip tabs, QsModelPager[] modelPagers) {
        this.viewPagerActivity = activity;
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

    public QsIViewPagerFragment getViewPagerFragment() {
        return viewPagerFragment;
    }

    public QsIViewPagerActivity getViewPagerActivity() {
        return viewPagerActivity;
    }

    public QsIViewPagerABActivity getViewPagerABActivity() {
        return viewPagerABActivity;
    }

    private class MyPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (viewPagerFragment != null) viewPagerFragment.onPageScrolled(position, positionOffset, positionOffsetPixels);
            if (viewPagerABActivity != null) viewPagerABActivity.onPageScrolled(position, positionOffset, positionOffsetPixels);
            if (viewPagerActivity != null) viewPagerActivity.onPageScrolled(position, positionOffset, positionOffsetPixels);
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
                    if (pager.getCurrentItem() == position) {
                        ((QsIFragment) viewPagerData[position].fragment).onActionBar();
                    }
                }
                viewPagerData[position].fragment.onResume();
            }
            currentPageIndex = position;
            if (viewPagerFragment != null) viewPagerFragment.onPageSelected(tabs == null ? null : tabs.tabsContainer.getChildAt(position), oldView, position, oldPosition);
            if (viewPagerABActivity != null) viewPagerABActivity.onPageSelected(tabs == null ? null : tabs.tabsContainer.getChildAt(position), oldView, position, oldPosition);
            if (viewPagerActivity != null) viewPagerActivity.onPageSelected(tabs == null ? null : tabs.tabsContainer.getChildAt(position), oldView, position, oldPosition);
            if (tabs != null) oldView = tabs.tabsContainer.getChildAt(position);
            oldPosition = position;
        }

        @Override public void onPageScrollStateChanged(int state) {
            if (viewPagerFragment != null) viewPagerFragment.onPageScrollStateChanged(state);
            if (viewPagerABActivity != null) viewPagerABActivity.onPageScrollStateChanged(state);
            if (viewPagerActivity != null) viewPagerActivity.onPageScrollStateChanged(state);
        }
    }
}
