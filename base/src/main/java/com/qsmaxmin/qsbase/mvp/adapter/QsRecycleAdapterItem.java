package com.qsmaxmin.qsbase.mvp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qsmaxmin.annotation.QsNotProguard;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.mvp.QsIRecyclerView;
import com.qsmaxmin.qsbase.plugin.bind.QsIBindView;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;


/**
 * @CreateBy qsmaxmin
 * @Date 16/8/5
 * @Description RecyclerView holder
 */
@SuppressWarnings("WeakerAccess")
public abstract class QsRecycleAdapterItem<D> implements QsIBindView, QsNotProguard {
    private View               mItemView;
    private QsIRecyclerView<D> viewLayer;

    public QsRecycleAdapterItem(LayoutInflater inflater, ViewGroup parent) {
        mItemView = inflater.inflate(itemViewLayoutId(), parent, false);
        bindViewByQsPlugin(mItemView);
    }

    /**
     * for QsTransform
     */
    @CallSuper @Override public void bindViewByQsPlugin(View view) {
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