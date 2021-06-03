package com.qsmaxmin.qsbase.common.widget.percentlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * @CreateBy qsmaxmin
 * @Date 15/6/30 10:49
 * @Description
 */
public class PercentLinearLayout extends LinearLayout {
    private final LayoutHelper mLayoutHelper = new LayoutHelper(this);

    public PercentLinearLayout(Context context) {
        super(context);
        init(null);
    }

    public PercentLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public PercentLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        mLayoutHelper.readAttributeSet(getContext(), attrs);
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

    @Override protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mLayoutHelper.restoreOriginalParams();
    }

    @Override public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    public static class LayoutParams extends LinearLayout.LayoutParams implements LayoutHelper.PercentLayoutParams {
        private LayoutInfo mLayoutInfo;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            mLayoutInfo = LayoutHelper.getPercentLayoutInfo(c, attrs);
        }

        @Override public LayoutInfo getPercentLayoutInfo() {
            return mLayoutInfo;
        }

        @Override protected void setBaseAttributes(TypedArray a, int widthAttr, int heightAttr) {
            LayoutHelper.fetchWidthAndHeight(this, a, widthAttr, heightAttr);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

    }

}
