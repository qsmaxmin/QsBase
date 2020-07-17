package com.qsmaxmin.qsbase.common.widget.dialog;

import android.view.Gravity;
import android.view.WindowManager;

import com.qsmaxmin.qsbase.common.log.L;

/**
 * @CreateBy qsmaxmin
 * @Date 17/8/3  上午12:35
 * @Description
 */
public abstract class QsProgressDialog extends QsDialogFragment {
    private CharSequence mMessage;
    private boolean      isShow;

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
        L.i(initTag(), "setIsShowing....... showing:true");
    }

    @Override protected void setAttribute(WindowManager.LayoutParams params) {
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
    }

    @Override public void onDetach() {
        super.onDetach();
        setIsShowing(false);
        L.i(initTag(), "onDetach....... set is showing:false");
    }
}
