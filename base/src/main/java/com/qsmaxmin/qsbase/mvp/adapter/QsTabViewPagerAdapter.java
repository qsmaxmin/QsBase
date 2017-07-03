package com.qsmaxmin.qsbase.mvp.adapter;

import android.support.v4.app.FragmentManager;
import android.view.View;

import com.qsmaxmin.qsbase.common.widget.viewpager.PagerSlidingTabStrip;
import com.qsmaxmin.qsbase.common.widget.viewpager.QsViewPager;
import com.qsmaxmin.qsbase.mvp.QsIViewPagerABActivity;
import com.qsmaxmin.qsbase.mvp.QsIViewPagerActivity;
import com.qsmaxmin.qsbase.mvp.fragment.QsIViewPagerFragment;
import com.qsmaxmin.qsbase.mvp.presenter.QsPresenter;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/7/3 13:14
 * @Description
 */

public class QsTabViewPagerAdapter extends QsViewPagerAdapter implements PagerSlidingTabStrip.CustomTabProvider {

    public QsTabViewPagerAdapter(String tag, FragmentManager fragmentManager, PagerSlidingTabStrip tabs, QsViewPager pager, QsIViewPagerABActivity j2WIViewViewpagerABActivity) {
        super(tag, fragmentManager, tabs, pager, j2WIViewViewpagerABActivity);
    }

    public QsTabViewPagerAdapter(String tag, FragmentManager fragmentManager, PagerSlidingTabStrip tabs, QsViewPager pager, QsIViewPagerActivity j2WIViewViewpagerActivity) {
        super(tag, fragmentManager, tabs, pager, j2WIViewViewpagerActivity);
    }

    public QsTabViewPagerAdapter(String tag, FragmentManager fragmentManager, PagerSlidingTabStrip tabs, QsViewPager pager, QsIViewPagerFragment j2WIViewViewpagerFragment) {
        super(tag, fragmentManager, tabs, pager, j2WIViewViewpagerFragment);
    }

    @Override public int getCustomTabView() {
        if (viewPagerABActivity != null) {
            if (viewPagerABActivity.getTabItemLayout() == 0) throw new IllegalArgumentException("QsTabViewPagerAdapter 必须要有自定义布局！");
            return viewPagerABActivity.getTabItemLayout();
        }
        if (viewPagerActivity != null) {
            if (viewPagerActivity.getTabItemLayout() == 0) throw new IllegalArgumentException("QsTabViewPagerAdapter  必须要有自定义布局！");
            return viewPagerActivity.getTabItemLayout();

        }
        if (viewPagerFragment != null) {
            if (viewPagerFragment.getTabItemLayout() == 0) throw new IllegalArgumentException("QsTabViewPagerAdapter  必须要有自定义布局！");
            return viewPagerFragment.getTabItemLayout();
        }
        return 0;
    }

    @Override public void initTabsItem(View view, int position) {
        if (viewPagerABActivity != null) viewPagerABActivity.initTab(view, viewPagerData[position]);
        if (viewPagerActivity != null) viewPagerActivity.initTab(view, viewPagerData[position]);
        if (viewPagerFragment != null) viewPagerFragment.initTab(view, viewPagerData[position]);
    }
}