package com.qsmaxmin.qsbase.mvvm.fragment;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.widget.viewpager.PagerSlidingTabStrip;
import com.qsmaxmin.qsbase.mvvm.MvIView;
import com.qsmaxmin.qsbase.mvvm.MvIViewPager;
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
 * @Date 2017/7/3 13:14
 * @Description
 */

public abstract class MvViewPagerFragment extends MvFragment implements MvIViewPager {
    private ViewPager            pager;
    private PagerSlidingTabStrip tabs;
    private MvIPagerAdapter      adapter;
    private MvTabAdapter         tabAdapter;

    @Override public int layoutId() {
        return R.layout.qs_viewpager_top_tab;
    }

    @Override protected View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        View view = super.initView(inflater, container);
        pager = view.findViewById(R.id.pager);
        tabs = view.findViewById(android.R.id.tabs);
        pager.setPageMargin(getPageMargin());
        if (tabs != null) {
            initTab(tabs);
            tabs.setViewPager(pager);
        }
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
            pager.setOffscreenPageLimit(offScreenPageLimit);
            if (firstTabItem != null) {
                tabAdapter = new MvTabAdapter(this, list, firstTabItem);
                if (adapter == null) adapter = createPagerAdapter(true);
            } else {
                if (adapter == null) adapter = createPagerAdapter(false);
            }
            adapter.setModelPagers(list);
            pager.setAdapter(adapter.getAdapter());
            if (tabs != null) tabs.notifyDataSetChanged();
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

    @Override public void replaceModelPager(MvModelPager oldPager, MvModelPager newPager) {
        if (adapter != null) {
            adapter.replaceModelPager(oldPager, newPager);
            if (tabs != null) tabs.notifyDataSetChanged();
        }
    }

    @Override public void replaceModelPager(int index, MvModelPager pager) {
        if (adapter != null) {
            adapter.replaceModelPager(index, pager);
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
        return getChildFragmentManager();
    }

    protected MvIPagerAdapter createPagerAdapter(boolean isCustomTabView) {
        if (isCustomTabView) {
            return new MvTabFragmentPagerAdapter(this);
        } else {
            return new MvFragmentPagerAdapter(this);
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
        return adapter == null ? null : adapter.getModelPager(pager.getCurrentItem()).fragment;
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
        return 2;
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
