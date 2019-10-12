package com.qsmaxmin.qsbase.common.viewbind;

import android.os.Bundle;
import android.view.View;

/**
 * @CreateBy qsmaxmin
 * @Date 2019/6/6 17:42
 * @Description
 */
public class AnnotationExecutor<T> {
    public void bindView(T target, View view) {
    }

    public void bindBundle(T target, Bundle bundle) {
    }

    public final <D extends View> D forceCast(View view) {
        return (D) view;
    }
}
