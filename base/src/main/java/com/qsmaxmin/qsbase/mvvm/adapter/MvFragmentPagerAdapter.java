package com.qsmaxmin.qsbase.mvvm.adapter;

import com.qsmaxmin.qsbase.mvvm.MvIViewPager;
import com.qsmaxmin.qsbase.mvvm.fragment.MvIFragment;
import com.qsmaxmin.qsbase.mvvm.model.MvModelPager;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/7/3 13:14
 * @Description 适合少量页面，常驻内存
 */
public class MvFragmentPagerAdapter extends FragmentPagerAdapter implements MvIPagerAdapter, ViewPager.OnPageChangeListener {
    private final MvIViewPager       iViewPager;
    private final List<MvModelPager> mPagerList;
    private final List<Integer>      mIdList;
    private       int                pageId;
    private       int                oldPosition = 0;

    public MvFragmentPagerAdapter(@NonNull MvIViewPager iViewPager) {
        super(iViewPager.getViewPagerFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.iViewPager = iViewPager;
        this.mPagerList = new ArrayList<>();
        this.mIdList = new ArrayList<>();
        this.pageId = 0;
        iViewPager.getViewPager().addOnPageChangeListener(this);
    }

    public MvIViewPager getIViewPager() {
        return iViewPager;
    }

    @Override public void setModelPagers(List<MvModelPager> pagerList) {
        mPagerList.clear();
        mIdList.clear();
        if (pagerList != null && pagerList.size() > 0) {
            for (MvModelPager pager : pagerList) {
                mPagerList.add(pager);
                pageId++;
                mIdList.add(pageId);
            }
        }
        notifyDataSetChanged();
    }

    @Override public void addModelPager(MvModelPager pager) {
        pageId++;
        mPagerList.add(pager);
        mIdList.add(pageId);
        notifyDataSetChanged();
    }

    @Override public void addModelPager(int index, MvModelPager pager) {
        pageId++;
        mPagerList.add(index, pager);
        mIdList.add(index, pageId);
        notifyDataSetChanged();
    }

    @Override public void removeModelPager(MvModelPager pager) {
        int index = mPagerList.indexOf(pager);
        if (index >= 0) removeModelPager(index);
    }

    @Override public void removeModelPager(Fragment fragment) {
        if (mPagerList.isEmpty()) return;
        for (int i = 0, size = getCount(); i < size; i++) {
            MvModelPager pager = mPagerList.get(i);
            if (pager.fragment == fragment) {
                removeModelPager(i);
                break;
            }
        }
    }

    @Override public void removeModelPager(int index) {
        mPagerList.remove(index);
        mIdList.remove(index);
        notifyDataSetChanged();
    }

    @Override public List<MvModelPager> getModelPagers() {
        return mPagerList;
    }

    @Override public MvModelPager getModelPager(int position) {
        return getModelPagers().get(position);
    }

    @Override public PagerAdapter getAdapter() {
        return this;
    }

    //------------------------------以下是FragmentPagerAdapter固有方法--------------------------------------

    @Override public int getCount() {
        return getModelPagers().size();
    }

    @Override public CharSequence getPageTitle(int position) {
        return getModelPager(position).title;
    }

    @NonNull @Override public Fragment getItem(int position) {
        return getModelPager(position).fragment;
    }

    @Override public int getItemPosition(@NonNull Object object) {
        if (object instanceof Fragment && getCount() > 0) {
            for (int i = 0, size = getCount(); i < size; i++) {
                if (mPagerList.get(i).fragment == object) {
                    return i;
                }
            }
        }
        return super.getItemPosition(object);
    }

    @Override public long getItemId(int position) {
        return mIdList.get(position);
    }

    protected void onPageSelected(int position, int oldPosition) {
        //custom your logic
    }

    @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (iViewPager != null) {
            iViewPager.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override public final void onPageSelected(int position) {
        for (int i = 0, size = getCount(); i < size; i++) {
            MvModelPager qsModelPager = mPagerList.get(i);
            if (qsModelPager.fragment instanceof MvIFragment) {
                ((MvIFragment) qsModelPager.fragment).onFragmentSelectedInViewPager(position == i, position, size);
            }
        }

        MvModelPager current = getModelPager(position);
        if (current instanceof MvIFragment && current != null && current.fragment.isAdded()) {
            ((MvIFragment) current).initDataWhenDelay(); // 调用延迟加载
        }

        onPageSelected(position, oldPosition);
        if (iViewPager != null) {
            iViewPager.onPageSelected(position, oldPosition);
        }
        oldPosition = position;
    }

    @Override public void onPageScrollStateChanged(int state) {
        if (iViewPager != null) {
            iViewPager.onPageScrollStateChanged(state);
        }
    }

}
