package com.qsmaxmin.qsbase.mvvm.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qsmaxmin.qsbase.common.widget.viewpager.MvViewPagerHelper;
import com.qsmaxmin.qsbase.common.widget.viewpager.PagerSlidingTabStrip;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/7/3 13:14
 * @Description
 */

public class MvTabFragmentStatePagerAdapter extends MvFragmentStatePagerAdapter implements PagerSlidingTabStrip.CustomTabProvider {

    public MvTabFragmentStatePagerAdapter(@NonNull FragmentManager fm, @NonNull MvViewPagerHelper helper) {
        super(fm, helper);
    }

    @Override public final int getCustomTabViewId(int position) {
        return 0;
    }

    @Override public View getCustomTabView(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent, int position) {
        if (getPagerHelper().getTabAdapter() != null) {
            return getPagerHelper().getTabAdapter().getTabItemView(inflater, parent, position);
        }
        return null;
    }

    @Override public void initTabsItem(@NonNull View view, int position) {
        if (getPagerHelper().getTabAdapter() != null && position >= 0 && position < getCount()) {
            getPagerHelper().getTabAdapter().init(view, position);
        }
    }
}