package com.qsmaxmin.qsbase.common.widget.viewpager.headerpager.base;

import androidx.viewpager.widget.ViewPager;
import android.view.View;

public interface OuterScroller extends ViewPager.OnPageChangeListener {

    /**
     * PermissionListener form current innerScroller scroll.
     *
     * @param scrollY InnerScroller's scrollY
     */
    void onInnerScroll(int pageIndex, int scrollY);

    void onPageSelected(int position);

    /**
     * Get overall height of header
     */
    int getHeaderHeight();

    int getTabHeight();

    /**
     * Get magic header's visible height.
     */
    int getHeaderVisibleHeight();

    /**
     * Synchronize Header scroll position. It can be enormously called. With many verification
     * inside, innerScrollers won't perform scroll unless necessary.
     *
     * @param currentIndex Current index，also is the excluded index in synchronization.
     */
    void syncPagesPosition(int currentIndex);

    InnerScroller getCurrentInnerScroller();

    int getCurrentInnerScrollerIndex();

    /**
     * Add custom HeaderView
     */
    void addPagerHeaderView(View view);

    void adjustChildrenEmptyHeaderHeight();

    /**
     * PermissionListener on current InnerScroller stop scrolling.
     */
    void onInnerScrollerStop();

    void registerInnerScroller(int index, InnerScroller innerScroller);

    void onInnerPullToRefreshScroll(int scrollY);

    /**
     * Get content visible area max height, equals to (view Height - stable area height)
     */
    int getContentAreaMaxVisibleHeight();

    /**
     * Update state of refresh.
     *
     * @param isRefreshing the coming state
     */
    void updateRefreshState(boolean isRefreshing);

    /**
     * No need to do anything here
     */
    @Deprecated void onPageScrollStateChanged(int state);

    /**
     * No need to do anything here
     */
    @Deprecated void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

}