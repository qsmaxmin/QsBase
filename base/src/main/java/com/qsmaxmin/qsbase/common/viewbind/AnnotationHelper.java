package com.qsmaxmin.qsbase.common.viewbind;

import android.os.Bundle;
import android.util.LruCache;
import android.view.View;

import com.qsmaxmin.qsbase.common.log.L;

/**
 * @CreateBy qsmaxmin
 * @Date 2019/6/6 17:35
 * @Description
 */
class AnnotationHelper {
    private static LruCache<Class<?>, AnnotationExecutor> viewCache = new LruCache<>(200);

    static <T> void bindView(T target, View view) {
        if (target == null || view == null) return;
        AnnotationExecutor<T> executor = getExecutor(target.getClass());
        executor.bindView(target, view);
    }

    static <T> void bindBundle(T target, Bundle bundle) {
        if (target == null || bundle == null) return;
        AnnotationExecutor<T> executor = getExecutor(target.getClass());
        executor.bindBundle(target, bundle);
    }

    private static <T> AnnotationExecutor<T> getExecutor(Class<?> clazz) {
        AnnotationExecutor executor = viewCache.get(clazz);
        if (executor == null) {
            try {
                long startTime = 0;
                if (L.isEnable()) startTime = System.nanoTime();

                String className = getExecuteClassName(clazz);
                Class<?> myClass = Class.forName(className);

                executor = (AnnotationExecutor) myClass.newInstance();
                viewCache.put(clazz, executor);
                if (L.isEnable()) {
                    long endTime = System.nanoTime();
                    L.i(clazz.getSimpleName(), "create new AnnotationExecutor by class(" + clazz.getName() + "), cache size:" + viewCache.size() + ", use time:" + (endTime - startTime) / 1000000f + "ms");
                }
            } catch (Exception e) {
                executor = new DefaultExecutor();
                viewCache.put(clazz, executor);
                if (L.isEnable()) L.i(clazz.getSimpleName(), "(" + clazz.getName() + ")Annotation is empty, so create default AnnotationExecutor");
            }
        }
        return executor;
    }

    private static String getExecuteClassName(Class clazz) {
        String name = clazz.getName();
        int index_$ = name.indexOf('$');
        if (index_$ > 0) {//内部类
            int pointIndex = name.lastIndexOf('.');
            String packageName = name.substring(0, pointIndex);
            String simpleName = name.substring(index_$ + 1);
            return packageName + "." + simpleName + "_QsAnn";
        } else {
            return name + "_QsAnn";
        }
    }
}
