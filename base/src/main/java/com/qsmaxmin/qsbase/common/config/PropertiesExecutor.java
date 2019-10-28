package com.qsmaxmin.qsbase.common.config;

import android.content.SharedPreferences;

import com.google.gson.Gson;

/**
 * @CreateBy qsmaxmin
 * @Date 2019/7/22 15:28
 * @Description executor 超类
 */
@SuppressWarnings({"unchecked", "WeakerAccess"})
public abstract class PropertiesExecutor<T> {
    private Gson gson;

    public abstract void bindConfig(T config, SharedPreferences sp);

    public abstract void commit(T config, SharedPreferences sp);

    /**
     * base on int
     */
    public final int forceCastInt(Object o) {
        return o == null ? 0 : (int) o;
    }

    /**
     * base on int
     */
    public final short forceCastToShort(Object o) {
        if (o == null) return 0;
        int intValue = (int) o;
        return (short) intValue;
    }

    /**
     * base on int
     */
    public final byte forceCastToByte(Object o) {
        if (o == null) return 0;
        int intValue = (int) o;
        return (byte) intValue;
    }

    /**
     * base on int
     */
    public final char forceCastToChar(Object o) {
        if (o == null) return 0;
        int intValue = (int) o;
        return (char) intValue;
    }

    /**
     * base on long
     */
    public final long forceCastToLong(Object o) {
        if (o == null) return 0;
        return (long) o;
    }

    /**
     * base on float
     */
    public final float forceCastToFloat(Object o) {
        return o == null ? 0f : (float) o;
    }

    /**
     * base on String
     * 历史遗留问题，因为double转float会丢失精度
     * 所以在以后的版本里同String保存double数据，此处做了兼容处理
     */
    public final double forceCastToDouble(Object o) {
        if (o == null) return 0;
        if (o instanceof String) {
            String stringValue = (String) o;
            return Double.parseDouble(stringValue);
        } else {
            float floatValue = (float) o;
            return (double) floatValue;
        }
    }

    /**
     * base on boolean
     */
    public final boolean forceCastToBoolean(Object o) {
        return o != null && (boolean) o;
    }

    /**
     * base on String
     */
    public final <D> D forceCastObject(Object object) {
        return object == null ? null : (D) object;
    }

    public final <D> D jsonStringToObject(Object o, Class<D> clazzOfD) {
        if (o instanceof String) {
            if (gson == null) gson = new Gson();
            return gson.fromJson((String) o, clazzOfD);
        }
        return null;
    }

    public final String objectToJsonString(Object o, Class clazz) {
        if (o != null) {
            if (gson == null) gson = new Gson();
            return gson.toJson(o, clazz);
        }
        return null;
    }

    public String doubleCastToString(Double d) {
        double doubleValue = d == null ? 0 : d;
        return String.valueOf(doubleValue);
    }
}
