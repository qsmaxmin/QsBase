package com.qsmaxmin.qsbase.common.utils.permission;

/**
 * @CreateBy qsmaxmin
 * @Date 17/7/16  下午3:27
 * @Description 权限回调接口
 */

public interface PermissionCallbackListener {
    /**
     * 申请权限的回调
     * @param isGrantedAll 是否全部同意
     */
    void onPermissionCallback(int requestCode, boolean isGrantedAll);
}
