package com.qsmaxmin.qsbase.common.widget.toast;

import android.content.Context;
import android.os.Looper;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.widget.Toast;

import com.qsmaxmin.qsbase.common.utils.QsHelper;

public class QsToast {

    private static Toast mToast = null;

    /**
     * 简单Toast 消息弹出
     */
    public static void show(final String msg) {
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
            showToast(QsHelper.getApplication(), msg, Toast.LENGTH_SHORT);
        }
    }

    public static void show(@StringRes int resId) {
        if (resId != 0) {
            show(QsHelper.getString(resId));
        }
    }

    /**
     * 弹出提示
     */
    private static void showToast(Context context, String text, int duration) {
        try {
            if (mToast == null) {
                mToast = Toast.makeText(context, text, duration);
                mToast.show();
            } else {
                mToast.setText(text);
                mToast.setDuration(duration);
                mToast.show();
            }
        } catch (Exception ignored) {
        }
    }
}
