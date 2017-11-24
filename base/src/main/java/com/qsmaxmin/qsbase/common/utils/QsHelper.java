package com.qsmaxmin.qsbase.common.utils;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
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
import com.qsmaxmin.qsbase.common.viewbind.ViewBind;
import com.qsmaxmin.qsbase.common.viewbind.ViewBindImpl;
import com.qsmaxmin.qsbase.mvp.fragment.QsFragment;

import org.greenrobot.eventbus.EventBus;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/6/20 16:46
 * @Description 帮助类中心
 */

public class QsHelper {
    private HttpAdapter httpAdapter;
    private ViewBind    viewBind;

    private static QsHelper helper = new QsHelper();

    private volatile static QsApplication mApplication;

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

    public void eventPost(Object object) {
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

    public void resetHttpAdapter() {
        httpAdapter = null;
    }

    public CacheHelper getCacheHelper() {
        return CacheHelper.getInstance();
    }

    public void intent2Activity(Class clazz) {
        intent2Activity(clazz, null);
    }

    public void intent2Activity(Class clazz, Bundle bundle) {
        intent2Activity(clazz, bundle, -1);
    }

    public void intent2Activity(Class clazz, Bundle bundle, int requestCode) {
        FragmentActivity activity = getScreenHelper().currentActivity();
        if (activity != null) {
            Intent intent = new Intent();
            intent.setClass(activity, clazz);
            if (bundle != null) intent.putExtras(bundle);
            if (requestCode > 0) {
                activity.startActivityForResult(intent, requestCode);
            } else {
                activity.startActivity(intent);
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
            fragmentManager.beginTransaction().add(layoutId, fragment, tag).setTransition(FragmentTransaction.TRANSIT_NONE).commitAllowingStateLoss();
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
            fragmentTransaction.add(layoutId, fragment, tag).setTransition(FragmentTransaction.TRANSIT_NONE).commitAllowingStateLoss();
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

    public void commitBackStackFragment(int layoutId, Fragment fragment, String tag) {
        FragmentActivity activity = getScreenHelper().currentActivity();
        if (activity == null) return;
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        commitBackStackFragment(fragmentManager, layoutId, fragment, tag);
    }

    @ThreadPoint(ThreadType.MAIN) public void commitBackStackFragment(FragmentManager fragmentManager, int layoutId, Fragment fragment, String tag) {
        if (layoutId > 0 && fragment != null && !fragment.isAdded() && fragmentManager != null) {
            if (fragment instanceof QsFragment) {
                ((QsFragment) fragment).enableBackgroundColor();
            }
            fragmentManager.beginTransaction().add(layoutId, fragment, tag).addToBackStack(null).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commitAllowingStateLoss();
        }
    }

    public void commitDialogFragment(DialogFragment dialogFragment) {
        FragmentActivity activity = getScreenHelper().currentActivity();
        if (dialogFragment == null || activity == null) return;
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        if (fragmentManager != null) {
            fragmentManager.beginTransaction().add(dialogFragment, dialogFragment.getClass().getSimpleName()).commitAllowingStateLoss();
        }
    }

    public ViewBind getViewBindHelper() {
        if (viewBind == null) {
            synchronized (QsHelper.class) {
                if (viewBind == null) viewBind = new ViewBindImpl();
            }
        }
        return viewBind;
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
}
