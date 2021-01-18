package com.qsmaxmin.qsbase.mvvm.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qsmaxmin.annotation.QsNotProguard;
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
public abstract class MvListAdapterItem<D> implements QsNotProguard, IView {
    private MvIListView<D> viewLayer;

    @Override public final void onViewClicked(@NonNull View view) {
        onViewClicked(view, 400);
    }

    @Override public final void onViewClicked(@NonNull View view, long interval) {
        if (interval > 0) {
            if (ViewHelper.isFastClick(interval)) return;
        }
        onViewClick(view);
    }

    @Override public final Context getContext() {
        return viewLayer.getContext();
    }

    @Override public final FragmentActivity getActivity() {
        return viewLayer.getActivity();
    }

    @Override public final void loading() {
        if (getActivity() instanceof IView) {
            ((IView) getActivity()).loading();
        }
    }

    @Override public final void loading(int resId) {
        if (getActivity() instanceof IView) {
            ((IView) getActivity()).loading(resId);
        }
    }

    @Override public final void loading(String message) {
        if (getActivity() instanceof IView) {
            ((IView) getActivity()).loading(message);
        }
    }

    @Override public final void loading(boolean cancelAble) {
        if (getActivity() instanceof IView) {
            ((IView) getActivity()).loading(cancelAble);
        }
    }

    @Override public final void loading(int resId, boolean cancelAble) {
        if (getActivity() instanceof IView) {
            ((IView) getActivity()).loading(resId, cancelAble);
        }
    }

    @Override public final void loading(String message, boolean cancelAble) {
        if (getActivity() instanceof IView) {
            ((IView) getActivity()).loading(message, cancelAble);
        }
    }

    @Override public final void loadingClose() {
        if (getActivity() instanceof IView) {
            ((IView) getActivity()).loadingClose();
        }
    }

    @Override public final void activityFinish() {
        if (getActivity() instanceof IView) {
            ((IView) getActivity()).activityFinish();
        }
    }

    @Override public final void activityFinish(int enterAnim, int exitAnim) {
        if (getActivity() instanceof IView) {
            ((IView) getActivity()).activityFinish(enterAnim, exitAnim);
        }
    }

    @Override public final void activityFinish(boolean finishAfterTransition) {
        if (getActivity() instanceof IView) {
            ((IView) getActivity()).activityFinish(finishAfterTransition);
        }
    }

    protected void onViewClick(@NonNull View view) {
    }

    protected String initTag() {
        return L.isEnable() ? getClass().getSimpleName() : "MvListAdapterItem";
    }

    public abstract View onCreateItemView(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent);

    public abstract void bindData(D data, int position, int count);

    public final void setViewLayer(MvIListView<D> viewLayer) {
        this.viewLayer = viewLayer;
    }

    @NonNull protected MvIListView<D> getViewLayer() {
        return viewLayer;
    }

    protected final void sendEvent(int eventType, D data, int position) {
        viewLayer.onReceiveAdapterItemEvent(eventType, data, position);
    }

    @Override public final void intent2Activity(Class clazz) {
        intent2Activity(clazz, null, 0, null, 0, 0);
    }

    @Override public final void intent2Activity(Class clazz, int requestCode) {
        intent2Activity(clazz, null, requestCode, null, 0, 0);
    }

    @Override public final void intent2Activity(Class clazz, Bundle bundle) {
        intent2Activity(clazz, bundle, 0, null, 0, 0);
    }

    @Override public final void intent2Activity(Class clazz, Bundle bundle, int requestCode) {
        intent2Activity(clazz, bundle, requestCode, null, 0, 0);
    }

    @Override public final void intent2Activity(Class clazz, Bundle bundle, int inAnimId, int outAnimId) {
        intent2Activity(clazz, bundle, 0, null, inAnimId, outAnimId);
    }

    @Override public final void intent2Activity(Class clazz, Bundle bundle, ActivityOptionsCompat optionsCompat) {
        intent2Activity(clazz, bundle, 0, optionsCompat, 0, 0);
    }

    @Override public final void intent2Activity(Class clazz, Bundle bundle, int requestCode, ActivityOptionsCompat optionsCompat) {
        intent2Activity(clazz, bundle, requestCode, optionsCompat, 0, 0);
    }

    @Override public final void intent2Activity(Class clazz, Bundle bundle, int requestCode, ActivityOptionsCompat optionsCompat, int enterAnim, int existAnim) {
        ViewHelper.intent2Activity(getActivity(), clazz, bundle, requestCode, optionsCompat, enterAnim, existAnim);
    }
}
