package com.qsmaxmin.qsbase;

import android.app.Activity;
import android.app.Application;
import android.support.annotation.LayoutRes;

import com.qsmaxmin.qsbase.common.http.QsHttpCallback;
import com.qsmaxmin.qsbase.common.utils.ImageHelper;
import com.qsmaxmin.qsbase.common.widget.dialog.QsProgressDialog;

/**
 * @CreateBy qsmaxmin
 * @Date 2019/7/24 15:02
 * @Description
 */
public interface QsIApplication {

    Application getApplication();

    boolean isLogOpen();

    void onActivityCreate(Activity activity);

    void onActivityStart(Activity activity);

    void onActivityResume(Activity activity);

    void onActivityPause(Activity activity);

    void onActivityStop(Activity activity);

    void onActivityDestroy(Activity activity);

    @LayoutRes int loadingLayoutId();

    @LayoutRes int emptyLayoutId();

    @LayoutRes int errorLayoutId();

    /**
     * 公共progressDialog
     */
    QsProgressDialog getLoadingDialog();

    /**
     * http请求全局回调
     */
    QsHttpCallback registerGlobalHttpListener();

    /**
     * 公共图片加载回调
     */
    void onCommonLoadImage(ImageHelper.Builder builder);
}
