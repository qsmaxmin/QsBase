package com.qsmaxmin.qsbase.mvp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qsmaxmin.qsbase.mvvm.adapter.MvListAdapterItem;
import com.qsmaxmin.qsbase.plugin.bind.QsIBindView;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;

/**
 * @CreateBy qsmaxmin
 * @Date 16/8/4
 * @Description ListView holder
 */
public abstract class QsListAdapterItem<D> extends MvListAdapterItem<D> implements QsIBindView {

    protected int getItemLayout() {
        return 0;
    }

    public View onCreateItemView(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return getItemLayout() == 0 ? null : inflater.inflate(getItemLayout(), parent, false);
    }

    @CallSuper public void init(@NonNull View contentView) {
        bindViewByQsPlugin(contentView);
    }

    /**
     * for QsTransform
     */
    @CallSuper @Override public void bindViewByQsPlugin(View view) {
    }

}
