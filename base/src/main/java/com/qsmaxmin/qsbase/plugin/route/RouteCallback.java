package com.qsmaxmin.qsbase.plugin.route;

/**
 * @CreateBy qsmaxmin
 * @Date 2021/8/26 11:34
 * @Description
 */
public interface RouteCallback {
    void onSuccess();

    void onFailed(Throwable t);
}
