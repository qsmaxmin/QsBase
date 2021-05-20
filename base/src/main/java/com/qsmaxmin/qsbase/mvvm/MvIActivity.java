package com.qsmaxmin.qsbase.mvvm;

import android.os.Bundle;
import android.view.KeyEvent;

import com.qsmaxmin.qsbase.common.viewbind.OnKeyDownListener;
import com.qsmaxmin.qsbase.common.viewbind.OnTouchListener;

import androidx.annotation.NonNull;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/6/21 15:00
 * @Description
 */

public interface MvIActivity extends MvIView {

    boolean isBlackIconStatusBar();

    boolean isTransparentStatusBar();

    boolean isTransparentNavigationBar();

    boolean isHideStatusNavigationBar();

    /**
     * 全屏时刘海屏页面适配
     */
    boolean isFullScreenFix();

    boolean onKeyDown(@NonNull KeyEvent event, int keyCode);

    void setOnKeyDownListener(OnKeyDownListener listener);

    void setActivityTitle(CharSequence title, int type);

    boolean isOpenSlidingToClose();

    void setAllowSlidingToClose(boolean allow);

    boolean isSlidingToCloseEnabled();

    void onReceivedEventFromFragment(int eventType, Bundle data);

    void setOnTouchListener(OnTouchListener listener);
}
