package com.qsmaxmin.qsbase.common.viewbind;

import android.os.Bundle;
import android.view.View;

import com.qsmaxmin.qsbase.common.ann.AnnotationHelper;

/**
 * @CreateBy qsmaxmin
 * @Date 2019/6/6 17:35
 * @Description
 */
@SuppressWarnings("unchecked")
public class ViewBindHelper {

    public static <T> void bindView(T target, View view) {
        if (target == null || view == null) return;
        ViewAnnotationExecutor<T> executor = AnnotationHelper.getViewAnnotationExecutor(target.getClass());
        executor.bindView(target, view);
    }

    public static <T> void bindBundle(T target, Bundle bundle) {
        if (target == null || bundle == null) return;
        ViewAnnotationExecutor<T> executor = AnnotationHelper.getViewAnnotationExecutor(target.getClass());
        executor.bindBundle(target, bundle);
    }

}
