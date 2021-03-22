package com.qsmaxmin.qsbase.mvvm;

import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;

import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.widget.viewpager.MvViewPagerHelper;
import com.qsmaxmin.qsbase.common.widget.viewpager.PagerSlidingTabStrip;
import com.qsmaxmin.qsbase.mvvm.adapter.MvFragmentPagerAdapter;
import com.qsmaxmin.qsbase.mvvm.adapter.MvIPagerAdapter;
import com.qsmaxmin.qsbase.mvvm.adapter.MvTabAdapter;
import com.qsmaxmin.qsbase.mvvm.adapter.MvTabAdapterItem;
import com.qsmaxmin.qsbase.mvvm.adapter.MvTabFragmentPagerAdapter;
import com.qsmaxmin.qsbase.mvvm.model.MvModelPager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/7/3 15:15
 * @Description
 */

public abstract class MvViewPagerActivity extends MvActivity implements MvIViewPager {
    private ViewPager            pager;
    private PagerSlidingTabStrip tabs;
    private MvIPagerAdapter      adapter;
    private MvTabAdapter         tabAdapter;

    @Override public int layoutId() {
        return R.layout.qs_viewpager_bottom_tab;
    }

    @NonNull @Override protected View initView(@NonNull LayoutInflater inflater) {
        View view = super.initView(inflater);
        pager = view.findViewById(R.id.pager);
        tabs = view.findViewById(android.R.id.tabs);
        if (tabs != null) initTab(tabs);
        initViewPager(createModelPagers());
        return view;
    }

    @Override public void initViewPager(@Nullable MvModelPager[] modelPagers) {
        initViewPager(modelPagers, getOffscreenPageLimit());
    }

    @Override public void initViewPager(@Nullable MvModelPager[] modelPagers, int offScreenPageLimit) {
        if (modelPagers != null && modelPagers.length > 0) {
            MvViewPagerHelper pagerHelper = new MvViewPagerHelper(this, pager, modelPagers);
            MvTabAdapterItem firstTabItem = createTabAdapterItemInner(0);
            if (firstTabItem != null) {
                tabAdapter = new MvTabAdapter(this, modelPagers, firstTabItem);
                adapter = createPagerAdapter(pagerHelper, true);
            } else {
                adapter = createPagerAdapter(pagerHelper, false);
            }
            pager.setAdapter(adapter.getAdapter());
            pager.setPageMargin(getPageMargin());
            pager.setOffscreenPageLimit(offScreenPageLimit);
            if (tabs != null) tabs.setViewPager(pager);
        }
    }

    @Override public void initTab(@NonNull PagerSlidingTabStrip tabStrip) {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        tabStrip.setIndicatorWidth((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, dm));
        tabStrip.setIndicatorCorner(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1.5f, dm));
        tabStrip.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, dm));
    }

    protected MvIPagerAdapter createPagerAdapter(MvViewPagerHelper pagerHelper, boolean customTabView) {
        if (customTabView) {
            return new MvTabFragmentPagerAdapter(getSupportFragmentManager(), pagerHelper);
        } else {
            return new MvFragmentPagerAdapter(getSupportFragmentManager(), pagerHelper);
        }
    }

    @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override public void onPageScrollStateChanged(int state) {
    }

    @Override public void onPageSelected(int position, int oldPosition) {
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

    @Override public final MvTabAdapterItem createTabAdapterItemInner(int position) {
        MvTabAdapterItem item = createTabAdapterItem(position);
        if (item != null) item.setLayer(this);
        return item;
    }

    @Override public MvTabAdapterItem createTabAdapterItem(int position) {
        return null;
    }

    @Override public final PagerSlidingTabStrip getTab() {
        return tabs;
    }

    @Override public final ViewPager getViewPager() {
        return pager;
    }

    @Override public final MvIPagerAdapter getViewPagerAdapter() {
        return adapter;
    }

    @Override public final Fragment getCurrentFragment() {
        return adapter == null ? null : adapter.getCurrentFragment();
    }

    @Override public final MvTabAdapter getTabAdapter() {
        return tabAdapter;
    }

    protected int getOffscreenPageLimit() {
        return 1;
    }

    protected int getPageMargin() {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
    }

    @Override public void smoothScrollToTop(boolean autoRefresh) {
        Fragment fragment = getCurrentFragment();
        if (fragment instanceof MvIView) {
            ((MvIView) fragment).smoothScrollToTop(autoRefresh);
        }
    }
}
