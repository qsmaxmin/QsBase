package com.qsmaxmin.qsbase.mvvm.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qsmaxmin.qsbase.common.widget.viewpager.MvViewPagerHelper;
import com.qsmaxmin.qsbase.common.widget.viewpager.PagerSlidingTabStrip;

import androidx.fragment.app.FragmentManager;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/7/3 13:14
 * @Description
 */

public class MvTabFragmentPagerAdapter extends MvFragmentPagerAdapter implements PagerSlidingTabStrip.CustomTabProvider {

    public MvTabFragmentPagerAdapter(FragmentManager fm, MvViewPagerHelper helper) {
        super(fm, helper);
    }

    @Override public final int getCustomTabViewId(int position) {
        return 0;
    }

    @Override public View getCustomTabView(LayoutInflater inflater, ViewGroup parent, int position) {
        if (getPagerHelper().getTabAdapter() != null) {
            return getPagerHelper().getTabAdapter().getTabItemView(inflater, parent, position);
        }
        return null;
    }

    @Override public void initTabsItem(View view, int position) {
        if (getPagerHelper().getTabAdapter() != null && position >= 0 && position < getCount()) {
            getPagerHelper().getTabAdapter().init(view, position);
        }
    }
}