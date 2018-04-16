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
import com.qsmaxmin.qsbase.mvp.adapter.QsTabViewPagerAdapter;
import com.qsmaxmin.qsbase.mvp.adapter.QsViewPagerAdapter;
import com.qsmaxmin.qsbase.mvp.fragment.QsFragment;
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
        pager = (QsViewPager) view.findViewById(R.id.pager);
        tabs = (PagerSlidingTabStrip) view.findViewById(android.R.id.tabs);
        initTabsValue(tabs);
        initViewPager(getModelPagers(), getOffscreenPageLimit());
    }


    @Override public void initViewPager(QsModelPager[] modelPagers, int offScreenPageLimit) {
        if (modelPagers != null) {
            adapter = createPagerAdapter(pager, tabs);
            adapter.setModelPagers(modelPagers);
            pager.setAdapter(adapter);
            final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
            pager.setPageMargin(pageMargin);
            pager.setOffscreenPageLimit(offScreenPageLimit);
            if (tabs != null) tabs.setViewPager(pager);
        }
    }

    protected QsViewPagerAdapter createPagerAdapter(QsViewPager pager, PagerSlidingTabStrip tabs) {
        if (getTabItemLayout() > 0) {
            return new QsTabViewPagerAdapter(initTag(), getSupportFragmentManager(), tabs, pager, this);
        } else {
            return new QsViewPagerAdapter(initTag(), getSupportFragmentManager(), tabs, pager, this);
        }
    }

    public final void initTabsValue(PagerSlidingTabStrip tab) {
        if (tab != null) {
            DisplayMetrics dm = getResources().getDisplayMetrics();
            tab.setShouldExpand(getTabsShouldExpand());
            tab.setDividerColor(getTabsDividerColor());
            tab.setUnderlineHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, getTabsUnderlineHeight(), dm));
            tab.setUnderlineColor(getTabsUnderlineColor());
            tab.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, getTabsIndicatorSize(), dm));
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
        Fragment fragment = getViewPagerAdapter().getData(position).fragment;
        if (fragment instanceof QsFragment) {
            ((QsFragment) fragment).onFragmentSelectedInViewPager(position);
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

    protected float getTabsIndicatorSize() {
        return 2;
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
