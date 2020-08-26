package com.qsmaxmin.qsbase.plugin.property;

import android.content.Context;
import android.content.SharedPreferences;

import com.qsmaxmin.annotation.QsNotProguard;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.utils.QsHelper;

import java.util.Map;
import java.util.Set;


/**
 * @CreateBy qsmaxmin
 * @Date 2017/7/3 9:15
 * @Description QsTransform supported, Never change any code !!!
 */
@SuppressWarnings("rawtypes")
public abstract class QsProperties implements QsIProperty, QsNotProguard {
    private final Object            locker = new Object();
    private final SharedPreferences sp;

    @Override public void readPropertiesByQsPlugin(Map map) {
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
            readPropertiesByQsPlugin(map);
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

    protected final int getInt(Map map, String key) {
        Object o = map.get(key);
        return o == null ? 0 : (int) o;
    }

    protected final Integer getInt2(Map map, String key) {
        return (Integer) map.get(key);
    }

    protected final float getFloat(Map map, String key) {
        Object o = map.get(key);
        return o == null ? 0f : (float) o;
    }

    protected final Float getFloat2(Map map, String key) {
        return (Float) map.get(key);
    }

    protected final long getLong(Map map, String key) {
        Object o = map.get(key);
        return o == null ? 0L : (long) o;
    }

    protected final Long getLong2(Map map, String key) {
        return (Long) map.get(key);
    }

    protected final boolean getBoolean(Map map, String key) {
        Object o = map.get(key);
        return o != null && (boolean) o;
    }

    protected final Boolean getBoolean2(Map map, String key) {
        return (Boolean) map.get(key);
    }

    protected final String getString(Map map, String key) {
        return (String) map.get(key);
    }

    @SuppressWarnings("unchecked")
    protected final Set<String> getStringSet(Map map, String key) {
        return (Set<String>) map.get(key);
    }
}
