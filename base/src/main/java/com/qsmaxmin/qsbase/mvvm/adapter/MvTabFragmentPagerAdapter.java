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
        if (getTabAdapter() != null) {
            return getTabAdapter().getTabItemView(inflater, parent, position);
        }
        return null;
    }

    @Override public void initTabsItem(@NonNull View view, int position) {
        if (getTabAdapter() != null && position >= 0 && position < getCount()) {
            getTabAdapter().init(view, position);
        }
    }

    @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        super.onPageScrolled(position, positionOffset, positionOffsetPixels);
        if (getTabAdapter() != null) {
            getTabAdapter().onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override protected void onPageSelected(int position, int oldPosition) {
        super.onPageSelected(position, oldPosition);
        if (getTabAdapter() != null) {
            getTabAdapter().onPageSelected(position, oldPosition);
        }
    }

    @Override public void onPageScrollStateChanged(int state) {
        super.onPageScrollStateChanged(state);
        if (getTabAdapter() != null) {
            getTabAdapter().onPageScrollStateChanged(state);
        }
    }

    private MvTabAdapter getTabAdapter() {
        return getIViewPager().getTabAdapter();
    }
}