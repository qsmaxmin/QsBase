package com.qsmaxmin.qsbase.common.widget.dialog;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

/**
 * @CreateBy qsmaxmin
 * @Date 17/8/3  上午12:35
 * @Description
 */
public abstract class QsProgressDialog {
    private boolean cancelable;

    public abstract View onCreateContentView(LayoutInflater inflater, ViewGroup parent);

    public abstract void onSetMessage(CharSequence text);

    public void show(@NonNull Activity activity, @NonNull View progressView) {
        showView(activity, progressView);
    }

    public void hide(@NonNull Activity activity, @NonNull View progressView) {
        hideView(activity, progressView);
    }

    protected final void showView(@NonNull Activity activity, @NonNull View progressView) {
        if (progressView.getParent() == null) {
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            decorView.addView(progressView);
        }
    }

    protected final void hideView(@NonNull Activity activity, @NonNull View progressView) {
        if (progressView.getParent() != null) {
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            decorView.removeView(progressView);
        }
    }

    /**
     * 当调用show方法时View已经被添加到Activity
     * 延迟一定ms后才让View显示出来
     * 适合的场景，如调用show方法后立即调用hidden，此时loading窗一闪而过，而延迟300ms就不会出现这种情况
     */
    public long getDelayedShowingTime() {
        return 0;
    }

    final void setCancelAble(boolean cancelable) {
        this.cancelable = cancelable;
    }

    public final boolean isCancelable() {
        return cancelable;
    }
}
