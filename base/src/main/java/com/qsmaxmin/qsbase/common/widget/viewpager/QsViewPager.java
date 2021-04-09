package com.qsmaxmin.qsbase.common.widget.viewpager;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

/**
 * @CreateBy QS
 * @Date 16/11/20  下午7:21
 * @Description 自定义viewpager
 */
public class QsViewPager extends ViewPager {
    private boolean canScroll = true;

    public QsViewPager(Context context) {
        this(context, null);
    }

    public QsViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN && getParent() != null && getAdapter() != null) {
            int count = getAdapter().getCount();
            int currentItem = getCurrentItem();
            getParent().requestDisallowInterceptTouchEvent(currentItem != 0 && currentItem != count - 1);
        }
        return canScroll && super.onInterceptTouchEvent(ev);
    }

    public void setCanScroll(boolean canScroll) {
        this.canScroll = canScroll;
    }

    public boolean isCanScroll() {
        return canScroll;
    }

    protected String initTag() {
        return getClass().getSimpleName();
    }
}