package com.qsmaxmin.qsbase.common.widget.sliding;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

/**
 * @CreateBy qsmaxmin
 * @Date 2021/3/15 17:53
 * @Description
 */
public class SlidingLinearLayout extends LinearLayout implements ISlidingViewGroup {
    private CustomDragHelper drag;

    public SlidingLinearLayout(@NonNull Context context) {
        super(context);
        init();
    }

    public SlidingLinearLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SlidingLinearLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        drag = new CustomDragHelper(this);
    }

    @Override public void setCanSliding(boolean canSliding) {
        drag.setCanSliding(canSliding);
    }

    @Override public boolean isCanSliding() {
        return drag.isCanSliding();
    }

    @Override public void setSlidingListener(SlidingListener listener) {
        this.drag.setSlidingListener(listener);
    }

    @Override public boolean onInterceptTouchEvent(MotionEvent ev) {
        return isCanSliding() ? drag.shouldInterceptTouchEvent(ev) : super.onInterceptTouchEvent(ev);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override public boolean onTouchEvent(MotionEvent ev) {
        if (isCanSliding()) {
            drag.onTouchEvent(ev);
            return true;
        } else {
            return super.onTouchEvent(ev);
        }
    }

    @Override public void computeScroll() {
        if (drag.continueSettling()) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (!isCanSliding() || drag.onViewLayout()) {
            super.onLayout(changed, l, t, r, b);
        }
    }
}
