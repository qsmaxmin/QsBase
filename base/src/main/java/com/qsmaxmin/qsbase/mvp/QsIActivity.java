package com.qsmaxmin.qsbase.mvp;

import android.view.KeyEvent;

import com.qsmaxmin.qsbase.common.viewbind.OnKeyDownListener;
import com.qsmaxmin.qsbase.common.widget.dialog.QsProgressDialog;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

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

    boolean onKeyDown(@NonNull KeyEvent event, int keyCode);

    void setOnKeyDownListener(OnKeyDownListener listener);

    void setActivityTitle(CharSequence title, int type);

    QsProgressDialog getLoadingDialog();
}
