package com.qsmaxmin.qsbase.common.widget.sliding;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

/**
 * @CreateBy qsmaxmin
 * @Date 2021/3/15 17:53
 * @Description
 */
public class SlidingFrameLayout extends FrameLayout implements ISlidingViewGroup {
    private boolean          canSliding;
    private ColorDrawable    bgDrawable;
    private CustomDragHelper dragHelper;
    private SlidingListener  slidingListener;

    public SlidingFrameLayout(@NonNull Context context) {
        super(context);
        init();
    }

    public SlidingFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SlidingFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        canSliding = true;
        bgDrawable = new ColorDrawable(0x88000000);
        setBackgroundDrawable(bgDrawable);

        dragHelper = new CustomDragHelper(this);
        dragHelper.setSlidingListener(new SlidingListener() {
            @Override public void onOpen() {
                if (slidingListener != null) slidingListener.onOpen();
            }

            @Override public void onClose() {
                if (slidingListener != null) slidingListener.onClose();
            }

            @Override public void onSliding(float ratio) {
                int alpha = (int) ((1f - ratio) * 255);
                bgDrawable.setAlpha(alpha);
                if (slidingListener != null) slidingListener.onSliding(ratio);
            }
        });
    }

    @Override public void setCanSliding(boolean canSliding) {
        this.canSliding = canSliding;
    }

    @Override public boolean isCanSliding() {
        return canSliding;
    }

    @Override public void setSlidingListener(SlidingListener listener) {
        this.slidingListener = listener;
    }

    @Override public boolean onInterceptTouchEvent(MotionEvent ev) {
        return canSliding ? dragHelper.shouldInterceptTouchEvent(ev) : super.onInterceptTouchEvent(ev);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override public boolean onTouchEvent(MotionEvent ev) {
        if (canSliding) {
            dragHelper.onTouchEvent(ev);
            return true;
        } else {
            return super.onTouchEvent(ev);
        }
    }

    @Override public void computeScroll() {
        if (dragHelper.continueSettling()) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }
}
