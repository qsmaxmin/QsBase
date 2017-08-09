package com.qsmaxmin.qsbase.common.utils;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.qsmaxmin.qsbase.QsApplication;
import com.qsmaxmin.qsbase.common.http.HttpAdapter;
import com.qsmaxmin.qsbase.common.threadpoll.QsThreadPollHelper;
import com.qsmaxmin.qsbase.common.viewbind.ViewBind;
import com.qsmaxmin.qsbase.common.viewbind.ViewBindImpl;

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

    public String getString(int resId) {
        return getApplication().getString(resId);
    }

    public Drawable getDrawable(int resId) {
        return getApplication().getResources().getDrawable(resId);
    }
}
