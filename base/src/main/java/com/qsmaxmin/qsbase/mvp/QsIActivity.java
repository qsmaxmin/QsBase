package com.qsmaxmin.qsbase.mvp;

import com.qsmaxmin.qsbase.common.viewbind.OnKeyDownListener;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/6/21 15:00
 * @Description
 */

public interface QsIActivity extends QsIView {

    boolean isTransparentStatusBar();

    boolean isBlackIconStatusBar();

    boolean isTransparentNavigationBar();

    void setOnKeyDownListener(OnKeyDownListener listener);
}
