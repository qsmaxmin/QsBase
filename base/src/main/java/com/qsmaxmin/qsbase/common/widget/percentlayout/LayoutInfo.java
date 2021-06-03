package com.qsmaxmin.qsbase.common.widget.percentlayout;

import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @CreateBy qsmaxmin
 * @Date 2021/6/3 12:24
 * @Description
 */
class LayoutInfo {
    private static final String REGEX_PERCENT      = "^(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)%([s]?[wh]?)$";
    private static final String PERCENT            = "%";
    private static final String W                  = "w";
    private static final String H                  = "h";
    private static final String SW                 = "sw";
    private static final String SH                 = "sh";
    private static final int    BASE_WIDTH         = 1;
    private static final int    BASE_HEIGHT        = 2;
    private static final int    BASE_SCREEN_WIDTH  = 3;
    private static final int    BASE_SCREEN_HEIGHT = 4;
    private              float  dimensionRatio     = -1;
    private              int    screenWidth;
    private              int    screenHeight;
    private              int    horMode;
    private              int    verticalMode;

    final ViewGroup.MarginLayoutParams preservedParams;
    float widthPercent  = -1;
    float heightPercent = -1;

    LayoutInfo(String widthPercentStr, String heightPercentStr, String dimensionRatioStr) {
        preservedParams = new ViewGroup.MarginLayoutParams(0, 0);

        if (!TextUtils.isEmpty(dimensionRatioStr)) {
            int index = dimensionRatioStr.indexOf(':');
            if (index == -1) {
                throw new IllegalArgumentException("the value of layout_percentDimensionRatio=" + dimensionRatioStr + " invalid!, such as: '50:40'");
            }
            try {
                String horValueStr = dimensionRatioStr.substring(0, index);
                String verValueStr = dimensionRatioStr.substring(index + 1);
                float horPercentRatio = Float.parseFloat(horValueStr);
                float verPercentRatio = Float.parseFloat(verValueStr);
                dimensionRatio = horPercentRatio / verPercentRatio;

            } catch (Exception e) {
                throw new IllegalArgumentException("the value of layout_percentDimensionRatio=" + dimensionRatioStr + " invalid!, such as: '50:40'");
            }
        }

        Pattern pattern = Pattern.compile(REGEX_PERCENT);
        if (!TextUtils.isEmpty(widthPercentStr)) {
            if (widthPercentStr.endsWith(SW)) {
                horMode = BASE_SCREEN_WIDTH;
            } else if (widthPercentStr.endsWith(SH)) {
                horMode = BASE_SCREEN_HEIGHT;
            } else if (widthPercentStr.endsWith(PERCENT)) {
                horMode = BASE_WIDTH;
            } else if (widthPercentStr.endsWith(W)) {
                horMode = BASE_WIDTH;
            } else if (widthPercentStr.endsWith(H)) {
                horMode = BASE_HEIGHT;
            } else {
                throw new IllegalArgumentException("the " + widthPercentStr + " must be endWith [%|w|h|sw|sh]");
            }
            Matcher matcher = pattern.matcher(widthPercentStr);
            if (!matcher.matches()) {
                throw new RuntimeException("the value of layout_xxxPercent:" + widthPercentStr + " invalid! such as: '80%','80%h'");
            }
            String floatVal = matcher.group(1);
            widthPercent = Float.parseFloat(floatVal) / 100f;
        }

        if (!TextUtils.isEmpty(heightPercentStr)) {
            if (heightPercentStr.endsWith(SW)) {
                verticalMode = BASE_SCREEN_WIDTH;
            } else if (heightPercentStr.endsWith(SH)) {
                verticalMode = BASE_SCREEN_HEIGHT;
            } else if (heightPercentStr.endsWith(PERCENT)) {
                verticalMode = BASE_HEIGHT;
            } else if (heightPercentStr.endsWith(W)) {
                verticalMode = BASE_WIDTH;
            } else if (heightPercentStr.endsWith(H)) {
                verticalMode = BASE_HEIGHT;
            } else {
                throw new IllegalArgumentException("the " + heightPercentStr + " must be endWith [%|w|h|sw|sh]");
            }

            Matcher matcher = pattern.matcher(heightPercentStr);
            if (!matcher.matches()) {
                throw new RuntimeException("the value of layout_xxxPercent:" + heightPercentStr + " invalid! such as: '80%', '80%w'");
            }
            String floatVal = matcher.group(1);
            heightPercent = Float.parseFloat(floatVal) / 100f;
        }
    }

    void fillLayoutParams(Context context, ViewGroup.LayoutParams params, int widthHint, int heightHint) {
        preservedParams.width = params.width;
        preservedParams.height = params.height;

        if (widthPercent != -1) {
            int base = getBaseByModeAndVal(context, widthHint, heightHint, horMode);
            params.width = (int) (base * widthPercent);
        }
        if (heightPercent != -1) {
            int base = getBaseByModeAndVal(context, widthHint, heightHint, verticalMode);
            params.height = (int) (base * heightPercent);
        }

        if (dimensionRatio != -1) {
            boolean widthExactly = params.width == ViewGroup.LayoutParams.MATCH_PARENT || params.width > 0;
            boolean heightExactly = params.height == ViewGroup.LayoutParams.MATCH_PARENT || params.height > 0;
            if (widthExactly && !heightExactly) {
                int childWidth = params.width == ViewGroup.LayoutParams.MATCH_PARENT ? widthHint : params.width;
                if (childWidth > 0) {
                    params.width = childWidth;
                    params.height = (int) (childWidth / dimensionRatio);
                }
            } else if (!widthExactly && heightExactly) {
                int childHeight = params.height == ViewGroup.LayoutParams.MATCH_PARENT ? heightHint : params.height;
                if (childHeight > 0) {
                    params.height = childHeight;
                    params.width = (int) (childHeight * dimensionRatio);
                }
            }
        }
    }

    /**
     * Restores original dimensions after they were changed for percentage based values. Calling
     * this method only makes sense if you previously called
     */
    void restoreLayoutParams(ViewGroup.LayoutParams params) {
        params.width = preservedParams.width;
        params.height = preservedParams.height;
    }

    private int getBaseByModeAndVal(Context context, int widthHint, int heightHint, int mode) {
        switch (mode) {
            case BASE_HEIGHT:
                return heightHint;
            case BASE_WIDTH:
                return widthHint;
            case BASE_SCREEN_WIDTH:
                return getScreenWidth(context);
            case BASE_SCREEN_HEIGHT:
                return getScreenHeight(context);
        }
        return 0;
    }

    private int getScreenWidth(Context context) {
        initScreenSize(context);
        return screenWidth;
    }

    private int getScreenHeight(Context context) {
        initScreenSize(context);
        return screenHeight;
    }

    private void initScreenSize(Context context) {
        if (screenWidth == 0 || screenHeight == 0) {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics outMetrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(outMetrics);
            screenWidth = outMetrics.widthPixels;
            screenHeight = outMetrics.heightPixels;
        }
    }
}
