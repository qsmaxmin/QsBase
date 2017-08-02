package com.qsmaxmin.qsbase.common.widget.dialog;

import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * @CreateBy qsmaxmin
 * @Date 17/8/3  上午12:35
 * @Description
 */

public abstract class QsProgressDialog extends QsDialogFragment {

    @Override protected void setAttribute(WindowManager.LayoutParams params) {
        params.gravity = Gravity.CENTER;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
    }

    public void setMessage(CharSequence sequence) {
        View view = getView();
        if (view != null) {
            TextView message = (TextView) view.findViewById(android.R.id.message);
            message.setText(sequence);
        }
    }
}
