package com.qsmaxmin.qsbase.common.config;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.utils.QsHelper;
import com.qsmaxmin.qsbase.common.utils.StreamCloseUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @CreateBy qsmaxmin
 * @Date 2018/12/7 10:07
 * @Description
 * @deprecated
 */
class PropertiesEngine {
    private final Object            mTarget;
    private final String            TAG;
    private final String            mKey;
    private       SharedPreferences sp;
    private       Gson              gson;
    private       List<Field>       fieldList = new ArrayList<>();

    PropertiesEngine(Object target, String key, String tag) {
        this.TAG = tag;
        this.mTarget = target;
        this.mKey = key;
        Application application = QsHelper.getApplication();
        sp = application.getSharedPreferences(key, Context.MODE_PRIVATE);

        Map<String, ?> spAll = sp.getAll();
        Class<?> clazz = mTarget.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Property.class)) {
                field.setAccessible(true);
                fieldList.add(field);
                String fieldName = field.getName();
                if (spAll != null) {
                    Object value = spAll.get(fieldName);
                    if (value != null) setFieldValue(field, value);
                }
            }
        }

        if (spAll == null || spAll.isEmpty()) {
            L.i(TAG, "get data from 'SP' return null, so try load old 'Properties'.........");
            boolean hasData = loadFromOldData();
            if (hasData) {
                L.i(TAG, "old 'Properties' has data, so loading data from 'Properties' and them delete it");
                commit();
                deleteOldCacheFile();
            } else {
                L.i(TAG, "old 'Properties' is empty too.........");
            }
        } else {
            L.i(TAG, "load data from SP success.........");
        }
    }

    void commit() {
        SharedPreferences.Editor edit = sp.edit();
        for (Field field : fieldList) {
            try {
                Object value = field.get(mTarget);
                putToSP(edit, field, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        edit.apply();
    }


    void clear() {
        SharedPreferences.Editor edit = sp.edit();
        for (Field field : fieldList) {
            edit.remove(field.getName());
        }
        edit.apply();
        deleteOldCacheFile();
    }

    private void putToSP(SharedPreferences.Editor editor, Field field, Object value) {
        Class<?> type = field.getType();
        if (value == null) {
            editor.remove(field.getName());
        } else if (type == String.class) {
            editor.putString(field.getName(), (String) value);
        } else if (type == int.class) {
            editor.putInt(field.getName(), (Integer) value);
        } else if (type == boolean.class) {
            editor.putBoolean(field.getName(), (Boolean) value);
        } else if (type == long.class) {
            editor.putLong(field.getName(), (Long) value);
        } else if (type == float.class) {
            editor.putFloat(field.getName(), (Float) value);
        } else if (isCommonUnSupportType(type)) {
            L.e(TAG, "commit failed..........(" + field.getName() + ")type not support!, please replace with 'int'");
        } else {
            if (gson == null) gson = new Gson();
            String jsonStr = gson.toJson(value);
            editor.putString(field.getName(), jsonStr);
        }
    }

    private void setFieldValue(Field field, Object value) {
        try {
            Class<?> type = field.getType();
            if (isCommonSupportType(type)) {
                field.set(mTarget, value);
            } else if (isCommonUnSupportType(type)) {
                L.e(TAG, "set field value field.......... (" + field.getName() + ")type not support!, please replace with 'int'");
            } else if (value instanceof String) {
                if (gson == null) gson = new Gson();
                Object object = gson.fromJson((String) value, type);
                field.set(mTarget, object);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isCommonSupportType(Class<?> clazz) {
        return clazz == String.class
                || clazz == int.class
                || clazz == long.class
                || clazz == float.class
                || clazz == boolean.class;
    }

    private boolean isCommonUnSupportType(Class<?> clazz) {
        return clazz == short.class
                || clazz == byte.class
                || clazz == double.class;
    }


    /**
     * ----------------------------------------------------------以下逻辑目的是兼容老版本---------------------------------------------------------------------
     * 从以前的Properties中读取数据并保存到SP
     */
    private boolean loadFromOldData() {
        File file = getOldCacheFile();
        if (!file.exists()) return false;

        Properties properties = new java.util.Properties();
        InputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(file));
            properties.load(in);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            StreamCloseUtils.close(in);
        }

        boolean hasData = false;
        for (Field field : fieldList) {
            Object value = properties.get(field.getName());
            if (value == null) continue;
            Class<?> type = field.getType();
            if (type == String.class) {
                setFieldValue(field, value);
            } else if (type == int.class) {
                setFieldValue(field, getInt(value));
            } else if (type == long.class) {
                setFieldValue(field, getLong(value));
            } else if (type == float.class) {
                setFieldValue(field, getFloat(value));
            } else if (type == boolean.class) {
                setFieldValue(field, getBoolean(value));
            } else {
                setFieldValue(field, value);
            }
            hasData = true;

        }
        return hasData;
    }

    private File getOldCacheFile() {
        File parentFile = QsHelper.getApplication().getFilesDir();
        String fileName = mKey + ".properties";
        return new File(parentFile, fileName);
    }

    private void deleteOldCacheFile() {
        File cacheFile = getOldCacheFile();
        if (cacheFile.exists()) {
            boolean delete = cacheFile.delete();
            L.i(TAG, "delete old cache file " + (delete ? "success" : "failed") + "......file:" + cacheFile.getPath());
        }
    }


    private int getInt(Object value) {
        try {
            String str = String.valueOf(value);
            if (TextUtils.isEmpty(str)) return 0;
            return Integer.parseInt(str);
        } catch (Exception e) {
            return 0;
        }
    }

    private long getLong(Object value) {
        try {
            String str = String.valueOf(value);
            if (TextUtils.isEmpty(str)) return 0;
            return Long.parseLong(str);
        } catch (Exception e) {
            return 0;
        }
    }

    private float getFloat(Object value) {
        try {
            String str = String.valueOf(value);
            if (TextUtils.isEmpty(str)) return 0;
            return Float.parseFloat(str);
        } catch (Exception e) {
            return 0;
        }
    }

    private boolean getBoolean(Object value) {
        try {
            String str = String.valueOf(value);
            return !TextUtils.isEmpty(str) && Boolean.parseBoolean(str);
        } catch (Exception e) {
            return false;
        }
    }
}
