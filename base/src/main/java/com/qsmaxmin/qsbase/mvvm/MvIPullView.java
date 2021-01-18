package com.qsmaxmin.qsbase.mvvm;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

/**
 * @CreateBy qsmaxmin
 * @Date 17/7/2  下午12:54
 * @Description
 */

public interface MvIPullView extends MvIPullToRefreshView {
    View onCreateChildView(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent);
}
