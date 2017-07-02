package com.qsmaxmin.qsbase.mvp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.mvp.adapter.QsRecycleAdapterItem;
import com.qsmaxmin.qsbase.mvp.presenter.QsPresenter;

/**
 * @CreateBy qsmaxmin
 * @Date 17/7/2  下午4:23
 * @Description
 */

public class QsPullRecyclerFragment<P extends QsPresenter, D> extends QsRecyclerFragment<P, D> implements QsIPullRecyclerFragment<D> {

    @Override public int getFooterLayout() {
        return R.layout.qs_loading_footer;
    }

    @Override public void initData(Bundle savedInstanceState) {

    }

    @Override public QsRecycleAdapterItem getRecycleAdapterItem(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return null;
    }
}
