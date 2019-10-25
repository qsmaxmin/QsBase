package com.qsmaxmin.qsbase.common.viewbind;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.LruCache;
import android.view.View;

import com.qsmaxmin.ann.viewbind.AnnotationExecutorFinder;
import com.qsmaxmin.qsbase.common.log.L;

/**
 * @CreateBy qsmaxmin
 * @Date 2019/6/6 17:35
 * @Description 将view绑定到layer
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
            long startTime = 0;
            if (L.isEnable()) startTime = System.nanoTime();
            Object newExecutor = finder.getViewAnnotationExecutor(clazz.getName());
            if (newExecutor == null) {
                if (L.isEnable()) {
                    long endTime = System.nanoTime();
                    L.i("ViewBindHelper", "current layer(" + clazz.getName() + ") does not need to bind view, logic use time:" + (endTime - startTime) / 1000000f + "ms");
                }
                return null;
            } else {
                if (L.isEnable()) {
                    long endTime = System.nanoTime();
                    L.i("ViewBindHelper", "create new ViewAnnotationExecutor by:" + clazz.getName()
                            + ", cache size:" + executorCache.size() + ", use time:" + (endTime - startTime) / 1000000f + "ms");
                }
                executor = (ViewAnnotationExecutor) newExecutor;
                executorCache.put(clazz, executor);
            }
        }
        return executor;
    }
}
