package com.qsmaxmin.qsbase.mvp;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;

import com.qsmaxmin.annotation.QsNotProguard;
import com.qsmaxmin.qsbase.common.viewbind.OnActivityResultListener;
import com.qsmaxmin.qsbase.common.widget.dialog.QsProgressDialog;
import com.qsmaxmin.qsbase.mvvm.IView;
import com.qsmaxmin.qsbase.plugin.bind.QsIBindBundle;
import com.qsmaxmin.qsbase.plugin.bind.QsIBindView;
import com.qsmaxmin.qsbase.plugin.event.QsIBindEvent;
import com.qsmaxmin.qsbase.plugin.presenter.QsIPresenter;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/6/21 14:02
 * @Description MVP架构，View层基类
 */
public interface QsIView<P> extends QsIBindView, QsIBindEvent, QsIBindBundle, QsIPresenter, QsNotProguard, IView {
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

    void onLoadingViewCreated(@NonNull View loadingView);

    void onContentViewCreated(@NonNull View contentView);

    void onEmptyViewCreated(@NonNull View emptyView);

    void onErrorViewCreated(@NonNull View errorView);

    void onViewCreated(View view);

    void initData(@Nullable Bundle savedInstanceState);

    void onViewClick(@NonNull View view);

    P getPresenter();

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

    @NonNull QsProgressDialog getLoadingDialog();
}
