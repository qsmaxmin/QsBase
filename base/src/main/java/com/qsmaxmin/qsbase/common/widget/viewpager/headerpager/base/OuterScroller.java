package com.qsmaxmin.qsbase.common.widget.viewpager.headerpager.base;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * @创建人 QS
 * @创建时间 16/11/19  下午6:21
 * @类描述
 */
public interface OuterScroller extends ViewPager.OnPageChangeListener {

    /**
     * PermissionListener form current innerScroller scroll.
     *
     * @param scrollY InnerScroller's scrollY
     */
    void onInnerScroll(int pageIndex, int scrollY);

    /**
     * （as its name）
     */
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


    /**
     * （as its name）
     */
    InnerScroller getCurrentInnerScroller();

    /**
     * （as its name）
     */
    int getCurrentInnerScrollerIndex();

    /**
     * Add custom HeaderView
     */
    void addPagerHeaderView(View view);

    /**
     * （as its name）
     */
    void adjustChildrenEmptyHeaderHeight();

    /**
     * PermissionListener on current InnerScroller stop scrolling.
     */
    void onInnerScrollerStop();

    void registerInnerScroller(int index, InnerScroller innerScroller);

    /************************ 内外刷新联动 **************************/
    /**
     * PermissionListener of InnerScroller's PullToRefresh
     */
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

    /******************* unused methods from OnPageChangeListener ********************/
    /**
     * No need to do anything here
     */
    @Deprecated void onPageScrollStateChanged(int state);

    /**
     * No need to do anything here
     */
    @Deprecated void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

}