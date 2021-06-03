package com.qsmaxmin.qsbase.common.widget.percentlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Subclass of {@link FrameLayout} that supports percentage based dimensions and
 * {@code layout_widthPercent}
 * {@code layout_heightPercent}
 * It is not necessary to specify {@code layout_width/height} if you specify {@code
 * layout_widthPercent.} However, if you want the view to be able to take up more space than what
 * percentage value permits, you can add {@code layout_width/height="wrap_content"}. In that case
 * if the percentage size is too small for the View's content, it will be resized using
 * {@code wrap_content} rule.
 */
public class PercentFrameLayout extends FrameLayout {
    private final LayoutHelper mLayoutHelper = new LayoutHelper(this);

    public PercentFrameLayout(Context context) {
        super(context);
        init(null);
    }

    public PercentFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public PercentFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        mLayoutHelper.readAttributeSet(getContext(), attrs);
    }

    @Override public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int[] sizes = mLayoutHelper.adjustChildren(widthMeasureSpec, heightMeasureSpec);
        if (sizes != null) {
            widthMeasureSpec = sizes[0];
            heightMeasureSpec = sizes[1];
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mLayoutHelper.handleMeasuredStateTooSmall()) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mLayoutHelper.restoreOriginalParams();
    }

    public static class LayoutParams extends FrameLayout.LayoutParams implements LayoutHelper.PercentLayoutParams {
        private LayoutInfo mLayoutInfo;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            mLayoutInfo = LayoutHelper.getPercentLayoutInfo(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(int width, int height, int gravity) {
            super(width, height, gravity);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(FrameLayout.LayoutParams source) {
            super((MarginLayoutParams) source);
            gravity = source.gravity;
        }

        public LayoutParams(LayoutParams source) {
            this((FrameLayout.LayoutParams) source);
            mLayoutInfo = source.mLayoutInfo;
        }

        @Override public LayoutInfo getPercentLayoutInfo() {
            return mLayoutInfo;
        }

        @Override protected void setBaseAttributes(TypedArray a, int widthAttr, int heightAttr) {
            LayoutHelper.fetchWidthAndHeight(this, a, widthAttr, heightAttr);
        }
    }
}
