package com.qsmaxmin.qsbase;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.support.annotation.LayoutRes;

import com.qsmaxmin.qsbase.common.http.HttpBuilder;
import com.qsmaxmin.qsbase.common.http.HttpResponse;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.utils.ImageHelper;
import com.qsmaxmin.qsbase.common.utils.QsHelper;
import com.qsmaxmin.qsbase.common.widget.dialog.QsProgressDialog;

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
    public QsProgressDialog getLoadingDialog() {
        return null;
    }

    public @LayoutRes int loadingLayoutId() {
        return 0;
    }

    public @LayoutRes int emptyLayoutId() {
        return 0;
    }

    public @LayoutRes int errorLayoutId() {
        return 0;
    }

    /**
     * @param builder http请求参数封装
     *                {@link HttpBuilder#setTerminal(String)}设置主机地址
     *                {@link HttpBuilder#getPath()}获取路径
     *                {@link HttpBuilder#getHeaderBuilder()} ()}获取header
     *                {@link HttpBuilder#getTerminal()} ()}获取主机地址
     */
    public abstract void initHttpAdapter(HttpBuilder builder);

    public void onCommonHttpResponse(HttpResponse response) {
    }

    /**
     * 获取当前进程名
     */
    public String getCurrentProcessName() {
        int pid = android.os.Process.myPid();
        String processName = "";
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (manager == null) return processName;
        for (ActivityManager.RunningAppProcessInfo process : manager.getRunningAppProcesses()) {
            if (process.pid == pid) {
                processName = process.processName;
                break;
            }
        }
        return processName;
    }

    public boolean isMainProcess() {
        return getPackageName().equals(getCurrentProcessName());
    }

    public boolean isCurrentProcess(String processName) {
        return getCurrentProcessName().equals(processName);
    }

    public void onCommonLoadImage(ImageHelper.Builder builder) {
    }
}
