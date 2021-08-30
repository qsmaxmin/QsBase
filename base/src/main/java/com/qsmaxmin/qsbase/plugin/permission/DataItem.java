package com.qsmaxmin.qsbase.plugin.permission;

import android.app.Activity;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/3/7 12:36
 * @Description
 */

class DataItem {
    private PermissionCallbackListener mListener;
    private Activity                   mActivity;
    private boolean                    showToastWhenReject;
    private String                     toastText;

    public PermissionCallbackListener getListener() {
        return mListener;
    }

    public DataItem setListener(PermissionCallbackListener listener) {
        this.mListener = listener;
        return this;
    }

    public Activity getActivity() {
        return mActivity;
    }

    public DataItem setActivity(Activity activity) {
        this.mActivity = activity;
        return this;
    }

    public boolean isShowToastWhenReject() {
        return showToastWhenReject;
    }

    public void setShowToastWhenReject(boolean showToastWhenReject) {
        this.showToastWhenReject = showToastWhenReject;
    }

    public String getToastText() {
        return toastText;
    }

    public void setToastText(String toastText) {
        this.toastText = toastText;
    }
}
