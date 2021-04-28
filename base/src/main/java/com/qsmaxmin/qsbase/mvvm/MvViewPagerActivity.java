package com.qsmaxmin.qsbase.mvvm;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;

import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.widget.viewpager.PagerSlidingTabStrip;
import com.qsmaxmin.qsbase.mvvm.adapter.MvFragmentPagerAdapter;
import com.qsmaxmin.qsbase.mvvm.adapter.MvIPagerAdapter;
import com.qsmaxmin.qsbase.mvvm.adapter.MvTabAdapter;
import com.qsmaxmin.qsbase.mvvm.adapter.MvTabAdapterItem;
import com.qsmaxmin.qsbase.mvvm.adapter.MvTabFragmentPagerAdapter;
import com.qsmaxmin.qsbase.mvvm.model.MvModelPager;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
        pager.setPageMargin(getPageMargin());
        if (tabs != null) initTab(tabs);
        initViewPager(createModelPagerList());
        return view;
    }

    @Nullable @Override public List<MvModelPager> createModelPagerList() {
        MvModelPager[] modelPagers = createModelPagers();
        return modelPagers == null ? null : Arrays.asList(modelPagers);
    }

    @Nullable @Override public MvModelPager[] createModelPagers() {
        return null;
    }

    @Override public final void initViewPager(@Nullable MvModelPager[] pagers) {
        initViewPager(pagers, getOffscreenPageLimit());
    }

    @Override public final void initViewPager(@Nullable MvModelPager[] modelPagers, int offScreenPageLimit) {
        if (modelPagers != null && modelPagers.length > 0) {
            initViewPager(Arrays.asList(modelPagers), offScreenPageLimit);
        }
    }

    @Override public final void initViewPager(@Nullable List<MvModelPager> list) {
        initViewPager(list, getOffscreenPageLimit());
    }

    @Override public final void initViewPager(@Nullable List<MvModelPager> list, int offScreenPageLimit) {
        if (list != null && !list.isEmpty()) {
            MvTabAdapterItem firstTabItem = createTabAdapterItemInner(0);
            if (firstTabItem != null) {
                tabAdapter = new MvTabAdapter(this, list, firstTabItem);
                if (adapter == null) {
                    adapter = createPagerAdapter(true);
                    adapter.setModelPagers(list);
                    pager.setAdapter(adapter.getAdapter());
                } else {
                    adapter.updateModelPagers(list);
                }
            } else {
                if (adapter == null) {
                    adapter = createPagerAdapter(false);
                    adapter.setModelPagers(list);
                    pager.setAdapter(adapter.getAdapter());
                } else {
                    adapter.updateModelPagers(list);
                }
            }
            pager.setOffscreenPageLimit(offScreenPageLimit);
            if (tabs != null) tabs.setViewPager(pager);
        }
    }

    @Override public final void addModelPager(MvModelPager pager) {
        if (adapter != null) {
            adapter.addModelPager(pager);
            if (tabs != null) tabs.notifyDataSetChanged();
        }
    }

    @Override public final void addModelPager(int index, MvModelPager pager) {
        if (adapter != null) {
            adapter.addModelPager(index, pager);
            if (tabs != null) tabs.notifyDataSetChanged();
        }
    }

    @Override public final void removeModelPager(int index) {
        if (adapter != null) {
            adapter.removeModelPager(index);
            if (tabs != null) tabs.notifyDataSetChanged();
        }
    }

    @Override public final void removeModelPager(Fragment fragment) {
        if (adapter != null) {
            adapter.removeModelPager(fragment);
            if (tabs != null) tabs.notifyDataSetChanged();
        }
    }

    @Override public final void removeModelPager(MvModelPager pager) {
        if (adapter != null) {
            adapter.removeModelPager(pager);
            if (tabs != null) tabs.notifyDataSetChanged();
        }
    }

    @Override public final MvModelPager getModelPager(int index) {
        return getModelPagers() == null ? null : getModelPagers().get(index);
    }

    @Override public final List<MvModelPager> getModelPagers() {
        return adapter == null ? null : adapter.getModelPagers();
    }

    @Override public final FragmentManager getViewPagerFragmentManager() {
        return getSupportFragmentManager();
    }

    /**
     * 重写可定义PagerSlidingTabStrip样式
     */
    @Override public void initTab(@NonNull PagerSlidingTabStrip tabStrip) {
    }

    protected MvIPagerAdapter createPagerAdapter(boolean isCustomTabView) {
        if (isCustomTabView) {
            return new MvTabFragmentPagerAdapter(this);
        } else {
            return new MvFragmentPagerAdapter(this);
        }
    }

    @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override public void onPageScrollStateChanged(int state) {
    }

    @Override public void onPageSelected(int position, int oldPosition) {
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

    @Override public final Fragment getCurrentFragment() {
        return adapter == null ? null : adapter.getModelPager(pager.getCurrentItem()).fragment;
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
