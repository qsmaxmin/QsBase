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
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.qsmaxmin.qsbase.QsIApplication;
import com.qsmaxmin.qsbase.common.aspect.ThreadPoint;
import com.qsmaxmin.qsbase.common.aspect.ThreadType;
import com.qsmaxmin.qsbase.common.http.HttpAdapter;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.threadpoll.QsThreadPollHelper;
import com.qsmaxmin.qsbase.common.utils.permission.PermissionHelper;

import org.greenrobot.eventbus.EventBus;

import java.io.Closeable;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/6/20 16:46
 * @Description 帮助类中心
 */
public class QsHelper {
    private static QsHelper           qsHelper;
    private        QsIApplication     mApplication;
    private        HttpAdapter        httpAdapter;
    private        ImageHelper        imageHelper;
    private        QsThreadPollHelper threadPollHelper;
    private        PermissionHelper   permissionHelper;

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

    public static ImageHelper getImageHelper() {
        if (getInstance().imageHelper == null) {
            getInstance().imageHelper = new ImageHelper();
        }
        return getInstance().imageHelper;
    }

    public static QsThreadPollHelper getThreadHelper() {
        if (getInstance().threadPollHelper == null) {
            getInstance().threadPollHelper = new QsThreadPollHelper();
        }
        return getInstance().threadPollHelper;
    }

    public static boolean isMainThread() {
        return getThreadHelper().isMainThread();
    }

    public static void post(Runnable r) {
        getThreadHelper().getMainThread().execute(r);
    }

    public static void postDelayed(Runnable r, long delayMillis) {
        getThreadHelper().getMainThread().executeDelayed(r, delayMillis);
    }

    public static void executeInHttpThread(Runnable r) {
        getThreadHelper().getHttpThreadPoll().execute(r);
    }

    public static void executeInWorkThread(Runnable r) {
        getThreadHelper().getWorkThreadPoll().execute(r);
    }

    public static void executeInSingleThread(Runnable r) {
        getThreadHelper().getWorkThreadPoll().execute(r);
    }

    public static ScreenHelper getScreenHelper() {
        return ScreenHelper.getInstance();
    }

    public static PermissionHelper getPermissionHelper() {
        if (getInstance().permissionHelper == null) {
            getInstance().permissionHelper = new PermissionHelper();
        }
        return getInstance().permissionHelper;
    }

    @ThreadPoint(ThreadType.MAIN)
    public static void eventPost(Object object) {
        EventBus.getDefault().post(object);
    }

    public static HttpAdapter getHttpHelper() {
        if (getInstance().httpAdapter == null) {
            synchronized (QsHelper.class) {
                if (getInstance().httpAdapter == null) getInstance().httpAdapter = new HttpAdapter();
            }
        }
        return getInstance().httpAdapter;
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

    @ThreadPoint(ThreadType.MAIN)
    private static void commitFragmentInner(FragmentManager manager, Fragment old, int layoutId, Fragment fragment, String tag) {
        if (manager == null) {
            FragmentActivity activity = getScreenHelper().currentActivity();
            if (activity == null) return;
            manager = activity.getSupportFragmentManager();
        }
        if (layoutId != 0 && fragment != null && !fragment.isAdded() && manager != null) {
            FragmentTransaction fragmentTransaction = manager.beginTransaction();
            if (old != null) fragmentTransaction.detach(old);
            fragmentTransaction.replace(layoutId, fragment, tag).setTransition(FragmentTransaction.TRANSIT_NONE).commitAllowingStateLoss();
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

    @ThreadPoint(ThreadType.MAIN)
    private static void commitBackStackFragmentInner(FragmentManager manager, int layoutId, Fragment fragment, String tag, int enterAnim, int exitAnim) {
        if (manager == null) {
            FragmentActivity activity = getScreenHelper().currentActivity();
            if (activity == null) return;
            manager = activity.getSupportFragmentManager();
        }
        if (layoutId != 0 && fragment != null && !fragment.isAdded() && manager != null) {
            FragmentTransaction transaction = manager.beginTransaction();
            if (enterAnim != 0 || exitAnim != 0) transaction.setCustomAnimations(enterAnim, exitAnim, enterAnim, exitAnim);
            transaction.add(layoutId, fragment, tag).addToBackStack(null).setTransition(FragmentTransaction.TRANSIT_NONE).commitAllowingStateLoss();
        }
    }

    public static void commitDialogFragment(DialogFragment dialogFragment) {
        commitDialogFragmentInner(null, dialogFragment);
    }


    public static void commitDialogFragment(FragmentManager fragmentManager, DialogFragment dialogFragment) {
        commitDialogFragmentInner(fragmentManager, dialogFragment);
    }

    @ThreadPoint(ThreadType.MAIN)
    private static void commitDialogFragmentInner(FragmentManager manager, DialogFragment dialogFragment) {
        if (manager == null) {
            FragmentActivity activity = getScreenHelper().currentActivity();
            if (activity == null) return;
            manager = activity.getSupportFragmentManager();
        }
        if (manager != null && dialogFragment != null && !dialogFragment.isAdded()) {
            manager.beginTransaction().add(dialogFragment, dialogFragment.getClass().getSimpleName()).commitAllowingStateLoss();
        }
    }

    @SuppressLint("MissingPermission") public static boolean isNetworkAvailable() {
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
        return getApplication().getResources().getDrawable(resId);
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

    public static void closeStream(Closeable... closeables) {
        StreamCloseUtils.close(closeables);
    }

    /**
     * 释放内存
     */
    static void release() {
        if (qsHelper != null) {
            if (qsHelper.threadPollHelper != null) {
                qsHelper.threadPollHelper.release();
                qsHelper.threadPollHelper = null;
            }
            if (qsHelper.imageHelper != null) {
                qsHelper.imageHelper.clearMemoryCache();
                qsHelper.imageHelper = null;
            }
            if (qsHelper.httpAdapter != null) {
                qsHelper.httpAdapter.setHttpClient(null);
                qsHelper.httpAdapter = null;
            }
            if (qsHelper.permissionHelper != null) {
                qsHelper.permissionHelper.release();
                qsHelper.permissionHelper = null;
            }
        }
    }
}
