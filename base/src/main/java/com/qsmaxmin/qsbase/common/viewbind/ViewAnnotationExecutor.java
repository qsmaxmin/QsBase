package com.qsmaxmin.qsbase.common.viewbind;

import android.os.Bundle;
import android.view.View;

/**
 * @CreateBy qsmaxmin
 * @Date 2019/6/6 17:42
 * @Description
 */
@SuppressWarnings({"WeakerAccess", "unchecked"})
public class ViewAnnotationExecutor<T> {
    public void bindView(T target, View view) {
    }

    public void bindBundle(T target, Bundle bundle) {
    }

    public final <D extends View> D forceCastView(View view) {
        return (D) view;
    }

    public final <D> D forceCastObject(Object o) {
        return (D) o;
    }
}
