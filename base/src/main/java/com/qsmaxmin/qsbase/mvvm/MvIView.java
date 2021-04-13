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
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

/**
 * @CreateBy qsmaxmin
 * @Date 2020/12/8 14:42
 * @Description MVVM架构的View层基类
 */
public interface MvIView extends IView, QsIBindEvent, QsIBindBundle, QsNotProguard {
    int VIEW_STATE_LOADING = 0;
    int VIEW_STATE_CONTENT = 1;
    int VIEW_STATE_EMPTY   = 2;
    int VIEW_STATE_ERROR   = 3;

    String initTag();

    void onViewClick(@NonNull View view);

    int rootViewLayoutId();

    int actionbarLayoutId();

    int layoutId();

    int loadingLayoutId();

    int emptyLayoutId();

    int errorLayoutId();

    View onCreateRootView(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent);

    View onCreateActionbarView(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent);

    View onCreateLoadingView(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent);

    View onCreateContentView(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent);

    View onCreateEmptyView(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent);

    View onCreateErrorView(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent);

    void onViewCreated(@NonNull View rootView);

    void initData(@Nullable Bundle savedInstanceState);

    boolean isOpenViewState();

    Animation viewStateInAnimation();

    Animation viewStateOutAnimation();

    int viewStateInAnimationId();

    int viewStateOutAnimationId();

    boolean viewStateAnimateFirstView();

    boolean isShowBackButtonInDefaultView();

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

    @NonNull <T> T createHttpRequest(Class<T> clazz);

    @NonNull <T> T createHttpRequest(Class<T> clazz, Object tag);

    @NonNull <T> T createHttpRequest(Class<T> clazz, NetworkErrorReceiver receiver);

    @NonNull <T> T createHttpRequest(Class<T> clazz, Object requestTag, NetworkErrorReceiver receiver);

    @NonNull QsProgressDialog getLoadingDialog();

    boolean isViewDestroyed();
}
