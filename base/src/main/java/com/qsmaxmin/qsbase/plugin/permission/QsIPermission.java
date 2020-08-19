package com.qsmaxmin.qsbase.plugin.permission;

/**
 * @CreateBy administrator
 * @Date 2020/8/19 14:55
 * @Description for QsTransform
 */
public interface QsIPermission {
    void requestPermission(PermissionCallbackListener listener, String... permissions);
}
