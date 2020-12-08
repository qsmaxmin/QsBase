package com.qsmaxmin.qsbase.common.log;

import android.text.TextUtils;
import android.util.Log;

/**
 * Create by qsmaxmin
 */
public final class L {

    private static boolean enable = false;

    private L() {
    }

    /**
     * init
     *
     * @param isLogOpen is show log
     */
    public static void init(boolean isLogOpen) {
        enable = isLogOpen;
    }

    public static boolean isEnable() {
        return enable;
    }

    public static void i(String tag, String message) {
        println(Log.INFO, tag, message);
    }

    public static void v(String tag, String message) {
        println(Log.VERBOSE, tag, message);
    }

    public static void d(String tag, String message) {
        println(Log.DEBUG, tag, message);
    }

    public static void w(String tag, String message) {
        println(Log.WARN, tag, message);
    }

    public static void e(String tag, String message) {
        println(Log.ERROR, tag, message);
    }

    public static void e(String tag, Throwable t) {
        if (enable) {
            Log.e(tag, t.getMessage(), t);
        }
    }

    public static void e(String tag, String msg, Throwable t) {
        if (enable) {
            Log.e(tag, msg, t);
        }
    }

    private static void println(int priority, String tag, String message) {
        if (enable && !TextUtils.isEmpty(message)) {
            Log.println(priority, tag, message);
        }
    }
}