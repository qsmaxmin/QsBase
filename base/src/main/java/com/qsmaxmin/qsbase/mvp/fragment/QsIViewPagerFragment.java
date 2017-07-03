package com.qsmaxmin.qsbase.mvp.fragment;

import android.view.View;

import com.qsmaxmin.qsbase.mvp.model.QsModelPager;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/7/3 13:15
 * @Description
 */

public interface QsIViewPagerFragment extends QsIFragment {

    void onPageScrollStateChanged(int state);

    void onPageSelected(View childAt, View oldView, int position, int oldPosition);

    void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

    QsModelPager[] getModelPagers();
}
