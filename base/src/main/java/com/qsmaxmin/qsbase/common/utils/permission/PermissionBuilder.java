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

    private List<String> wantPermissionArr   = new ArrayList<>();
    private boolean      mIsShowCustomDialog = true;

    private int                        mRequestCode;
    private PermissionCallbackListener mListener;
    private Activity                   mActivity;

    public List<String> getWantPermissionArr() {
        return wantPermissionArr;
    }

    public PermissionBuilder addWantPermission(String permission) {
        if (!wantPermissionArr.contains(permission)) wantPermissionArr.add(permission);
        return this;
    }

    public boolean isShowCustomDialog() {
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

    /**
     * 自增量
     */
    public int getRequestCode() {
        return mRequestCode;
    }

    void setRequestCode(int mRequestCode) {
        this.mRequestCode = mRequestCode;
    }

    public void start() {
        PermissionUtils.getInstance().startRequestPermission(this);
    }


    @Override public String toString() {
        return "PermissionBuilder{" + "wantPermissionArr=" + wantPermissionArr + ", mIsShowCustomDialog=" + mIsShowCustomDialog + ", mRequestCode=" + mRequestCode + '}';
    }
}
