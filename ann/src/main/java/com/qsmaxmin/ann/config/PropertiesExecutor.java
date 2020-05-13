package com.qsmaxmin.ann.config;

import android.content.SharedPreferences;


/**
 * @CreateBy qsmaxmin
 * @Date 2019/7/22 15:28
 * @Description 该代码由QsPlugin动态生成
 */
public abstract class PropertiesExecutor<T> {
    public abstract void bindConfig(T config, SharedPreferences sp);

    public abstract void commit(T config, SharedPreferences sp);
}
