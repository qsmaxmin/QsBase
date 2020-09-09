package com.qsmaxmin.qsbase.common.widget.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.qsmaxmin.annotation.QsNotProguard;
import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.utils.QsHelper;
import com.qsmaxmin.qsbase.plugin.bind.QsIBindBundle;
import com.qsmaxmin.qsbase.plugin.bind.QsIBindView;
import com.qsmaxmin.qsbase.plugin.event.QsIBindEvent;

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
public abstract class QsDialogFragment extends DialogFragment implements QsIBindView, QsIBindBundle, QsIBindEvent, QsNotProguard {

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
        bindBundleByQsPlugin(getArguments());
        if (getDialog() != null) {
            getDialog().setCanceledOnTouchOutside(true);
            getDialog().setCancelable(true);
        }
        View customView = inflater.inflate(layoutId(), null);
        bindViewByQsPlugin(customView);
        if (isOpenEventBus()) {
            bindEventByQsPlugin();
        }
        return customView;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        if (isOpenEventBus()) {
            unbindEventByQsPlugin();
        }
    }

    @Override public void bindBundleByQsPlugin(Bundle bundle) {
    }

    @Override public void bindViewByQsPlugin(View view) {
    }

    @Override public boolean isOpenEventBus() {
        return true;
    }

    @Override public void bindEventByQsPlugin() {
    }

    @Override public void unbindEventByQsPlugin() {
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

    public void show(final FragmentManager manager, Bundle bundle) {
        if (isAdded()) {
            L.e(initTag(), "show......dialog is added");
            return;
        }
        if (bundle != null) setArguments(bundle);
        if (QsHelper.isMainThread()) {
            show(manager, getClass().getSimpleName());
        } else {
            QsHelper.post(new Runnable() {
                @Override public void run() {
                    show(manager, getClass().getSimpleName());
                }
            });
        }
    }
}
