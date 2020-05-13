package com.qsmaxmin.qsbase.common.viewbind;

import android.os.Bundle;
import androidx.annotation.NonNull;
import android.util.LruCache;
import android.view.View;

import com.qsmaxmin.ann.AnnotationExecutorFinder;
import com.qsmaxmin.ann.viewbind.ViewAnnotationExecutor;
import com.qsmaxmin.qsbase.common.log.L;

/**
 * @CreateBy qsmaxmin
 * @Date 2019/6/6 17:35
 * @Description
 */
public class ViewBindHelper {
    private static LruCache<Class<?>, ViewAnnotationExecutor> viewCache = new LruCache<>(300);
    private static ViewAnnotationExecutor                     emptyExecutor;

    public static <T> void bindView(T target, View view) {
        if (target == null || view == null) return;
        ViewAnnotationExecutor<T> executor = getExecutor(target.getClass());
        executor.bindView(target, view);
    }

    public static <T> void bindBundle(T target, Bundle bundle) {
        if (target == null || bundle == null) return;
        ViewAnnotationExecutor<T> executor = getExecutor(target.getClass());
        executor.bindBundle(target, bundle);
    }

    //61.90854, 11947.418, 2297.3909
    @NonNull @SuppressWarnings("unchecked")
    private static <T> ViewAnnotationExecutor<T> getExecutor(Class<?> clazz) {
        ViewAnnotationExecutor executor = viewCache.get(clazz);
        if (executor == null) {
            long startTime = 0;
            if (L.isEnable()) startTime = System.nanoTime();
            executor = AnnotationExecutorFinder.getViewAnnotationExecutor(clazz);
            if (executor != null) {
                viewCache.put(clazz, executor);
                if (L.isEnable()) {
                    long endTime = System.nanoTime();
                    L.i(clazz.getSimpleName(), "create new ViewAnnotationExecutor by class(" + clazz.getName() + "), cache size:" + viewCache.size() + ", use time:" + (endTime - startTime) / 1000000f + "ms");
                }
            } else {
                if (emptyExecutor == null) {
                    emptyExecutor = new ViewAnnotationExecutor();
                }
                executor = emptyExecutor;
                viewCache.put(clazz, executor);
                if (L.isEnable()) {
                    long endTime = System.nanoTime();
                    L.i(clazz.getSimpleName(), "(" + clazz.getName() + ")Annotation is empty, so return default ViewAnnotationExecutor" + ", use time:" + (endTime - startTime) / 1000000f + "ms");
                }
            }
        }
        return executor;
    }

}
