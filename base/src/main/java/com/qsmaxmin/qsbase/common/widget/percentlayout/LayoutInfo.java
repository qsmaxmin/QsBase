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
    private              int    screenWidth;
    private              int    screenHeight;
    private              int    horMode;
    private              int    verticalMode;

    int   preservedParamsWidth;
    int   preservedParamsHeight;
    float widthPercent  = -1;
    float heightPercent = -1;

    LayoutInfo(String widthPercentStr, String heightPercentStr) {
        Pattern pattern = Pattern.compile(REGEX_PERCENT);
        if (!TextUtils.isEmpty(widthPercentStr)) {
            int splitIndex = widthPercentStr.indexOf(':');
            if (splitIndex == -1) {
                splitIndex = widthPercentStr.indexOf('/');
            }
            if (splitIndex == -1) {
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
                    throw new IllegalArgumentException("the value of layout_widthPercent:" + widthPercentStr + " invalid! such as: '80%','80%h'");
                }
                String floatVal = matcher.group(1);
                widthPercent = Float.parseFloat(floatVal) / 100f;
            } else {
                horMode = BASE_HEIGHT;
                try {
                    String horValueStr = widthPercentStr.substring(0, splitIndex);
                    String verValueStr = widthPercentStr.substring(splitIndex + 1);
                    this.widthPercent = Float.parseFloat(horValueStr) / Float.parseFloat(verValueStr);
                } catch (Exception e) {
                    throw new IllegalArgumentException("the value of layout_widthPercent=" + widthPercentStr + " invalid!, such as: '50:40'");
                }
            }
        }

        if (!TextUtils.isEmpty(heightPercentStr)) {
            int splitIndex = heightPercentStr.indexOf(':');
            if (splitIndex == -1) {
                splitIndex = heightPercentStr.indexOf('/');
            }
            if (splitIndex == -1) {
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
                    throw new IllegalArgumentException("the value of layout_heightPercent:" + heightPercentStr + " invalid! such as: '80%', '80%w'");
                }
                String floatVal = matcher.group(1);
                heightPercent = Float.parseFloat(floatVal) / 100f;
            } else {
                verticalMode = BASE_WIDTH;
                try {
                    String horValueStr = heightPercentStr.substring(0, splitIndex);
                    String verValueStr = heightPercentStr.substring(splitIndex + 1);
                    this.heightPercent = Float.parseFloat(horValueStr) / Float.parseFloat(verValueStr);
                } catch (Exception e) {
                    throw new IllegalArgumentException("the value of layout_heightPercent=" + heightPercentStr + " invalid!, such as: '50:40'");
                }
            }
        }
    }

    void fillLayoutParams(Context context, ViewGroup.LayoutParams params, int parentWidth, int parentHeight) {
        preservedParamsWidth = params.width;
        preservedParamsHeight = params.height;
        if (widthPercent != -1) {
            int base = getBaseByModeAndVal(context, parentWidth, parentHeight, horMode);
            params.width = (int) (base * widthPercent);
        }
        if (heightPercent != -1) {
            int base = getBaseByModeAndVal(context, parentWidth, parentHeight, verticalMode);
            params.height = (int) (base * heightPercent);
        }
    }

    /**
     * Restores original dimensions after they were changed for percentage based values. Calling
     * this method only makes sense if you previously called
     */
    void restoreLayoutParams(ViewGroup.LayoutParams params) {
        params.width = preservedParamsWidth;
        params.height = preservedParamsHeight;
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
