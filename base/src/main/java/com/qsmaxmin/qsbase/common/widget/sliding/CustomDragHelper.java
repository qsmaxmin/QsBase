package com.qsmaxmin.qsbase.common.widget.sliding;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper;

/**
 * @CreateBy qsmaxmin
 * @Date 2021/3/16 17:45
 * @Description
 */
public final class CustomDragHelper extends ViewDragHelper.Callback {
    private final ViewGroup       parentView;
    private final ViewDragHelper  dragHelper;
    private final Drawable        bgDrawable;
    private       SlidingListener listener;
    private       boolean         canSliding;
    private       boolean         isDragIng;
    private       float           slidingRatio;
    private       float           downY;
    private       float           downX;

    public CustomDragHelper(ViewGroup parent) {
        this.parentView = parent;
        this.dragHelper = ViewDragHelper.create(parent, 1f, this);
        this.bgDrawable = new ColorDrawable(0x88000000);
        this.canSliding = true;
        parent.setBackgroundDrawable(bgDrawable);
    }

    public final boolean onTouchEvent(MotionEvent ev) {
        if (canSliding) {
            dragHelper.processTouchEvent(ev);
        }
        return canSliding;
    }

    public final boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!canSliding) {
            return false;
        }
        int action = ev.getAction();
        if (action == MotionEvent.ACTION_MOVE) {
            float x = ev.getX();
            float y = ev.getY();
            return Math.abs(x - downX) > Math.abs(y - downY) && dragHelper.shouldInterceptTouchEvent(ev);
        } else if (action == MotionEvent.ACTION_DOWN) {
            downX = ev.getX();
            downY = ev.getY();
            return dragHelper.shouldInterceptTouchEvent(ev);
        }
        return dragHelper.shouldInterceptTouchEvent(ev);
    }

    public final void computeScroll() {
        if (dragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(parentView);
        }
    }

    public final boolean onViewLayout() {
        if (canSliding && isDragIng) {
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = getChildAt(i);
                if (childAt != null && childAt.getVisibility() != View.GONE) {
                    childAt.layout(childAt.getLeft(), childAt.getTop(), childAt.getRight(), childAt.getBottom());
                }
            }
            return false;
        }
        return true;
    }

    public final void setCanSliding(boolean canSliding) {
        this.canSliding = canSliding;
        if (!canSliding) {
            dragHelper.cancel();
        }
    }

    public final boolean isCanSliding() {
        return canSliding;
    }

    public final void setSlidingListener(SlidingListener listener) {
        this.listener = listener;
    }

    public final boolean isDragIng() {
        return isDragIng;
    }

    @Override public int getViewHorizontalDragRange(@NonNull View child) {
        return getWidth();
    }

    @Override public boolean tryCaptureView(@NonNull View child, int pointerId) {
        isDragIng = true;
        return true;
    }

    @Override public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
        return child.getTop();
    }

    @Override public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
        int leftMargin = getViewLeftMargin(child);
        int newLeft;
        if (isLayoutRtlSupport()) {
            int startBound = getPaddingLeft() + leftMargin;
            int endBound = getRange();
            newLeft = left < endBound ? endBound : (Math.min(left, startBound));
        } else {
            int startBound = getPaddingLeft() + leftMargin;
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
        int leftMargin = getViewLeftMargin(changedView);
        int range = getRange();
        slidingRatio = (left - getPaddingLeft() - leftMargin) / (float) range;
        callbackSliding(slidingRatio);
        if (!isDragIng) {
            if (left == getPaddingLeft() + leftMargin) {
                callbackClose();
            } else if (left == getPaddingLeft() + leftMargin + range) {
                callbackOpen();
            }
        }
    }

    @Override public void onViewReleased(@NonNull View releasedChild, float xVel, float yVel) {
        int left = getPaddingLeft() + getViewLeftMargin(releasedChild);
        if (isLayoutRtlSupport()) {
            if ((xVel < 0.0F && -xVel > Math.abs(yVel)) || (xVel == 0.0F && slidingRatio > 0.5F)) {
                left += getRange();
            }
        } else {
            if ((xVel > 0.0F && xVel > Math.abs(yVel)) || (xVel == 0.0F && slidingRatio > 0.5F)) {
                left += getRange();
            }
        }
        isDragIng = false;
        if (dragHelper.settleCapturedViewAt(left, releasedChild.getTop())) {
            parentView.invalidate();
        } else {
            if (releasedChild.getLeft() == left) {
                callbackClose();
            } else {
                callbackOpen();
            }
        }
    }

    private void callbackSliding(float ratio) {
        int alpha = (int) ((1f - ratio) * 255);
        bgDrawable.setAlpha(alpha);
        if (listener != null) listener.onSliding(ratio);
    }

    private void callbackOpen() {
        if (listener != null) listener.onOpen();
    }

    private void callbackClose() {
        if (listener != null) listener.onClose();
    }

    private int getViewLeftMargin(@NonNull View child) {
        ViewGroup.LayoutParams lp = child.getLayoutParams();
        if (lp instanceof ViewGroup.MarginLayoutParams) {
            return ((ViewGroup.MarginLayoutParams) lp).leftMargin;
        }
        return 0;
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
