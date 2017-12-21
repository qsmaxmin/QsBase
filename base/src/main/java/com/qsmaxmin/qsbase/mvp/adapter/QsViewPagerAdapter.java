package com.qsmaxmin.qsbase.mvp.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.widget.viewpager.PagerSlidingTabStrip;
import com.qsmaxmin.qsbase.common.widget.viewpager.QsViewPager;
import com.qsmaxmin.qsbase.common.widget.viewpager.headerpager.base.InnerScrollerContainer;
import com.qsmaxmin.qsbase.common.widget.viewpager.headerpager.base.OuterPagerAdapter;
import com.qsmaxmin.qsbase.common.widget.viewpager.headerpager.base.OuterScroller;
import com.qsmaxmin.qsbase.mvp.QsIViewPagerABActivity;
import com.qsmaxmin.qsbase.mvp.QsIViewPagerActivity;
import com.qsmaxmin.qsbase.mvp.fragment.QsFragment;
import com.qsmaxmin.qsbase.mvp.fragment.QsIFragment;
import com.qsmaxmin.qsbase.mvp.fragment.QsIViewPagerFragment;
import com.qsmaxmin.qsbase.mvp.model.QsModelPager;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/7/3 13:14
 * @Description
 */

public class QsViewPagerAdapter extends PagerAdapter implements OuterPagerAdapter, ViewPager.OnPageChangeListener {
    private static final String TAG              = "QsViewPagerAdapter";
    private              View   oldView          = null;
    private              int    oldPosition      = -1;
    private              int    currentPageIndex = -1;        // 当前page索引（切换之前）
    private              int    replacePosition  = -1;        // 替换标识

    private   OuterScroller          mOuterScroller;
    private   FragmentManager        fragmentManager;
    protected String                 tag;
    protected PagerSlidingTabStrip   tabs;
    private   QsViewPager            pager;
    private   ViewGroup              container;
    protected QsModelPager[]         viewPagerData;
    protected QsIViewPagerFragment   viewPagerFragment;
    protected QsIViewPagerABActivity viewPagerABActivity;
    protected QsIViewPagerActivity   viewPagerActivity;

    protected String initTag() {
        return getClass().getSimpleName();
    }

    public QsViewPagerAdapter(String tag, FragmentManager fragmentManager, PagerSlidingTabStrip tabs, QsViewPager pager, QsIViewPagerFragment viewPagerFragment) {
        this.tag = tag;
        this.viewPagerFragment = viewPagerFragment;
        this.fragmentManager = fragmentManager;
        this.tabs = tabs;
        this.pager = pager;
        this.tabs.setOnPageChangeListener(this);
    }

    public QsViewPagerAdapter(String tag, FragmentManager fragmentManager, PagerSlidingTabStrip tabs, QsViewPager pager, QsIViewPagerABActivity viewPagerABActivity) {
        this.tag = tag;
        this.viewPagerABActivity = viewPagerABActivity;
        this.fragmentManager = fragmentManager;
        this.tabs = tabs;
        this.pager = pager;
        this.tabs.setOnPageChangeListener(this);
    }

    public QsViewPagerAdapter(String tag, FragmentManager fragmentManager, PagerSlidingTabStrip tabs, QsViewPager pager, QsIViewPagerActivity viewPagerActivity) {
        this.tag = tag;
        this.viewPagerActivity = viewPagerActivity;
        this.fragmentManager = fragmentManager;
        this.tabs = tabs;
        this.pager = pager;
        this.tabs.setOnPageChangeListener(this);
    }

    /**
     * 设置数据
     */
    public void setModelPagers(QsModelPager[] data) {
        this.viewPagerData = data;
    }

    /**
     * 替换
     */
    public void replaceViewPagerDatas(QsModelPager... modelPagers) {
        replacePosition = pager.getCurrentItem();
        for (QsModelPager modelPager : modelPagers) {
            int position = modelPager.position;
            container.removeView(viewPagerData[position].fragment.getView());
            FragmentTransaction fragmentTransaction = this.fragmentManager.beginTransaction();
            fragmentTransaction.detach(viewPagerData[position].fragment).commitAllowingStateLoss();
            viewPagerData[position] = modelPager;
        }
    }

    @Override public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public QsModelPager[] getAllData() {
        return viewPagerData;
    }

    public QsModelPager getData(int position) {
        return viewPagerData[position];
    }

    @Override public CharSequence getPageTitle(int position) {
        return viewPagerData[position].title;
    }

    @Override public int getCount() {
        return viewPagerData.length;
    }

    @Override public void destroyItem(ViewGroup container, int position, Object object) {
        this.container = container;
        if (position < viewPagerData.length && viewPagerData[position].fragment != null) container.removeView(viewPagerData[position].fragment.getView());
    }

    @Override public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    /**
     * 生成
     */
    @Override public Object instantiateItem(ViewGroup container, int position) {
        this.container = container;
        Fragment fragment = viewPagerData[position].fragment;
        if (!fragment.isAdded()) {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out, android.R.animator.fade_in, android.R.animator.fade_out);
            ft.add(fragment, fragment.getClass().getSimpleName() + position);
            ft.commitAllowingStateLoss();
            fragmentManager.executePendingTransactions();
            fragment.setHasOptionsMenu(false);// 设置actionbar不执行
            if (replacePosition != -1) {
                ((QsIFragment) viewPagerData[replacePosition].fragment).initDataWhenDelay();
                ((QsIFragment) viewPagerData[replacePosition].fragment).onActionBar();
                viewPagerData[replacePosition].fragment.onResume();
                replacePosition = -1;
            }
        }
        if (fragment.getView() == null) throw new NullPointerException("fragment has not view...");
        if (fragment.getView().getParent() == null) container.addView(fragment.getView());

        pager.setObjectForPosition(fragment.getView(), position);

        if (mOuterScroller != null && fragment instanceof InnerScrollerContainer) {
            L.i(TAG, "activate header viewpager... current fragment is:" + fragment.getClass().getSimpleName());
            ((InnerScrollerContainer) fragment).setMyOuterScroller(mOuterScroller, position);
        }
        return fragment.getView();
    }

    @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (viewPagerFragment != null) viewPagerFragment.onPageScrolled(position, positionOffset, positionOffsetPixels);
        if (viewPagerABActivity != null) viewPagerABActivity.onPageScrolled(position, positionOffset, positionOffsetPixels);
        if (viewPagerActivity != null) viewPagerActivity.onPageScrolled(position, positionOffset, positionOffsetPixels);
    }

    @Override public void onPageSelected(int position) {
        if (currentPageIndex == -1) {
            currentPageIndex = position;
            oldView = tabs.tabsContainer.getChildAt(0);
            oldPosition = position;
        }

        if (currentPageIndex < viewPagerData.length) viewPagerData[currentPageIndex].fragment.onPause();

        if (position < viewPagerData.length && viewPagerData[position].fragment.isAdded()) {
            ((QsFragment) viewPagerData[position].fragment).initDataWhenDelay(); // 调用延迟加载
            if (pager.getCurrentItem() == position) {
                ((QsFragment) viewPagerData[position].fragment).onActionBar();
            }
            viewPagerData[position].fragment.onResume();
        }
        currentPageIndex = position;
        if (viewPagerFragment != null) viewPagerFragment.onPageSelected(tabs.tabsContainer.getChildAt(position), oldView, position, oldPosition);
        if (viewPagerABActivity != null) viewPagerABActivity.onPageSelected(tabs.tabsContainer.getChildAt(position), oldView, position, oldPosition);
        if (viewPagerActivity != null) viewPagerActivity.onPageSelected(tabs.tabsContainer.getChildAt(position), oldView, position, oldPosition);

        oldView = tabs.tabsContainer.getChildAt(position);
        oldPosition = position;
    }

    @Override public void notifyDataSetChanged() {
        currentPageIndex = -1;
        super.notifyDataSetChanged();
    }

    @Override public void onPageScrollStateChanged(int state) {
        if (viewPagerFragment != null) viewPagerFragment.onPageScrollStateChanged(state);
        if (viewPagerABActivity != null) viewPagerABActivity.onPageScrollStateChanged(state);
        if (viewPagerActivity != null) viewPagerActivity.onPageScrollStateChanged(state);

    }

    @Override public void setPageOuterScroller(OuterScroller outerScroller) {
        this.mOuterScroller = outerScroller;
    }
}
