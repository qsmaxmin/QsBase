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

public class QsTabFragmentStatePagerAdapter extends QsFragmentStatePagerAdapter implements PagerSlidingTabStrip.CustomTabProvider {

    public QsTabFragmentStatePagerAdapter(FragmentManager fm, ViewPagerHelper helper) {
        super(fm, helper);
    }

    @Override public int getCustomTabView() {
        if (getPagerHelper().getViewPagerLayer() != null) {
            if (getPagerHelper().getViewPagerLayer().getTabItemLayout() == 0) {
                throw new IllegalArgumentException("QsTabViewPagerAdapter getTabItemLayout() return 0");
            }
            return getPagerHelper().getViewPagerLayer().getTabItemLayout();
        }
        return 0;
    }

    @Override public void initTabsItem(View view, int position) {
        if (getPagerHelper().getViewPagerLayer() != null) {
            getPagerHelper().getViewPagerLayer().initTabItem(view, getPagerHelper().getModelPagers()[position]);
        }
    }
}