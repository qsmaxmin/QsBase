package com.qsmaxmin.qsbase.plugin.event;

import com.qsmaxmin.annotation.QsNotProguard;

/**
 * @CreateBy qsmaxmin
 * @Date 2020/8/18 16:36
 * @Description for QsTransform
 */
public interface QsIBindEvent extends QsNotProguard {

    /**
     * for QsTransform
     */
    void bindEventByQsPlugin();

    /**
     * for QsTransform
     */
    void unbindEventByQsPlugin();
}
