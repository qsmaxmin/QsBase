package com.qsmaxmin.qsbase.mvvm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qsmaxmin.annotation.QsNotProguard;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.mvvm.model.MvModelPager;

/**
 * @CreateBy qsmaxmin
 * @Date 2020/10/26 17:28
 * @Description
 */
public abstract class MvTabAdapterItem implements QsNotProguard {
    private final int            position;
    private       View           itemView;
    private       MvModelPager[] modelPagers;

    protected String initTag() {
        return L.isEnable() ? getClass().getSimpleName() : "MvTabAdapterItem";
    }

    public MvTabAdapterItem(int position) {
        this.position = position;
    }

    public final int getPosition() {
        return position;
    }

    public final void init(View itemView, MvModelPager[] modelPagers) {
        this.itemView = itemView;
        this.modelPagers = modelPagers;
        initView(itemView);
        bindData(modelPagers[position], position);
    }

    protected final MvModelPager[] getModelPagers() {
        return modelPagers;
    }

    protected final MvModelPager getModelPager(int index) {
        return modelPagers[index];
    }

    protected void initView(View itemView) {
        //custom your logic
    }

    protected View getItemView() {
        return itemView;
    }

    protected Context getContext() {
        return itemView.getContext();
    }

    public abstract View onCreateTabItemView(LayoutInflater inflater, ViewGroup parent);

    public abstract void bindData(MvModelPager pagers, int position);

    public abstract void onPageSelectChanged(boolean selected);

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    public void onPageScrollStateChanged(int state) {
    }
}
