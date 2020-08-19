package com.qsmaxmin.qsbase.mvp.adapter;

import android.view.View;

import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.widget.viewpager.PagerSlidingTabStrip;
import com.qsmaxmin.qsbase.common.widget.viewpager.ViewPagerHelper;

import androidx.fragment.app.FragmentManager;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/7/3 13:14
 * @Description
 */

public class QsTabViewPagerAdapter extends QsViewPagerAdapter implements PagerSlidingTabStrip.CustomTabProvider {

    protected String initTag() {
        return L.isEnable() ? getClass().getSimpleName() : "QsTabViewPagerAdapter";
    }

    public QsTabViewPagerAdapter(FragmentManager fragmentManager, ViewPagerHelper helper) {
        super(fragmentManager, helper);
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
            getPagerHelper().getViewPagerLayer().initTabItem(view, getPagerHelper().getViewPagerData()[position]);
        }
    }
}