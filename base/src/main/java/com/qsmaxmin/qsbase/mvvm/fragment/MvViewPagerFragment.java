package com.qsmaxmin.qsbase.mvvm.fragment;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.widget.viewpager.MvViewPagerHelper;
import com.qsmaxmin.qsbase.common.widget.viewpager.PagerSlidingTabStrip;
import com.qsmaxmin.qsbase.mvvm.MvIView;
import com.qsmaxmin.qsbase.mvvm.MvIViewPager;
import com.qsmaxmin.qsbase.mvvm.adapter.MvFragmentPagerAdapter;
import com.qsmaxmin.qsbase.mvvm.adapter.MvIPagerAdapter;
import com.qsmaxmin.qsbase.mvvm.adapter.MvTabAdapter;
import com.qsmaxmin.qsbase.mvvm.adapter.MvTabAdapterItem;
import com.qsmaxmin.qsbase.mvvm.adapter.MvTabFragmentPagerAdapter;
import com.qsmaxmin.qsbase.mvvm.model.MvModelPager;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/7/3 13:14
 * @Description
 */

public abstract class MvViewPagerFragment extends MvFragment implements MvIViewPager {
    private ViewPager            pager;
    private PagerSlidingTabStrip tabs;
    private MvIPagerAdapter      adapter;
    private MvTabAdapter         tabAdapter;
    private MvTabAdapterItem     firstTabItem;

    @Override public int layoutId() {
        return R.layout.qs_viewpager_top_tab;
    }

    @Override protected View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        View view = super.initView(inflater, container);
        pager = view.findViewById(R.id.pager);
        tabs = view.findViewById(android.R.id.tabs);
        firstTabItem = createTabAdapterItemInner(0);
        if (tabs != null) initTab(tabs);
        initViewPager(createModelPagers());
        return view;
    }

    @Override public final boolean isCustomTabView() {
        return firstTabItem != null;
    }

    @Override public final void initViewPager(@Nullable List<MvModelPager> modelPagers) {
        initViewPager(modelPagers, getOffscreenPageLimit());
    }

    @Override public final void initViewPager(@Nullable List<MvModelPager> modelPagers, int offScreenPageLimit) {
        if (modelPagers != null && !modelPagers.isEmpty()) {
            int size = modelPagers.size();
            MvModelPager[] pagers = modelPagers.toArray(new MvModelPager[size]);
            initViewPager(pagers, offScreenPageLimit);
        }
    }

    @Override public final void initViewPager(@Nullable MvModelPager[] modelPagers) {
        initViewPager(createModelPagers(), getOffscreenPageLimit());
    }

    @Override public final void initViewPager(@Nullable MvModelPager[] modelPagers, int offScreenPageLimit) {
        if (modelPagers != null && modelPagers.length > 0) {
            MvViewPagerHelper pagerHelper = new MvViewPagerHelper(this, pager, modelPagers);
            if (isCustomTabView()) {
                tabAdapter = new MvTabAdapter(this, modelPagers, firstTabItem);
            }
            adapter = createPagerAdapter(pagerHelper);
            pager.setAdapter(adapter.getAdapter());
            pager.setPageMargin(getPageMargin());
            pager.setOffscreenPageLimit(offScreenPageLimit);
            if (tabs != null) tabs.setViewPager(pager);
        }
    }

    protected MvIPagerAdapter createPagerAdapter(MvViewPagerHelper pagerHelper) {
        if (isCustomTabView()) {
            return new MvTabFragmentPagerAdapter(getChildFragmentManager(), pagerHelper);
        } else {
            return new MvFragmentPagerAdapter(getChildFragmentManager(), pagerHelper);
        }
    }

    /**
     * 重写可定义PagerSlidingTabStrip样式
     */
    @Override public void initTab(@NonNull PagerSlidingTabStrip tabStrip) {
    }

    @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override public void onPageScrollStateChanged(int state) {
    }

    @Override public void onPageSelected(int position, int oldPosition) {
    }

    @Override public final Fragment getCurrentFragment() {
        return adapter == null ? null : adapter.getCurrentPager().fragment;
    }

    @Override public final void setIndex(int index) {
        setIndex(index, true);
    }

    @Override public final void setIndex(int index, boolean bool) {
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
