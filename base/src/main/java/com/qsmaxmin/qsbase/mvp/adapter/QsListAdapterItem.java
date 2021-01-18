package com.qsmaxmin.qsbase.mvp.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.qsmaxmin.annotation.QsNotProguard;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.mvp.QsIListView;
import com.qsmaxmin.qsbase.plugin.bind.QsIBindView;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
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

    @CallSuper public void init(@NonNull View contentView) {
        bindViewByQsPlugin(contentView);
    }

    /**
     * for QsTransform
     */
    @CallSuper @Override public void bindViewByQsPlugin(View view) {
    }

    public abstract void bindData(D data, int position, int count);

    public void onViewClick(@NonNull View view) {
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

    public final void intent2Activity(Class clazz) {
        intent2Activity(clazz, null, 0, null, 0, 0);
    }

    public final void intent2Activity(Class clazz, int requestCode) {
        intent2Activity(clazz, null, requestCode, null, 0, 0);
    }

    public final void intent2Activity(Class clazz, Bundle bundle) {
        intent2Activity(clazz, bundle, 0, null, 0, 0);
    }

    public final void intent2Activity(Class clazz, Bundle bundle, int inAnimId, int outAnimId) {
        intent2Activity(clazz, bundle, 0, null, inAnimId, outAnimId);
    }

    public final void intent2Activity(Class clazz, Bundle bundle, ActivityOptionsCompat optionsCompat) {
        intent2Activity(clazz, bundle, 0, optionsCompat, 0, 0);
    }

    public final void intent2Activity(Class clazz, Bundle bundle, int requestCode, ActivityOptionsCompat optionsCompat) {
        intent2Activity(clazz, bundle, requestCode, optionsCompat, 0, 0);
    }

    public final void intent2Activity(Class clazz, Bundle bundle, int requestCode, ActivityOptionsCompat optionsCompat, int inAnimId, int outAnimId) {
        FragmentActivity activity = getActivity();
        if (clazz != null && activity != null && !activity.isFinishing()) {
            Intent intent = new Intent();
            intent.setClass(activity, clazz);
            if (bundle != null) intent.putExtras(bundle);
            if (optionsCompat == null) {
                if (requestCode > 0) {
                    activity.startActivityForResult(intent, requestCode);
                } else {
                    activity.startActivity(intent);
                }
                if (inAnimId != 0 || outAnimId != 0) activity.overridePendingTransition(inAnimId, outAnimId);
            } else {
                if (requestCode > 0) {
                    ActivityCompat.startActivityForResult(activity, intent, requestCode, optionsCompat.toBundle());
                } else {
                    ActivityCompat.startActivity(activity, intent, optionsCompat.toBundle());
                }
            }
        }
    }
}
