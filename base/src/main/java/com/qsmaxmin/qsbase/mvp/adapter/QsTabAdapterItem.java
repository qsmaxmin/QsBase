package com.qsmaxmin.qsbase.mvp.adapter;

import android.content.Context;
import android.view.View;

import com.qsmaxmin.annotation.QsNotProguard;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.mvp.model.QsModelPager;
import com.qsmaxmin.qsbase.plugin.bind.QsIBindView;

/**
 * @CreateBy qsmaxmin
 * @Date 2020/10/26 17:28
 * @Description
 */
public abstract class QsTabAdapterItem implements QsIBindView, QsNotProguard {
    private final int            position;
    private       View           itemView;
    private       QsModelPager[] modelPagers;

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

    protected void initView(View itemView) {
        //custom your logic
    }

    protected View getItemView() {
        return itemView;
    }

    protected Context getContext() {
        return itemView.getContext();
    }

    public abstract int tabItemLayoutId();

    public abstract void bindData(QsModelPager pagers, int position);

    public abstract void onPageSelectChanged(boolean selected);

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    public void onPageScrollStateChanged(int state) {
    }
}
