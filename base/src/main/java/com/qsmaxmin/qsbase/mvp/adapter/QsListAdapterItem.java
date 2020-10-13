package com.qsmaxmin.qsbase.mvp.adapter;

import android.content.Context;
import android.view.View;

import com.qsmaxmin.annotation.QsNotProguard;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.mvp.QsIListView;
import com.qsmaxmin.qsbase.plugin.bind.QsIBindView;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

/**
 * @CreateBy qsmaxmin
 * @Date 16/8/4
 * @Description ListView holder
 */
public abstract class QsListAdapterItem<D> implements QsIBindView, QsNotProguard {
    private QsIListView<D> viewLayer;

    protected String initTag() {
        return L.isEnable() ? getClass().getSimpleName() : "QsListAdapterItem";
    }

    public abstract int getItemLayout();

    @CallSuper public void init(View contentView) {
        bindViewByQsPlugin(contentView);
    }

    /**
     * for QsTransform
     */
    @CallSuper @Override public void bindViewByQsPlugin(View view) {
    }

    public abstract void bindData(D data, int position, int count);

    public void onViewClick(View view) {
    }

    public final void setViewLayer(QsIListView<D> viewLayer) {
        this.viewLayer = viewLayer;
    }

    @NonNull protected QsIListView<D> getViewLayer() {
        return viewLayer;
    }

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
