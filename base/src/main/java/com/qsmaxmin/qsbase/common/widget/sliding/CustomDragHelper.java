package com.qsmaxmin.qsbase.common.widget.sliding;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper;

/**
 * @CreateBy qsmaxmin
 * @Date 2021/3/16 17:45
 * @Description
 */
class CustomDragHelper extends ViewDragHelper.Callback {
    private final ViewGroup       parentView;
    private final ViewDragHelper  dragHelper;
    private       boolean         isReleased;
    private       float           slidingRatio;
    private       SlidingListener listener;

    CustomDragHelper(ViewGroup parent) {
        this.parentView = parent;
        this.dragHelper = ViewDragHelper.create(parent, 1f, this);
    }

    void onTouchEvent(MotionEvent ev) {
        dragHelper.processTouchEvent(ev);
    }

    boolean shouldInterceptTouchEvent(MotionEvent ev) {
        return dragHelper.shouldInterceptTouchEvent(ev);
    }

    boolean continueSettling() {
        return dragHelper.continueSettling(true);
    }

    public void setSlidingListener(SlidingListener listener) {
        this.listener = listener;
    }

    @Override public int getViewHorizontalDragRange(@NonNull View child) {
        return getWidth();
    }

    @Override public boolean tryCaptureView(@NonNull View child, int pointerId) {
        isReleased = false;
        return true;
    }

    @Override public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
        return child.getTop();
    }

    @Override public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
        int newLeft;

        if (isLayoutRtlSupport()) {
            int startBound = getPaddingLeft() + lp.leftMargin;
            int endBound = getRange();
            newLeft = left < endBound ? endBound : (Math.min(left, startBound));
        } else {
            int startBound = getPaddingLeft() + lp.leftMargin;
            int endBound = getRange();
            newLeft = Math.min(Math.max(left, startBound), endBound);
        }
        return newLeft;
    }

    @Override public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
        if (getChildCount() > 1) {
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = getChildAt(i);
                if (childAt != changedView) {
                    childAt.offsetLeftAndRight(dx);
                }
            }
        }
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) changedView.getLayoutParams();
        int range = getRange();
        slidingRatio = (left - getPaddingLeft() - lp.leftMargin) / (float) range;
        if (listener != null) listener.onSliding(slidingRatio);
        if (isReleased) {
            if (left == getPaddingLeft() + lp.leftMargin) {
                if (listener != null) listener.onClose();
            } else if (left == getPaddingLeft() + lp.leftMargin + range) {
                if (listener != null) listener.onOpen();
            }
        }
    }

    @Override public void onViewReleased(@NonNull View releasedChild, float xVel, float yVel) {
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) releasedChild.getLayoutParams();
        int left = getPaddingLeft() + lp.leftMargin;
        if (isLayoutRtlSupport()) {
            if (xVel < 0.0F || (xVel == 0.0F && slidingRatio > 0.5F)) {
                left += getRange();
            }
        } else {
            if (xVel > 0.0F || (xVel == 0.0F && slidingRatio > 0.5F)) {
                left += getRange();
            }
        }
        isReleased = true;
        dragHelper.settleCapturedViewAt(left, releasedChild.getTop());
        parentView.invalidate();
    }

    private int getWidth() {
        return parentView.getWidth();
    }

    private int getRange() {
        return isLayoutRtlSupport() ? -getWidth() : getWidth();
    }

    private boolean isLayoutRtlSupport() {
        return ViewCompat.getLayoutDirection(parentView) == ViewCompat.LAYOUT_DIRECTION_RTL;
    }

    private int getPaddingLeft() {
        return parentView.getPaddingLeft();
    }

    private View getChildAt(int index) {
        return parentView.getChildAt(index);
    }

    private int getChildCount() {
        return parentView.getChildCount();
    }
}
