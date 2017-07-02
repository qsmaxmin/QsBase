package com.qsmaxmin.qsbase.mvp.adapter;

import android.view.View;

/**
 * Created by sky on 15/2/6. 适配器
 */
public abstract class QsListAdapterItem<T> {

    public abstract int getItemLayout();

    public abstract void init(View contentView);

    public abstract void bindData(T t, int position, int count);
}
