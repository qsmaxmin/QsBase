package com.qsmaxmin.qsbase.mvp.adapter;

import com.qsmaxmin.qsbase.common.widget.viewpager.ViewPagerHelper;
import com.qsmaxmin.qsbase.mvp.model.QsModelPager;

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
public class QsFragmentPagerAdapter extends FragmentPagerAdapter implements QsIPagerAdapter {
    private final ViewPagerHelper pagerHelper;

    public QsFragmentPagerAdapter(FragmentManager fm, ViewPagerHelper pagerHelper) {
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

    @Override public QsModelPager[] getModelPagers() {
        return pagerHelper.getModelPagers();
    }

    @Override public QsModelPager getModelPager(int position) {
        return pagerHelper.getModelPager(position);
    }

    @Override public QsModelPager getCurrentPager() {
        return pagerHelper.getCurrentPager();
    }

    @Override public Fragment getCurrentFragment() {
        return pagerHelper.getCurrentFragment();
    }

    protected ViewPagerHelper getPagerHelper() {
        return pagerHelper;
    }
}
