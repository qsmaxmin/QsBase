package com.qsmaxmin.qsbase.common.ann;

import android.util.LruCache;

import com.qsmaxmin.qsbase.common.config.PropertiesExecutor;
import com.qsmaxmin.qsbase.common.viewbind.ViewAnnotationExecutor;

/**
 * @CreateBy administrator
 * @Date 2020/5/14 15:45
 * @Description Annotation帮助类，APT衔接层
 */
@QsAnn(isLibrary = true)
public class AnnotationHelper {
    private static final AnnotationHelper                           helper        = new AnnotationHelper();
    private final        ViewAnnotationExecutor                     emptyExecutor = new ViewAnnotationExecutor();
    private final        LruCache<Class<?>, ViewAnnotationExecutor> viewCache     = new LruCache<>(300);

    private AnnotationHelper() {
    }

    public static ViewAnnotationExecutor getViewAnnotationExecutor(Class<?> clazz) {
        ViewAnnotationExecutor executor = helper.viewCache.get(clazz);
        if (executor != null) {
            return executor;
        }

        executor = AnnotationExecutorFinder.getViewAnnotationExecutor(clazz);
        if (executor == null) {
            helper.viewCache.put(clazz, helper.emptyExecutor);
            return helper.emptyExecutor;
        } else {
            helper.viewCache.put(clazz, executor);
            return executor;
        }
    }

    public static <T> PropertiesExecutor<T> getPropertiesExecutor(Class<?> aClass) {
        return AnnotationExecutorFinder.getPropertiesExecutor(aClass);
    }

    public static void release() {
        helper.viewCache.evictAll();
    }
}
