package com.qsmaxmin.qsbase;

import android.app.Activity;
import android.app.Application;

import com.qsmaxmin.qsbase.common.http.QsHttpCallback;
import com.qsmaxmin.qsbase.common.utils.ImageHelper;
import com.qsmaxmin.qsbase.common.utils.QsHelper;
import com.qsmaxmin.qsbase.common.widget.dialog.DefaultProgressDialog;
import com.qsmaxmin.qsbase.common.widget.dialog.QsProgressDialog;

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

    @Override public abstract boolean isLogOpen();

    @Override public void onActivityCreate(Activity activity) {
    }

    @Override public void onActivityStart(Activity activity) {
    }

    @Override public void onActivityResume(Activity activity) {
    }

    @Override public void onActivityPause(Activity activity) {
    }

    @Override public void onActivityStop(Activity activity) {
    }

    @Override public void onActivityDestroy(Activity activity) {
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

    @Override public QsHttpCallback registerGlobalHttpListener() {
        return null;
    }

    public void onCommonLoadImage(ImageHelper.Builder builder) {
    }
}
