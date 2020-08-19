package com.qsmaxmin.qsbase.plugin.property;

import java.util.Map;

/**
 * @CreateBy administrator
 * @Date 2020/8/19 15:10
 * @Description for QsTransform
 */
public interface QsIProperty {

    /**
     * auto generate code by QsTransform
     */
    void readPropertiesByQsPlugin(Map map);

    /**
     * auto generate code by QsTransform
     */
    void savePropertiesByQsPlugin();

    /**
     * auto generate code by QsTransform
     */
    void clearPropertiesByQsPlugin();
}
