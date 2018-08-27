package com.qsmaxmin.qsbase.mvp.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.utils.QsHelper;
import com.qsmaxmin.qsbase.common.widget.viewpager.QsViewPager;
import com.qsmaxmin.qsbase.common.widget.viewpager.ViewPagerHelper;
import com.qsmaxmin.qsbase.common.widget.viewpager.headerpager.base.InnerScrollerContainer;
import com.qsmaxmin.qsbase.common.widget.viewpager.headerpager.base.OuterPagerAdapter;
import com.qsmaxmin.qsbase.common.widget.viewpager.headerpager.base.OuterScroller;
import com.qsmaxmin.qsbase.mvp.fragment.QsIFragment;
import com.qsmaxmin.qsbase.mvp.model.QsModelPager;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/7/3 13:14
 * @Description
 */

public class QsViewPagerAdapter extends PagerAdapter implements OuterPagerAdapter {
    private static final String TAG             = "QsViewPagerAdapter";
    private              int    replacePosition = -1;        // 替换标识

    private OuterScroller   mOuterScroller;
    private FragmentManager fragmentManager;
    private ViewGroup       container;
    private ViewPagerHelper pagerHelper;

    protected String initTag() {
        return QsHelper.getInstance().getApplication().isLogOpen() ? getClass().getSimpleName() : "QsViewPagerAdapter";
    }

    public QsViewPagerAdapter(FragmentManager fragmentManager, ViewPagerHelper pagerHelper) {
        this.fragmentManager = fragmentManager;
        this.pagerHelper = pagerHelper;
    }

    public ViewPagerHelper getPagerHelper() {
        return pagerHelper;
    }

    public QsModelPager[] getViewPagerData() {
        return pagerHelper.getViewPagerData();
    }

    public QsViewPager getViewPager() {
        return pagerHelper.getViewPager();
    }

    /**
     * 设置数据
     */
    public void setModelPagers(QsModelPager[] data) {
        pagerHelper.setViewPagerData(data);
    }

    /**
     * 替换
     */
    public void replaceViewPagerDatas(QsModelPager... modelPagers) {
        replacePosition = getViewPager().getCurrentItem();
        for (QsModelPager modelPager : modelPagers) {
            int position = modelPager.position;
            Fragment oldFragment = getViewPagerData()[position].fragment;
            if (oldFragment.getView() != null) container.removeView(oldFragment.getView());
            FragmentTransaction transaction = this.fragmentManager.beginTransaction();
            transaction.detach(oldFragment).commitAllowingStateLoss();
            getViewPagerData()[position] = modelPager;
        }
    }

    @Override public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public QsModelPager[] getAllData() {
        return getViewPagerData();
    }

    public QsModelPager getData(int position) {
        return getViewPagerData()[position];
    }

    @Override public CharSequence getPageTitle(int position) {
        return getViewPagerData()[position].title;
    }

    @Override public int getCount() {
        return getViewPagerData().length;
    }

    @Override public void destroyItem(ViewGroup container, int position, Object object) {
        this.container = container;
        if (position < getViewPagerData().length && getViewPagerData()[position].fragment != null) container.removeView(getViewPagerData()[position].fragment.getView());
    }

    @Override public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    /**
     * 生成
     */
    @Override public Object instantiateItem(ViewGroup container, int position) {
        this.container = container;
        Fragment fragment = getViewPagerData()[position].fragment;
        if (!fragment.isAdded()) {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out);
            ft.add(fragment, fragment.getClass().getSimpleName() + position);
            ft.commitAllowingStateLoss();
            fragmentManager.executePendingTransactions();
            fragment.setHasOptionsMenu(false);// 设置actionbar不执行
            if (replacePosition != -1) {
                if (getViewPagerData()[replacePosition].fragment instanceof QsIFragment) {
                    ((QsIFragment) getViewPagerData()[replacePosition].fragment).initDataWhenDelay();
                    ((QsIFragment) getViewPagerData()[replacePosition].fragment).onActionBar();
                }
                getViewPagerData()[replacePosition].fragment.onResume();
                replacePosition = -1;
            }
        }
        if (fragment.getView() == null) throw new NullPointerException("fragment has not view...");
        if (fragment.getView().getParent() == null) container.addView(fragment.getView());

        if (mOuterScroller != null && fragment instanceof InnerScrollerContainer) {
            L.i(TAG, "activate header viewpager... current fragment is:" + fragment.getClass().getSimpleName());
            ((InnerScrollerContainer) fragment).setMyOuterScroller(mOuterScroller, position);
        }
        return fragment.getView();
    }

    @Override public void notifyDataSetChanged() {
        pagerHelper.resetPageIndex();
        super.notifyDataSetChanged();
    }

    @Override public void setPageOuterScroller(OuterScroller outerScroller) {
        this.mOuterScroller = outerScroller;
    }
}
