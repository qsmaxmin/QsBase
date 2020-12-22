package com.qsmaxmin.qsbase.common.widget.dialog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.qsmaxmin.annotation.QsNotProguard;
import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.utils.QsHelper;
import com.qsmaxmin.qsbase.common.utils.ViewHelper;
import com.qsmaxmin.qsbase.mvp.QsIView;
import com.qsmaxmin.qsbase.mvvm.IView;
import com.qsmaxmin.qsbase.plugin.bind.QsIBindBundle;
import com.qsmaxmin.qsbase.plugin.bind.QsIBindView;
import com.qsmaxmin.qsbase.plugin.event.QsIBindEvent;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

/**
 * @CreateBy qsmaxmin
 * @Date 17/8/3  上午12:35
 * @Description
 */
public abstract class QsDialogFragment extends DialogFragment implements IView, QsIBindView, QsIBindBundle, QsIBindEvent, QsNotProguard {
    private SimpleClickListener listener;

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
        View customView;
        if (layoutId() != 0) {
            customView = inflater.inflate(layoutId(), container, false);
        } else {
            customView = onCreateContentView(inflater, container);
        }
        bindViewByQsPlugin(customView);
        bindEventByQsPlugin();
        return customView;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbindEventByQsPlugin();
    }

    @CallSuper @Override public void bindBundleByQsPlugin(Bundle bundle) {
    }

    @CallSuper @Override public void bindViewByQsPlugin(View view) {
    }

    @CallSuper @Override public void bindEventByQsPlugin() {
    }

    @CallSuper @Override public void unbindEventByQsPlugin() {
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

    @Override public final void onViewClicked(View view) {
        onViewClicked(view, 400);
    }

    @Override public final void onViewClicked(View view, long interval) {
        if (interval > 0) {
            if (ViewHelper.isFastClick(interval)) return;
        }
        onViewClick(view);
    }

    @Override public final void loading() {
        if (getActivity() instanceof IView) {
            ((IView) getActivity()).loading();
        } else if (getActivity() instanceof QsIView) {
            ((QsIView) getActivity()).loading();
        }
    }

    @Override public final void loading(int resId) {
        if (getActivity() instanceof IView) {
            ((IView) getActivity()).loading(resId);
        } else if (getActivity() instanceof QsIView) {
            ((QsIView) getActivity()).loading(resId);
        }
    }

    @Override public final void loading(String message) {
        if (getActivity() instanceof IView) {
            ((IView) getActivity()).loading(message);
        } else if (getActivity() instanceof QsIView) {
            ((QsIView) getActivity()).loading(message);
        }
    }

    @Override public final void loading(boolean cancelAble) {
        if (getActivity() instanceof IView) {
            ((IView) getActivity()).loading(cancelAble);
        } else if (getActivity() instanceof QsIView) {
            ((QsIView) getActivity()).loading(cancelAble);
        }
    }

    @Override public final void loading(int resId, boolean cancelAble) {
        if (getActivity() instanceof IView) {
            ((IView) getActivity()).loading(resId, cancelAble);
        } else if (getActivity() instanceof QsIView) {
            ((QsIView) getActivity()).loading(resId, cancelAble);
        }
    }

    @Override public final void loading(String message, boolean cancelAble) {
        if (getActivity() instanceof IView) {
            ((IView) getActivity()).loading(message, cancelAble);
        } else if (getActivity() instanceof QsIView) {
            ((QsIView) getActivity()).loading(message, cancelAble);
        }
    }

    @Override public final void loadingClose() {
        if (getActivity() instanceof IView) {
            ((IView) getActivity()).loadingClose();
        } else if (getActivity() instanceof QsIView) {
            ((QsIView) getActivity()).loadingClose();
        }
    }

    @Nullable @Override public final Context getContext() {
        return super.getContext();
    }

    @Override public void activityFinish() {
        if (getActivity() instanceof IView) {
            ((IView) getActivity()).activityFinish();
        } else if (getActivity() instanceof QsIView) {
            ((QsIView) getActivity()).activityFinish();
        }
    }

    @Override public void activityFinish(int enterAnim, int exitAnim) {
        if (getActivity() instanceof IView) {
            ((IView) getActivity()).activityFinish(enterAnim, exitAnim);
        } else if (getActivity() instanceof QsIView) {
            ((QsIView) getActivity()).activityFinish(enterAnim, exitAnim);
        }
    }

    @Override public void activityFinish(boolean finishAfterTransition) {
        if (getActivity() instanceof IView) {
            ((IView) getActivity()).activityFinish(finishAfterTransition);
        } else if (getActivity() instanceof QsIView) {
            ((QsIView) getActivity()).activityFinish(finishAfterTransition);
        }
    }

    protected void onViewClick(View view) {
    }

    protected int layoutId() {
        return 0;
    }

    protected View onCreateContentView(LayoutInflater inflater, ViewGroup parent) {
        return null;
    }

    protected abstract void initData();

    public QsDialogFragment setClickListener(SimpleClickListener listener) {
        this.listener = listener;
        return this;
    }

    protected SimpleClickListener getClickListener() {
        return listener;
    }

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
            try {
                show(manager, getClass().getSimpleName());
            } catch (Exception ignored) {
            }

        } else {
            QsHelper.post(new Runnable() {
                @Override public void run() {
                    try {
                        show(manager, getClass().getSimpleName());
                    } catch (Exception ignored) {
                    }
                }
            });
        }
    }

    @Override public void dismiss() {
        if (QsHelper.isMainThread()) {
            dismissSuper();
        } else {
            QsHelper.post(new Runnable() {
                @Override public void run() {
                    dismissSuper();
                }
            });
        }
    }

    @Override public void dismissAllowingStateLoss() {
        if (QsHelper.isMainThread()) {
            dismissAllowingStateLossSuper();
        } else {
            QsHelper.post(new Runnable() {
                @Override public void run() {
                    dismissAllowingStateLossSuper();
                }
            });
        }
    }

    private void dismissAllowingStateLossSuper() {
        try {
            super.dismissAllowingStateLoss();
        } catch (Exception e) {
            if (L.isEnable()) L.e(initTag(), e);
        }
    }

    private void dismissSuper() {
        try {
            super.dismiss();
        } catch (Exception e) {
            if (L.isEnable()) L.e(initTag(), e);
        }
    }

    public final void intent2Activity(Class clazz) {
        intent2Activity(clazz, null, 0, null, 0, 0);
    }

    public final void intent2Activity(Class clazz, int requestCode) {
        intent2Activity(clazz, null, requestCode, null, 0, 0);
    }

    public final void intent2Activity(Class clazz, Bundle bundle) {
        intent2Activity(clazz, bundle, 0, null, 0, 0);
    }

    public final void intent2Activity(Class clazz, Bundle bundle, int inAnimId, int outAnimId) {
        intent2Activity(clazz, bundle, 0, null, inAnimId, outAnimId);
    }

    public final void intent2Activity(Class clazz, Bundle bundle, ActivityOptionsCompat optionsCompat) {
        intent2Activity(clazz, bundle, 0, optionsCompat, 0, 0);
    }

    public final void intent2Activity(Class clazz, Bundle bundle, int requestCode, ActivityOptionsCompat optionsCompat) {
        intent2Activity(clazz, bundle, requestCode, optionsCompat, 0, 0);
    }

    public final void intent2Activity(Class clazz, Bundle bundle, int requestCode, ActivityOptionsCompat optionsCompat, int inAnimId, int outAnimId) {
        FragmentActivity activity = getActivity();
        if (clazz != null && activity != null && !activity.isFinishing()) {
            Intent intent = new Intent();
            intent.setClass(activity, clazz);
            if (bundle != null) intent.putExtras(bundle);
            if (optionsCompat == null) {
                if (requestCode > 0) {
                    activity.startActivityForResult(intent, requestCode);
                } else {
                    activity.startActivity(intent);
                }
                if (inAnimId != 0 || outAnimId != 0) activity.overridePendingTransition(inAnimId, outAnimId);
            } else {
                if (requestCode > 0) {
                    ActivityCompat.startActivityForResult(activity, intent, requestCode, optionsCompat.toBundle());
                } else {
                    ActivityCompat.startActivity(activity, intent, optionsCompat.toBundle());
                }
            }
        }
    }
}
