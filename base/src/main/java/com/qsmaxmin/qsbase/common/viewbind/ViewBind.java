package com.qsmaxmin.qsbase.common.viewbind;

import android.view.View;

/**
 * Created by wyouflf on 15/10/29.
 * view注入接口
 */
public interface ViewBind {

    /**
     * 注入view holder
     */
    void bind(Object handler, View view);
}
