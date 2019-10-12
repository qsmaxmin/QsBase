package com.qsmaxmin.qsbase.common.viewbind;

import android.os.Bundle;
import android.view.View;

/**
 * @CreateBy qsmaxmin
 * @Date 2019/6/6 17:42
 * @Description
 */
public interface AnnotationExecutor<T> {
    void bindView(T target, View view, boolean forceBind);

    void bindBundle(T target, Bundle bundle);
}
