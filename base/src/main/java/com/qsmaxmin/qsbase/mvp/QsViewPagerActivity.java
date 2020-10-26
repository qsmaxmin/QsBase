package com.qsmaxmin.qsbase.mvp;

import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;

import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.widget.viewpager.PagerSlidingTabStrip;
import com.qsmaxmin.qsbase.common.widget.viewpager.ViewPagerHelper;
import com.qsmaxmin.qsbase.mvp.adapter.QsFragmentPagerAdapter;
import com.qsmaxmin.qsbase.mvp.adapter.QsIPagerAdapter;
import com.qsmaxmin.qsbase.mvp.adapter.QsTabAdapter;
import com.qsmaxmin.qsbase.mvp.adapter.QsTabAdapterItem;
import com.qsmaxmin.qsbase.mvp.adapter.QsTabFragmentPagerAdapter;
import com.qsmaxmin.qsbase.mvp.model.QsModelPager;
import com.qsmaxmin.qsbase.mvp.presenter.QsPresenter;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/7/3 15:15
 * @Description
 */

public abstract class QsViewPagerActivity<P extends QsPresenter> extends QsActivity<P> implements QsIViewPager {
    private ViewPager            pager;
    private PagerSlidingTabStrip tabs;
    private QsIPagerAdapter      adapter;

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
            if (isCustomTabView()) {
                ViewPagerHelper pagerHelper = new ViewPagerHelper(this, pager, tabs, modelPagers, new QsTabAdapter(this, modelPagers));
                adapter = createPagerAdapter(pagerHelper, true);
            } else {
                ViewPagerHelper pagerHelper = new ViewPagerHelper(this, pager, tabs, modelPagers, null);
                adapter = createPagerAdapter(pagerHelper, false);
            }
            pager.setAdapter(adapter.getAdapter());
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

    protected QsIPagerAdapter createPagerAdapter(ViewPagerHelper pagerHelper, boolean customTabView) {
        if (customTabView) {
            return new QsTabFragmentPagerAdapter(getSupportFragmentManager(), pagerHelper);
        } else {
            return new QsFragmentPagerAdapter(getSupportFragmentManager(), pagerHelper);
        }
    }

    @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override public void onPageScrollStateChanged(int state) {
    }

    @Override public void onPageSelected(View currentTabItem, View oldTabItem, int position, int oldPosition) {
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

    @Override public QsTabAdapterItem createTabAdapterItem(int position) {
        return null;
    }

    @Override public boolean isCustomTabView() {
        return false;
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

    @Override public final Fragment getCurrentFragment() {
        return adapter == null ? null : adapter.getCurrentFragment();
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
