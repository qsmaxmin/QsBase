package com.qsmaxmin.ann;


import com.qsmaxmin.ann.config.PropertiesExecutor;
import com.qsmaxmin.ann.event.EventExecutor;
import com.qsmaxmin.ann.viewbind.ViewAnnotationExecutor;

/**
 * 该代码由QsPlugin动态生成，拒绝外部修改
 */
public final class AnnotationExecutorFinder {

    public static <T> ViewAnnotationExecutor<T> getViewAnnotationExecutor(Class clazz) {
        return create(clazz, "_QsBind", true);
    }

    public static <T> PropertiesExecutor<T> getPropertiesExecutor(Class clazz) {
        return create(clazz, "_QsConfig", false);
    }

    public static <T> EventExecutor<T> getEventExecutor(Class clazz) {
        return create(clazz, "_QsEvent", false);
    }

    @SuppressWarnings("unchecked")
    public static <T> T create(Class clazz, String extraName, boolean supportInnerClass) {
        String className;
        String name = clazz.getName();
        int index_ = name.indexOf('$');
        if (index_ != -1) {
            if (supportInnerClass) {
                int pointIndex = name.lastIndexOf('.');
                String packageName = name.substring(0, pointIndex);
                String simpleName = name.substring(index_ + 1);
                className = packageName + "." + simpleName + extraName;
            } else {
                return null;
            }
        } else {
            className = name + extraName;
        }
        try {
            Class<?> myClass = Class.forName(className);
            return (T) myClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
