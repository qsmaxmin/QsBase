package com.qsmaxmin.qsbase.mvp.adapter;

import android.support.annotation.CallSuper;
import android.view.View;

import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.model.QsNotProguard;
import com.qsmaxmin.qsbase.common.viewbind.ViewBindHelper;

/**
 * Created by sky on 15/2/6. 适配器
 */
public abstract class QsListAdapterItem<T> implements QsNotProguard {

    protected String initTag() {
        return L.isEnable() ? getClass().getSimpleName() : "QsListAdapterItem";
    }

    public abstract int getItemLayout();

    @CallSuper public void init(View contentView) {
        ViewBindHelper.bindView(this, contentView);
    }

    public abstract void bindData(T t, int position, int count);

    public void onViewClick(View view) {
    }
}
