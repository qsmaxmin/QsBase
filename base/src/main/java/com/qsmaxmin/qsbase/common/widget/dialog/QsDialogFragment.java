package com.qsmaxmin.qsbase.common.widget.dialog;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.common.aspect.ThreadPoint;
import com.qsmaxmin.qsbase.common.aspect.ThreadType;
import com.qsmaxmin.qsbase.common.log.L;
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
        if (getDialogTheme() != 0) setStyle(DialogFragment.STYLE_NO_TITLE, getDialogTheme());
    }

    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().getDecorView().setPadding(0, 0, 0, 0);
            setAttribute(getDialog().getWindow().getAttributes());
        }
        initData();
    }

    @Override @CallSuper @NonNull public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewBindHelper.bindBundle(this, getArguments());
        if (getDialog() != null) {
            getDialog().setCanceledOnTouchOutside(true);
            getDialog().setCancelable(true);
        }
        View customView = inflater.inflate(layoutId(), null);
        ViewBindHelper.bindView(this, customView);
        return customView;
    }

    @Override @CallSuper public void onDestroyView() {
        super.onDestroyView();
        ViewBindHelper.unbind(this);
    }

    protected int getDialogTheme() {
        return R.style.QsDialogTheme_FullScreen_TranslucentStatus;
    }

    protected void setAttribute(WindowManager.LayoutParams params) {
    }

    protected String initTag() {
        return L.isEnable() ? getClass().getSimpleName() : "QsDialogFragment";
    }

    public void onViewClick(View view) {
    }

    protected abstract int layoutId();

    protected abstract void initData();

    public void show() {
        show(QsHelper.getScreenHelper().currentActivity(), null);
    }

    public void show(Bundle bundle) {
        show(QsHelper.getScreenHelper().currentActivity(), bundle);
    }

    public void show(FragmentActivity activity) {
        show(activity, null);
    }

    @ThreadPoint(ThreadType.MAIN)
    public void show(FragmentActivity activity, Bundle bundle) {
        if (activity == null || activity.isFinishing()) {
            L.e(initTag(), "activity is null or activity is finished!");
            return;
        }
        L.i(initTag(), "show......activity:" + activity.getClass().getSimpleName());
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        if (fragmentManager != null) {
            if (isAdded()) {
                L.e(initTag(), "show......dialog is added");
                return;
            }
            if (bundle != null) setArguments(bundle);
            fragmentManager.beginTransaction().add(this, getClass().getSimpleName()).commitAllowingStateLoss();
        } else {
            L.e(initTag(), "show......fragmentManager is null");
        }
    }
}
