package com.qsmaxmin.qsbase.mvp;

import android.view.View;

import com.qsmaxmin.qsbase.common.widget.viewpager.PagerSlidingTabStrip;
import com.qsmaxmin.qsbase.common.widget.viewpager.QsViewPager;
import com.qsmaxmin.qsbase.mvp.adapter.QsViewPagerAdapter;
import com.qsmaxmin.qsbase.mvp.model.QsModelPager;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/7/3 15:26
 * @Description
 */

public interface QsIViewPagerActivity extends QsIActivity {

    void onPageScrollStateChanged(int state);

    void onPageSelected(View childAt, View oldView, int position, int oldPosition);

    void initViewPager(QsModelPager[] modelPagers, int offScreenPageLimit);

    void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

    QsModelPager[] getModelPagers();

    void replaceViewPageItem(QsModelPager... modelPagers);

    void setIndex(int index, boolean bool);

    PagerSlidingTabStrip getTabs();

    QsViewPager getViewPager();

    QsViewPagerAdapter getViewPagerAdapter();

    int getTabItemLayout();

    void initTab(View view, QsModelPager modelPager);
}
