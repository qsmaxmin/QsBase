package com.qsmaxmin.qsbase.mvvm.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qsmaxmin.qsbase.common.widget.viewpager.PagerSlidingTabStrip;
import com.qsmaxmin.qsbase.mvvm.MvIViewPager;

import androidx.annotation.NonNull;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/7/3 13:14
 * @Description
 */

public class MvTabFragmentPagerAdapter extends MvFragmentPagerAdapter implements PagerSlidingTabStrip.CustomTabProvider {

    public MvTabFragmentPagerAdapter(MvIViewPager iViewPager) {
        super(iViewPager);
    }

    @Override public View getCustomTabView(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent, int position) {
        if (getIViewPager().getTabAdapter() != null) {
            return getIViewPager().getTabAdapter().getTabItemView(inflater, parent, position);
        }
        return null;
    }

    @Override public void initTabsItem(@NonNull View view, int position) {
        if (getIViewPager().getTabAdapter() != null && position >= 0 && position < getCount()) {
            getIViewPager().getTabAdapter().init(view, position);
        }
    }
}