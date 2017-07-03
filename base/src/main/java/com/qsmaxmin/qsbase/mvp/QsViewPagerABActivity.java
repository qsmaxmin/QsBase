package com.qsmaxmin.qsbase.mvp;

import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.common.widget.viewpager.PagerSlidingTabStrip;
import com.qsmaxmin.qsbase.common.widget.viewpager.QsViewPager;
import com.qsmaxmin.qsbase.mvp.adapter.QsViewPagerAdapter;
import com.qsmaxmin.qsbase.mvp.model.QsModelPager;
import com.qsmaxmin.qsbase.mvp.presenter.QsPresenter;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/7/3 15:15
 * @Description
 */

public abstract class QsViewPagerABActivity<P extends QsPresenter> extends QsABActivity<P> implements QsIViewPagerABActivity {

    private PagerAdapter         adapter;
    private QsViewPager          pager;
    private PagerSlidingTabStrip tabs;

    @Override public int layoutId() {
        return R.layout.qs_activity_viewpager;
    }

    @Override protected View initView() {
        View view = super.initView();
        initViewPager(view);
        return view;
    }


    private void initViewPager(View view) {
        pager = (QsViewPager) view.findViewById(R.id.pager);
        tabs = (PagerSlidingTabStrip) view.findViewById(android.R.id.tabs);
        initTabsValue(tabs);
        initViewPager(getModelPagers(), pager, tabs, 3);
    }

    public void initViewPager(QsModelPager[] modelPagers, QsViewPager pager, PagerSlidingTabStrip tabs, int offScreenPageLimit) {
        if (modelPagers != null) {
            adapter = getPagerAdapter(pager, tabs);
            ((QsViewPagerAdapter) adapter).setModelPagers(modelPagers);
            pager.setAdapter(adapter);
            final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
            pager.setPageMargin(pageMargin);
            pager.setOffscreenPageLimit(offScreenPageLimit);
            tabs.setViewPager(pager);
        }
    }

    public PagerAdapter getPagerAdapter(QsViewPager pager, PagerSlidingTabStrip tabs) {
        return new QsViewPagerAdapter(initTag(), getSupportFragmentManager(), tabs, pager, this);
    }

    public final void initTabsValue(PagerSlidingTabStrip tabs) {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        tabs.setShouldExpand(getTabsShouldExpand());
        tabs.setDividerColor(getTabsDividerColor());
        tabs.setUnderlineHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, getTabsUnderlineHeight(), dm));
        tabs.setUnderlineColor(getTabsUnderlineColor());
        tabs.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, getTabsIndicatorSize(), dm));
        tabs.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, getTabsTitleSize(), dm));
        tabs.setIndicatorColor(getTabsIndicatorColor());
        tabs.setSelectedTextColor(getTabsSelectedTitleColor());
        tabs.setTextColor(getTabsTitleColor());
        tabs.setTabBackground(getTabsOnClickTitleColor());
        tabs.setBackgroundResource(getTabsBackgroundResource());
        tabs.setTabWidth(getTabWidth());
        tabs.setTabMarginsLeftRight(getTabMargins());
        tabs.setTabPaddingLeftRight(getTabPaddingLeftRight());
        tabs.setIndicatorMargin(getTabsIndicatorMargin());
        tabs.setIsCurrentItemAnimation(getTabsCurrentItemAnimation());
    }

    @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override public void onPageScrollStateChanged(int state) {

    }

    @Override public void onPageSelected(View childAt, View oldView, int position, int oldPosition) {

    }

    @Override public void replaceViewPageItem(QsModelPager... modelPagers) {
        if (adapter instanceof QsViewPagerAdapter) {
            ((QsViewPagerAdapter) adapter).replaceViewPagerDatas(modelPagers);
            adapter.notifyDataSetChanged();
        }
    }

    @Override public void setIndex(int index, boolean bool) {
        int childCount = pager.getAdapter().getCount();
        if (0 <= index && index < childCount) {
            pager.setCurrentItem(index, bool);
        }
    }

    @Override public PagerSlidingTabStrip getTabs() {
        return tabs;
    }

    @Override public QsViewPager getViewPager() {
        return pager;
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
        return 0;
    }
}
