package com.qsmaxmin.qsbase.mvvm;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import com.qsmaxmin.annotation.QsNotProguard;
import com.qsmaxmin.qsbase.common.http.NetworkErrorReceiver;
import com.qsmaxmin.qsbase.common.viewbind.OnActivityResultListener;
import com.qsmaxmin.qsbase.common.widget.dialog.QsProgressDialog;
import com.qsmaxmin.qsbase.plugin.bind.QsIBindBundle;
import com.qsmaxmin.qsbase.plugin.event.QsIBindEvent;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

/**
 * @CreateBy administrator
 * @Date 2020/12/8 14:42
 * @Description MVVM架构的View层基类
 */
public interface MvIView extends IView, QsIBindEvent, QsIBindBundle, QsNotProguard, NetworkErrorReceiver {
    int VIEW_STATE_LOADING = 0;
    int VIEW_STATE_CONTENT = 1;
    int VIEW_STATE_EMPTY   = 2;
    int VIEW_STATE_ERROR   = 3;

    String initTag();

    void onViewClick(View view);

    int rootViewLayoutId();

    QsProgressDialog getLoadingDialog();

    View onCreateLoadingView(@NonNull LayoutInflater inflater, ViewGroup parent);

    View onCreateContentView(@NonNull LayoutInflater inflater, ViewGroup parent);

    View onCreateEmptyView(@NonNull LayoutInflater inflater, ViewGroup parent);

    View onCreateErrorView(@NonNull LayoutInflater inflater, ViewGroup parent);

    void initData(Bundle savedInstanceState);

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

    void showLoadingView();

    void showContentView();

    void showEmptyView();

    void showErrorView();

    boolean isShowLoadingView();

    boolean isShowContentView();

    boolean isShowEmptyView();

    boolean isShowErrorView();

    int currentViewState();

    void commitFragment(Fragment fragment);

    void commitFragment(Fragment fragment, String tag);

    void commitFragment(Fragment fragment, String tag, int enterAnim, int existAnim);

    void commitFragment(Fragment fragment, int enterAnim, int existAnim);

    void commitFragment(int layoutId, Fragment fragment);

    void commitFragment(int layoutId, Fragment fragment, String tag);

    void commitFragment(int layoutId, Fragment fragment, int enterAnim, int existAnim);

    void commitFragment(int layoutId, Fragment fragment, String tag, int enterAnim, int existAnim);

    void commitDialogFragment(DialogFragment fragment);

    void post(Runnable action);

    void postDelayed(Runnable action, long delayed);

    void runOnHttpThread(Runnable action);

    void runOnWorkThread(Runnable action);

    void smoothScrollToTop(boolean autoRefresh);

    void setOnActivityResultListener(OnActivityResultListener listener);

    int contentViewBackgroundColor();

    void onBackPressed();

    <T> T createHttpRequest(Class<T> clazz);

    <T> T createHttpRequest(Class<T> clazz, Object tag);

    <T> T createHttpRequest(Class<T> clazz, NetworkErrorReceiver receiver);

    <T> T createHttpRequest(Class<T> clazz, Object requestTag, NetworkErrorReceiver receiver);
}
