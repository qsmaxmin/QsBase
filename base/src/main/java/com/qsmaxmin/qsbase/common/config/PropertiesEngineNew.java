package com.qsmaxmin.qsbase.common.config;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.qsmaxmin.ann.viewbind.AnnotationExecutorFinder;
import com.qsmaxmin.qsbase.common.utils.QsHelper;

/**
 * @CreateBy qsmaxmin
 * @Date 2018/12/7 10:07
 * @Description
 */
class PropertiesEngineNew<T> {
    private final T                     mConfig;
    private       SharedPreferences     sp;
    private       PropertiesExecutor<T> executor;

    @SuppressWarnings("unchecked") PropertiesEngineNew(T config, String key) {
        this.mConfig = config;
        Application application = QsHelper.getApplication();
        sp = application.getSharedPreferences(key, Context.MODE_PRIVATE);
        executor = (PropertiesExecutor<T>) AnnotationExecutorFinder.getPropertiesExecutor(config.getClass().getName());
        if (executor != null) executor.bindConfig(config, sp);

//        Class<?> configClass = config.getClass();
//        try {
//            Class<?> executeClazz = Class.forName(getExecuteClassName(configClass));
//            executor = (PropertiesExecutor<T>) executeClazz.newInstance();
//            executor.bindConfig(config, sp);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    void commit() {
        if (executor != null) {
            try {
                executor.commit(mConfig, sp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void clear() {
        if (sp != null) {
            SharedPreferences.Editor edit = sp.edit();
            edit.clear();
            edit.apply();
        }
    }

    private String getExecuteClassName(Class clazz) {
        String name = clazz.getName();
        int index_$ = name.indexOf('$');
        if (index_$ > 0) {//内部类
            throw new IllegalStateException("InnerClass not support");
        } else {
            return name + "_QsAnn";
        }
    }
}
