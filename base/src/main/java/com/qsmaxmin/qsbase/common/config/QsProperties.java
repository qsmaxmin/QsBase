package com.qsmaxmin.qsbase.common.config;

import androidx.annotation.NonNull;

import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.model.QsNotProguard;

/**
 * @CreateBy sky
 * @Date 2017/7/3 9:15
 * @Description
 */

public abstract class QsProperties implements QsNotProguard {
    private PropertiesEngine<QsProperties> engine;

    public String initTag() {
        return L.isEnable() ? getClass().getSimpleName() : "QsProperties";
    }

    private QsProperties() {
    }

    public QsProperties(@NonNull String key) {
        engine = new PropertiesEngine<>(this, key);
    }

    /**
     * 提交
     */
    public void commit() {
        engine.commit();
    }

    /**
     * 清空文件内容
     */
    public void clear() {
        engine.clear();
    }
}
