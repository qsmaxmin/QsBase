package com.qsmaxmin.qsbase.mvp.fragment;

import android.view.View;

import com.qsmaxmin.qsbase.common.widget.viewpager.PagerSlidingTabStrip;
import com.qsmaxmin.qsbase.common.widget.viewpager.QsViewPager;
import com.qsmaxmin.qsbase.mvp.model.QsModelPager;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/7/3 13:15
 * @Description
 */

public interface QsIViewPagerFragment extends QsIFragment {

    void onPageScrollStateChanged(int state);

    void onPageSelected(View childAt, View oldView, int position, int oldPosition);

    void initViewPager(QsModelPager[] modelPagers, int offScreenPageLimit);

    void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

    QsModelPager[] getModelPagers();

    void replaceViewPageItem(QsModelPager... modelPagers);

    void setIndex(int index, boolean bool);

    PagerSlidingTabStrip getTabs();

    QsViewPager getViewPager();

    int getTabItemLayout();

    void initTab(View view, QsModelPager modelPager);
}
