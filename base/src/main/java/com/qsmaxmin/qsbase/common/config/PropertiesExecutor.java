package com.qsmaxmin.qsbase.common.config;

import android.content.SharedPreferences;

/**
 * @CreateBy qsmaxmin
 * @Date 2019/7/22 15:28
 * @Description
 */
public interface PropertiesExecutor<T> {

    void bindConfig(T config, SharedPreferences sp);

    void commit(T config, SharedPreferences sp);

    void clear(T config, SharedPreferences sp);
}
