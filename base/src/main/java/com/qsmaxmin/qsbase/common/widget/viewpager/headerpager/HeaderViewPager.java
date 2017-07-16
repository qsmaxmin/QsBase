package com.qsmaxmin.qsbase.common.widget.viewpager.headerpager;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.common.widget.viewpager.headerpager.base.MagicHeaderViewPager;
import com.qsmaxmin.qsbase.common.widget.viewpager.headerpager.help.MagicHeaderUtils;

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

    @Override protected void initTabs(LinearLayout linearLayout) {

    }
}
