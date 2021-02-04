package com.qsmaxmin.qsbase.mvp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

/**
 * @CreateBy qsmaxmin
 * @Date 17/7/2  下午12:54
 * @Description
 */

public interface QsIPullView extends QsIPullToRefreshView {
    View onCreateChildView(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent);
}
