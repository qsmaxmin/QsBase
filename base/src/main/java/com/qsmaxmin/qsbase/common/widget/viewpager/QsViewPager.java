package com.qsmaxmin.qsbase.common.widget.viewpager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.utils.QsHelper;

import java.lang.reflect.Field;

/**
 * @CreateBy QS
 * @Date 16/11/20  下午7:21
 * @Description 自定义viewpager
 */
public class QsViewPager extends ViewPager {
    private boolean canScroll = true;
    private float   mFactor   = 1.0f;
    private Field mIsBeingDraggedField;
    private float distanceX;
    private float distanceY;
    private float lastX;
    private float lastY;


    public QsViewPager(Context context) {
        this(context, null);
    }

    public QsViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override public boolean onInterceptTouchEvent(MotionEvent arg0) {
        return canScroll && parseInterceptTouchEvent(arg0);
    }

    public void setCanScroll(boolean canScroll) {
        this.canScroll = canScroll;
    }

    public boolean isCanScroll() {
        return canScroll;
    }

    protected String initTag() {
        return QsHelper.getInstance().getApplication().isLogOpen() ? getClass().getSimpleName() : "QsViewPager";
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
                if (distanceX > distanceY * mFactor) {
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

    public void setTouchDirectionFactor(float factor) {
        this.mFactor = factor;
    }

    /**
     * 反射使view处于被drag状态
     */
    private void setBeingDragged() {
        try {
            if (mIsBeingDraggedField == null) {
                mIsBeingDraggedField = ViewPager.class.getDeclaredField("mIsBeingDragged");
                if (mIsBeingDraggedField != null) mIsBeingDraggedField.setAccessible(true);
            }
            if (mIsBeingDraggedField != null) mIsBeingDraggedField.set(this, true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}