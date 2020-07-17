package com.qsmaxmin.qsbase.common.widget.dialog;

import android.os.Bundle;
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

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

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
            int[] padding = getPadding();
            getDialog().getWindow().getDecorView().setPadding(padding[0], padding[1], padding[2], padding[3]);
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

    protected int getDialogTheme() {
        return R.style.QsDialogTheme_FullScreen_TranslucentStatus;
    }

    protected int[] getPadding() {
        return new int[]{0, 0, 0, 0};
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

    public void show(FragmentActivity activity, Bundle bundle) {
        if (activity == null || activity.isFinishing()) {
            L.e(initTag(), "activity is null or activity is finished!");
            return;
        }
        show(activity.getSupportFragmentManager(), bundle);
    }

    public void show(Fragment fragment) {
        show(fragment, null);
    }

    public void show(Fragment fragment, Bundle bundle) {
        show(fragment.getFragmentManager(), bundle);
    }

    @ThreadPoint(ThreadType.MAIN)
    public void show(FragmentManager manager, Bundle bundle) {
        if (isAdded()) {
            L.e(initTag(), "show......dialog is added");
            return;
        }
        if (bundle != null) setArguments(bundle);
        show(manager, getClass().getSimpleName());
    }
}
