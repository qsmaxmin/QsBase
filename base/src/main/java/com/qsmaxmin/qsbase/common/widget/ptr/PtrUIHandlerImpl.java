package com.qsmaxmin.qsbase.common.widget.ptr;


import com.qsmaxmin.qsbase.common.widget.ptr.indicator.PtrIndicator;

/**
 * PtrFrameLayout交互监听实现
 */
public class PtrUIHandlerImpl implements PtrUIHandler {

    /**
     * When the content view has reached top and refresh has been completed, view will be reset.
     */
    public void onUIReset(PtrFrameLayout frame) {

    }

    /**
     * prepare for loading
     */
    public void onUIRefreshPrepare(PtrFrameLayout frame) {
    }

    /**
     * perform refreshing UI
     */
    public void onUIRefreshBegin(PtrFrameLayout frame) {
    }

    /**
     * perform UI after refresh
     */
    public void onUIRefreshComplete(PtrFrameLayout frame) {
    }

    /**
     * @param status {@link PtrFrameLayout#PTR_STATUS_INIT}等
     */
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {
    }
}
