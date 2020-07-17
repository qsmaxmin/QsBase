package com.qsmaxmin.qsbase.mvp;

import android.view.KeyEvent;

import com.qsmaxmin.qsbase.common.viewbind.OnKeyDownListener;

import androidx.annotation.LayoutRes;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/6/21 15:00
 * @Description
 */

public interface QsIActivity extends QsIView {

    @LayoutRes int actionbarLayoutId();

    boolean isBlackIconStatusBar();

    boolean isTransparentStatusBar();

    boolean isTransparentNavigationBar();

    boolean onKeyDown(KeyEvent event, int keyCode);

    void setOnKeyDownListener(OnKeyDownListener listener);
}
