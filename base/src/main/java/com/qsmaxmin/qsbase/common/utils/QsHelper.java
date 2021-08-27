package com.qsmaxmin.qsbase.common.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;

import com.qsmaxmin.qsbase.LifeCycleCallbacksAdapter;
import com.qsmaxmin.qsbase.QsIApplication;
import com.qsmaxmin.qsbase.common.downloader.QsDownloadHelper;
import com.qsmaxmin.qsbase.common.http.HttpHelper;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.plugin.event.EventHelper;
import com.qsmaxmin.qsbase.plugin.permission.PermissionHelper;
import com.qsmaxmin.qsbase.plugin.route.RouteDataHolder;
import com.qsmaxmin.qsbase.plugin.threadpoll.QsThreadPollHelper;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/6/20 16:46
 * @Description Qs center
 */
public class QsHelper {
    private static QsHelper                  qsHelper;
    private final  LifeCycleCallbacksAdapter lifeCycleCallback;
    private        QsIApplication            mApplication;

    private QsHelper() {
        lifeCycleCallback = new LifeCycleCallbacksAdapter() {
            @Override public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                ScreenHelper.getInstance().pushActivity(activity);
                mApplication.onActivityCreated(activity, savedInstanceState);
            }

            @Override public void onActivityStarted(Activity activity) {
                mApplication.onActivityStarted(activity);
            }

            @Override public void onActivityResumed(Activity activity) {
                mApplication.onActivityResumed(activity);
            }

            @Override public void onActivityPaused(Activity activity) {
                mApplication.onActivityPaused(activity);
            }

            @Override public void onActivityStopped(Activity activity) {
                mApplication.onActivityStopped(activity);
            }

            @Override public void onActivityDestroyed(Activity activity) {
                ScreenHelper.getInstance().popActivity(activity);
                mApplication.onActivityDestroyed(activity);
            }
        };
    }

    private static QsHelper getInstance() {
        if (qsHelper == null) {
            synchronized (QsHelper.class) {
                if (qsHelper == null) qsHelper = new QsHelper();
            }
        }
        return qsHelper;
    }

    public static void init(QsIApplication iApp) {
        QsHelper instance = getInstance();
        instance.mApplication = iApp;

        HashMap<String, Class<?>> map = RouteDataHolder.getData();
        if (map.isEmpty()) iApp.bindRouteByQsPlugin(map);

        Application app = iApp.getApplication();
        app.unregisterActivityLifecycleCallbacks(instance.lifeCycleCallback);
        app.registerActivityLifecycleCallbacks(instance.lifeCycleCallback);
    }

    public static QsIApplication getAppInterface() {
        return getInstance().mApplication;
    }

    public static Application getApplication() {
        return getAppInterface().getApplication();
    }

    public static boolean isLogOpen() {
        return L.isEnable();
    }

    public static boolean isDebug() {
        return getAppInterface().isDebug();
    }

    public static ImageHelper.Builder getImageHelper() {
        return ImageHelper.createRequest();
    }

    public static boolean isMainThread() {
        return QsThreadPollHelper.isMainThread();
    }

    public static void post(Runnable r) {
        QsThreadPollHelper.post(r);
    }

    public static void postDelayed(Runnable r, long delayMillis) {
        QsThreadPollHelper.postDelayed(r, delayMillis);
    }

    public static void executeInHttpThread(Runnable r) {
        QsThreadPollHelper.runOnHttpThread(r);
    }

    public static void executeInWorkThread(Runnable r) {
        QsThreadPollHelper.runOnWorkThread(r);
    }

    public static void executeInSingleThread(Runnable r) {
        QsThreadPollHelper.runOnSingleThread(r);
    }

    public static void executeInLIFOThread(Runnable r) {
        QsThreadPollHelper.runOnLIFOThread(r);
    }

    public static ScreenHelper getScreenHelper() {
        return ScreenHelper.getInstance();
    }

    public static PermissionHelper getPermissionHelper() {
        return PermissionHelper.getInstance();
    }

    public static void eventPost(Object object) {
        EventHelper.eventPost(object);
    }

    public static void eventSend(Object object) {
        EventHelper.eventSend(object);
    }

    public static HttpHelper getHttpHelper() {
        return HttpHelper.getInstance();
    }

    public static void intent2Activity(Class<?> clazz) {
        intent2ActivityInner(clazz, null, 0, null, 0, 0);
    }

    public static void intent2Activity(Class<?> clazz, Bundle bundle) {
        intent2ActivityInner(clazz, bundle, 0, null, 0, 0);
    }

    public static void intent2Activity(Class<?> clazz, int requestCode) {
        intent2ActivityInner(clazz, null, requestCode, null, 0, 0);
    }

    public static void intent2Activity(Class<?> clazz, Bundle bundle, ActivityOptionsCompat optionsCompat) {
        intent2ActivityInner(clazz, bundle, 0, optionsCompat, 0, 0);
    }

    public static void intent2Activity(Class<?> clazz, int inAnimId, int outAnimId) {
        intent2ActivityInner(clazz, null, 0, null, inAnimId, outAnimId);
    }

    public static void intent2Activity(Class<?> clazz, Bundle bundle, int inAnimId, int outAnimId) {
        intent2ActivityInner(clazz, bundle, 0, null, inAnimId, outAnimId);
    }

    public static void intent2Activity(Class<?> clazz, Bundle bundle, int requestCode, ActivityOptionsCompat optionsCompat, int inAnimId, int outAnimId) {
        intent2ActivityInner(clazz, bundle, requestCode, optionsCompat, inAnimId, outAnimId);
    }

    private static void intent2ActivityInner(Class<?> clazz, Bundle bundle, int requestCode, ActivityOptionsCompat optionsCompat, int inAnimId, int outAnimId) {
        Activity activity = getScreenHelper().currentActivity();
        if (clazz != null && activity != null) {
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

    public static void commitFragment(FragmentManager manager, int layoutId, Fragment fragment) {
        commitFragment(manager, layoutId, fragment, fragment.getClass().getSimpleName(), 0, 0);
    }

    public static void commitFragment(FragmentManager manager, int layoutId, Fragment fragment, String tag) {
        commitFragment(manager, layoutId, fragment, tag, 0, 0);
    }

    public static void commitFragment(final FragmentManager manager, final int layoutId, final Fragment fragment, final String tag, final int enterAnim, final int existAnim) {
        if (manager == null || layoutId == 0 || fragment == null) return;
        if (!fragment.isAdded()) {
            FragmentTransaction ft = manager.beginTransaction();
            if (enterAnim != 0 || existAnim != 0) ft.setCustomAnimations(enterAnim, existAnim);
            ft.add(layoutId, fragment, tag).commitAllowingStateLoss();
        }
    }

    /**
     * @see com.qsmaxmin.qsbase.common.widget.dialog.QsDialogFragment#show(FragmentActivity)
     * @deprecated
     */
    public static void commitDialogFragment(DialogFragment fragment) {
        Activity activity = getScreenHelper().currentActivity();
        if (activity instanceof FragmentActivity) {
            FragmentManager manager = ((FragmentActivity) activity).getSupportFragmentManager();
            commitDialogFragmentInner(manager, fragment);
        }
    }

    public static void commitDialogFragment(FragmentManager manager, DialogFragment fragment) {
        commitDialogFragmentInner(manager, fragment);
    }

    private static void commitDialogFragmentInner(FragmentManager manager, final DialogFragment fragment) {
        if (manager != null && fragment != null && !fragment.isAdded()) {
            manager.beginTransaction().add(fragment, fragment.getClass().getSimpleName()).commitAllowingStateLoss();
        }
    }

    @SuppressLint("MissingPermission")
    public static boolean isNetworkAvailable() {
        ConnectivityManager connect = (ConnectivityManager) getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connect == null) {
            return false;
        } else {
            NetworkInfo activeNetworkInfo = connect.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isAvailable();
        }
    }

    public static String getString(@StringRes int resId) {
        return getApplication().getString(resId);
    }

    public static String getString(@StringRes int resId, Object... formatArgs) {
        return getApplication().getString(resId, formatArgs);
    }

    public static Drawable getDrawable(@DrawableRes int resId) {
        return ResourcesCompat.getDrawable(getApplication().getResources(), resId, null);
    }

    public static int getColor(@ColorRes int resId) {
        return getApplication().getResources().getColor(resId);
    }

    public static float getDimension(@DimenRes int resId) {
        return getApplication().getResources().getDimension(resId);
    }

    public static boolean isSdCardAvailable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    public static void closeStream(Closeable closeable) {
        StreamUtil.close(closeable);
    }

    public static void copyStream(InputStream is, OutputStream os) throws IOException {
        StreamUtil.copyStream(is, os);
    }

    /**
     * 释放内存
     */
    public static void release() {
        QsThreadPollHelper.release();
        PermissionHelper.release();
        HttpHelper.release();
        QsDownloadHelper.releaseAll();
    }
}
