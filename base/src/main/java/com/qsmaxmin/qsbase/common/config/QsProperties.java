package com.qsmaxmin.qsbase.common.config;

import android.content.res.Resources;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.utils.QsHelper;
import com.qsmaxmin.qsbase.common.utils.StreamCloseUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.Properties;

/**
 * @CreateBy sky
 * @Date 2017/7/3 9:15
 * @Description
 */

public abstract class QsProperties {

    private static final String     DEFAULT_CODE             = "utf-8";
    private static final String     DEFAULT_ANNOTATION_VALUE = "";
    private static final String     EXTENSION                = ".properties";
    private final        Properties mProperties              = new java.util.Properties();
    public static final  int        OPEN_TYPE_ASSETS         = 1;
    public static final  int        OPEN_TYPE_DATA           = 2;

    private String           mPropertiesFileName;
    private File             propertyFilePath;
    private PropertyCallback propertyCallback;
    private Gson             gson;

    public String initTag() {
        return QsHelper.getInstance().getApplication().isLogOpen() ? getClass().getSimpleName() : "QsProperties";
    }

    protected int initType() {
        return OPEN_TYPE_DATA;
    }

    public QsProperties() {
        this("config");
    }

    public QsProperties(String propertiesFileName) {
        mPropertiesFileName = propertiesFileName;
        propertyFilePath = QsHelper.getInstance().getApplication().getFilesDir();
        switch (initType()) {
            case OPEN_TYPE_ASSETS:
                InputStream inputStream = null;
                Resources resources = QsHelper.getInstance().getApplication().getResources();
                try {
                    inputStream = resources.getAssets().open(mPropertiesFileName + EXTENSION);
                    mProperties.load(inputStream);
                    loadPropertiesValues();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    StreamCloseUtils.close(inputStream);
                }
                break;
            case OPEN_TYPE_DATA:
                InputStream in = null;
                try {
                    String stringBuilder = mPropertiesFileName + EXTENSION;
                    File file = new File(propertyFilePath, stringBuilder);
                    if (!file.exists()) {
                        boolean success = file.createNewFile();
                        L.i(initTag(), "create properties file " + (success ? "success" : "fail") + " !  file:" + file.getAbsolutePath());
                    }
                    in = new BufferedInputStream(new FileInputStream(file));
                    mProperties.load(in);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    StreamCloseUtils.close(in);
                }
                loadPropertiesValues();
                break;
        }
    }

    private void loadPropertiesValues() {
        Class<? extends QsProperties> thisClass = this.getClass();
        Field[] fields = thisClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Property.class)) {
                field.setAccessible(true);
                String fieldName = field.getName();
                Property annotation = field.getAnnotation(Property.class);

                if (annotation.value().equals(DEFAULT_ANNOTATION_VALUE)) {
                    setFieldValue(field, fieldName);
                } else {
                    setFieldValue(field, annotation.value());
                }
            }
        }
    }

    public void setPropertyCallback(PropertyCallback propertyCallback) {
        this.propertyCallback = propertyCallback;
    }

    private int getInt(String key, int defaultValue) {
        String value;
        try {
            value = mProperties.getProperty(key);
            if (TextUtils.isEmpty(value)) return 0;
            return Integer.parseInt(mProperties.getProperty(key));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private long getLong(String key, long defaultValue) {
        String value;
        try {
            value = mProperties.getProperty(key);
            if (TextUtils.isEmpty(value)) return 0;
            return Long.parseLong(mProperties.getProperty(key));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private float getFloat(String key, float defaultValue) {
        String value;
        try {
            value = mProperties.getProperty(key);
            if (TextUtils.isEmpty(value)) return 0;
            return Float.parseFloat(mProperties.getProperty(key));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private double getDouble(String key, double defaultValue) {
        String value;
        try {
            value = mProperties.getProperty(key);
            if (TextUtils.isEmpty(value)) return 0;
            return Double.parseDouble(mProperties.getProperty(key));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private boolean getBoolean(String key, boolean defaultValue) {
        String value;
        try {
            value = mProperties.getProperty(key);
            return !TextUtils.isEmpty(value) && Boolean.parseBoolean(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private String getString(String key, String defaultValue) {
        String result = null;
        switch (initType()) {
            case OPEN_TYPE_ASSETS:
                try {
                    result = new String(mProperties.getProperty(key, defaultValue).getBytes("ISO-8859-1"), DEFAULT_CODE);
                } catch (UnsupportedEncodingException e) {
                    return defaultValue;
                }
                break;
            case OPEN_TYPE_DATA:
                result = mProperties.getProperty(key, defaultValue);
                break;
        }
        return result;
    }

    private Object getObject(String key, Class<?> clazz, Object defaultValue) {
        Object object;
        try {
            String property = mProperties.getProperty(key);
            if (gson == null) gson = new Gson();
            object = gson.fromJson(property, clazz);
        } catch (Exception e) {
            L.e(initTag(), e.getMessage());
            return null;
        }
        return object;
    }

    /**
     * 所有属性写入到properties里
     */
    private void writePropertiesValues() {
        Class<? extends QsProperties> thisClass = this.getClass();
        Field[] fields = thisClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Property.class)) {
                field.setAccessible(true);
                String fieldName = field.getName();
                Property annotation = field.getAnnotation(Property.class);
                if (annotation.value().equals(DEFAULT_ANNOTATION_VALUE)) {
                    try {
                        Object value = field.get(this);
                        if (isCommonlyType(field.getType())) {
                            mProperties.put(fieldName, field.get(this) == null ? "" : String.valueOf(value));
                        } else {
                            if (gson == null) gson = new Gson();
                            String jsonStr = gson.toJson(value);
                            mProperties.put(fieldName, jsonStr);
                        }
                    } catch (Exception e) {
                        L.e(initTag(), "Properties写入错误:" + e.toString());
                    }
                } else {
                    try {
                        Object value = field.get(this);
                        if (isCommonlyType(field.getType())) {
                            mProperties.put(annotation.value(), field.get(this) == null ? "" : String.valueOf(value));
                        } else {
                            if (gson == null) gson = new Gson();
                            String jsonStr = gson.toJson(value);
                            mProperties.put(annotation.value(), jsonStr);
                        }
                    } catch (Exception e) {
                        L.e(initTag(), "Properties写入错误:" + e.toString());
                    }
                }
            }
        }
    }

    private boolean isCommonlyType(Class<?> clazz) {
        return clazz == String.class
                || clazz == int.class
                || clazz == Integer.class
                || clazz == boolean.class
                || clazz == Boolean.class
                || clazz == long.class
                || clazz == Long.class
                || clazz == float.class
                || clazz == Float.class
                || clazz == double.class
                || clazz == Double.class;
    }

    /**
     * 设置属性值 *
     */
    private void setFieldValue(Field field, String propertiesName) {
        Object value = getPropertyValue(field.getType(), propertiesName);
        if (value == null) return;
        try {
            field.set(this, value);
        } catch (Exception e) {
            L.e(initTag(), field.getName() + propertiesName);
        }
    }

    /**
     * 设置属性值
     */
    private void setFieldDefaultValue(Field field, String propertiesName) {
        Object value = getPropertyDefaultValue(field.getType());
        if (value == null) {
            return;
        }
        try {
            field.set(this, value);
        } catch (Exception e) {
            L.e(initTag(), "setFieldValue失败...属性名:" + propertiesName + " 文件名:" + field.getName());
        }
    }

    /**
     * 获取类型
     */
    private Object getPropertyValue(Class<?> clazz, String key) {
        if (clazz == String.class) {
            return getString(key, "");
        } else if (clazz == float.class || clazz == Float.class) {
            return getFloat(key, 0);
        } else if (clazz == double.class || clazz == Double.class) {
            return getDouble(key, 0);
        } else if (clazz == boolean.class || clazz == Boolean.class) {
            return getBoolean(key, false);
        } else if (clazz == int.class || clazz == Integer.class) {
            return getInt(key, 0);
        } else if (clazz == long.class || clazz == Long.class) {
            return getLong(key, 0);
        } else {
            return getObject(key, clazz, null);
        }
    }

    /**
     * 获取类型
     */
    private Object getPropertyDefaultValue(Class<?> clazz) {
        if (clazz == String.class) {
            return "";
        } else if (clazz == float.class || clazz == Float.class) {
            return 0;
        } else if (clazz == double.class || clazz == Double.class) {
            return 0;
        } else if (clazz == boolean.class || clazz == Boolean.class) {
            return false;
        } else if (clazz == int.class || clazz == Integer.class) {
            return 0;
        } else if (clazz == long.class || clazz == Long.class) {
            return 0;
        } else {
            return null;
        }
    }

    /**
     * 提交
     */
    public void commit() {
        commit(propertyCallback);
    }

    /**
     * 提交
     */
    public void commit(PropertyCallback callback) {
        OutputStream out = null;
        try {
            File file = new File(propertyFilePath, mPropertiesFileName + EXTENSION);
            if (!file.exists()) {
                boolean success = file.createNewFile();
                L.e(initTag(), "create new file isSuccess:" + success);
                if (!success) {
                    return;
                }
            }
            synchronized (mProperties) {
                out = new BufferedOutputStream(new FileOutputStream(file));
                writePropertiesValues();
                mProperties.store(out, "");
            }
            if (callback != null) {
                callback.onSuccess();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            StreamCloseUtils.close(out);
        }
    }

    /**
     * 清空文件内容
     */
    public void clear() {
        OutputStream out = null;
        try {
            File file = new File(propertyFilePath, mPropertiesFileName + EXTENSION);
            if (!file.exists()) {
                return;
            }
            synchronized (mProperties) {
                out = new BufferedOutputStream(new FileOutputStream(file));
                writeDefaultPropertiesValues();
                mProperties.store(out, "");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            StreamCloseUtils.close(out);
        }
    }

    /**
     * 恢复初始值
     */
    private void writeDefaultPropertiesValues() {
        L.i(initTag(), "writePropertiesValues()-写入所有的值");
        Class<? extends QsProperties> thisClass = this.getClass();
        Field[] fields = thisClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Property.class)) {
                field.setAccessible(true);
                String fieldName = field.getName();
                Property annotation = field.getAnnotation(Property.class);
                if (annotation.value().equals(DEFAULT_ANNOTATION_VALUE)) {
                    try {
                        mProperties.put(fieldName, "");
                        setFieldDefaultValue(field, fieldName);
                    } catch (Exception e) {
                        L.e(initTag(), "Properties写入错误:" + e.toString());
                    }
                } else {
                    try {
                        mProperties.put(annotation.value(), "");
                        setFieldDefaultValue(field, annotation.value());
                    } catch (Exception e) {
                        L.e(initTag(), "Properties写入错误:" + e.toString());
                    }
                }
            }
        }
    }
}
