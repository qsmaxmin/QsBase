package com.qsmaxmin.qsbase.common.widget.ptr;

import android.view.View;

import com.qsmaxmin.qsbase.mvp.QsIPullToRefreshView;
import com.qsmaxmin.qsbase.mvvm.MvIPullToRefreshView;

public class PtrDefaultHandler implements PtrHandler {
    private QsIPullToRefreshView mvpRefreshView;
    private MvIPullToRefreshView mvvmRefreshView;

    public PtrDefaultHandler(QsIPullToRefreshView refreshView) {
        this.mvpRefreshView = refreshView;
    }

    public PtrDefaultHandler(MvIPullToRefreshView refreshView) {
        this.mvvmRefreshView = refreshView;
    }

    @Override public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return !content.canScrollVertically(-1);
    }

    @Override public void onRefreshBegin(PtrFrameLayout frame) {
        if (mvvmRefreshView != null) {
            mvvmRefreshView.onRefresh();
        } else if (mvpRefreshView != null) {
            mvpRefreshView.onRefresh();
        }
    }
}