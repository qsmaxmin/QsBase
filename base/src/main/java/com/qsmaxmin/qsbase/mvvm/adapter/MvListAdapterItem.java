package com.qsmaxmin.qsbase.mvvm.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.utils.ViewHelper;
import com.qsmaxmin.qsbase.mvvm.IView;
import com.qsmaxmin.qsbase.mvvm.MvIListView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.FragmentActivity;

/**
 * @CreateBy qsmaxmin
 * @Date 16/8/4
 * @Description ListView holder
 */
public abstract class MvListAdapterItem<D> implements IView {
    private MvIListView<D> viewLayer;
    private D              data;
    private int            position;
    private int            totalCount;
    private int            scrollState;

    protected String initTag() {
        return L.isEnable() ? getClass().getSimpleName() : "MvListAdapterItem";
    }

    @Override public final void onViewClicked(@NonNull View view) {
        onViewClicked(view, 400);
    }

    @Override public final void onViewClicked(@NonNull View view, long interval) {
        if (interval > 0 && ViewHelper.isFastClick(interval)) return;
        onViewClick(view);
    }

    protected void onViewClick(@NonNull View view) {
    }

    public void init(@NonNull View contentView) {
    }

    public abstract View onCreateItemView(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent);

    final void bindDataInner(D data, int position, int count) {
        this.data = data;
        this.position = position;
        this.totalCount = count;
        bindData(data, position, count);
    }

    public abstract void bindData(D data, int position, int totalCount);

    final void onScrollStateChangedInner(int scrollState) {
        this.scrollState = scrollState;
        onScrollStateChanged(scrollState);
    }

    protected void onScrollStateChanged(int scrollState) {
    }

    public final int getPosition() {
        return position;
    }

    public final int getTotalCount() {
        return totalCount;
    }

    public final D getData() {
        return data;
    }

    public final int getScrollState() {
        return scrollState;
    }

    public final boolean isListViewIdle() {
        return scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE;
    }

    public final boolean isListViewFling() {
        return scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING;
    }

    public final boolean isListViewTouchScroll() {
        return scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL;
    }

    public final void setViewLayer(MvIListView<D> viewLayer) {
        this.viewLayer = viewLayer;
    }

    @NonNull protected MvIListView<D> getViewLayer() {
        return viewLayer;
    }

    protected final void sendEvent(int eventType, D data, int position) {
        viewLayer.onReceiveAdapterItemEvent(eventType, data, position);
    }

    @Override public final Context getContext() {
        return viewLayer.getContext();
    }

    @Override public final FragmentActivity getActivity() {
        return viewLayer.getActivity();
    }

    @Override public final void loading() {
        viewLayer.loading();
    }

    @Override public final void loading(int resId) {
        viewLayer.loading(resId);
    }

    @Override public final void loading(String message) {
        viewLayer.loading(message);
    }

    @Override public final void loading(boolean cancelAble) {
        viewLayer.loading(cancelAble);
    }

    @Override public final void loading(int resId, boolean cancelAble) {
        viewLayer.loading(resId, cancelAble);
    }

    @Override public final void loading(String message, boolean cancelAble) {
        viewLayer.loading(message, cancelAble);
    }

    @Override public final void loadingClose() {
        viewLayer.loadingClose();
    }

    @Override public final void activityFinish() {
        viewLayer.activityFinish();
    }

    @Override public final void activityFinish(int enterAnim, int exitAnim) {
        viewLayer.activityFinish(enterAnim, exitAnim);
    }

    @Override public final void activityFinish(boolean finishAfterTransition) {
        viewLayer.activityFinish(finishAfterTransition);
    }

    @Override public final void intent2Activity(Class<?> clazz) {
        viewLayer.intent2Activity(clazz);
    }

    @Override public final void intent2Activity(Class<?> clazz, int requestCode) {
        viewLayer.intent2Activity(clazz, requestCode);
    }

    @Override public final void intent2Activity(Class<?> clazz, Bundle bundle) {
        viewLayer.intent2Activity(clazz, bundle);
    }

    @Override public final void intent2Activity(Class<?> clazz, Bundle bundle, int requestCode) {
        viewLayer.intent2Activity(clazz, bundle, requestCode);
    }

    @Override public final void intent2Activity(Class<?> clazz, Bundle bundle, int inAnimId, int outAnimId) {
        viewLayer.intent2Activity(clazz, bundle, inAnimId, outAnimId);
    }

    @Override public final void intent2Activity(Class<?> clazz, Bundle bundle, ActivityOptionsCompat optionsCompat) {
        viewLayer.intent2Activity(clazz, bundle, optionsCompat);
    }

    @Override public final void intent2Activity(Class<?> clazz, Bundle bundle, int requestCode, ActivityOptionsCompat optionsCompat) {
        viewLayer.intent2Activity(clazz, bundle, requestCode, optionsCompat);
    }

    @Override public final void intent2Activity(Class<?> clazz, Bundle bundle, int requestCode, ActivityOptionsCompat optionsCompat, int enterAnim, int existAnim) {
        viewLayer.intent2Activity(clazz, bundle, requestCode, optionsCompat, enterAnim, existAnim);
    }

    @Override public boolean isViewDestroyed() {
        return viewLayer.isViewDestroyed();
    }
}
