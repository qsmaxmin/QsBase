package com.qsmaxmin.qsbase.common.viewbind;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.LruCache;
import android.view.View;

import com.qsmaxmin.ann.viewbind.AnnotationExecutorFinder;

/**
 * @CreateBy qsmaxmin
 * @Date 2019/6/6 17:35
 * @Description
 */
public class ViewBindHelper {
    private final static LruCache<Class, ViewAnnotationExecutor> executorCache = new LruCache<>(400);
    private final static AnnotationExecutorFinder                finder        = new AnnotationExecutorFinder();

    public static <T> void bindView(T target, View view) {
        if (target != null && view != null) {
            ViewAnnotationExecutor<T> executor = getExecutor(target.getClass());
            if (executor != null) executor.bindView(target, view);
        }
    }

    public static <T> void bindBundle(T target, Bundle bundle) {
        if (target != null && bundle != null) {
            ViewAnnotationExecutor<T> executor = getExecutor(target.getClass());
            if (executor != null) executor.bindBundle(target, bundle);
        }
    }

    @SuppressWarnings("unchecked") @Nullable
    private static <T> ViewAnnotationExecutor<T> getExecutor(Class<?> clazz) {
        ViewAnnotationExecutor executor = executorCache.get(clazz);
        if (executor == null) {
            Object newExecutor = finder.getViewAnnotationExecutor(clazz.getName());
            if (newExecutor == null) {
                return null;
            } else {
                executor = (ViewAnnotationExecutor) newExecutor;
                executorCache.put(clazz, executor);
            }
        }
        return executor;
    }
}
