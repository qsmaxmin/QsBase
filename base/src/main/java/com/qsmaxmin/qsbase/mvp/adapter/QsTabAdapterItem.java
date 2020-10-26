package com.qsmaxmin.qsbase.mvp.adapter;

import android.view.View;

import com.qsmaxmin.annotation.QsNotProguard;
import com.qsmaxmin.qsbase.mvp.model.QsModelPager;
import com.qsmaxmin.qsbase.plugin.bind.QsIBindView;

/**
 * @CreateBy qsmaxmin
 * @Date 2020/10/26 17:28
 * @Description
 */
public abstract class QsTabAdapterItem implements QsIBindView, QsNotProguard {
    private final int position;
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
        bindViewByQsPlugin(itemView);
        initView(itemView);
        bindData(modelPagers, position);
    }

    protected void initView(View itemView) {
    }

    public abstract int tabItemLayoutId();

    public abstract void bindData(QsModelPager[] pager, int position);

    public abstract void onPageSelectChanged(boolean selected);

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    public void onPageScrollStateChanged(int state) {
    }
}
