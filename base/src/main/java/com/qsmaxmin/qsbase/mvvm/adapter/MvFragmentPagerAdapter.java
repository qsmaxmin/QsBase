package com.qsmaxmin.qsbase.mvvm.adapter;

import com.qsmaxmin.qsbase.common.widget.viewpager.MvViewPagerHelper;
import com.qsmaxmin.qsbase.mvvm.model.MvModelPager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/7/3 13:14
 * @Description 适合少量页面，常驻内存
 */
public class MvFragmentPagerAdapter extends FragmentPagerAdapter implements MvIPagerAdapter {
    private final MvViewPagerHelper pagerHelper;

    public MvFragmentPagerAdapter(@NonNull FragmentManager fm, @NonNull MvViewPagerHelper pagerHelper) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.pagerHelper = pagerHelper;
    }

    @Override public CharSequence getPageTitle(int position) {
        return getModelPager(position).title;
    }

    @Override public int getCount() {
        return getModelPagers().length;
    }

    @NonNull @Override public Fragment getItem(int position) {
        return getModelPager(position).fragment;
    }

    @Override public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override public PagerAdapter getAdapter() {
        return this;
    }

    @Override public MvModelPager[] getModelPagers() {
        return pagerHelper.getModelPagers();
    }

    @Override public MvModelPager getModelPager(int position) {
        return pagerHelper.getModelPager(position);
    }

    @Override public MvModelPager getCurrentPager() {
        return pagerHelper.getCurrentPager();
    }

    @Override public Fragment getCurrentFragment() {
        return pagerHelper.getCurrentFragment();
    }

    protected MvViewPagerHelper getPagerHelper() {
        return pagerHelper;
    }
}
