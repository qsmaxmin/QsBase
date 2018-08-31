package com.qsmaxmin.qsbase.common.widget.viewpager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

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

    @Override public boolean onInterceptTouchEvent(MotionEvent arg0) {
        return canScroll && super.onInterceptTouchEvent(arg0);
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