package com.qsmaxmin.qsbase.common.widget.viewpager.autoscroll;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * create by skyJC
 */
public class InfinitePagerAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener {

    private static final float   PAGE_WIDTH_SINGLE_ITEM = 1.0f;
    private              boolean infinitePagesEnabled   = true;
    private              float   pageWidth              = PAGE_WIDTH_SINGLE_ITEM;
    private InfiniteStatePagerAdapter adapter;

    public InfinitePagerAdapter(InfiniteStatePagerAdapter adapter) {
        this.adapter = adapter;
        adapter.indicator.setOnPageChangeListener(this);
    }

    @Override public int getCount() {
        if (infinitePagesEnabled) {
            return Integer.MAX_VALUE;
        } else {
            return adapter.getCount();
        }
    }

    @Override public float getPageWidth(int position) {
        return pageWidth;
    }

    public int getRealCount() {
        return adapter.getCount();
    }

    @Override public Object instantiateItem(ViewGroup container, int position) {
        int virtualPosition = getVirtualPosition(position);
        Log.i("instantiateItem", "" + virtualPosition + ":" + position);
        return adapter.instantiateItem(container, virtualPosition);
    }

    @Override public void destroyItem(ViewGroup container, int position, Object object) {
        int virtualPosition = getVirtualPosition(position);
        Log.i("destroyItem", "" + virtualPosition + ":" + position);
        adapter.destroyItem(container, virtualPosition, object);
    }

    @Override public void finishUpdate(ViewGroup container) {
        adapter.finishUpdate(container);
    }

    @Override public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public int getVirtualPosition(int position) {
        return infinitePagesEnabled ? position % getRealCount() : position;
    }

    public void enableInfinitePages(boolean enable) {
        infinitePagesEnabled = enable;
    }

    @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override public void onPageSelected(int position) {
    }

    @Override public void onPageScrollStateChanged(int state) {

    }
}
