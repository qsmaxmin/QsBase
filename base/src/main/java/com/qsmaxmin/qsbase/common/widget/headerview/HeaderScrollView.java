package com.qsmaxmin.qsbase.common.widget.headerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import android.widget.Scroller;

import com.qsmaxmin.qsbase.R;

/**
 * @CreateBy qsmaxmin
 * @Date 2020/9/8 14:20
 * @Description header scroll view
 */
public class HeaderScrollView extends LinearLayout {
    private static final int                  DIRECTION_UP   = 1;
    private static final int                  DIRECTION_DOWN = 2;
    private              int                  direction;
    private              Scroller             scroller;
    private              HeaderScrollHelper   helper;
    private              HeaderScrollListener headerScrollListener;
    private              int                  headHeight;
    private              int                  tabOffsetY;
    private              int                  contentHeight;
    private              int                  maxScrollY;
    private              int                  lastScrollerY;
    private              boolean              isTouchedHeader;
    private              float                dx;
    private              float                dy;
    private              float                lastY;
    private              boolean              verticalScrollFlag;
    private              SmoothScrollRunnable scrollRunnable;
    private              boolean              isSendDownEvent;

    public HeaderScrollView(Context context) {
        super(context);
        init(null);
    }

    public HeaderScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public HeaderScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        setOrientation(VERTICAL);
        scroller = new Scroller(getContext());
        helper = new HeaderScrollHelper(getContext());
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.HeaderScrollView);
            tabOffsetY = (int) typedArray.getDimension(R.styleable.HeaderScrollView_hsv_tabOffsetY, 0);
            typedArray.recycle();
        }
    }

    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (getChildCount() >= 2) {
            View headView = getChildAt(0);
            measureChildWithMargins(headView, widthMeasureSpec, 0, MeasureSpec.UNSPECIFIED, 0);
            LayoutParams lp = (LayoutParams) headView.getLayoutParams();
            headHeight = headView.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            contentHeight = MeasureSpec.getSize(heightMeasureSpec);
            maxScrollY = headHeight - tabOffsetY;
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(headHeight + contentHeight - tabOffsetY, MeasureSpec.EXACTLY));
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override protected int computeVerticalScrollRange() {
        return headHeight + contentHeight - tabOffsetY;
    }

    @Override protected int computeVerticalScrollExtent() {
        return contentHeight;
    }

    @Override public boolean dispatchTouchEvent(MotionEvent ev) {
        float currentX = ev.getX();
        float currentY = ev.getY();
        float shiftX = Math.abs(currentX - dx);
        float shiftY = Math.abs(currentY - dy);
        float deltaY;
        VelocityTracker velocityTracker = helper.obtainVelocityTracker(ev);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                verticalScrollFlag = false;
                dx = currentX;
                dy = currentY;
                lastY = currentY;
                checkIsTouchHeader((int) currentY);
                scroller.abortAnimation();
                isSendDownEvent = false;
                break;
            case MotionEvent.ACTION_MOVE:
                deltaY = lastY - currentY;
                lastY = currentY;
                if (shiftX > helper.getTouchSlop() && shiftX > shiftY) { //水平滑动
                    verticalScrollFlag = false;
                } else if (shiftY > helper.getTouchSlop() && shiftY > shiftX) {//垂直滑动
                    verticalScrollFlag = true;
                }
                boolean scrollableViewInTop = helper.isScrollableViewInTop();
                if (verticalScrollFlag && (!isInBottom() || scrollableViewInTop || isTouchedHeader)) {

                    if (isInBottom() && scrollableViewInTop) {
                        velocityTracker.computeCurrentVelocity(1000, helper.getMaximumVelocity());
                        float yVelocity = velocityTracker.getYVelocity();
                        //yVelocity < 0 mean scroll up
                        if (yVelocity < 0 && !isSendDownEvent) {
                            isSendDownEvent = true;
                            ev.setAction(MotionEvent.ACTION_DOWN);
                            super.dispatchTouchEvent(ev);
                            ev.setAction(MotionEvent.ACTION_MOVE);
                        }
                    }
                    scrollBy(0, (int) (deltaY + 0.5));
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (verticalScrollFlag) {
                    velocityTracker.computeCurrentVelocity(1000, helper.getMaximumVelocity());
                    float yVelocity = velocityTracker.getYVelocity();
                    direction = yVelocity > 0 ? DIRECTION_DOWN : DIRECTION_UP;
                    scroller.fling(0, getScrollY(), 0, -(int) yVelocity, 0, 0, -Integer.MAX_VALUE, Integer.MAX_VALUE);
                    lastScrollerY = getScrollY();
                    invalidate();
                    if ((shiftX > helper.getTouchSlop() || shiftY > helper.getTouchSlop())) {
                        if (isTouchedHeader || !isInBottom()) {
                            ev.setAction(MotionEvent.ACTION_CANCEL);
                            super.dispatchTouchEvent(ev);
                            ev.setAction(MotionEvent.ACTION_UP);
                        }
                    }
                }
                helper.recycleVelocityTracker();
                break;
            case MotionEvent.ACTION_CANCEL:
                helper.recycleVelocityTracker();
                break;
            default:
                break;
        }
        super.dispatchTouchEvent(ev);
        return true;
    }

    private void checkIsTouchHeader(float downY) {
        isTouchedHeader = (downY + getScrollY()) <= headHeight;
    }

    @Override public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            final int currY = scroller.getCurrY();
            if (direction == DIRECTION_UP) {
                if (isInBottom()) {
                    int distance = scroller.getFinalY() - currY;
                    int duration = calcDuration(scroller.getDuration(), scroller.timePassed());
                    helper.smoothScrollBy(getScrollerVelocity(), distance, duration);
                    scroller.abortAnimation();
                    return;
                } else {
                    scrollTo(0, currY);
                    invalidate();
                }
            } else {
                if (getScrollY() == 0 && !helper.isScrollableViewInTop() && !isTouchedHeader) {
                    int distance = scroller.getFinalY() - currY;
                    int duration = calcDuration(scroller.getDuration(), scroller.timePassed());
                    helper.smoothScrollBy(-getScrollerVelocity(), distance, duration);
                }
                if (helper.isScrollableViewInTop() || isTouchedHeader) {
                    int deltaY = (currY - lastScrollerY);
                    int toY = getScrollY() + deltaY;
                    scrollTo(0, toY);
                    if (getScrollY() <= 0) {
                        scroller.abortAnimation();
                        return;
                    }
                }
                invalidate();
            }
            lastScrollerY = currY;
        }
    }

    private int getScrollerVelocity() {
        return (int) scroller.getCurrVelocity();
    }

    @Override public void scrollBy(int x, int y) {
        int scrollY = getScrollY();
        int toY = scrollY + y;
        if (toY >= maxScrollY) {
            toY = maxScrollY;
        } else if (toY <= 0) {
            toY = 0;
        }
        y = toY - scrollY;
        super.scrollBy(x, y);
    }

    @Override public void scrollTo(int x, int y) {
        if (y > maxScrollY) {
            y = maxScrollY;
        } else if (y < 0) {
            y = 0;
        }
        super.scrollTo(x, y);
    }

    @Override protected void onScrollChanged(int l, int t, int oldL, int oldT) {
        super.onScrollChanged(l, t, oldL, oldT);
        if (headerScrollListener != null) {
            headerScrollListener.onHeaderScroll(t, maxScrollY);
        }
    }

    private int calcDuration(int duration, int timePass) {
        return duration - timePass;
    }

    public int getMaxScrollY() {
        return maxScrollY;
    }

    public boolean isInBottom() {
        return getScrollY() == maxScrollY;
    }

    public boolean isInTop() {
        return getScrollY() == 0;
    }

    public void registerScrollerProvider(ScrollerProvider provider) {
        helper.registerScrollerProvider(provider);
    }

    public void setOnScrollListener(HeaderScrollListener onScrollListener) {
        this.headerScrollListener = onScrollListener;
    }

    public void smoothScrollToTop() {
        if (scrollRunnable == null) scrollRunnable = new SmoothScrollRunnable();
        scrollRunnable.scrollToPos(0);
    }

    public void smoothScrollToBottom() {
        if (scrollRunnable == null) scrollRunnable = new SmoothScrollRunnable();
        scrollRunnable.scrollToPos(getMaxScrollY());
    }

    public void smoothScrollTo(int y) {
        if (scrollRunnable == null) scrollRunnable = new SmoothScrollRunnable();
        scrollRunnable.scrollToPos(y);
    }

    private class SmoothScrollRunnable implements Runnable {
        private final Interpolator interpolator;
        private       float        startY;
        private       float        endY;
        private       float        currentY;
        private       float        step;

        public SmoothScrollRunnable() {
            interpolator = new DecelerateInterpolator(2f);
        }

        @Override public void run() {
            if (shouldScroll()) {
                currentY += step;
                float ratio = interpolator.getInterpolation((currentY - startY) / (endY - startY));
                scrollTo(0, (int) (startY + (endY - startY) * ratio));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    postOnAnimation(this);
                } else {
                    postDelayed(this, 16L);
                }
            }
        }

        public void scrollToPos(int y) {
            if (y < 0) {
                y = 0;
            } else if (y > getMaxScrollY()) {
                y = getMaxScrollY();
            }
            startY = getScrollY();
            endY = y;
            currentY = startY;
            step = (endY - startY) / 30f;
            if (shouldScroll()) {
                removeCallbacks(this);
                post(this);
            }
        }

        private boolean shouldScroll() {
            return Math.abs(endY - currentY) >= 1f;
        }
    }
}