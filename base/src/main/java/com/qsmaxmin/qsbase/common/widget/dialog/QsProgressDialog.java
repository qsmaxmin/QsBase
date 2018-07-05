package com.qsmaxmin.qsbase.common.widget.dialog;

import android.view.Gravity;
import android.view.WindowManager;

/**
 * @CreateBy qsmaxmin
 * @Date 17/8/3  上午12:35
 * @Description
 */

public abstract class QsProgressDialog extends QsDialogFragment {

    private CharSequence mMessage;
    private boolean      isShow;

    @Override protected void setAttribute(WindowManager.LayoutParams params) {
        params.gravity = Gravity.CENTER;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
    }

    public void setMessage(CharSequence sequence) {
        this.mMessage = sequence;
    }

    public CharSequence getMessage() {
        return mMessage;
    }

    public boolean isShowing() {
        return isShow;
    }

    public void setIsShowing(boolean show) {
        this.isShow = show;
    }

    @Override public void dismissAllowingStateLoss() {
        super.dismissAllowingStateLoss();
        setIsShowing(false);
    }
}
