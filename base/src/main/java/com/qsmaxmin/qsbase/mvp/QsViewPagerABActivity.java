package com.qsmaxmin.qsbase.mvp;

import android.graphics.Color;
import android.support.annotation.CallSuper;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.widget.viewpager.PagerSlidingTabStrip;
import com.qsmaxmin.qsbase.common.widget.viewpager.QsViewPager;
import com.qsmaxmin.qsbase.common.widget.viewpager.ViewPagerHelper;
import com.qsmaxmin.qsbase.mvp.adapter.QsTabViewPagerAdapter;
import com.qsmaxmin.qsbase.mvp.adapter.QsViewPagerAdapter;
import com.qsmaxmin.qsbase.mvp.fragment.QsIFragment;
import com.qsmaxmin.qsbase.mvp.model.QsModelPager;
import com.qsmaxmin.qsbase.mvp.presenter.QsPresenter;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/7/3 15:15
 * @Description
 */

public abstract class QsViewPagerABActivity<P extends QsPresenter> extends QsABActivity<P> implements QsIViewPagerABActivity {

    private QsViewPagerAdapter   adapter;
    private QsViewPager          pager;
    private PagerSlidingTabStrip tabs;

    @Override public int layoutId() {
        return R.layout.qs_activity_viewpager;
    }

    @Override protected View initView() {
        View view = super.initView();
        initTabAndPager(view);
        return view;
    }


    private void initTabAndPager(View view) {
        pager = view.findViewById(R.id.pager);
        tabs = view.findViewById(android.R.id.tabs);
        initTabsValue(tabs);
        initViewPager(getModelPagers(), getOffscreenPageLimit());
    }


    @Override public void initViewPager(QsModelPager[] modelPagers, int offScreenPageLimit) {
        if (modelPagers != null && modelPagers.length > 0) {
            ViewPagerHelper pagerHelper = new ViewPagerHelper(this, pager, tabs, modelPagers);
            adapter = createPagerAdapter(pagerHelper);
            pager.setAdapter(adapter);
            int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
            pager.setPageMargin(pageMargin);
            pager.setOffscreenPageLimit(offScreenPageLimit);
            if (tabs != null) tabs.setViewPager(pager);
        }
    }

    protected QsViewPagerAdapter createPagerAdapter(ViewPagerHelper pagerHelper) {
        if (getTabItemLayout() > 0) {
            return new QsTabViewPagerAdapter(getSupportFragmentManager(), pagerHelper);
        } else {
            return new QsViewPagerAdapter(getSupportFragmentManager(), pagerHelper);
        }
    }

    public final void initTabsValue(PagerSlidingTabStrip tab) {
        if (tab != null) {
            DisplayMetrics dm = getResources().getDisplayMetrics();
            tab.setShouldExpand(getTabsShouldExpand());
            tab.setDividerColor(getTabsDividerColor());
            tab.setUnderlineHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, getTabsUnderlineHeight(), dm));
            tab.setUnderlineColor(getTabsUnderlineColor());
            if (getTabsIndicatorHeight() > 0)tab.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, getTabsIndicatorHeight(), dm));
            if (getTabsIndicatorWidth() > 0)tab.setIndicatorWidth((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, getTabsIndicatorWidth(), dm));
            tab.setIndicatorCorner((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, getTabsIndicatorCorner(), dm));
            tab.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, getTabsTitleSize(), dm));
            tab.setIndicatorColor(getTabsIndicatorColor());
            tab.setSelectedTextColor(getTabsSelectedTitleColor());
            tab.setTextColor(getTabsTitleColor());
            tab.setTabBackground(getTabsOnClickTitleColor());
            tab.setBackgroundResource(getTabsBackgroundResource());
            tab.setTabWidth(getTabWidth());
            tab.setTabMarginsLeftRight(getTabMargins());
            tab.setTabPaddingLeftRight(getTabPaddingLeftRight());
            tab.setIndicatorMargin(getTabsIndicatorMargin());
            tab.setIsCurrentItemAnimation(getTabsCurrentItemAnimation());
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

    @Override public void replaceViewPageItem(QsModelPager... modelPagers) {
        if (adapter != null) {
            adapter.replaceViewPagerDatas(modelPagers);
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

    @Override public PagerSlidingTabStrip getTabs() {
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

    protected boolean getTabsCurrentItemAnimation() {
        return false;
    }

    protected int getTabsIndicatorMargin() {
        return 0;
    }

    protected int getTabPaddingLeftRight() {
        return 20;
    }

    protected int getTabMargins() {
        return 0;
    }

    protected int getTabWidth() {
        return 0;
    }

    protected int getTabsBackgroundResource() {
        return android.R.color.transparent;
    }

    protected int getTabsOnClickTitleColor() {
        return 0;
    }

    protected int getTabsTitleColor() {
        return Color.GRAY;
    }

    protected int getTabsSelectedTitleColor() {
        return getResources().getColor(R.color.colorAccent);
    }

    protected int getTabsIndicatorColor() {
        return Color.TRANSPARENT;
    }

    protected int getTabsTitleSize() {
        return 12;
    }

    protected float getTabsIndicatorHeight() {
        return 2;
    }

    protected float getTabsIndicatorWidth() {
        return 0;
    }

    protected float getTabsIndicatorCorner() {
        return 1;
    }

    protected float getTabsUnderlineHeight() {
        return 1;
    }

    protected boolean getTabsShouldExpand() {
        return true;
    }

    protected int getTabsDividerColor() {
        return Color.TRANSPARENT;
    }

    protected int getTabsUnderlineColor() {
        return Color.TRANSPARENT;
    }

    protected int getOffscreenPageLimit() {
        return 3;
    }
}
