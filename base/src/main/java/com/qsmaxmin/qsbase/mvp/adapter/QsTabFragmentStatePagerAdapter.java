package com.qsmaxmin.qsbase.mvp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qsmaxmin.qsbase.common.widget.viewpager.PagerSlidingTabStrip;
import com.qsmaxmin.qsbase.common.widget.viewpager.QsViewPagerHelper;

import androidx.fragment.app.FragmentManager;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/7/3 13:14
 * @Description
 */

public class QsTabFragmentStatePagerAdapter extends QsFragmentStatePagerAdapter implements PagerSlidingTabStrip.CustomTabProvider {

    public QsTabFragmentStatePagerAdapter(FragmentManager fm, QsViewPagerHelper helper) {
        super(fm, helper);
    }

    @Override public int getCustomTabViewId(int position) {
        if (getPagerHelper().getTabAdapter() != null) {
            return getPagerHelper().getTabAdapter().tabItemLayoutId(position);
        }
        return 0;
    }

    @Override public View getCustomTabView(LayoutInflater inflater, ViewGroup parent, int position) {
        return null;
    }

    @Override public void initTabsItem(View view, int position) {
        if (getPagerHelper().getTabAdapter() != null && position >= 0 && position < getCount()) {
            getPagerHelper().getTabAdapter().init(view, position);
        }
    }
}