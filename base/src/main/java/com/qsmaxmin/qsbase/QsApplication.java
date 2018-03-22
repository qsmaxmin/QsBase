package com.qsmaxmin.qsbase;

import android.app.Activity;
import android.app.Application;
import android.support.annotation.IdRes;

import com.qsmaxmin.qsbase.common.http.HttpBuilder;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.utils.QsHelper;
import com.qsmaxmin.qsbase.common.widget.dialog.QsProgressDialog;

import okhttp3.Response;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/6/20 16:40
 * @Description
 */

public abstract class QsApplication extends Application {

    @Override public void onCreate() {
        super.onCreate();
        if (isLogOpen()) L.init(true);
        QsHelper.getInstance().init(this);
    }

    public abstract boolean isLogOpen();

    public abstract void initHttpAdapter(HttpBuilder builder);

    public void onActivityCreate(Activity activity) {
    }

    public void onActivityStart(Activity activity) {
    }

    public void onActivityResume(Activity activity) {
    }

    public void onActivityPause(Activity activity) {
    }

    public void onActivityStop(Activity activity) {
    }

    public void onActivityDestroy(Activity activity) {
    }

    /**
     * 公共progressDialog
     */
    public QsProgressDialog getCommonProgressDialog() {
        return null;
    }

    public int loadingLayoutId() {
        return 0;
    }

    public int emptyLayoutId() {
        return 0;
    }

    public int errorLayoutId() {
        return 0;
    }

    public int listFooterLayoutId() {
        return R.layout.qs_loading_footer;
    }

    public void onCommonHttpResponse(Response response) {
    }

    public @IdRes int defaultImageHolder() {
        return 0;
    }
}
