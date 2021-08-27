package com.qsmaxmin.qsbase.plugin.route;

import java.util.HashMap;

/**
 * @CreateBy qsmaxmin
 * @Date 2021/8/26 11:28
 * @Description for QsTransform
 */
public class RouteDataHolder {
    private static final HashMap<String, Class<?>> classHolder = new HashMap<>();

    static Class<?> findClass(String key) {
        return classHolder.get(key);
    }

    public static HashMap<String, Class<?>> getData() {
        return classHolder;
    }
}
