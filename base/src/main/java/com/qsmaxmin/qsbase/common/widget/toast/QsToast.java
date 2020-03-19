package com.qsmaxmin.qsbase.common.widget.toast;

import android.content.Context;
import android.os.Looper;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.widget.Toast;

import com.qsmaxmin.qsbase.common.utils.QsHelper;

import java.lang.ref.SoftReference;

public class QsToast {

    private static SoftReference<Toast> lastToast = null;

    /**
     * 简单Toast 消息弹出
     */
    public static void show(final String msg) {
        show(msg, Toast.LENGTH_SHORT);
    }


    public static void show(@StringRes int resId) {
        if (resId != 0) show(QsHelper.getString(resId));
    }

    public static void show(@StringRes int resId, int duration) {
        if (resId != 0) show(QsHelper.getString(resId), duration);
    }

    public static void show(final String msg, int duration) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        // 判断是否在主线程
        if (Looper.getMainLooper().getThread() != Thread.currentThread()) {
            QsHelper.post(new Runnable() {
                @Override public void run() {
                    showToast(QsHelper.getApplication(), msg, Toast.LENGTH_SHORT);
                }
            });
        } else {
            showToast(QsHelper.getApplication(), msg, duration);
        }
    }

    /**
     * 弹出提示
     */
    private static void showToast(Context context, String text, int duration) {
        try {
            int size = QsHelper.getScreenHelper().getActivityStack().size();
            if (size > 0) {
                cancelLastToast();
                Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
                toast.setDuration(duration);
                toast.show();
                lastToast = new SoftReference<>(toast);
            } else {
                cancelLastToast();
            }
        } catch (Exception ignored) {
        }
    }

    private static void cancelLastToast() {
        if (lastToast != null) {
            Toast toast = lastToast.get();
            if (toast != null) toast.cancel();
        }
    }
}
