package com.qsmaxmin.qsbase;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.Animation;

import com.qsmaxmin.annotation.route.QsIRoute;
import com.qsmaxmin.qsbase.common.http.HttpInterceptor;
import com.qsmaxmin.qsbase.common.utils.ImageHelper;
import com.qsmaxmin.qsbase.common.widget.dialog.QsProgressDialog;
import com.qsmaxmin.qsbase.mvvm.MvIView;

import androidx.annotation.LayoutRes;

/**
 * @CreateBy qsmaxmin
 * @Date 2019/7/24 15:02
 * @Description
 */
public interface QsIApplication extends QsIRoute {

    Application getApplication();

    /**
     * 是否是测试模式
     */
    boolean isDebug();

    /**
     * 全局加载中布局
     *
     * @see MvIView#isOpenViewState() return true生效
     * @see MvIView#onCreateLoadingView(LayoutInflater, ViewGroup)
     */
    @LayoutRes int loadingLayoutId();

    /**
     * 全局空页面布局
     *
     * @see MvIView#isOpenViewState() return true生效
     * @see MvIView#onCreateEmptyView(LayoutInflater, ViewGroup)
     */
    @LayoutRes int emptyLayoutId();

    /**
     * 全局数据异常布局
     *
     * @see MvIView#isOpenViewState() return true生效
     * @see MvIView#onCreateErrorView(LayoutInflater, ViewGroup)
     */
    @LayoutRes int errorLayoutId();

    /**
     * 公共progressDialog
     */
    QsProgressDialog getLoadingDialog();

    /**
     * http请求全局拦截器
     */
    HttpInterceptor registerGlobalHttpInterceptor();

    /**
     * 公共图片加载回调
     */
    void onCommonLoadImage(ImageHelper.Builder builder);

    void onActivityCreated(Activity activity, Bundle savedInstanceState);

    void onActivityStarted(Activity activity);

    void onActivityResumed(Activity activity);

    void onActivityPaused(Activity activity);

    void onActivityStopped(Activity activity);

    void onActivityDestroyed(Activity activity);

    Animation viewStateInAnimation();

    Animation viewStateOutAnimation();

    int viewStateInAnimationId();

    int viewStateOutAnimationId();

    boolean viewStateAnimateFirstView();
}
