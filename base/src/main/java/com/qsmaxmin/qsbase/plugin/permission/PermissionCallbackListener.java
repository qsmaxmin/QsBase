package com.qsmaxmin.qsbase.plugin.permission;

/**
 * @CreateBy qsmaxmin
 * @Date 17/7/16  下午3:27
 * @Description permission callback
 */

public interface PermissionCallbackListener {
    void onPermissionCallback(boolean grantedAll, boolean shouldShowRationale, String[] permissions, int[] grantResults);
}
