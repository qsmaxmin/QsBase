package com.qsmaxmin.qsbase.mvp.fragment;

import android.util.TypedValue;
import android.view.View;

import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.common.widget.viewpager.headerpager.HeaderViewPager;
import com.qsmaxmin.qsbase.common.widget.viewpager.headerpager.base.MagicHeaderViewPager;
import com.qsmaxmin.qsbase.mvp.model.QsModelPager;
import com.qsmaxmin.qsbase.mvp.presenter.QsPresenter;

/**
 * @CreateBy qsmaxmin
 * @Date 2016/11/21 16:06
 * @Description
 */

public abstract class QsHeaderViewpagerFragment<T extends QsPresenter> extends QsViewPagerFragment<T> implements QsIHeaderViewPagerFragment {

    protected HeaderViewPager headerViewPager;

    @Override public int layoutId() {
        return R.layout.qs_fragment_header_viewpager;
    }

    @Override protected void initPagerView(View view) {
        if (view instanceof MagicHeaderViewPager) {
            headerViewPager = (HeaderViewPager) view;
        } else {
            headerViewPager = (HeaderViewPager) view.findViewById(R.id.pager);
        }
        pager = headerViewPager.getViewPager();
        tabs = headerViewPager.getPagerSlidingTabStrip();
        initTabsValue(tabs);
        initViewPager(getModelPagers(), 3);
    }

    @Override public void initViewPager(QsModelPager[] modelPagers, int offScreenPageLimit) {
        if (modelPagers != null && modelPagers.length > 0) {
            adapter = createPagerAdapter(pager, tabs);
            adapter.setModelPagers(modelPagers);
            pager.setPageMargin((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics()));
            pager.setOffscreenPageLimit(offScreenPageLimit);
            headerViewPager.setPagerAdapter(adapter);
        }
    }
}
