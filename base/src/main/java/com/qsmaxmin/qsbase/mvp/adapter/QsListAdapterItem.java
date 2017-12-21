package com.qsmaxmin.qsbase.mvp.adapter;

import android.view.View;

import com.qsmaxmin.qsbase.common.utils.QsHelper;

/**
 * Created by sky on 15/2/6. 适配器
 */
public abstract class QsListAdapterItem<T> {

    protected String initTag() {
        return getClass().getSimpleName();
    }

    public abstract int getItemLayout();

    public void init(View contentView) {
        QsHelper.getInstance().getViewBindHelper().bind(this, contentView);
    }

    public abstract void bindData(T t, int position, int count);
}
