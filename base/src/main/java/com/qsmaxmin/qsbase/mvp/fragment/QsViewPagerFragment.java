package com.qsmaxmin.qsbase.mvp.fragment;

import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;

import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.widget.viewpager.PagerSlidingTabStrip;
import com.qsmaxmin.qsbase.common.widget.viewpager.ViewPagerHelper;
import com.qsmaxmin.qsbase.mvp.QsIView;
import com.qsmaxmin.qsbase.mvp.QsIViewPager;
import com.qsmaxmin.qsbase.mvp.adapter.QsFragmentPagerAdapter;
import com.qsmaxmin.qsbase.mvp.adapter.QsIPagerAdapter;
import com.qsmaxmin.qsbase.mvp.adapter.QsTabAdapter;
import com.qsmaxmin.qsbase.mvp.adapter.QsTabFragmentPagerAdapter;
import com.qsmaxmin.qsbase.mvp.model.QsModelPager;
import com.qsmaxmin.qsbase.mvp.presenter.QsPresenter;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/7/3 13:14
 * @Description
 */

public abstract class QsViewPagerFragment<P extends QsPresenter> extends QsFragment<P> implements QsIViewPager {
    private ViewPager            pager;
    private PagerSlidingTabStrip tabs;
    private QsIPagerAdapter      adapter;

    @Override public int layoutId() {
        return R.layout.qs_viewpager_top_tab;
    }

    @Override protected View initView(LayoutInflater inflater) {
        View view = super.initView(inflater);
        initCustomView(view);
        return view;
    }

    private void initCustomView(View view) {
        pager = view.findViewById(R.id.pager);
        tabs = view.findViewById(android.R.id.tabs);
        if (tabs != null) initTab(tabs);
        initViewPager(getModelPagers());
    }

    @Override public void initViewPager(QsModelPager[] modelPagers) {
        initViewPager(getModelPagers(), getOffscreenPageLimit());
    }

    @Override public void initViewPager(QsModelPager[] modelPagers, int offScreenPageLimit) {
        if (modelPagers != null && modelPagers.length > 0) {
            QsTabAdapter tabAdapter = createTabAdapter();
            ViewPagerHelper pagerHelper = new ViewPagerHelper(this, pager, tabs, modelPagers, tabAdapter);

            adapter = createPagerAdapter(pagerHelper, tabAdapter != null);
            pager.setAdapter(adapter.getAdapter());
            pager.setPageMargin(getPageMargin());
            pager.setOffscreenPageLimit(offScreenPageLimit);
            if (tabs != null) tabs.setViewPager(pager);
        }
    }

    protected QsIPagerAdapter createPagerAdapter(ViewPagerHelper pagerHelper, boolean customTabView) {
        if (customTabView) {
            return new QsTabFragmentPagerAdapter(getChildFragmentManager(), pagerHelper);
        } else {
            return new QsFragmentPagerAdapter(getChildFragmentManager(), pagerHelper);
        }
    }

    @Override public void initTab(PagerSlidingTabStrip tabStrip) {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        tabStrip.setIndicatorWidth((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, dm));
        tabStrip.setIndicatorCorner(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1.5f, dm));
        tabStrip.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, dm));
    }

    @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override public void onPageScrollStateChanged(int state) {
    }

    @Override public void onPageSelected(View currentTabItem, View oldTabItem, int position, int oldPosition) {
    }

    @Override public final Fragment getCurrentFragment() {
        return adapter == null ? null : adapter.getCurrentPager().fragment;
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

    @Override public QsTabAdapter createTabAdapter() {
        return null;
    }

    @Override public final PagerSlidingTabStrip getTab() {
        return tabs;
    }

    @Override public final ViewPager getViewPager() {
        return pager;
    }

    @Override public final QsIPagerAdapter getViewPagerAdapter() {
        return adapter;
    }

    protected int getOffscreenPageLimit() {
        return 1;
    }

    protected int getPageMargin() {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
    }

    @Override public void smoothScrollToTop(boolean autoRefresh) {
        Fragment fragment = getCurrentFragment();
        if (fragment instanceof QsIView) {
            ((QsIView) fragment).smoothScrollToTop(autoRefresh);
        }
    }
}
