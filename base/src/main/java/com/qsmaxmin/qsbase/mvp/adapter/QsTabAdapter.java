package com.qsmaxmin.qsbase.mvp.adapter;

import android.view.View;

import com.qsmaxmin.annotation.QsNotProguard;
import com.qsmaxmin.qsbase.mvp.model.QsModelPager;
import com.qsmaxmin.qsbase.plugin.bind.QsIBindView;

import androidx.annotation.CallSuper;

/**
 * @CreateBy qsmaxmin
 * @Date 2020/10/26 15:22
 * @Description tab 适配器
 */
public abstract class QsTabAdapter implements QsIBindView, QsNotProguard {

    public final void init(View itemView, QsModelPager pager, int position, int totalCount) {
        bindViewByQsPlugin(itemView);
        bindData(pager, position, totalCount);
    }

    /**
     * for QsTransform
     */
    @CallSuper @Override public void bindViewByQsPlugin(View view) {
    }

    public abstract int tabItemLayoutId();

    public abstract void bindData(QsModelPager pager, int position, int totalCount);

    public abstract void onPageSelected(View currentView, View oldView, int currentPosition, int oldPosition);

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    public void onPageScrollStateChanged(int state) {
    }
}
