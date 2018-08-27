package com.qsmaxmin.qsbase.common.widget.viewpager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.qsmaxmin.qsbase.common.log.L;

/**
 * @CreateBy QS
 * @Date 16/11/20  下午7:21
 * @Description 自定义viewpager
 */
public class QsViewPager extends ViewPager {
    public boolean canScroll = true;
    float distanceX;
    float distanceY;
    float lastX;
    float lastY;

    public QsViewPager(Context context) {
        this(context, null);
    }

    public QsViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override public boolean onTouchEvent(MotionEvent arg0) {
        return canScroll && super.onTouchEvent(arg0);
    }

    @Override public boolean onInterceptTouchEvent(MotionEvent arg0) {
        return canScroll && parseTouchEvent(arg0);
    }

    public void setCanScroll(boolean canScroll) {
        this.canScroll = canScroll;
    }

    public boolean isCanScroll() {
        return canScroll;
    }

    public boolean parseTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                distanceX = distanceY = 0f;
                lastX = ev.getX();
                lastY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float curX = ev.getX();
                final float curY = ev.getY();
                distanceX += Math.abs(curX - lastX);
                distanceY += Math.abs(curY - lastY);
                lastX = curX;
                lastY = curY;
                L.i("QsViewPager", "parseTouchEvent.........distanceX:" + distanceX + "  distanceY:" + distanceY + ", so " + (distanceX > distanceY ? "" : "not") + " intercept touch event");
                if (distanceX > distanceY) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    return true;
                }
        }
        return super.onInterceptTouchEvent(ev);
    }
}
