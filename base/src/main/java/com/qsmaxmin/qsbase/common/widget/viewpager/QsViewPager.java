package com.qsmaxmin.qsbase.common.widget.viewpager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * @CreateBy QS
 * @Date 16/11/20  下午7:21
 * @Description 自定义viewpager
 */
public class QsViewPager extends ViewPager {

    public static final String TAG = "J2WViewPager";

    public  boolean                  canScroll = true;
    private HashMap<Integer, Object> objects   = new LinkedHashMap<Integer, Object>();

    public boolean shouldAnimate;//是否滑动时执行动画


    public QsViewPager(Context context) {
        this(context, null);
    }

    public QsViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    protected void animateFade(View left, View right, float positionOffset) {
        if (left != null) {
            left.setAlpha(1 - positionOffset);
        }
        if (right != null) {
            right.setAlpha(positionOffset);
        }
    }

    @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        View mLeft = findViewFromObject(position);
        View mRight = findViewFromObject(position + 1);
        if (shouldAnimate) {
            animateFade(mLeft, mRight, positionOffset);
        }
        super.onPageScrolled(position, positionOffset, positionOffsetPixels);
    }

    public void setObjectForPosition(Object obj, int position) {
        objects.put(Integer.valueOf(position), obj);
    }

    public View findViewFromObject(int position) {
        Object o = objects.get(position);
        if (o == null) {
            return null;
        }
        PagerAdapter a = getAdapter();
        View v;
        for (int i = 0; i < getChildCount(); i++) {
            v = getChildAt(i);
            if (a.isViewFromObject(v, o)) return v;
        }
        return null;
    }

    @Override public boolean onTouchEvent(MotionEvent arg0) {
        return canScroll && super.onTouchEvent(arg0);
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

    public boolean isShouldAnimate() {
        return shouldAnimate;
    }

    public void setShouldAnimate(boolean shouldAnimate) {
        this.shouldAnimate = shouldAnimate;
    }
}
