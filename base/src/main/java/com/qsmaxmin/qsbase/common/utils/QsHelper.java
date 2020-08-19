package com.qsmaxmin.qsbase.common.utils;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import com.qsmaxmin.qsbase.QsIApplication;
import com.qsmaxmin.qsbase.common.http.HttpHelper;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.plugin.event.EventHelper;
import com.qsmaxmin.qsbase.plugin.permission.PermissionHelper;
import com.qsmaxmin.qsbase.plugin.threadpoll.QsThreadPollHelper;

import java.io.Closeable;

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
    private static QsHelper       qsHelper;
    private        QsIApplication mApplication;

    private QsHelper() {
    }

    private static QsHelper getInstance() {
        if (qsHelper == null) {
            synchronized (QsHelper.class) {
                if (qsHelper == null) qsHelper = new QsHelper();
            }
        }
        return qsHelper;
    }

    @SuppressWarnings("unchecked")
    public static <T extends View> T forceCastToView(View view) {
        return (T) view;
    }

    public static void init(QsIApplication application) {
        getInstance().mApplication = application;
        if (application.isLogOpen()) {
            L.init(true);
        }
    }

    public static Application getApplication() {
        return getInstance().mApplication.getApplication();
    }

    public static QsIApplication getAppInterface() {
        return getInstance().mApplication;
    }

    public static boolean isLogOpen() {
        return getInstance().mApplication.isLogOpen();
    }

    public static ImageHelper.Builder getImageHelper() {
        return ImageHelper.createRequest();
    }

    public static QsThreadPollHelper getThreadHelper() {
        return QsThreadPollHelper.getInstance();
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

    public static ScreenHelper getScreenHelper() {
        return ScreenHelper.getInstance();
    }

    public static PermissionHelper getPermissionHelper() {
        return PermissionHelper.getInstance();
    }

    public static void eventPost(final Object object) {
        if (isMainThread()) {
            EventHelper.eventPost(object);
        } else {
            post(new Runnable() {
                @Override public void run() {
                    EventHelper.eventPost(object);
                }
            });
        }
    }

    public static HttpHelper getHttpHelper() {
        return HttpHelper.getInstance();
    }

    public static CacheHelper getCacheHelper() {
        return new CacheHelper();
    }

    public static void intent2Activity(Class clazz) {
        intent2ActivityInner(clazz, null, 0, null, 0, 0);
    }

    public static void intent2Activity(Class clazz, Bundle bundle) {
        intent2ActivityInner(clazz, bundle, 0, null, 0, 0);
    }

    public static void intent2Activity(Class clazz, int requestCode) {
        intent2ActivityInner(clazz, null, requestCode, null, 0, 0);
    }

    public static void intent2Activity(Class clazz, Bundle bundle, ActivityOptionsCompat optionsCompat) {
        intent2ActivityInner(clazz, bundle, 0, optionsCompat, 0, 0);
    }

    public static void intent2Activity(Class clazz, int inAnimId, int outAnimId) {
        intent2ActivityInner(clazz, null, 0, null, inAnimId, outAnimId);
    }

    public static void intent2Activity(Class clazz, Bundle bundle, int inAnimId, int outAnimId) {
        intent2ActivityInner(clazz, bundle, 0, null, inAnimId, outAnimId);
    }

    public static void intent2Activity(Class clazz, Bundle bundle, int requestCode, ActivityOptionsCompat optionsCompat, int inAnimId, int outAnimId) {
        intent2ActivityInner(clazz, bundle, requestCode, optionsCompat, inAnimId, outAnimId);
    }

    private static void intent2ActivityInner(Class clazz, Bundle bundle, int requestCode, ActivityOptionsCompat optionsCompat, int inAnimId, int outAnimId) {
        FragmentActivity activity = getScreenHelper().currentActivity();
        if (clazz != null && activity != null) {
            Intent intent = new Intent();
            intent.setClass(activity, clazz);
            if (bundle != null) intent.putExtras(bundle);
            if (optionsCompat == null) {
                if (requestCode > 0) {
                    activity.startActivityForResult(intent, requestCode);
                    if (inAnimId != 0 || outAnimId != 0) activity.overridePendingTransition(inAnimId, outAnimId);
                } else {
                    activity.startActivity(intent);
                    if (inAnimId != 0 || outAnimId != 0) activity.overridePendingTransition(inAnimId, outAnimId);
                }
            } else {
                if (requestCode > 0) {
                    ActivityCompat.startActivityForResult(activity, intent, requestCode, optionsCompat.toBundle());
                } else {
                    ActivityCompat.startActivity(activity, intent, optionsCompat.toBundle());
                }
            }
        }
    }

    public static void commitFragment(Fragment fragment) {
        commitFragmentInner(null, null, android.R.id.custom, fragment, fragment.getClass().getSimpleName());
    }

    public static void commitFragment(Fragment fragment, String tag) {
        commitFragmentInner(null, null, android.R.id.custom, fragment, tag);
    }

    public static void commitFragment(int layoutId, Fragment fragment) {
        commitFragmentInner(null, null, layoutId, fragment, fragment.getClass().getSimpleName());
    }

    public static void commitFragment(int layoutId, Fragment fragment, String tag) {
        commitFragmentInner(null, null, layoutId, fragment, tag);
    }

    public static void commitFragment(FragmentManager fragmentManager, int layoutId, Fragment fragment, String tag) {
        commitFragmentInner(fragmentManager, null, layoutId, fragment, tag);
    }

    public static void commitFragment(Fragment old, Fragment fragment) {
        commitFragmentInner(null, old, android.R.id.custom, fragment, fragment.getClass().getSimpleName());
    }

    public static void commitFragment(Fragment old, Fragment fragment, String tag) {
        commitFragmentInner(null, old, android.R.id.custom, fragment, tag);
    }

    public static void commitFragment(Fragment old, int layoutId, Fragment fragment) {
        commitFragmentInner(null, old, layoutId, fragment, fragment.getClass().getSimpleName());
    }

    public static void commitFragment(Fragment old, int layoutId, Fragment fragment, String tag) {
        commitFragmentInner(null, old, layoutId, fragment, tag);
    }

    public static void commitFragment(FragmentManager fragmentManager, Fragment old, int layoutId, Fragment fragment, String tag) {
        commitFragmentInner(fragmentManager, old, layoutId, fragment, tag);
    }

    private static void commitFragmentInner(FragmentManager manager, final Fragment old, final int layoutId, final Fragment fragment, final String tag) {
        if (manager == null) {
            FragmentActivity activity = getScreenHelper().currentActivity();
            if (activity == null) return;
            manager = activity.getSupportFragmentManager();
        }
        if (isMainThread()) {
            if (layoutId != 0 && fragment != null && !fragment.isAdded()) {
                FragmentTransaction fragmentTransaction = manager.beginTransaction();
                if (old != null) fragmentTransaction.detach(old);
                fragmentTransaction.replace(layoutId, fragment, tag).setTransition(FragmentTransaction.TRANSIT_NONE).commitAllowingStateLoss();
            }
        } else {
            final FragmentManager finalManager = manager;
            post(new Runnable() {
                @Override public void run() {
                    if (layoutId != 0 && fragment != null && !fragment.isAdded()) {
                        FragmentTransaction fragmentTransaction = finalManager.beginTransaction();
                        if (old != null) fragmentTransaction.detach(old);
                        fragmentTransaction.replace(layoutId, fragment, tag).setTransition(FragmentTransaction.TRANSIT_NONE).commitAllowingStateLoss();
                    }
                }
            });
        }
    }


    public static void commitBackStackFragment(Fragment fragment) {
        commitBackStackFragmentInner(null, android.R.id.custom, fragment, fragment.getClass().getSimpleName(), 0, 0);
    }

    public static void commitBackStackFragment(Fragment fragment, String tag) {
        commitBackStackFragmentInner(null, android.R.id.custom, fragment, tag, 0, 0);
    }

    public static void commitBackStackFragment(int layoutId, Fragment fragment) {
        commitBackStackFragmentInner(null, layoutId, fragment, fragment.getClass().getSimpleName(), 0, 0);
    }

    public static void commitBackStackFragment(Fragment fragment, int enterAnim, int exitAnim) {
        commitBackStackFragmentInner(null, android.R.id.custom, fragment, fragment.getClass().getSimpleName(), enterAnim, exitAnim);
    }

    public static void commitBackStackFragment(int layoutId, Fragment fragment, String tag) {
        commitBackStackFragmentInner(null, layoutId, fragment, tag, 0, 0);
    }

    public static void commitBackStackFragment(FragmentManager fragmentManager, int layoutId, Fragment fragment, String tag) {
        commitBackStackFragmentInner(fragmentManager, layoutId, fragment, tag, 0, 0);
    }

    public static void commitBackStackFragment(FragmentManager fragmentManager, int layoutId, Fragment fragment, String tag, int enterAnim, int exitAnim) {
        commitBackStackFragmentInner(fragmentManager, layoutId, fragment, tag, enterAnim, exitAnim);
    }

    private static void commitBackStackFragmentInner(FragmentManager manager, final int layoutId, final Fragment fragment, final String tag, final int enterAnim, final int exitAnim) {
        if (manager == null) {
            FragmentActivity activity = getScreenHelper().currentActivity();
            if (activity == null) return;
            manager = activity.getSupportFragmentManager();
        }
        if (isMainThread()) {
            if (layoutId != 0 && fragment != null && !fragment.isAdded()) {
                FragmentTransaction transaction = manager.beginTransaction();
                if (enterAnim != 0 || exitAnim != 0) transaction.setCustomAnimations(enterAnim, exitAnim, enterAnim, exitAnim);
                transaction.add(layoutId, fragment, tag).addToBackStack(null).setTransition(FragmentTransaction.TRANSIT_NONE).commitAllowingStateLoss();
            }
        } else {
            final FragmentManager finalManager = manager;
            post(new Runnable() {
                @Override public void run() {
                    if (layoutId != 0 && fragment != null && !fragment.isAdded()) {
                        FragmentTransaction transaction = finalManager.beginTransaction();
                        if (enterAnim != 0 || exitAnim != 0) transaction.setCustomAnimations(enterAnim, exitAnim, enterAnim, exitAnim);
                        transaction.add(layoutId, fragment, tag).addToBackStack(null).setTransition(FragmentTransaction.TRANSIT_NONE).commitAllowingStateLoss();
                    }
                }
            });
        }

    }

    public static void commitDialogFragment(DialogFragment dialogFragment) {
        commitDialogFragmentInner(null, dialogFragment);
    }


    public static void commitDialogFragment(FragmentManager fragmentManager, DialogFragment dialogFragment) {
        commitDialogFragmentInner(fragmentManager, dialogFragment);
    }

    private static void commitDialogFragmentInner(FragmentManager manager, final DialogFragment dialogFragment) {
        if (manager == null) {
            FragmentActivity activity = getScreenHelper().currentActivity();
            if (activity == null) return;
            manager = activity.getSupportFragmentManager();
        }
        if (isMainThread()) {
            if (dialogFragment != null && !dialogFragment.isAdded()) {
                manager.beginTransaction().add(dialogFragment, dialogFragment.getClass().getSimpleName()).commitAllowingStateLoss();
            }
        } else {
            final FragmentManager finalManager = manager;
            post(new Runnable() {
                @Override public void run() {
                    if (dialogFragment != null && !dialogFragment.isAdded()) {
                        finalManager.beginTransaction().add(dialogFragment, dialogFragment.getClass().getSimpleName()).commitAllowingStateLoss();
                    }
                }
            });
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
        StreamCloseUtils.close(closeable);
    }

    /**
     * 释放内存
     */
    static void release() {
        QsThreadPollHelper.release();
        PermissionHelper.release();
        HttpHelper.release();
        qsHelper = null;
    }
}
