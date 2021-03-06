package com.qsmaxmin.qsbase.mvvm.adapter;

import com.qsmaxmin.qsbase.mvvm.model.MvModelPager;

import java.util.List;

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

    void setModelPagers(List<MvModelPager> list);

    void updateModelPagers(List<MvModelPager> list);

    List<MvModelPager> getModelPagers();

    MvModelPager getModelPager(int position);

    void addModelPager(MvModelPager pager);

    void addModelPager(int index, MvModelPager pager);

    void removeModelPager(MvModelPager pager);

    void removeModelPager(Fragment fragment);

    void removeModelPager(int index);

    void replaceModelPager(MvModelPager oldPager, MvModelPager newPager);

    void replaceModelPager(int index, MvModelPager pager);
}
