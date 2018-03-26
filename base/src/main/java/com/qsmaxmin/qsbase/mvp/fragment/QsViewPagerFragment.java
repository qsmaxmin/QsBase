package com.qsmaxmin.qsbase.mvp.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.widget.viewpager.PagerSlidingTabStrip;
import com.qsmaxmin.qsbase.common.widget.viewpager.QsViewPager;
import com.qsmaxmin.qsbase.common.widget.viewpager.headerpager.InnerViewPager;
import com.qsmaxmin.qsbase.common.widget.viewpager.headerpager.base.InnerScroller;
import com.qsmaxmin.qsbase.common.widget.viewpager.headerpager.base.InnerScrollerContainer;
import com.qsmaxmin.qsbase.common.widget.viewpager.headerpager.base.OuterScroller;
import com.qsmaxmin.qsbase.mvp.adapter.QsTabViewPagerAdapter;
import com.qsmaxmin.qsbase.mvp.adapter.QsViewPagerAdapter;
import com.qsmaxmin.qsbase.mvp.model.QsModelPager;
import com.qsmaxmin.qsbase.mvp.presenter.QsPresenter;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/7/3 13:14
 * @Description
 */

public abstract class QsViewPagerFragment<P extends QsPresenter> extends QsFragment<P> implements QsIViewPagerFragment, InnerScrollerContainer {

    protected QsViewPagerAdapter   adapter;
    protected QsViewPager          pager;
    protected PagerSlidingTabStrip tabs;
    private   InnerViewPager       mInnerViewPager;

    @Override public int layoutId() {
        return R.layout.qs_fragment_viewpager;
    }

    @Override public void initData(Bundle savedInstanceState) {

    }

    @Override protected View initView(LayoutInflater inflater) {
        View view = super.initView(inflater);
        initTabAndPager(view);
        return view;
    }

    protected void initTabAndPager(View view) {
        View pagerView = view.findViewById(R.id.pager);
        if (pagerView instanceof QsViewPager) {
            pager = (QsViewPager) pagerView;
        } else if (pagerView instanceof InnerViewPager) {
            this.mInnerViewPager = (InnerViewPager) pagerView;
            pager = mInnerViewPager.getViewPager();
        }
        tabs = (PagerSlidingTabStrip) view.findViewById(android.R.id.tabs);
        initTabsValue(tabs);
        initViewPager(getModelPagers(), getOffscreenPageLimit());
    }

    @Override public void initViewPager(QsModelPager[] modelPagers, int offScreenPageLimit) {
        if (modelPagers != null && modelPagers.length > 0) {
            adapter = createPagerAdapter(pager, tabs);
            adapter.setModelPagers(modelPagers);
            pager.setAdapter(adapter);
            final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
            pager.setPageMargin(pageMargin);
            pager.setOffscreenPageLimit(offScreenPageLimit);
            tabs.setViewPager(pager);
        }
    }

    protected QsViewPagerAdapter createPagerAdapter(QsViewPager pager, PagerSlidingTabStrip tabs) {
        if (getTabItemLayout() > 0) {
            return new QsTabViewPagerAdapter(initTag(), getChildFragmentManager(), tabs, pager, this);
        } else {
            return new QsViewPagerAdapter(initTag(), getChildFragmentManager(), tabs, pager, this);
        }
    }

    public final void initTabsValue(PagerSlidingTabStrip tab) {
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

    @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override public void onPageScrollStateChanged(int state) {

    }

    @Override public void onPageSelected(View currentTabItem, View oldTabItem, int position, int oldPosition) {
    }

    @Override public void initTab(View tabItem, QsModelPager modelPager) {

    }

    @Override public void replaceViewPageItem(QsModelPager... modelPagers) {
        adapter.replaceViewPagerDatas(modelPagers);
        adapter.notifyDataSetChanged();
    }

    @Override public Fragment getCurrentFragment() {
        return adapter.getAllData()[pager.getCurrentItem()].fragment;
    }

    @Override public void setIndex(int index, boolean bool) {
        int childCount = pager.getAdapter().getCount();
        if (0 <= index && index < childCount) {
            pager.setCurrentItem(index, bool);
        }
    }

    @Override public int getTabItemLayout() {
        return 0;
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
        return android.R.color.white;
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
        return getResources().getColor(R.color.colorAccent);
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


    /*----------------------- 以下是HeaderViewPager支持 ----------------------------*/
    @Override public void setMyOuterScroller(OuterScroller outerScroller, int myPosition) {
        if (mInnerViewPager != null) {
            QsModelPager[] allData = getViewPagerAdapter().getAllData();
            if (allData != null) {
                for (QsModelPager pager : allData) {
                    if (pager.fragment instanceof QsIListFragment) {
                        ListView listView = ((QsIListFragment) pager.fragment).getListView();
                        if (listView instanceof InnerScroller) {
                            L.i(initTag(), "注册调度控件： position:" + myPosition + "  fragment:" + pager.fragment.getClass().getSimpleName());
                            ((InnerScroller) listView).register2Outer(outerScroller, myPosition);
                        }
                    } else if (pager.fragment instanceof QsIFragment) {
                        View view = pager.fragment.getView();
                        if (view instanceof InnerScroller) {
                            L.i(initTag(), "注册调度控件:  position:" + myPosition + "  fragment:" + pager.fragment.getClass().getSimpleName());
                            ((InnerScroller) view).register2Outer(outerScroller, myPosition);
                        } else if (view != null) {
                            View contentView = view.findViewById(R.id.scroll_view_inner);
                            if (contentView instanceof InnerScroller) {
                                L.i(initTag(), "注册调度控件:  position:" + myPosition + "  fragment:" + pager.fragment.getClass().getSimpleName());
                                ((InnerScroller) contentView).register2Outer(outerScroller, myPosition);
                            }
                        }
                    }
                }
            }
        }
    }
}
