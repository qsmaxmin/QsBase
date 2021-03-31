package com.qsmaxmin.qsbase;

import android.app.Activity;
import android.app.Application;
import android.view.LayoutInflater;
import android.view.ViewGroup;

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
public interface QsIApplication {

    Application getApplication();

    boolean isLogOpen();

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
}
