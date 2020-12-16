package com.qsmaxmin.qsbase.mvvm.adapter;

import com.qsmaxmin.qsbase.mvvm.model.MvModelPager;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;

/**
 * @CreateBy qsmaxmin
 * @Date 2020/9/24 14:45
 * @Description
 */
public interface MvIPagerAdapter {

    int getCount();

    PagerAdapter getAdapter();

    MvModelPager[] getModelPagers();

    MvModelPager getModelPager(int position);

    MvModelPager getCurrentPager();

    Fragment getCurrentFragment();
}
