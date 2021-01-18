package com.qsmaxmin.qsbase.mvvm;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.FragmentActivity;

/**
 * @CreateBy qsmaxmin
 * @Date 2020/12/22 14:52
 * @Description view层超类
 */
public interface IView {
    void onViewClicked(@NonNull View view);

    void onViewClicked(@NonNull View view, long interval);

    Context getContext();

    FragmentActivity getActivity();

    void loading();

    void loading(String message);

    void loading(boolean cancelAble);

    void loading(@StringRes int resId);

    void loading(@StringRes int resId, boolean cancelAble);

    void loading(String message, boolean cancelAble);

    void loadingClose();

    void activityFinish();

    void activityFinish(int enterAnim, int exitAnim);

    void activityFinish(boolean finishAfterTransition);

    void intent2Activity(Class clazz);

    void intent2Activity(Class clazz, int requestCode);

    void intent2Activity(Class clazz, Bundle bundle);

    void intent2Activity(Class clazz, Bundle bundle, int requestCode);

    void intent2Activity(Class clazz, Bundle bundle, int inAnimId, int outAnimId);

    void intent2Activity(Class clazz, Bundle bundle, ActivityOptionsCompat optionsCompat);

    void intent2Activity(Class clazz, Bundle bundle, int requestCode, ActivityOptionsCompat optionsCompat);

    void intent2Activity(Class clazz, Bundle bundle, int requestCode, ActivityOptionsCompat optionsCompat, int enterAnim, int existAnim);

}
