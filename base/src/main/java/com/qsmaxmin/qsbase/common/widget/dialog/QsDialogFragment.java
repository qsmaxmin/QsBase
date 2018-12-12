package com.qsmaxmin.qsbase.common.widget.dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.common.utils.QsHelper;
import com.qsmaxmin.qsbase.common.viewbind.ViewBindHelper;

/**
 * @CreateBy qsmaxmin
 * @Date 17/8/3  上午12:35
 * @Description
 */
public abstract class QsDialogFragment extends DialogFragment {

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getDialogTheme() > 0) setStyle(DialogFragment.STYLE_NO_TITLE, getDialogTheme());
    }

    @Override public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewBindHelper.bindBundle(this, getArguments());
        getDialog().setCanceledOnTouchOutside(true);
        View dialogView = getDialogView(inflater, container);
        ViewBindHelper.bindView(this, dialogView);
        return dialogView;
    }

    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final Window window = getDialog().getWindow();
        if (window != null) {
            window.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams params = window.getAttributes();
            setAttribute(params);
            window.setAttributes(params);
        }
    }

    protected void setAttribute(WindowManager.LayoutParams params) {
        params.gravity = Gravity.BOTTOM;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
    }

    @Override public void onStart() {
        super.onStart();
        initData();
    }

    protected int getDialogTheme() {
        return R.style.QsDialogTheme_Light;
    }

    protected abstract View getDialogView(LayoutInflater inflater, ViewGroup container);

    protected String initTag() {
        return QsHelper.getInstance().getApplication().isLogOpen() ? getClass().getSimpleName() : "QsDialogFragment";
    }

    public void onViewClick(View view) {
    }

    protected void initData() {

    }

    public void show() {
        QsHelper.getInstance().commitDialogFragment(this);
    }
}
