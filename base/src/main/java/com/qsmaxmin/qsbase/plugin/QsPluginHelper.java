package com.qsmaxmin.qsbase.plugin;

import android.view.View;

/**
 * for QsPlugin
 */
@SuppressWarnings({"WeakerAccess", "unchecked"})
public class QsPluginHelper {
    private static long lastClickTime;

    public static <T extends View> T forceCastToView(View view) {
        return (T) view;
    }

    public static <D> D forceCastObject(Object o) {
        return (D) o;
    }

    public static boolean isFastDoubleClick(long duration) {
        if (duration <= 0) return false;
        long time = System.currentTimeMillis();
        if (time - lastClickTime < duration) {
            return true;
        } else {
            lastClickTime = time;
            return false;
        }
    }
}
