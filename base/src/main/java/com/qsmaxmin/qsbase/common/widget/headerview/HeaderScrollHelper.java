package com.qsmaxmin.qsbase.common.widget.headerview;

import android.content.Context;
import android.os.Build;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.ScrollView;

import androidx.recyclerview.widget.RecyclerView;


/**
 * @CreateBy qsmaxmin
 * @Date 2020/9/8 14:22
 * @Description header scroll helper
 */
class HeaderScrollHelper {
    private final int              touchSlop;
    private final int              maximumVelocity;
    private       ScrollerProvider scrollerProvider;
    private       VelocityTracker  velocityTracker;

    public HeaderScrollHelper(Context context) {
        ViewConfiguration configuration = ViewConfiguration.get(context);
        touchSlop = configuration.getScaledTouchSlop();
        maximumVelocity = configuration.getScaledMaximumFlingVelocity();
    }

    VelocityTracker obtainVelocityTracker(MotionEvent event) {
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        }
        velocityTracker.addMovement(event);
        return velocityTracker;
    }

    void recycleVelocityTracker() {
        if (velocityTracker != null) {
            velocityTracker.recycle();
            velocityTracker = null;
        }
    }

    boolean isScrollableViewInTop() {
        View scrollableView = getScrollableView();
        if (scrollableView == null) {
            return false;
        }
        return !scrollableView.canScrollVertically(-1);
    }

    void smoothScrollBy(int velocityY, int distance, int duration) {
        View scrollableView = getScrollableView();
        if (scrollableView instanceof AbsListView) {
            AbsListView absListView = (AbsListView) scrollableView;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                absListView.fling(velocityY);
            } else {
                absListView.smoothScrollBy(distance, duration);
            }
        } else if (scrollableView instanceof ScrollView) {
            ((ScrollView) scrollableView).fling(velocityY);
        } else if (scrollableView instanceof RecyclerView) {
            ((RecyclerView) scrollableView).fling(0, velocityY);
        } else if (scrollableView instanceof WebView) {
            ((WebView) scrollableView).flingScroll(0, velocityY);
        }
    }

    private View getScrollableView() {
        return scrollerProvider == null ? null : scrollerProvider.getScrollableView();
    }

    void registerScrollerProvider(ScrollerProvider provider) {
        this.scrollerProvider = provider;
    }

    float getTouchSlop() {
        return touchSlop;
    }

    float getMaximumVelocity() {
        return maximumVelocity;
    }
}
