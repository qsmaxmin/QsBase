package com.qsmaxmin.qsbase.mvvm;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qsmaxmin.qsbase.common.viewbind.OnKeyDownListener;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/6/21 15:00
 * @Description
 */

public interface MvIActivity extends MvIView {

    boolean isBlackIconStatusBar();

    boolean isTransparentStatusBar();

    boolean isTransparentNavigationBar();

    boolean onKeyDown(KeyEvent event, int keyCode);

    void setOnKeyDownListener(OnKeyDownListener listener);

    void setActivityTitle(CharSequence title, int type);

    View onCreateActionbarView(LayoutInflater inflater, ViewGroup parent);
}
