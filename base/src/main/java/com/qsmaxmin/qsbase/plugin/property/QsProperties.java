package com.qsmaxmin.qsbase.plugin.property;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.qsmaxmin.annotation.QsNotProguard;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.utils.QsHelper;

import java.util.Map;
import java.util.Set;


/**
 * @CreateBy qsmaxmin
 * @Date 2017/7/3 9:15
 * @Description QsTransform supported, Never change any code !!!
 * support only: int, long, float, boolean, String, Set<String>
 */
@SuppressWarnings("rawtypes")
public abstract class QsProperties implements QsIProperty, QsNotProguard {
    private final Object            locker = new Object();
    private final SharedPreferences sp;
    private       Gson              gson;

    @Override public void readPropertiesByQsPlugin(Map<String, ?> map) {
    }

    @Override public void savePropertiesByQsPlugin() {
    }

    @Override public void clearPropertiesByQsPlugin() {
    }

    public String initTag() {
        return L.isEnable() ? getClass().getSimpleName() : "QsProperties";
    }

    /**
     * load data from sp
     */
    public QsProperties(String key) {
        sp = QsHelper.getApplication().getSharedPreferences(key, Context.MODE_PRIVATE);
        Map<String, ?> map = sp.getAll();
        if (map != null && !map.isEmpty()) {
            try {
                readPropertiesByQsPlugin(map);
            } catch (Exception e) {
                if (L.isEnable()) e.printStackTrace();
            }
        }
    }

    private SharedPreferences.Editor edit;

    /**
     * save sp
     */
    public void commit() {
        synchronized (locker) {
            edit = sp.edit();
            savePropertiesByQsPlugin();
            edit.apply();
            edit = null;
        }
    }

    /**
     * clear sp
     */
    public void clear() {
        clearPropertiesByQsPlugin();
        sp.edit().clear().apply();
    }


    protected final void putString(String key, String value) {
        if (edit != null) edit.putString(key, value);
    }

    protected final void putInt(String key, int value) {
        if (edit != null) edit.putInt(key, value);
    }

    protected final void putInt2(String key, Integer value) {
        if (edit != null) {
            edit.putInt(key, value == null ? 0 : value);
        }
    }

    protected final void putFloat(String key, float value) {
        if (edit != null) edit.putFloat(key, value);
    }

    protected final void putFloat2(String key, Float value) {
        if (edit != null) {
            edit.putFloat(key, value == null ? 0f : value);
        }
    }

    protected final void putLong(String key, long value) {
        if (edit != null) edit.putLong(key, value);
    }

    protected final void putLong2(String key, Long value) {
        if (edit != null) {
            edit.putLong(key, value == null ? 0L : value);
        }
    }

    protected final void putBoolean(String key, boolean value) {
        if (edit != null) edit.putBoolean(key, value);
    }

    protected final void putBoolean2(String key, Boolean value) {
        if (edit != null) edit.putBoolean(key, value == null ? false : value);
    }

    protected final void putStringSet(String key, Set<String> value) {
        if (edit != null) edit.putStringSet(key, value);
    }

    protected final void putByte(String key, byte value) {
        if (edit != null) edit.putInt(key, value);
    }

    protected final void putByte2(String key, Byte value) {
        if (edit != null) edit.putInt(key, value == null ? 0 : value);
    }

    protected final void putShort(String key, short value) {
        if (edit != null) edit.putInt(key, value);
    }

    protected final void putShort2(String key, Short value) {
        if (edit != null) edit.putInt(key, value == null ? 0 : value);
    }

    protected final void putDouble(String key, double value) {
        if (edit != null) edit.putString(key, String.valueOf(value));
    }

    protected final void putDouble2(String key, Double value) {
        if (edit != null) edit.putString(key, value == null ? null : String.valueOf(value));
    }

    protected final void putChar(String key, char value) {
        if (edit != null) edit.putInt(key, value);
    }

    protected final void putChar2(String key, Character value) {
        if (edit != null) edit.putInt(key, value == null ? 0 : value);
    }

    protected final int getInt(Map<String, ?> map, String key) {
        Object o = map.get(key);
        return o == null ? 0 : (int) o;
    }

    protected final Integer getInt2(Map<String, ?> map, String key) {
        return (Integer) map.get(key);
    }

    protected final float getFloat(Map<String, ?> map, String key) {
        Object o = map.get(key);
        return o == null ? 0f : (float) o;
    }

    protected final Float getFloat2(Map<String, ?> map, String key) {
        return (Float) map.get(key);
    }

    protected final long getLong(Map<String, ?> map, String key) {
        Object o = map.get(key);
        return o == null ? 0L : (long) o;
    }

    protected final Long getLong2(Map<String, ?> map, String key) {
        return (Long) map.get(key);
    }

    protected final boolean getBoolean(Map<String, ?> map, String key) {
        Object o = map.get(key);
        return o != null && (boolean) o;
    }

    protected final Boolean getBoolean2(Map<String, ?> map, String key) {
        return (Boolean) map.get(key);
    }

    protected final String getString(Map<String, ?> map, String key) {
        return (String) map.get(key);
    }

    @SuppressWarnings("unchecked")
    protected final Set<String> getStringSet(Map<String, ?> map, String key) {
        return (Set<String>) map.get(key);
    }

    protected final byte getByte(Map<String, ?> map, String key) {
        Object o = map.get(key);
        return o == null ? 0 : (byte) o;
    }

    protected final Byte getByte2(Map<String, ?> map, String key) {
        return (Byte) map.get(key);
    }

    protected final short getShort(Map<String, ?> map, String key) {
        Object o = map.get(key);
        return o == null ? 0 : (short) o;
    }

    protected final Short getShort2(Map<String, ?> map, String key) {
        return (Short) map.get(key);
    }

    protected final double getDouble(Map<String, ?> map, String key) {
        Object o = map.get(key);
        return o == null ? 0 : (double) o;
    }

    protected final Double getDouble2(Map<String, ?> map, String key) {
        return (Double) map.get(key);
    }

    protected final char getChar(Map<String, ?> map, String key) {
        Object o = map.get(key);
        return o == null ? 0 : (char) o;
    }

    protected final Character getChar2(Map<String, ?> map, String key) {
        return (Character) map.get(key);
    }

    protected void putObject(String key, Object obj) {
        if (edit != null) {
            if (obj == null) {
                edit.putString(key, null);
            } else {
                if (gson == null) gson = new Gson();
                String text = gson.toJson(obj);
                edit.putString(key, text);
            }
        }
    }

    protected final <T> T getObject(Map<String, ?> map, String key, Class<T> clazz) {
        Object obj = map.get(key);
        if (obj instanceof String) {
            if (gson == null) gson = new Gson();
            try {
                return gson.fromJson((String) obj, clazz);
            } catch (Exception e) {
                if (L.isEnable()) e.printStackTrace();
                return null;
            }
        }
        return null;
    }
}
