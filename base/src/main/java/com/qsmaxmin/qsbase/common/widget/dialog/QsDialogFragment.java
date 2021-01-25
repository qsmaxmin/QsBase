package com.qsmaxmin.qsbase.common.widget.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.qsmaxmin.annotation.QsNotProguard;
import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.utils.ViewHelper;
import com.qsmaxmin.qsbase.mvvm.IView;
import com.qsmaxmin.qsbase.plugin.bind.QsIBindBundle;
import com.qsmaxmin.qsbase.plugin.bind.QsIBindView;
import com.qsmaxmin.qsbase.plugin.event.QsIBindEvent;

import java.lang.reflect.Field;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * @CreateBy qsmaxmin
 * @Date 17/8/3  上午12:35
 * @Description
 */
public abstract class QsDialogFragment extends DialogFragment implements IView, QsIBindView, QsIBindBundle, QsIBindEvent, QsNotProguard {
    private SimpleClickListener listener;
    private boolean             isShow;

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

    @Override public final void onViewClicked(@NonNull View view) {
        onViewClicked(view, 400);
    }

    @Override public final void onViewClicked(@NonNull View view, long interval) {
        if (interval > 0 && ViewHelper.isFastClick(interval)) return;
        onViewClick(view);
    }


    @Override public final void loading() {
        if (getActivity() instanceof IView) {
            ((IView) getActivity()).loading();
        }
    }

    @Override public final void loading(int resId) {
        if (getActivity() instanceof IView) {
            ((IView) getActivity()).loading(resId);
        }
    }

    @Override public final void loading(String message) {
        if (getActivity() instanceof IView) {
            ((IView) getActivity()).loading(message);
        }
    }

    @Override public final void loading(boolean cancelAble) {
        if (getActivity() instanceof IView) {
            ((IView) getActivity()).loading(cancelAble);
        }
    }

    @Override public final void loading(int resId, boolean cancelAble) {
        if (getActivity() instanceof IView) {
            ((IView) getActivity()).loading(resId, cancelAble);
        }
    }

    @Override public final void loading(String message, boolean cancelAble) {
        if (getActivity() instanceof IView) {
            ((IView) getActivity()).loading(message, cancelAble);
        }
    }

    @Override public final void loadingClose() {
        if (getActivity() instanceof IView) {
            ((IView) getActivity()).loadingClose();
        }
    }

    @Nullable @Override public final Context getContext() {
        return super.getContext();
    }

    @Override public final void activityFinish() {
        if (getActivity() instanceof IView) {
            ((IView) getActivity()).activityFinish();
        }
    }

    @Override public final void activityFinish(int enterAnim, int exitAnim) {
        if (getActivity() instanceof IView) {
            ((IView) getActivity()).activityFinish(enterAnim, exitAnim);
        }
    }

    @Override public final void activityFinish(boolean finishAfterTransition) {
        if (getActivity() instanceof IView) {
            ((IView) getActivity()).activityFinish(finishAfterTransition);
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

    public final QsDialogFragment setClickListener(SimpleClickListener listener) {
        this.listener = listener;
        return this;
    }

    protected final SimpleClickListener getClickListener() {
        return listener;
    }

    public final void show(FragmentActivity activity) {
        show(activity, null);
    }

    public final void show(FragmentActivity activity, Bundle bundle) {
        if (activity == null || activity.isFinishing()) {
            L.e(initTag(), "activity is null or activity is finished!");
            return;
        }
        show(activity.getSupportFragmentManager(), bundle);
    }

    public final void show(Fragment fragment) {
        show(fragment, null);
    }

    public final void show(Fragment fragment, Bundle bundle) {
        show(fragment.getFragmentManager(), bundle);
    }

    public final void show(final FragmentManager manager, Bundle bundle) {
        showInner(manager, bundle, getClass().getSimpleName());
    }

    @Override public final void show(@NonNull FragmentManager manager, @Nullable String tag) {
        showInner(manager, null, tag);
    }

    private void showInner(FragmentManager manager, Bundle bundle, String tag) {
        if (manager == null) {
            L.e(initTag(), "show......fragmentManager is null");
            return;
        }
        if (isShow || isAdded()) {
            L.e(initTag(), "current dialog is showing...");
            return;
        }
        isShow = true;
        if (bundle != null) setArguments(bundle);

        setFiledValue("mDismissed", false);
        setFiledValue("mShownByMe", true);
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }

    private void setFiledValue(String fieldName, boolean value) {
        try {
            Field shown = DialogFragment.class.getDeclaredField(fieldName);
            shown.setAccessible(true);
            shown.set(this, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override public void dismiss() {
        isShow = false;
        try {
            super.dismiss();
        } catch (Exception e) {
            if (L.isEnable()) L.e(initTag(), e);
        }
    }

    @Override public void dismissAllowingStateLoss() {
        isShow = false;
        try {
            super.dismissAllowingStateLoss();
        } catch (Exception e) {
            if (L.isEnable()) L.e(initTag(), e);
        }
    }

    @Override public void onCancel(@NonNull DialogInterface dialog) {
        isShow = false;
        super.onCancel(dialog);
    }

    @Override public void onDestroyView() {
        isShow = false;
        super.onDestroyView();
        unbindEventByQsPlugin();
        L.i(initTag(), "onDestroyView....... set is showing:false");
    }

    @Override public void onDetach() {
        isShow = false;
        super.onDetach();
    }

    public final boolean isShowing() {
        return isShow;
    }

    @Override public final void intent2Activity(Class clazz) {
        intent2Activity(clazz, null, 0, null, 0, 0);
    }

    @Override public final void intent2Activity(Class clazz, int requestCode) {
        intent2Activity(clazz, null, requestCode, null, 0, 0);
    }

    @Override public final void intent2Activity(Class clazz, Bundle bundle) {
        intent2Activity(clazz, bundle, 0, null, 0, 0);
    }

    @Override public final void intent2Activity(Class clazz, Bundle bundle, int requestCode) {
        intent2Activity(clazz, bundle, requestCode, null, 0, 0);
    }

    @Override public final void intent2Activity(Class clazz, Bundle bundle, int inAnimId, int outAnimId) {
        intent2Activity(clazz, bundle, 0, null, inAnimId, outAnimId);
    }

    @Override public final void intent2Activity(Class clazz, Bundle bundle, ActivityOptionsCompat optionsCompat) {
        intent2Activity(clazz, bundle, 0, optionsCompat, 0, 0);
    }

    @Override public final void intent2Activity(Class clazz, Bundle bundle, int requestCode, ActivityOptionsCompat optionsCompat) {
        intent2Activity(clazz, bundle, requestCode, optionsCompat, 0, 0);
    }

    @Override public final void intent2Activity(Class clazz, Bundle bundle, int requestCode, ActivityOptionsCompat optionsCompat, int enterAnim, int existAnim) {
        ViewHelper.intent2Activity(this, clazz, bundle, requestCode, optionsCompat, enterAnim, existAnim);
    }
}
