package com.qsmaxmin.qsbase.mvp;

import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

/**
 * @CreateBy qsmaxmin
 * @Date 2020/7/21 11:05
 * @Description
 */
public interface OnActivityResultListener {
    void onActivityResult(FragmentActivity activity, int requestCode, int resultCode, @Nullable Intent intent);
}
