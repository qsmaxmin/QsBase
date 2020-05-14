package com.qsmaxmin.qsbase.common.config;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.qsmaxmin.qsbase.common.ann.AnnotationHelper;
import com.qsmaxmin.qsbase.common.utils.QsHelper;

/**
 * @CreateBy qsmaxmin
 * @Date 2018/12/7 10:07
 * @Description
 */
class PropertiesEngine<T> {
    private final T                     mConfig;
    private       SharedPreferences     sp;
    private       PropertiesExecutor<T> executor;

    PropertiesEngine(T config, String key) {
        this.mConfig = config;
        Application application = QsHelper.getApplication();
        sp = application.getSharedPreferences(key, Context.MODE_PRIVATE);
        executor = AnnotationHelper.getPropertiesExecutor(config.getClass());
        if (executor != null) {
            executor.bindConfig(config, sp);
        }
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
}
