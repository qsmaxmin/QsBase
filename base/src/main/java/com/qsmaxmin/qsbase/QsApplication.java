package com.qsmaxmin.qsbase;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.view.animation.Animation;

import com.qsmaxmin.qsbase.common.http.HttpInterceptor;
import com.qsmaxmin.qsbase.common.utils.ImageHelper;
import com.qsmaxmin.qsbase.common.utils.QsHelper;
import com.qsmaxmin.qsbase.common.widget.dialog.DefaultProgressDialog;
import com.qsmaxmin.qsbase.common.widget.dialog.QsProgressDialog;
import com.qsmaxmin.qsbase.mvvm.MvIView;

import java.util.HashMap;

import androidx.annotation.LayoutRes;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/6/20 16:40
 * @Description
 */

public abstract class QsApplication extends Application implements QsIApplication {

    @Override public void onCreate() {
        super.onCreate();
        QsHelper.init(this);
    }

    @Override public Application getApplication() {
        return this;
    }

    @Override public @LayoutRes int loadingLayoutId() {
        return R.layout.qs_default_loading;
    }

    @Override public @LayoutRes int emptyLayoutId() {
        return R.layout.qs_default_empty;
    }

    @Override public @LayoutRes int errorLayoutId() {
        return R.layout.qs_default_error;
    }

    @Override public QsProgressDialog getLoadingDialog() {
        return new DefaultProgressDialog();
    }

    @Override public HttpInterceptor registerGlobalHttpInterceptor() {
        return null;
    }

    @Override public void onCommonLoadImage(ImageHelper.Builder builder) {
    }

    @Override public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override public void onActivityStarted(Activity activity) {
    }

    @Override public void onActivityResumed(Activity activity) {
    }

    @Override public void onActivityPaused(Activity activity) {
    }

    @Override public void onActivityStopped(Activity activity) {
    }

    @Override public void onActivityDestroyed(Activity activity) {
    }

    /**
     * 布局状态切换时播放的入场动画
     *
     * @see MvIView#isOpenViewState()
     */
    @Override public Animation viewStateInAnimation() {
        return null;
    }

    /**
     * 布局状态切换时播放的出场动画
     *
     * @see MvIView#isOpenViewState()
     */
    @Override public Animation viewStateOutAnimation() {
        return null;
    }

    /**
     * 布局状态切换时播放的入场动画
     *
     * @see MvIView#isOpenViewState()
     */
    @Override public int viewStateInAnimationId() {
        return 0;
    }

    /**
     * 布局状态切换时播放的出场动画
     *
     * @see MvIView#isOpenViewState()
     */
    @Override public int viewStateOutAnimationId() {
        return 0;
    }

    /**
     * 第一次切换时是否播放动画
     *
     * @see MvIView#isOpenViewState()
     */
    @Override public boolean viewStateAnimateFirstView() {
        return true;
    }

    /**
     * 绑定路由，用户无需修改
     * for QsTransform
     */
    @Override public void bindRouteByQsPlugin(HashMap<String, Class<?>> map) {
    }
}
