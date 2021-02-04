package com.qsmaxmin.qsbase.mvp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qsmaxmin.qsbase.common.widget.viewpager.PagerSlidingTabStrip;
import com.qsmaxmin.qsbase.common.widget.viewpager.QsViewPagerHelper;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/7/3 13:14
 * @Description
 */

public class QsTabFragmentPagerAdapter extends QsFragmentPagerAdapter implements PagerSlidingTabStrip.CustomTabProvider {

    public QsTabFragmentPagerAdapter(@NonNull FragmentManager fm, @NonNull QsViewPagerHelper helper) {
        super(fm, helper);
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