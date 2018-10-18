package com.qsmaxmin.qsbase.mvp.adapter;

import android.support.annotation.CallSuper;
import android.view.View;

import com.qsmaxmin.qsbase.common.utils.QsHelper;
import com.qsmaxmin.qsbase.common.viewbind.ViewBindHelper;

/**
 * Created by sky on 15/2/6. 适配器
 */
public abstract class QsListAdapterItem<T> {

    protected String initTag() {
        return QsHelper.getInstance().getApplication().isLogOpen() ? getClass().getSimpleName() : "QsListAdapterItem";
    }

    public abstract int getItemLayout();

    @CallSuper public void init(View contentView) {
        ViewBindHelper.bindView(this, contentView);
    }

    public abstract void bindData(T t, int position, int count);

    public void onViewClick(View view) {
    }
}
