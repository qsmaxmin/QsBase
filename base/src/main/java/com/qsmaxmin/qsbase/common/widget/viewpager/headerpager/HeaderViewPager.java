package com.qsmaxmin.qsbase.common.widget.viewpager.headerpager;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.common.widget.viewpager.headerpager.base.MagicHeaderViewPager;
import com.qsmaxmin.qsbase.common.widget.viewpager.headerpager.help.MagicHeaderUtils;
import com.qsmaxmin.qsbase.common.widget.viewpager.PagerSlidingTabStrip;

/**
 * @CreateBy QS
 * @Date 16/11/20  下午8:05
 * @Description 带头部的ViewPager
 */
public class HeaderViewPager extends MagicHeaderViewPager {
    public HeaderViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HeaderViewPager(Context context) {
        super(context);
    }

    public HeaderViewPager(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override protected void initTabsArea(LinearLayout container) {
        ViewGroup tabsArea = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.qs_layout_tabs, null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, MagicHeaderUtils.dp2px(getContext(), 48));
        container.addView(tabsArea, lp);
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) tabsArea.findViewById(R.id.tabs);
        setTabsArea(tabsArea);
        setPagerSlidingTabStrip(tabs);
    }
}
