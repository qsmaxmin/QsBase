package com.qsmaxmin.qsbase.common.utils.permission;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/3/7 12:36
 * @Description
 */

public class PermissionBuilder {
    private List<String>               wantPermissionArr   = new ArrayList<>();
    private boolean                    mIsShowCustomDialog = true;
    private boolean                    mForceGoOn;
    private int                        mRequestCode;
    private PermissionCallbackListener mListener;
    private Activity                   mActivity;

    List<String> getWantPermissionArr() {
        return wantPermissionArr;
    }

    public PermissionBuilder addWantPermission(String permission) {
        if (!wantPermissionArr.contains(permission)) wantPermissionArr.add(permission);
        return this;
    }

    boolean isShowCustomDialog() {
        return mIsShowCustomDialog;
    }

    public PermissionBuilder setShowCustomDialog(boolean isShowCustomDialog) {
        this.mIsShowCustomDialog = isShowCustomDialog;
        return this;
    }

    public PermissionCallbackListener getListener() {
        return mListener;
    }

    public PermissionBuilder setListener(PermissionCallbackListener listener) {
        this.mListener = listener;
        return this;
    }

    public Activity getActivity() {
        return mActivity;
    }

    public PermissionBuilder setActivity(Activity activity) {
        this.mActivity = activity;
        return this;
    }

    public PermissionBuilder setForceGoOn(boolean forceGoOn) {
        this.mForceGoOn = forceGoOn;
        return this;
    }

    boolean isForceGoOn() {
        return mForceGoOn;
    }

    /**
     * 自增量
     */
    int getRequestCode() {
        return mRequestCode;
    }

    void setRequestCode(int mRequestCode) {
        this.mRequestCode = mRequestCode;
    }

    @Override public String toString() {
        return "PermissionBuilder{" + "wantPermissionArr=" + wantPermissionArr + ", mIsShowCustomDialog=" + mIsShowCustomDialog + ", mRequestCode=" + mRequestCode + '}';
    }
}
