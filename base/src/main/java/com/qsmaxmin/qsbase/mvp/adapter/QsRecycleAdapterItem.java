package com.qsmaxmin.qsbase.mvp.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.model.QsNotProguard;
import com.qsmaxmin.qsbase.common.viewbind.ViewBindHelper;
import com.qsmaxmin.qsbase.mvp.QsIRecyclerView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;


/**
 * @CreateBy qsmaxmin
 * @Date 16/8/5
 * @Description RecyclerView的Item封装类
 */
@SuppressWarnings("WeakerAccess")
public abstract class QsRecycleAdapterItem<D> implements QsNotProguard {
    private View               mItemView;
    private QsIRecyclerView<D> viewLayer;

    public QsRecycleAdapterItem(LayoutInflater inflater, ViewGroup parent) {
        mItemView = inflater.inflate(itemViewLayoutId(), parent, false);
        ViewBindHelper.bindView(this, mItemView);
    }

    protected String initTag() {
        return L.isEnable() ? getClass().getSimpleName() : "QsRecycleAdapterItem";
    }

    protected abstract int itemViewLayoutId();

    protected abstract void onBindItemData(D data, int position, int totalCount);

    View getItemView() {
        return mItemView;
    }

    public void onViewClick(View view) {
    }

    public final void setViewLayer(QsIRecyclerView<D> viewLayer) {
        this.viewLayer = viewLayer;
    }

    @NonNull protected QsIRecyclerView<D> getViewLayer() {
        return viewLayer;
    }

    /**
     * 发送事件给view层（Fragment或者Activity）
     *
     * @param eventType 事件类型
     * @param data      数据
     * @param position  item索引
     */
    protected final void sendEvent(int eventType, D data, int position) {
        viewLayer.onReceiveAdapterItemEvent(eventType, data, position);
    }

    public final Context getContext() {
        return viewLayer.getContext();
    }

    protected final FragmentActivity getActivity() {
        return viewLayer.getActivity();
    }
}