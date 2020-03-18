/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2019-2019. All rights reserved.
 */
package com.qsmaxmin.qsbase.common.widget.percentlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;


import com.qsmaxmin.qsbase.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Helper for layouts that want to support percentage based dimensions.
 * <p/>
 * This class collects utility methods that are involved in extracting percentage based dimension
 * attributes and applying them to ViewGroup's children. If you would like to implement a layout
 * that supports percentage based dimensions, you need to take several steps:
 */
class PercentLayout {
    private static int       mWidthScreen;
    private static int       mHeightScreen;
    private final  ViewGroup mHost;

    PercentLayout(ViewGroup host) {
        mHost = host;
        getScreenSize();
    }

    private void getScreenSize() {
        WindowManager wm = (WindowManager) mHost.getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        mWidthScreen = outMetrics.widthPixels;
        mHeightScreen = outMetrics.heightPixels;
    }


    /**
     * that reads layout_width and layout_height attribute values without throwing an exception if
     * they aren't present.
     */
    static void fetchWidthAndHeight(ViewGroup.LayoutParams params, TypedArray array, int widthAttr, int heightAttr) {
        params.width = array.getLayoutDimension(widthAttr, 0);
        params.height = array.getLayoutDimension(heightAttr, 0);
    }

    /**
     * Iterates over children and changes their width and height to one calculated from percentage
     * values.
     *
     * @param widthMeasureSpec  Width MeasureSpec of the parent ViewGroup.
     * @param heightMeasureSpec Height MeasureSpec of the parent ViewGroup.
     */
    void adjustChildren(int widthMeasureSpec, int heightMeasureSpec) {
        int widthHint = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightHint = View.MeasureSpec.getSize(heightMeasureSpec);

        for (int i = 0, N = mHost.getChildCount(); i < N; i++) {
            View view = mHost.getChildAt(i);
            ViewGroup.LayoutParams params = view.getLayoutParams();

            if (params instanceof PercentLayoutParams) {
                PercentLayoutInfo info = ((PercentLayoutParams) params).getPercentLayoutInfo();
                if (info != null) {
                    info.fillLayoutParams(params, widthHint, heightHint);
                }
            }
        }
    }

    private static int getBaseByModeAndVal(int widthHint, int heightHint, PercentLayoutInfo.BASE_MODE mode) {
        switch (mode) {
            case BASE_HEIGHT:
                return heightHint;
            case BASE_WIDTH:
                return widthHint;
            case BASE_SCREEN_WIDTH:
                return mWidthScreen;
            case BASE_SCREEN_HEIGHT:
                return mHeightScreen;
        }
        return 0;
    }

    /**
     * Constructs a PercentLayoutInfo from attributes associated with a View. Call this method from
     * {@code LayoutParams(Context c, AttributeSet attrs)} constructor.
     */
    static PercentLayoutInfo getPercentLayoutInfo(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.PercentLayout_Layout);
        PercentLayoutInfo info = setWidthAndHeightVal(array);
        array.recycle();
        return info;
    }

    private static PercentLayoutInfo setWidthAndHeightVal(TypedArray array) {
        PercentLayoutInfo info = new PercentLayoutInfo();
        PercentLayoutInfo.PercentVal percentVal = getPercentVal(array, R.styleable.PercentLayout_Layout_layout_widthPercent, true);
        if (percentVal != null) {
            info.widthPercent = percentVal;
        }
        percentVal = getPercentVal(array, R.styleable.PercentLayout_Layout_layout_heightPercent, false);
        if (percentVal != null) {
            info.heightPercent = percentVal;
        }
        return info;
    }

    private static PercentLayoutInfo.PercentVal getPercentVal(TypedArray array, int index, boolean baseWidth) {
        String sizeStr = array.getString(index);
        return getPercentVal(sizeStr, baseWidth);
    }

    private static final String REGEX_PERCENT = "^(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)%([s]?[wh]?)$";

    /**
     * widthStr to PercentVal
     * <br/>
     * eg: 35%w => new PercentVal(35, true)
     */
    private static PercentLayoutInfo.PercentVal getPercentVal(String percentStr, boolean isOnWidth) {
        if (percentStr == null) {
            return null;
        }
        Pattern p = Pattern.compile(REGEX_PERCENT);
        Matcher matcher = p.matcher(percentStr);
        if (!matcher.matches()) {
            throw new RuntimeException("the value of layout_xxxPercent invalid! ==>" + percentStr);
        }
        String floatVal = matcher.group(1);
        float percent = Float.parseFloat(floatVal) / 100f;

        PercentLayoutInfo.PercentVal percentVal = new PercentLayoutInfo.PercentVal();
        percentVal.percent = percent;
        if (percentStr.endsWith(PercentLayoutInfo.BASE_MODE.SW)) {
            percentVal.baseMode = PercentLayoutInfo.BASE_MODE.BASE_SCREEN_WIDTH;
        } else if (percentStr.endsWith(PercentLayoutInfo.BASE_MODE.SH)) {
            percentVal.baseMode = PercentLayoutInfo.BASE_MODE.BASE_SCREEN_HEIGHT;
        } else if (percentStr.endsWith(PercentLayoutInfo.BASE_MODE.PERCENT)) {
            if (isOnWidth) {
                percentVal.baseMode = PercentLayoutInfo.BASE_MODE.BASE_WIDTH;
            } else {
                percentVal.baseMode = PercentLayoutInfo.BASE_MODE.BASE_HEIGHT;
            }
        } else if (percentStr.endsWith(PercentLayoutInfo.BASE_MODE.W)) {
            percentVal.baseMode = PercentLayoutInfo.BASE_MODE.BASE_WIDTH;
        } else if (percentStr.endsWith(PercentLayoutInfo.BASE_MODE.H)) {
            percentVal.baseMode = PercentLayoutInfo.BASE_MODE.BASE_HEIGHT;
        } else {
            throw new IllegalArgumentException("the " + percentStr + " must be endWith [%|w|h|sw|sh]");
        }
        return percentVal;
    }

    /**
     * Iterates over children and restores their original dimensions that were changed for
     * percentage values. Calling this method only makes sense if you previously called
     * {@link PercentLayout#adjustChildren(int, int)}.
     */

    void restoreOriginalParams() {
        for (int i = 0, N = mHost.getChildCount(); i < N; i++) {
            View view = mHost.getChildAt(i);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            if (params instanceof PercentLayoutParams) {
                PercentLayoutInfo info = ((PercentLayoutParams) params).getPercentLayoutInfo();
                if (info != null) {
                    info.restoreLayoutParams(params);
                }
            }
        }
    }

    /**
     * Iterates over children and checks if any of them would like to get more space than it
     * received through the percentage dimension.
     * <p/>
     * If you are building a layout that supports percentage dimensions you are encouraged to take
     * advantage of this method. The developer should be able to specify that a child should be
     * remeasured by adding normal dimension attribute with {@code wrap_content} value. For example
     * he might specify child's attributes as {@code app:layout_widthPercent="60%p"} and
     * {@code android:layout_width="wrap_content"}. In this case if the child receives too little
     * space, it will be remeasured with width set to {@code WRAP_CONTENT}.
     *
     * @return True if the measure phase needs to be rerun because one of the children would like
     * to receive more space.
     */
    boolean handleMeasuredStateTooSmall() {
        boolean needsSecondMeasure = false;
        for (int i = 0, size = mHost.getChildCount(); i < size; i++) {
            View view = mHost.getChildAt(i);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            if (params instanceof PercentLayoutParams) {
                PercentLayoutInfo info = ((PercentLayoutParams) params).getPercentLayoutInfo();
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

    private static boolean shouldHandleMeasuredWidthTooSmall(View view, PercentLayoutInfo info) {
        int state = view.getMeasuredWidthAndState() & View.MEASURED_STATE_MASK;
        if (info == null || info.widthPercent == null) {
            return false;
        }
        return state == View.MEASURED_STATE_TOO_SMALL
                && info.widthPercent.percent >= 0
                && info.mPreservedParams.width == ViewGroup.LayoutParams.WRAP_CONTENT;
    }

    private static boolean shouldHandleMeasuredHeightTooSmall(View view, PercentLayoutInfo info) {
        int state = view.getMeasuredHeightAndState() & View.MEASURED_STATE_MASK;
        if (info == null || info.heightPercent == null) {
            return false;
        }
        return state == View.MEASURED_STATE_TOO_SMALL
                && info.heightPercent.percent >= 0
                && info.mPreservedParams.height == ViewGroup.LayoutParams.WRAP_CONTENT;
    }

    /**
     * Container for information about percentage dimensions and margins. It acts as an extension
     * for {@code LayoutParams}.
     */
    static class PercentLayoutInfo {

        private enum BASE_MODE {
            BASE_WIDTH,
            BASE_HEIGHT,
            BASE_SCREEN_WIDTH,
            BASE_SCREEN_HEIGHT;
            /**
             * width_parent
             */
            public static final String PERCENT = "%";
            /**
             * width_parent
             */
            public static final String W       = "w";
            /**
             * height_parent
             */
            public static final String H       = "h";
            /**
             * width_screen
             */
            public static final String SW      = "sw";
            /**
             * height_screen
             */
            public static final String SH      = "sh";
        }

        static class PercentVal {
            float     percent = -1;
            BASE_MODE baseMode;
        }

        PercentVal widthPercent;
        PercentVal heightPercent;


        private final ViewGroup.MarginLayoutParams mPreservedParams;

        PercentLayoutInfo() {
            mPreservedParams = new ViewGroup.MarginLayoutParams(0, 0);
        }

        /**
         * Fills {@code ViewGroup.LayoutParams} dimensions based on percentage values.
         */
        void fillLayoutParams(ViewGroup.LayoutParams params, int widthHint, int heightHint) {
            mPreservedParams.width = params.width;
            mPreservedParams.height = params.height;

            if (widthPercent != null) {
                int base = getBaseByModeAndVal(widthHint, heightHint, widthPercent.baseMode);
                params.width = (int) (base * widthPercent.percent);
            }
            if (heightPercent != null) {
                int base = getBaseByModeAndVal(widthHint, heightHint, heightPercent.baseMode);
                params.height = (int) (base * heightPercent.percent);
            }
        }

        /**
         * Restores original dimensions after they were changed for percentage based values. Calling
         * this method only makes sense if you previously called
         * {@link PercentLayoutInfo#fillLayoutParams}.
         */
        void restoreLayoutParams(ViewGroup.LayoutParams params) {
            params.width = mPreservedParams.width;
            params.height = mPreservedParams.height;
        }
    }

    /**
     * If a layout wants to support percentage based dimensions and use this helper class, its
     * {@code LayoutParams} subclass must implement this interface.
     * <p/>
     * Your {@code LayoutParams} subclass should contain an instance of {@code PercentLayoutInfo}
     * and the implementation of this interface should be a simple accessor.
     */
    public interface PercentLayoutParams {
        PercentLayoutInfo getPercentLayoutInfo();
    }
}
