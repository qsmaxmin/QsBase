package com.qsmaxmin.qsbase.mvp.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qsmaxmin.annotation.QsNotProguard;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.utils.ViewHelper;
import com.qsmaxmin.qsbase.mvp.model.QsModelPager;
import com.qsmaxmin.qsbase.mvvm.IView;
import com.qsmaxmin.qsbase.plugin.bind.QsIBindView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.FragmentActivity;

/**
 * @CreateBy qsmaxmin
 * @Date 2020/10/26 17:28
 * @Description
 */
public abstract class QsTabAdapterItem implements QsIBindView, QsNotProguard, IView {
    private final int            position;
    private       View           itemView;
    private       QsModelPager[] modelPagers;
    private       IView          viewLayer;

    protected String initTag() {
        return L.isEnable() ? getClass().getSimpleName() : "QsTabAdapterItem";
    }

    /**
     * for QsTransform
     */
    @Override public void bindViewByQsPlugin(View view) {
    }

    public QsTabAdapterItem(int position) {
        this.position = position;
    }

    public final int getPosition() {
        return position;
    }

    public final void init(View itemView, QsModelPager[] modelPagers) {
        this.itemView = itemView;
        this.modelPagers = modelPagers;
        bindViewByQsPlugin(itemView);
        initView(itemView);
        bindData(modelPagers[position], position);
    }

    protected final QsModelPager[] getModelPagers() {
        return modelPagers;
    }

    protected final QsModelPager getModelPager(int index) {
        return modelPagers[index];
    }

    protected void initView(@NonNull View itemView) {
        //custom your logic
    }

    protected final View getItemView() {
        return itemView;
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
        return itemView.getContext();
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

    @Override public final void intent2Activity(Class clazz, Bundle bundle, int requestCode, ActivityOptionsCompat optionsCompat, int inAnimId, int outAnimId) {
        ViewHelper.intent2Activity(getActivity(), clazz, bundle, requestCode, optionsCompat, inAnimId, outAnimId);
    }

    public final void setLayer(IView viewLayer) {
        this.viewLayer = viewLayer;
    }

    public int tabItemLayoutId() {
        return 0;
    }

    public View onCreateTabItemView(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return inflater.inflate(tabItemLayoutId(), parent, false);
    }

    public abstract void bindData(@NonNull QsModelPager pagers, int position);

    public abstract void onPageSelectChanged(boolean selected);

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    public void onPageScrollStateChanged(int state) {
    }


}
