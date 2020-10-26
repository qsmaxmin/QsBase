package com.qsmaxmin.qsbase.mvp.adapter;

import android.view.View;

import com.qsmaxmin.qsbase.common.widget.viewpager.PagerSlidingTabStrip;
import com.qsmaxmin.qsbase.common.widget.viewpager.ViewPagerHelper;

import androidx.fragment.app.FragmentManager;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/7/3 13:14
 * @Description
 */

public class QsTabFragmentPagerAdapter extends QsFragmentPagerAdapter implements PagerSlidingTabStrip.CustomTabProvider {

    public QsTabFragmentPagerAdapter(FragmentManager fm, ViewPagerHelper helper) {
        super(fm, helper);
    }

    @Override public int getCustomTabView(int position) {
        if (getPagerHelper().getTabAdapter() != null) {
            return getPagerHelper().getTabAdapter().tabItemLayoutId(position);
        }
        return 0;
    }

    @Override public void initTabsItem(View view, int position) {
        if (getPagerHelper().getTabAdapter() != null && position >= 0 && position < getCount()) {
            getPagerHelper().getTabAdapter().init(view, position);
        }
    }
}