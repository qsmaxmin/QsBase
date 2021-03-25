package com.qsmaxmin.qsbase.plugin.bind;

import android.os.Bundle;

/**
 * @CreateBy qsmaxmin
 * @Date 2021/3/25 15:34
 * @Description
 */
public class ViewBindHelper {

    public static int getInt(Bundle bundle, String key) {
        return bundle.getInt(key);
    }

    public static float getFloat(Bundle bundle, String key) {
        return bundle.getFloat(key);
    }

    public static byte getByte(Bundle bundle, String key) {
        return bundle.getByte(key);
    }

    public static char getChar(Bundle bundle, String key) {
        return bundle.getChar(key);
    }

    public static long getLong(Bundle bundle, String key) {
        return bundle.getLong(key);
    }

    public static double getDouble(Bundle bundle, String key) {
        return bundle.getDouble(key);
    }

    public static boolean getBoolean(Bundle bundle, String key) {
        return bundle.getBoolean(key);
    }

    public static short getShort(Bundle bundle, String key) {
        return bundle.getShort(key);
    }

    public static String getString(Bundle bundle, String key) {
        return bundle.getString(key);
    }

    public static Object get(Bundle bundle, String key) {
        return bundle.get(key);
    }
}
