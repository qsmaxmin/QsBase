package com.qsmaxmin.qsbase.mvp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.View;

import com.qsmaxmin.qsbase.common.widget.dialog.QsProgressDialog;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/6/21 14:02
 * @Description
 */

public interface QsIView<P> {

    String initTag();

    int layoutId();

    int emptyLayoutId();

    int loadingLayoutId();

    int errorLayoutId();

    QsProgressDialog getLoadingDialog();

    void initData(Bundle savedInstanceState);

    void onViewClick(View view);

    boolean isDelayData();

    void initDataWhenDelay();

    P getPresenter();

    Context getContext();

    boolean isOpenEventBus();

    boolean isOpenViewState();

    boolean isShowBackButtonInDefaultView();

    void activityFinish();

    void activityFinish(int enterAnim, int exitAnim);

    void activityFinish(boolean finishAfterTransition);

    void loading();

    void loading(String message);

    void loading(boolean cancelAble);

    void loading(@StringRes int resId);

    void loading(@StringRes int resId, boolean cancelAble);

    void loading(String message, boolean cancelAble);

    void loadingClose();

    void showLoadingView();

    void showEmptyView();

    void showErrorView();

    void showContentView();

    int currentViewState();

    void intent2Activity(Class clazz);

    void intent2Activity(Class clazz, int requestCode);

    void intent2Activity(Class clazz, Bundle bundle);

    void intent2Activity(Class clazz, Bundle bundle, int inAnimId, int outAnimId);

    void intent2Activity(Class clazz, Bundle bundle, ActivityOptionsCompat optionsCompat);

    void intent2Activity(Class clazz, Bundle bundle, int requestCode, ActivityOptionsCompat optionsCompat);

    void intent2Activity(Class clazz, Bundle bundle, int requestCode, ActivityOptionsCompat optionsCompat, int inAnimId, int outAnimId);

    void commitFragment(Fragment fragment);

    void commitFragment(Fragment fragment, String tag);

    void commitFragment(int layoutId, Fragment fragment);

    void commitFragment(int layoutId, Fragment fragment, String tag);

    void commitFragment(Fragment old, Fragment fragment);

    void commitFragment(Fragment old, Fragment fragment, String tag);

    void commitFragment(Fragment old, int layoutId, Fragment fragment);

    void commitFragment(Fragment old, int layoutId, Fragment fragment, String tag);

    void commitBackStackFragment(Fragment fragment);

    void commitBackStackFragment(Fragment fragment, String tag);

    void commitBackStackFragment(int layoutId, Fragment fragment);

    void commitBackStackFragment(Fragment fragment, int enterAnim, int exitAnim);

    void commitBackStackFragment(int layoutId, Fragment fragment, String tag);

    void commitDialogFragment(DialogFragment fragment);
}
