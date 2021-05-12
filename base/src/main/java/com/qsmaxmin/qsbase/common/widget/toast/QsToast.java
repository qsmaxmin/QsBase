package com.qsmaxmin.qsbase.common.widget.toast;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.utils.QsHelper;
import com.qsmaxmin.qsbase.plugin.threadpoll.QsThreadPollHelper;

import java.lang.ref.SoftReference;
import java.lang.reflect.Field;

import androidx.annotation.StringRes;

public class QsToast {
    private static SoftReference<Toast> lastToast = null;
    private static Field                sField_TN;
    private static Field                sField_TN_Handler;

    static {
        try {
            sField_TN = Toast.class.getDeclaredField("mTN");
            sField_TN.setAccessible(true);
            sField_TN_Handler = sField_TN.getType().getDeclaredField("mHandler");
            sField_TN_Handler.setAccessible(true);
        } catch (Exception ignored) {
        }
    }

    private static void hook(Toast toast) {
        try {
            Object tn = sField_TN.get(toast);
            Handler preHandler = (Handler) sField_TN_Handler.get(tn);
            sField_TN_Handler.set(tn, new SafeHandler(preHandler));
        } catch (Exception ignored) {
        }
    }

    private static class SafeHandler extends Handler {
        private final Handler impl;

        SafeHandler(Handler impl) {
            this.impl = impl;
        }

        @Override
        public void dispatchMessage(Message msg) {
            try {
                super.dispatchMessage(msg);
            } catch (Exception e) {
                if (L.isEnable()) {
                    L.e("QsToast", "hand exception..." + e.getMessage(), e);
                }
            }
        }

        @Override
        public void handleMessage(Message msg) {
            impl.handleMessage(msg);
        }
    }

    public static void show(@StringRes int resId) {
        if (resId != 0) {
            show(QsHelper.getString(resId));
        }
    }

    public static void show(final String msg) {
        show(msg, Toast.LENGTH_SHORT);
    }

    public static void show(@StringRes int resId, int duration) {
        if (resId != 0) {
            show(QsHelper.getString(resId), duration);
        }
    }

    public static void show(final String msg, final int duration) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        // 判断是否在主线程
        if (QsThreadPollHelper.isMainThread()) {
            showToast(QsHelper.getApplication(), msg, duration);
        } else {
            QsThreadPollHelper.post(new Runnable() {
                @Override public void run() {
                    showToast(QsHelper.getApplication(), msg, duration);
                }
            });
        }
    }

    private static void showToast(Context context, String text, int duration) {
        try {
            cancelLastToast();
            int size = QsHelper.getScreenHelper().getActivityStack().size();
            if (size > 0) {
                Toast toast = Toast.makeText(context, text, duration);
                hook(toast);
                toast.setDuration(duration);
                toast.show();
                lastToast = new SoftReference<>(toast);
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
