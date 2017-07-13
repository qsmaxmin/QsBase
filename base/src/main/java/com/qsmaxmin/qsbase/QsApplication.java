package com.qsmaxmin.qsbase;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.qsmaxmin.qsbase.common.http.HttpAdapter;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.utils.QsHelper;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/6/20 16:40
 * @Description
 */

public abstract class QsApplication extends Application {

    @Override protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override public void onCreate() {
        super.onCreate();
        if (isLogOpen()) L.init(true);
        QsHelper.getInstance().init(this);
    }

    protected abstract boolean isLogOpen();

    public abstract void initHttpAdapter(HttpAdapter httpAdapter);

    public void onActivityCreate() {

    }

    public void onActivityStart() {
    }

    public void onActivityResume() {
    }

    public void onActivityPause() {
    }

    public void onActivityStop() {
    }

    public void onActivityDestroy() {
    }

    /**
     * 公共progressDialog
     */
    public ProgressDialog getCommonProgressDialog(Context context) {
        return new ProgressDialog(context);
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
}
