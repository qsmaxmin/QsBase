package com.qsmaxmin.qsbase.common.widget.viewpager.autoscroll;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;

import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.widget.viewpager.QsViewPager;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @CreateBy qsmaxmin
 * @Date 2017-7-6 12:37:16
 * @Description 自动轮播，支持无限轮播
 */
public final class AutoScrollViewPager extends QsViewPager {
    public static final int                    DEFAULT_INTERVAL            = 3000;
    public static final int                    LEFT                        = 0;
    public static final int                    RIGHT                       = 1;
    public static final int                    SLIDE_BORDER_MODE_NONE      = 0;
    public static final int                    SLIDE_BORDER_MODE_CYCLE     = 1;
    public static final int                    SLIDE_BORDER_MODE_TO_PARENT = 2;
    private             long                   interval                    = DEFAULT_INTERVAL;
    private             int                    direction                   = RIGHT;
    private             boolean                isCycle                     = true;
    private             boolean                stopScrollWhenTouch         = true;
    private             int                    slideBorderMode             = SLIDE_BORDER_MODE_NONE;
    private             double                 autoScrollFactor            = 1.0;
    private             double                 swipeScrollFactor           = 1.0;
    private             boolean                isAutoScroll                = false;
    private             boolean                isStopByTouch               = false;
    private             float                  downX                       = 0f;
    private             CustomDurationScroller scroller                    = null;
    public static final int                    SCROLL_WHAT                 = 0;
    private             float                  mFactor                     = 1.0f;
    private MyHandler handler;
    private Field     mIsBeingDraggedField;
    private Method    setScrollStateMethod;
    private float     distanceX;
    private float     distanceY;
    private float     lastX;
    private float     lastY;

    public AutoScrollViewPager(Context paramContext) {
        this(paramContext, null);
    }

    public AutoScrollViewPager(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        init();
    }

    private void init() {
        handler = new MyHandler(this);
        setViewPagerScroller();
    }

    @Override public void setAdapter(PagerAdapter adapter) {
        if (!(adapter instanceof InfinitePagerAdapter)) {
            throw new RuntimeException("setAdapter(..) this adapter must be instance of InfinitePagerAdapter");
        }
        super.setAdapter(adapter);
    }

    @Override public InfinitePagerAdapter getAdapter() {
        return (InfinitePagerAdapter) super.getAdapter();
    }

    /**
     * 计算位置
     */
    @Override public void setCurrentItem(int item) {
        if (getAdapter() == null) return;
        if (item < 0) item = 0;
        int realCount = getAdapter().getRealCount();
        if (getAdapter().isEnableInfinite()) {
            int halfCount = getAdapter().getCount() / 2;
            super.setCurrentItem(halfCount - halfCount % realCount + item);
        } else {
            super.setCurrentItem(item >= realCount ? realCount - 1 : realCount);
        }
    }

    @Override public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item, smoothScroll);
    }

    @Override protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (getAdapter() == null) return;
        if (visibility == View.VISIBLE && isAutoScroll) {
            L.i(initTag(), "onWindowVisibilityChanged............view VISIBLE, start auto scroll !");
            sendScrollMessage((int) (interval + scroller.getDuration() / autoScrollFactor * swipeScrollFactor));
        } else {
            L.i(initTag(), "onWindowVisibilityChanged............stop auto scroll, isAutoScroll:" + isAutoScroll + "  view visibility:" + visibility);
            handler.removeMessages(SCROLL_WHAT);
        }
    }

    private void sendScrollMessage(long delay) {
        handler.removeMessages(SCROLL_WHAT);
        handler.sendEmptyMessageDelayed(SCROLL_WHAT, delay);
    }

    private void setViewPagerScroller() {
        try {
            Field interpolatorField = ViewPager.class.getDeclaredField("sInterpolator");
            interpolatorField.setAccessible(true);
            scroller = new CustomDurationScroller(getContext(), (Interpolator) interpolatorField.get(null));

            Field scrollerField = ViewPager.class.getDeclaredField("mScroller");
            scrollerField.setAccessible(true);
            scrollerField.set(this, scroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void scrollOnce() {
        PagerAdapter adapter = getAdapter();
        if (adapter == null) return;
        int currentItem = getCurrentItem();
        int totalCount = adapter.getCount();
        if (totalCount <= 1) return;
        int nextItem = (direction == LEFT) ? --currentItem : ++currentItem;
        if (nextItem < 0) {
            if (isCycle) {
                setCurrentItem(totalCount - 1, false);
            }
        } else if (nextItem == totalCount) {
            if (isCycle) {
                setCurrentItem(0, false);
            }
        } else {
            setCurrentItem(nextItem, true);
        }
    }

    /**
     * <ul>
     * if stopScrollWhenTouch is true
     * <li>if event is down, stop auto scroll.</li>
     * <li>if event is up, start auto scroll again.</li>
     */
    @Override public boolean dispatchTouchEvent(MotionEvent ev) {
        if (stopScrollWhenTouch) {
            if ((ev.getAction() == MotionEvent.ACTION_DOWN) && isAutoScroll) {
                isStopByTouch = true;
                stopAutoScroll();
            } else if ((ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL) && isStopByTouch) {
                startAutoScroll();
            }
        }
        if (dispatchTouchEventByScrollMode(ev)) return super.dispatchTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    private boolean dispatchTouchEventByScrollMode(MotionEvent ev) {
        if (slideBorderMode == SLIDE_BORDER_MODE_TO_PARENT || slideBorderMode == SLIDE_BORDER_MODE_CYCLE) {
            float touchX = ev.getX();
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                downX = touchX;
            }
            int currentItem = getCurrentItem();
            PagerAdapter adapter = getAdapter();
            int pageCount = adapter == null ? 0 : adapter.getCount();
            if ((currentItem == 0 && downX <= touchX) || (currentItem == pageCount - 1 && downX >= touchX)) {
                if (slideBorderMode == SLIDE_BORDER_MODE_TO_PARENT) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                } else {
                    if (pageCount > 1) {
                        setCurrentItem(pageCount - currentItem - 1, true);
                    }
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                return true;
            }
        }
        return false;
    }

    @Override public boolean onInterceptTouchEvent(MotionEvent arg0) {
        return isCanScroll() && parseInterceptTouchEvent(arg0);
    }

    private boolean parseInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                distanceX = 0f;
                distanceY = 0f;
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
                if (distanceX * mFactor > distanceY) {
                    if (getParent() != null) getParent().requestDisallowInterceptTouchEvent(true);
                    setBeingDragged();
                    L.i(initTag(), "parseInterceptTouchEvent.........setBeingDragged");
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                distanceX = 0f;
                distanceY = 0f;
                lastX = 0f;
                lastY = 0f;
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    /**
     * 反射使view处于被drag状态
     */
    private void setBeingDragged() {
        try {
            if (mIsBeingDraggedField == null || setScrollStateMethod == null) {
                mIsBeingDraggedField = ViewPager.class.getDeclaredField("mIsBeingDragged");
                if (mIsBeingDraggedField != null) mIsBeingDraggedField.setAccessible(true);

                setScrollStateMethod = ViewPager.class.getDeclaredMethod("setScrollState", int.class);
                if (setScrollStateMethod != null) setScrollStateMethod.setAccessible(true);
                L.i(initTag(), "setBeingDragged based on reflex to get field and method......");
            }
            if (mIsBeingDraggedField != null) mIsBeingDraggedField.set(this, true);
            if (setScrollStateMethod != null) setScrollStateMethod.invoke(this, SCROLL_STATE_DRAGGING);

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class MyHandler extends Handler {
        private WeakReference<AutoScrollViewPager> reference;

        MyHandler(AutoScrollViewPager viewPager) {
            reference = new WeakReference<>(viewPager);
        }

        @Override public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SCROLL_WHAT:
                    AutoScrollViewPager viewPager = reference.get();
                    if (viewPager == null) return;
                    boolean shown = viewPager.isShown();
                    if (shown) {
                        viewPager.scroller.setScrollDurationFactor(viewPager.autoScrollFactor);
                        viewPager.scrollOnce();
                        viewPager.scroller.setScrollDurationFactor(viewPager.swipeScrollFactor);
                    } else {
                        L.i("AutoScrollViewPager", "can not visible, not scroll....");
                    }
                    viewPager.sendScrollMessage(viewPager.interval + viewPager.scroller.getDuration());
                default:
                    break;
            }
        }
    }

    public long getInterval() {
        return interval;
    }

    /**
     * set auto scroll time in milliseconds, default is
     */
    public void setInterval(long interval) {
        this.interval = interval;
    }

    public int getDirection() {
        return (direction == LEFT) ? LEFT : RIGHT;
    }

    /**
     * set auto scroll direction
     *
     * @param direction {@link #LEFT} or {@link #RIGHT}, default is {@link #RIGHT}
     */
    public void setDirection(int direction) {
        this.direction = direction;
    }

    /**
     * whether automatic cycle when auto scroll reaching the last or first item,
     * default is true
     *
     * @return the isCycle
     */
    public boolean isCycle() {
        return isCycle;
    }

    /**
     * set whether automatic cycle when auto scroll reaching the last or first
     * item, default is true
     *
     * @param isCycle the isCycle to set
     */
    public void setCycle(boolean isCycle) {
        this.isCycle = isCycle;
    }

    /**
     * whether stop auto scroll when touching, default is true
     *
     * @return the stopScrollWhenTouch
     */
    public boolean isStopScrollWhenTouch() {
        return stopScrollWhenTouch;
    }

    /**
     * set whether stop auto scroll when touching, default is true
     */
    public void setStopScrollWhenTouch(boolean stopScrollWhenTouch) {
        this.stopScrollWhenTouch = stopScrollWhenTouch;
    }

    /**
     * get how to process when sliding at the last or first item
     *
     * @return the slideBorderMode {@link #SLIDE_BORDER_MODE_NONE},
     * {@link #SLIDE_BORDER_MODE_TO_PARENT},
     * {@link #SLIDE_BORDER_MODE_CYCLE}, default is
     * {@link #SLIDE_BORDER_MODE_NONE}
     */
    public int getSlideBorderMode() {
        return slideBorderMode;
    }

    /**
     * set how to process when sliding at the last or first item
     *
     * @param slideBorderMode {@link #SLIDE_BORDER_MODE_NONE},
     *                        {@link #SLIDE_BORDER_MODE_TO_PARENT},
     *                        {@link #SLIDE_BORDER_MODE_CYCLE}, default is
     *                        {@link #SLIDE_BORDER_MODE_NONE}
     */
    public void setSlideBorderMode(int slideBorderMode) {
        this.slideBorderMode = slideBorderMode;
    }

    public void setScrollerInterpolator(Interpolator interpolator) {
        try {
            Field mInterpolator = scroller.getClass().getSuperclass().getDeclaredField("mInterpolator");
            mInterpolator.setAccessible(true);
            mInterpolator.set(scroller, interpolator);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设定因子的滑动动画持续时间会改变
     */
    public void setSwipeScrollDurationFactor(double scrollFactor) {
        swipeScrollFactor = scrollFactor;
    }

    /**
     * 设定因子的滑动动画持续时间会改变 在自动滚动
     */
    public void setAutoScrollDurationFactor(double scrollFactor) {
        autoScrollFactor = scrollFactor;
    }

    /**
     * 执行滚动
     */
    public void startAutoScroll() {
        isAutoScroll = true;
        sendScrollMessage((int) (interval + scroller.getDuration() / autoScrollFactor * swipeScrollFactor));
    }

    /**
     * 停止滚动
     */
    public void stopAutoScroll() {
        isAutoScroll = false;
        handler.removeMessages(SCROLL_WHAT);
    }

    public void setTouchDirectionFactor(float factor) {
        this.mFactor = factor;
    }
}
