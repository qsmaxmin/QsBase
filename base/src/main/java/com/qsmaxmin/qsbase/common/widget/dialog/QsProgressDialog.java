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

    public void setMessage(CharSequence sequence) {
        this.mMessage = sequence;
    }

    public CharSequence getMessage() {
        return mMessage;
    }

    @Override protected void setAttribute(WindowManager.LayoutParams params) {
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
    }
}
