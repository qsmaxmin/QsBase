package com.qsmaxmin.qsbase.common.widget.viewpager.headerpager.base;

/**
 * Interface of MagicHeaderViewPager's Adapter.
 * Use a FragmentPagerAdapter class to implement like this:
 * public class DemoPagerAdapter extends FragmentPagerAdapter implements OuterPagerAdapter
 * TODO: Make sure to put codes below in your PagerAdapter's instantiateItem()
 * InnerScrollerContainer fragment = (InnerScrollerContainer) super.instantiateItem(container, position);
 * fragment.setMyOuterScroller(mOuterScroller, position);<br>
 * return fragment;
 */
public interface OuterPagerAdapter {
    void setPageOuterScroller(OuterScroller outerScroller);
}
