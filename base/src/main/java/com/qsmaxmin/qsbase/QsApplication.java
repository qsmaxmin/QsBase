package com.qsmaxmin.qsbase;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;

import com.qsmaxmin.qsbase.common.http.HttpBuilder;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.utils.QsHelper;

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

    /**
     * 当前线程是HTTP时，检查token是否可用
     * 供项目集成时使用，用户可以根据自己的token状态返回true或者false
     * 当返回true时将不影响程序正常使用
     * 当返回false时，HTTP线程将进入wait状态，直到被notify
     */
    public boolean isTokenAvailable() {
        return true;
    }
}
