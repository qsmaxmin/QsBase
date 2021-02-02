package com.qsmaxmin.qsbase.common.widget.dialog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @CreateBy qsmaxmin
 * @Date 17/8/3  上午12:35
 * @Description
 */
public abstract class QsProgressDialog {

    public abstract View onCreateContentView(LayoutInflater inflater, ViewGroup parent);

    public abstract void onSetMessage(CharSequence text);

    public void onShowing() {
        //custom your logic
    }

    public void onHidden() {
        //custom your logic
    }
}
