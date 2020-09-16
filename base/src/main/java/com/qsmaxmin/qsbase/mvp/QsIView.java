package com.qsmaxmin.qsbase.mvp;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;

import com.qsmaxmin.annotation.QsNotProguard;
import com.qsmaxmin.qsbase.common.widget.dialog.QsProgressDialog;
import com.qsmaxmin.qsbase.plugin.bind.QsIBindBundle;
import com.qsmaxmin.qsbase.plugin.bind.QsIBindView;
import com.qsmaxmin.qsbase.plugin.event.QsIBindEvent;
import com.qsmaxmin.qsbase.plugin.presenter.QsIPresenter;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/6/21 14:02
 * @Description
 */

public interface QsIView<P> extends QsIBindView, QsIBindEvent, QsIBindBundle, QsIPresenter, QsNotProguard {
    int VIEW_STATE_LOADING = 0;
    int VIEW_STATE_CONTENT = 1;
    int VIEW_STATE_EMPTY   = 2;
    int VIEW_STATE_ERROR   = 3;

    String initTag();

    @LayoutRes int rootViewLayoutId();

    @LayoutRes int layoutId();

    @LayoutRes int emptyLayoutId();

    @LayoutRes int loadingLayoutId();

    @LayoutRes int errorLayoutId();

    void onCreateLoadingView(@NonNull View loadingView);

    void onCreateContentView(@NonNull View contentView);

    void onCreateEmptyView(@NonNull View emptyView);

    void onCreateErrorView(@NonNull View errorView);

    QsProgressDialog getLoadingDialog();

    void initData(Bundle savedInstanceState);

    void onViewClick(View view);

    boolean isDelayData();

    void initDataWhenDelay();

    P getPresenter();

    Context getContext();

    boolean isOpenViewState();

    Animation viewStateInAnimation();

    Animation viewStateOutAnimation();

    int viewStateInAnimationId();

    int viewStateOutAnimationId();

    boolean viewStateAnimateFirstView();

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

    void showContentView();

    void showEmptyView();

    void showErrorView();

    boolean isShowLoadingView();

    boolean isShowContentView();

    boolean isShowEmptyView();

    boolean isShowErrorView();

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

    void post(Runnable action);

    void postDelayed(Runnable action, long delayed);

    void runOnHttpThread(Runnable action);

    void runOnWorkThread(Runnable action);

    FragmentActivity getActivity();

    void smoothScrollToTop(boolean autoRefresh);

    void setOnActivityResultListener(OnActivityResultListener listener);

    int contentViewBackgroundColor();
}
