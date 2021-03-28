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
import com.qsmaxmin.qsbase.mvvm.MvIView;
import com.qsmaxmin.qsbase.mvvm.model.MvModelPager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.FragmentActivity;

/**
 * @CreateBy qsmaxmin
 * @Date 2020/10/26 17:28
 * @Description
 */
public abstract class MvTabAdapterItem implements IView, QsNotProguard {
    private final int            position;
    private       MvIView        viewLayer;
    private       View           itemView;
    private       MvModelPager[] modelPagers;

    @NonNull protected final String initTag() {
        return L.isEnable() ? getClass().getSimpleName() : "MvTabAdapterItem";
    }

    public MvTabAdapterItem(int position) {
        this.position = position;
    }

    public final int getPosition() {
        return position;
    }

    public void init(View itemView, @NonNull MvModelPager[] modelPagers) {
        this.itemView = itemView;
        this.modelPagers = modelPagers;
        bindData(modelPagers[position], position);
    }

    protected final MvModelPager[] getModelPagers() {
        return modelPagers;
    }

    protected final MvModelPager getModelPager(int index) {
        return modelPagers[index];
    }

    protected final View getItemView() {
        return itemView;
    }

    public final void setLayer(MvIView viewLayer) {
        this.viewLayer = viewLayer;
    }

    protected void onViewClick(@NonNull View view) {
    }

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    public void onPageScrollStateChanged(int state) {
    }

    public abstract View onCreateTabItemView(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent);

    public abstract void bindData(@NonNull MvModelPager pagers, int position);

    public abstract void onPageSelectChanged(boolean selected);


    @Override public final void onViewClicked(@NonNull View view) {
        onViewClicked(view, 400);
    }

    @Override public final void onViewClicked(@NonNull View view, long interval) {
        if (interval > 0 && ViewHelper.isFastClick(interval)) return;
        onViewClick(view);
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

}
