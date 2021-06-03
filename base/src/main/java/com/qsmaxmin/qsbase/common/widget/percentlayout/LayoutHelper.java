package com.qsmaxmin.qsbase.common.widget.percentlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.qsmaxmin.qsbase.R;


/**
 * Helper for layouts that want to support percentage based dimensions.
 * <p/>
 * This class collects utility methods that are involved in extracting percentage based dimension
 * attributes and applying them to ViewGroup's children. If you would like to implement a layout
 * that supports percentage based dimensions, you need to take several steps:
 */
class LayoutHelper {
    private final ViewGroup mHost;
    private       float     dimensionRatio;

    LayoutHelper(ViewGroup host) {
        mHost = host;
    }

    void readAttributeSet(Context context, AttributeSet attrs) {
        if (attrs == null) return;
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.PercentLayout);
        String dimensionRatio = array.getString(R.styleable.PercentLayout_percentDimensionRatio);
        if (!TextUtils.isEmpty(dimensionRatio)) {
            int index = dimensionRatio.indexOf(':');
            if (index == -1) {
                throw new IllegalArgumentException("the value of percentDimensionRatio=" + dimensionRatio + " invalid!, such as: '50:40'");
            }
            try {
                String horValueStr = dimensionRatio.substring(0, index);
                String verValueStr = dimensionRatio.substring(index + 1);
                this.dimensionRatio = Float.parseFloat(horValueStr) / Float.parseFloat(verValueStr);
            } catch (Exception e) {
                throw new IllegalArgumentException("the value of percentDimensionRatio=" + dimensionRatio + " invalid!, such as: '50:40'");
            }
        }
        array.recycle();
    }

    /**
     * Constructs a PercentLayoutInfo from attributes associated with a View. Call this method from
     * {@code LayoutParams(Context c, AttributeSet attrs)} constructor.
     */
    static LayoutInfo getPercentLayoutInfo(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.PercentLayout_Layout);
        String widthPercentStr = array.getString(R.styleable.PercentLayout_Layout_layout_widthPercent);
        String heightPercentStr = array.getString(R.styleable.PercentLayout_Layout_layout_heightPercent);
        array.recycle();
        if (!TextUtils.isEmpty(widthPercentStr) || !TextUtils.isEmpty(heightPercentStr)) {
            return new LayoutInfo(widthPercentStr, heightPercentStr);
        }
        return null;
    }

    static void fetchWidthAndHeight(ViewGroup.LayoutParams params, TypedArray array, int widthAttr, int heightAttr) {
        params.width = array.getLayoutDimension(widthAttr, 0);
        params.height = array.getLayoutDimension(heightAttr, 0);
    }

    int[] adjustChildren(int widthMeasureSpec, int heightMeasureSpec) {
        int widthHint = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightHint = View.MeasureSpec.getSize(heightMeasureSpec);
        int[] result = null;

        if (dimensionRatio > 0) {
            int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
            int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
            if (widthMode == View.MeasureSpec.EXACTLY && (heightMode != View.MeasureSpec.EXACTLY || heightHint == 0)) {
                heightHint = (int) (widthHint / dimensionRatio);
                result = new int[]{widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(heightHint, View.MeasureSpec.EXACTLY)};
            } else if (heightMode == View.MeasureSpec.EXACTLY && (widthMode != View.MeasureSpec.EXACTLY || widthHint == 0)) {
                widthHint = (int) (heightHint * dimensionRatio);
                result = new int[]{View.MeasureSpec.makeMeasureSpec(widthHint, View.MeasureSpec.EXACTLY), heightMeasureSpec};
            }
        }

        for (int i = 0, N = mHost.getChildCount(); i < N; i++) {
            View view = mHost.getChildAt(i);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            if (params instanceof PercentLayoutParams) {
                LayoutInfo info = ((PercentLayoutParams) params).getPercentLayoutInfo();
                if (info != null) {
                    info.fillLayoutParams(mHost.getContext(), params, widthHint, heightHint);
                }
            }
        }
        return result;
    }

    void restoreOriginalParams() {
        for (int i = 0, N = mHost.getChildCount(); i < N; i++) {
            View view = mHost.getChildAt(i);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            if (params instanceof PercentLayoutParams) {
                LayoutInfo info = ((PercentLayoutParams) params).getPercentLayoutInfo();
                if (info != null) {
                    info.restoreLayoutParams(params);
                }
            }
        }
    }

    boolean handleMeasuredStateTooSmall() {
        boolean needsSecondMeasure = false;
        for (int i = 0, size = mHost.getChildCount(); i < size; i++) {
            View view = mHost.getChildAt(i);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            if (params instanceof PercentLayoutParams) {
                LayoutInfo info = ((PercentLayoutParams) params).getPercentLayoutInfo();
                if (info != null) {
                    if (shouldHandleMeasuredWidthTooSmall(view, info)) {
                        needsSecondMeasure = true;
                        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                    }
                    if (shouldHandleMeasuredHeightTooSmall(view, info)) {
                        needsSecondMeasure = true;
                        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    }
                }
            }
        }
        return needsSecondMeasure;
    }

    private boolean shouldHandleMeasuredWidthTooSmall(View view, LayoutInfo info) {
        if (info == null || info.widthPercent == -1) {
            return false;
        }
        int state = view.getMeasuredWidthAndState() & View.MEASURED_STATE_MASK;
        return state == View.MEASURED_STATE_TOO_SMALL
                && info.widthPercent >= 0
                && info.preservedParamsWidth == ViewGroup.LayoutParams.WRAP_CONTENT;
    }

    private boolean shouldHandleMeasuredHeightTooSmall(View view, LayoutInfo info) {
        if (info == null || info.heightPercent == -1) {
            return false;
        }
        int state = view.getMeasuredHeightAndState() & View.MEASURED_STATE_MASK;
        return state == View.MEASURED_STATE_TOO_SMALL
                && info.heightPercent >= 0
                && info.preservedParamsHeight == ViewGroup.LayoutParams.WRAP_CONTENT;
    }

    public interface PercentLayoutParams {
        LayoutInfo getPercentLayoutInfo();
    }
}
