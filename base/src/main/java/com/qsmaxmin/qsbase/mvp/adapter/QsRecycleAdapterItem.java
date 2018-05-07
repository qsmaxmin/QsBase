package com.qsmaxmin.qsbase.mvp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qsmaxmin.qsbase.common.utils.QsHelper;


/**
 * @CreateBy qsmaxmin
 * @Date 16/8/5
 * @Description RecyclerView的Item封装类
 */
public abstract class QsRecycleAdapterItem<T> {
    private View    mItemView;
    private Context mParentContext;

    public QsRecycleAdapterItem(LayoutInflater inflater, ViewGroup parent) {
        mItemView = inflater.inflate(itemViewLayoutId(), parent, false);
        QsHelper.getInstance().getViewBindHelper().bind(this, mItemView);
        mParentContext = parent.getContext();
    }

    protected String initTag() {
        return getClass().getSimpleName();
    }

    protected abstract int itemViewLayoutId();

    protected abstract void onBindItemData(T data, int position, int totalCount);

    public Context getContext() {
        return mParentContext;
    }

    View getItemView() {
        return mItemView;
    }

    public void onViewClick(View view) {
    }
}