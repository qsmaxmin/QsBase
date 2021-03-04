package com.qsmaxmin.qsbase.mvp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qsmaxmin.qsbase.mvvm.adapter.MvTabAdapterItem;
import com.qsmaxmin.qsbase.mvvm.model.MvModelPager;
import com.qsmaxmin.qsbase.plugin.bind.QsIBindView;

import androidx.annotation.NonNull;

/**
 * @CreateBy qsmaxmin
 * @Date 2020/10/26 17:28
 * @Description
 */
public abstract class QsTabAdapterItem extends MvTabAdapterItem implements QsIBindView {

    public QsTabAdapterItem(int position) {
        super(position);
    }

    /**
     * for QsTransform
     */
    @Override public void bindViewByQsPlugin(View view) {
    }

    @Override public void init(View itemView, @NonNull MvModelPager[] modelPagers) {
        bindViewByQsPlugin(itemView);
        super.init(itemView, modelPagers);
    }

    @Override public View onCreateTabItemView(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return tabItemLayoutId() == 0 ? null : inflater.inflate(tabItemLayoutId(), parent, false);
    }

    protected int tabItemLayoutId() {
        return 0;
    }
}
