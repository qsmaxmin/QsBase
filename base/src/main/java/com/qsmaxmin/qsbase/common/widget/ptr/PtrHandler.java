package com.qsmaxmin.qsbase.common.widget.ptr;

import android.view.View;

public interface PtrHandler {

    /**
     * Check can do refresh or not. For example the content is empty or the first child is in view.
     */
    boolean checkCanDoRefresh(final PtrFrameLayout frame, final View content, final View header);

    /**
     * When refresh begin
     */
    void onRefreshBegin(final PtrFrameLayout frame);
}