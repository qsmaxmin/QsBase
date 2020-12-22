package com.qsmaxmin.qsbase.mvvm;

import android.view.View;

/**
 * @CreateBy qsmaxmin
 * @Date 2020/12/22 14:52
 * @Description view层超类
 */
public interface IView {
    void onViewClicked(View view);

    void onViewClicked(View view, long interval);
}
