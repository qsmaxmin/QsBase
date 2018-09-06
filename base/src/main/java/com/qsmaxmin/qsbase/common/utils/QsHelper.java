package com.qsmaxmin.qsbase.common.utils;

import android.annotation.SuppressLint;
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

import com.qsmaxmin.qsbase.QsApplication;
import com.qsmaxmin.qsbase.common.aspect.ThreadPoint;
import com.qsmaxmin.qsbase.common.aspect.ThreadType;
import com.qsmaxmin.qsbase.common.http.HttpAdapter;
import com.qsmaxmin.qsbase.common.threadpoll.QsThreadPollHelper;
import com.qsmaxmin.qsbase.common.utils.permission.PermissionUtils;
import com.qsmaxmin.qsbase.common.widget.dialog.QsProgressDialog;

import org.greenrobot.eventbus.EventBus;

import java.io.Closeable;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/6/20 16:46
 * @Description 帮助类中心
 */

public class QsHelper {
    private static QsHelper helper = new QsHelper();

    private QsApplication mApplication;
    private HttpAdapter   httpAdapter;

    private QsHelper() {
    }

    public static QsHelper getInstance() {
        return helper;
    }

    public void init(QsApplication application) {
        mApplication = application;
    }

    public QsApplication getApplication() {
        return mApplication;
    }

    public ImageHelper getImageHelper() {
        return ImageHelper.getInstance();
    }

    public QsThreadPollHelper getThreadHelper() {
        return QsThreadPollHelper.getInstance();
    }

    public ScreenHelper getScreenHelper() {
        return ScreenHelper.getInstance();
    }

    @ThreadPoint(ThreadType.MAIN) public void eventPost(Object object) {
        EventBus.getDefault().post(object);
    }

    public HttpAdapter getHttpHelper() {
        if (httpAdapter == null) {
            synchronized (this) {
                if (httpAdapter == null) httpAdapter = new HttpAdapter();
            }
        }
        return httpAdapter;
    }

    public CacheHelper getCacheHelper() {
        return new CacheHelper();
    }

    public void intent2Activity(Class clazz) {
        intent2Activity(clazz, null, 0, null, 0, 0);
    }

    public void intent2Activity(Class clazz, Bundle bundle) {
        intent2Activity(clazz, bundle, 0, null, 0, 0);
    }

    public void intent2Activity(Class clazz, int requestCode) {
        intent2Activity(clazz, null, requestCode, null, 0, 0);
    }

    public void intent2Activity(Class clazz, Bundle bundle, ActivityOptionsCompat optionsCompat) {
        intent2Activity(clazz, bundle, 0, optionsCompat, 0, 0);
    }

    public void intent2Activity(Class clazz, int inAnimId, int outAnimId) {
        intent2Activity(clazz, null, 0, null, inAnimId, outAnimId);
    }

    public void intent2Activity(Class clazz, Bundle bundle, int inAnimId, int outAnimId) {
        intent2Activity(clazz, bundle, 0, null, inAnimId, outAnimId);
    }

    public void intent2Activity(Class clazz, Bundle bundle, int requestCode, ActivityOptionsCompat optionsCompat, int inAnimId, int outAnimId) {
        FragmentActivity activity = getScreenHelper().currentActivity();
        if (clazz != null && activity != null) {
            Intent intent = new Intent();
            intent.setClass(activity, clazz);
            if (bundle != null) intent.putExtras(bundle);
            if (optionsCompat == null) {
                if (requestCode > 0) {
                    activity.startActivityForResult(intent, requestCode);
                    if (inAnimId > 0 || outAnimId > 0) activity.overridePendingTransition(inAnimId, outAnimId);
                } else {
                    activity.startActivity(intent);
                    if (inAnimId > 0 || outAnimId > 0) activity.overridePendingTransition(inAnimId, outAnimId);
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

    public void commitFragment(Fragment fragment) {
        commitFragment(fragment, fragment.getClass().getSimpleName());
    }

    public void commitFragment(Fragment fragment, String tag) {
        commitFragment(android.R.id.custom, fragment, tag);
    }

    public void commitFragment(int layoutId, Fragment fragment) {
        commitFragment(layoutId, fragment, fragment.getClass().getSimpleName());
    }

    public void commitFragment(int layoutId, Fragment fragment, String tag) {
        FragmentActivity activity = getScreenHelper().currentActivity();
        if (activity == null) return;
        commitFragment(activity.getSupportFragmentManager(), layoutId, fragment, tag);
    }

    @ThreadPoint(ThreadType.MAIN) public void commitFragment(FragmentManager fragmentManager, int layoutId, Fragment fragment, String tag) {
        if (fragment != null && !fragment.isAdded() && fragmentManager != null && layoutId > 0) {
            fragmentManager.beginTransaction().replace(layoutId, fragment, tag).setTransition(FragmentTransaction.TRANSIT_NONE).commitAllowingStateLoss();
        }
    }

    public void commitFragment(Fragment old, Fragment fragment) {
        commitFragment(old, fragment, fragment.getClass().getSimpleName());
    }

    public void commitFragment(Fragment old, Fragment fragment, String tag) {
        commitFragment(old, android.R.id.custom, fragment, tag);
    }

    public void commitFragment(Fragment old, int layoutId, Fragment fragment) {
        commitFragment(old, layoutId, fragment, fragment.getClass().getSimpleName());
    }

    public void commitFragment(Fragment old, int layoutId, Fragment fragment, String tag) {
        FragmentActivity activity = getScreenHelper().currentActivity();
        if (activity == null) return;
        commitFragment(activity.getSupportFragmentManager(), old, layoutId, fragment, tag);
    }

    @ThreadPoint(ThreadType.MAIN) public void commitFragment(FragmentManager fragmentManager, Fragment old, int layoutId, Fragment fragment, String tag) {
        if (layoutId > 0 && fragment != null && !fragment.isAdded() && fragmentManager != null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if (old != null) fragmentTransaction.detach(old);
            fragmentTransaction.replace(layoutId, fragment, tag).setTransition(FragmentTransaction.TRANSIT_NONE).commitAllowingStateLoss();
        }
    }

    public void commitBackStackFragment(Fragment fragment) {
        commitBackStackFragment(fragment, fragment.getClass().getSimpleName());
    }

    public void commitBackStackFragment(Fragment fragment, String tag) {
        commitBackStackFragment(android.R.id.custom, fragment, tag);
    }

    public void commitBackStackFragment(int layoutId, Fragment fragment) {
        commitBackStackFragment(layoutId, fragment, fragment.getClass().getSimpleName());
    }

    public void commitBackStackFragment(Fragment fragment, int enterAnim, int exitAnim) {
        FragmentActivity activity = getScreenHelper().currentActivity();
        if (activity == null) return;
        commitBackStackFragment(activity.getSupportFragmentManager(), android.R.id.custom, fragment, fragment.getClass().getSimpleName(), enterAnim, exitAnim);
    }

    public void commitBackStackFragment(int layoutId, Fragment fragment, String tag) {
        FragmentActivity activity = getScreenHelper().currentActivity();
        if (activity == null) return;
        commitBackStackFragment(activity.getSupportFragmentManager(), layoutId, fragment, tag);
    }

    public void commitBackStackFragment(FragmentManager fragmentManager, int layoutId, Fragment fragment, String tag) {
        commitBackStackFragment(fragmentManager, layoutId, fragment, tag, 0, 0);
    }

    @ThreadPoint(ThreadType.MAIN) public void commitBackStackFragment(FragmentManager fragmentManager, int layoutId, Fragment fragment, String tag, int enterAnim, int exitAnim) {
        if (layoutId > 0 && fragment != null && !fragment.isAdded() && fragmentManager != null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if (enterAnim > 0 || exitAnim > 0) transaction.setCustomAnimations(enterAnim, exitAnim, enterAnim, exitAnim);
            transaction.add(layoutId, fragment, tag).addToBackStack(null).setTransition(FragmentTransaction.TRANSIT_NONE).commitAllowingStateLoss();
        }
    }

    public void commitDialogFragment(DialogFragment dialogFragment) {
        FragmentActivity activity = getScreenHelper().currentActivity();
        if (dialogFragment == null || activity == null) return;
        commitDialogFragment(activity.getSupportFragmentManager(), dialogFragment);
    }

    @ThreadPoint(ThreadType.MAIN) public void commitDialogFragment(FragmentManager fragmentManager, DialogFragment dialogFragment) {
        if (fragmentManager != null && dialogFragment != null) {
            if (dialogFragment instanceof QsProgressDialog) {
                QsProgressDialog dialog = (QsProgressDialog) dialogFragment;
                if (dialog.isAdded() || dialog.isShowing()) {
                    return;
                }
                ((QsProgressDialog) dialogFragment).setIsShowing(true);
            } else if (dialogFragment.isAdded()) {
                return;
            }
            fragmentManager.beginTransaction().add(dialogFragment, dialogFragment.getClass().getSimpleName()).commitAllowingStateLoss();
        }
    }

    @SuppressLint("MissingPermission") public boolean isNetworkAvailable() {
        ConnectivityManager connect = (ConnectivityManager) getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connect == null) {
            return false;
        } else {
            NetworkInfo activeNetworkInfo = connect.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isAvailable();
        }
    }

    public String getString(@StringRes int resId) {
        return getApplication().getString(resId);
    }

    public String getString(@StringRes int resId, Object... formatArgs) {
        return getApplication().getString(resId, formatArgs);
    }

    public Drawable getDrawable(@DrawableRes int resId) {
        return getApplication().getResources().getDrawable(resId);
    }

    public int getColor(@ColorRes int resId) {
        return getApplication().getResources().getColor(resId);
    }

    public float getDimension(@DimenRes int resId) {
        return getApplication().getResources().getDimension(resId);
    }

    public boolean isSdCardAvailable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    public void closeStream(Closeable... closeables) {
        StreamCloseUtils.close(closeables);
    }

    public PermissionUtils getPermissionHelper() {
        return PermissionUtils.getInstance();
    }
}
