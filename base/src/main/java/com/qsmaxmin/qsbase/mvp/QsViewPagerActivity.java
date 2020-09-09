package com.qsmaxmin.qsbase.mvp;

import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;

import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.utils.QsHelper;
import com.qsmaxmin.qsbase.common.widget.viewpager.PagerSlidingTabStrip;
import com.qsmaxmin.qsbase.common.widget.viewpager.QsViewPager;
import com.qsmaxmin.qsbase.common.widget.viewpager.ViewPagerHelper;
import com.qsmaxmin.qsbase.mvp.adapter.QsTabViewPagerAdapter;
import com.qsmaxmin.qsbase.mvp.adapter.QsViewPagerAdapter;
import com.qsmaxmin.qsbase.mvp.fragment.QsIFragment;
import com.qsmaxmin.qsbase.mvp.model.QsModelPager;
import com.qsmaxmin.qsbase.mvp.presenter.QsPresenter;

import androidx.annotation.CallSuper;
import androidx.fragment.app.Fragment;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/7/3 15:15
 * @Description
 */

public abstract class QsViewPagerActivity<P extends QsPresenter> extends QsActivity<P> implements QsIViewPager {
    private QsViewPagerAdapter   adapter;
    private QsViewPager          pager;
    private PagerSlidingTabStrip tabs;

    @Override public int layoutId() {
        return R.layout.qs_viewpager_bottom_tab;
    }

    @Override protected View initView(LayoutInflater inflater) {
        View view = super.initView(inflater);
        pager = view.findViewById(R.id.pager);
        tabs = view.findViewById(android.R.id.tabs);
        if (tabs != null) initTab(tabs);
        initViewPager(getModelPagers());
        return view;
    }

    @Override public void initViewPager(QsModelPager[] modelPagers) {
        initViewPager(modelPagers, getOffscreenPageLimit());
    }

    @Override public void initViewPager(QsModelPager[] modelPagers, int offScreenPageLimit) {
        if (modelPagers != null && modelPagers.length > 0) {
            ViewPagerHelper pagerHelper = new ViewPagerHelper(this, pager, tabs, modelPagers);
            adapter = createPagerAdapter(pagerHelper);
            pager.setAdapter(adapter);
            pager.setPageMargin(getPageMargin());
            pager.setOffscreenPageLimit(offScreenPageLimit);
            if (tabs != null) tabs.setViewPager(pager);
        }
    }

    @Override public void initTab(PagerSlidingTabStrip tabStrip) {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        tabStrip.setIndicatorWidth((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, dm));
        tabStrip.setIndicatorCorner(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1.5f, dm));
        tabStrip.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, dm));
    }

    protected QsViewPagerAdapter createPagerAdapter(ViewPagerHelper pagerHelper) {
        if (getTabItemLayout() != 0) {
            return new QsTabViewPagerAdapter(getSupportFragmentManager(), pagerHelper);
        } else {
            return new QsViewPagerAdapter(getSupportFragmentManager(), pagerHelper);
        }
    }

    @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override public void onPageScrollStateChanged(int state) {

    }

    @CallSuper @Override public void onPageSelected(View currentTabItem, View oldTabItem, int position, int oldPosition) {
        QsModelPager[] allData = getViewPagerAdapter().getAllData();
        if (allData == null || allData.length < 1) return;
        for (int i = 0; i < allData.length; i++) {
            QsModelPager qsModelPager = allData[i];
            if (qsModelPager.fragment instanceof QsIFragment) {
                ((QsIFragment) qsModelPager.fragment).onFragmentSelectedInViewPager(position == i, position, allData.length);
            }
        }
    }

    @Override public void initTabItem(View tabItem, QsModelPager modelPager) {
    }

    @Override public void replaceViewPageItem(QsModelPager... modelPagers) {
        if (adapter != null) {
            adapter.replaceViewPagerData(modelPagers);
            adapter.notifyDataSetChanged();
        } else {
            L.e(initTag(), "adapter is null.... override getModelPagers() return not null or call initViewPager() before !");
        }
    }

    @Override public void setIndex(int index, boolean bool) {
        if (adapter != null) {
            int childCount = adapter.getCount();
            if (0 <= index && index < childCount) {
                pager.setCurrentItem(index, bool);
            }
        } else {
            L.e(initTag(), "adapter is null.... override getModelPagers() return not null or call initViewPager() before !");
        }
    }

    @Override public int getTabItemLayout() {
        return 0;
    }

    @Override public PagerSlidingTabStrip getTab() {
        return tabs;
    }

    @Override public QsViewPager getViewPager() {
        return pager;
    }

    @Override public QsViewPagerAdapter getViewPagerAdapter() {
        return adapter;
    }

    @Override public Fragment getCurrentFragment() {
        return adapter.getAllData()[pager.getCurrentItem()].fragment;
    }

    protected int getOffscreenPageLimit() {
        return 3;
    }

    protected int getPageMargin() {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
    }
}
