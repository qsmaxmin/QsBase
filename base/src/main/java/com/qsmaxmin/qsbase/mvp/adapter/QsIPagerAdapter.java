package com.qsmaxmin.qsbase.mvp.adapter;

import com.qsmaxmin.qsbase.mvp.model.QsModelPager;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;

/**
 * @CreateBy qsmaxmin
 * @Date 2020/9/24 14:45
 * @Description
 */
public interface QsIPagerAdapter {

    int getCount();

    PagerAdapter getAdapter();

    QsModelPager[] getModelPagers();

    QsModelPager getModelPager(int position);

    QsModelPager getCurrentPager();

    Fragment getCurrentFragment();
}
