package com.qsmaxmin.qsbase.common.widget.ptr;

import android.view.View;

import com.qsmaxmin.qsbase.mvvm.MvIPullToRefreshView;

public class PtrDefaultHandler implements PtrHandler {
    private final MvIPullToRefreshView refreshView;

    public PtrDefaultHandler(MvIPullToRefreshView refreshView) {
        this.refreshView = refreshView;
    }

    @Override public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return !content.canScrollVertically(-1);
    }

    @Override public void onRefreshBegin(PtrFrameLayout frame) {
        if (refreshView != null) {
            refreshView.onRefresh();
        }
    }
}