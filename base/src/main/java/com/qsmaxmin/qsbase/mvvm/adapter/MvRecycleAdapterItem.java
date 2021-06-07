package com.qsmaxmin.qsbase.mvvm.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.utils.ViewHelper;
import com.qsmaxmin.qsbase.mvvm.IView;
import com.qsmaxmin.qsbase.mvvm.MvIRecyclerView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;


/**
 * @CreateBy qsmaxmin
 * @Date 16/8/5
 * @Description RecyclerView holder
 */
public abstract class MvRecycleAdapterItem<D> implements IView {
    private MvIRecyclerView<D> viewLayer;
    private View               itemView;
    private D                  data;
    private int                position;
    private int                totalCount;
    private int                scrollState;
    private boolean            hasBindDataIdle;

    /**
     * @deprecated
     */
    public MvRecycleAdapterItem(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
    }

    public MvRecycleAdapterItem() {
    }

    protected final String initTag() {
        return L.isEnable() ? getClass().getSimpleName() : "MvRecycleAdapterItem";
    }

    protected abstract View onCreateItemView(LayoutInflater inflater, ViewGroup parent);

    final void onBindItemDataInner(D data, int position, int totalCount) {
        this.data = data;
        this.position = position;
        this.totalCount = totalCount;
        onBindItemData(data, position, totalCount);
        if (isRecyclerViewIdle()) {
            hasBindDataIdle = true;
            onBindItemDataIdle(data, position, totalCount);
        } else {
            hasBindDataIdle = false;
        }
    }

    protected abstract void onBindItemData(D data, int position, int totalCount);

    /**
     * 当列表滚动停止时触发，每个适配器项仅触发一次
     * 常见的做法如：
     * 1，在{@link #onBindItemData(Object, int, int)}里将ImageView控件的纹理置空
     * 2，重写该方法，在该方法里给ImageView设置图片
     * 这么做的好处是能够带来更流畅的滑动体验
     */
    protected void onBindItemDataIdle(D data, int position, int totalCount) {
    }

    final void onScrollStateChangedInner(int scrollState) {
        this.scrollState = scrollState;
        if (!hasBindDataIdle && isRecyclerViewIdle()) {
            hasBindDataIdle = true;
            onBindItemDataIdle(data, position, totalCount);
        }
        onScrollStateChanged(scrollState);
    }

    protected void onScrollStateChanged(int scrollState) {
    }

    public final D getData() {
        return data;
    }

    public final int getPosition() {
        return position;
    }

    public final int getTotalCount() {
        return totalCount;
    }

    public final int getScrollState() {
        return scrollState;
    }

    public final boolean isRecyclerViewIdle() {
        return scrollState == RecyclerView.SCROLL_STATE_IDLE;
    }

    public final boolean isRecyclerViewDragging() {
        return scrollState == RecyclerView.SCROLL_STATE_DRAGGING;
    }

    public final boolean isRecyclerViewSettling() {
        return scrollState == RecyclerView.SCROLL_STATE_SETTLING;
    }

    public final View getItemView() {
        return itemView;
    }

    public final void setViewLayer(final MvIRecyclerView<D> viewLayer, LayoutInflater inflater, ViewGroup parent) {
        this.viewLayer = viewLayer;
        this.itemView = onCreateItemView(inflater, parent);
        init(itemView);
    }

    protected void init(View itemView) {
    }

    @NonNull protected final MvIRecyclerView<D> getViewLayer() {
        return viewLayer;
    }

    protected final void sendEvent(int eventType, D data, int position) {
        viewLayer.onReceiveAdapterItemEvent(eventType, data, position);
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