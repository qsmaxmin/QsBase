package com.qsmaxmin.qsbase.plugin.property;

import com.qsmaxmin.annotation.QsNotProguard;

import java.util.Map;

/**
 * @CreateBy qsmaxmin
 * @Date 2020/8/19 15:10
 * @Description for QsTransform
 */
public interface QsIProperty extends QsNotProguard {

    /**
     * auto generate code by QsTransform
     */
    void readPropertiesByQsPlugin(Map<String, ?> map);

    /**
     * auto generate code by QsTransform
     */
    void savePropertiesByQsPlugin();

    /**
     * auto generate code by QsTransform
     */
    void clearPropertiesByQsPlugin();
}
