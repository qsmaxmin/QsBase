package com.qsmaxmin.qsbase.common.widget.ptr;


import com.qsmaxmin.qsbase.common.widget.ptr.indicator.PtrIndicator;

/**
 * PtrFrameLayout交互监听
 */
public interface PtrUIHandler {

    /**
     * When the content view has reached top and refresh has been completed, view will be reset.
     */
    void onUIReset(PtrFrameLayout frame);

    /**
     * prepare for loading
     */
    void onUIRefreshPrepare(PtrFrameLayout frame);

    /**
     * perform refreshing UI
     */
    void onUIRefreshBegin(PtrFrameLayout frame);

    /**
     * perform UI after refresh
     */
    void onUIRefreshComplete(PtrFrameLayout frame);

    /**
     * @param status {@link PtrFrameLayout#PTR_STATUS_INIT}等
     */
    void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator);
}
