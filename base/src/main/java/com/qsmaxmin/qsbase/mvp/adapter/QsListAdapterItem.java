package com.qsmaxmin.qsbase.mvp.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.model.QsNotProguard;
import com.qsmaxmin.qsbase.common.viewbind.ViewBindHelper;
import com.qsmaxmin.qsbase.mvp.QsIListView;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

/**
 * Created by sky on 15/2/6. 适配器
 */
public abstract class QsListAdapterItem<D> implements QsNotProguard {
    private QsIListView<D> viewLayer;

    protected String initTag() {
        return L.isEnable() ? getClass().getSimpleName() : "QsListAdapterItem";
    }

    public abstract int getItemLayout();

    @CallSuper public void init(View contentView) {
        ViewBindHelper.bindView(this, contentView);
    }

    public abstract void bindData(D t, int position, int count);

    public void onViewClick(View view) {
    }

    public final void setViewLayer(QsIListView<D> viewLayer) {
        this.viewLayer = viewLayer;
    }

    @NonNull protected QsIListView<D> getViewLayer() {
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

    protected final Context getContext() {
        return viewLayer.getContext();
    }

    protected final FragmentActivity getActivity() {
        return viewLayer.getActivity();
    }
}
