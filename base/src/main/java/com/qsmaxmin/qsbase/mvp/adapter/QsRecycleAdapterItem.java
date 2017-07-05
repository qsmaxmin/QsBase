package com.qsmaxmin.qsbase.mvp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;

import com.qsmaxmin.qsbase.common.utils.QsHelper;


/**
 * @CreateBy qsmaxmin
 * @Date 16/8/5
 * @Description RecyclerView的Item封装类
 */
public abstract class QsRecycleAdapterItem<T> {
    public          View           mItemView;
    private         MyViewHolder   mViewHolder;
    protected       Context        mParentContext;
    protected final LayoutInflater mInflater;

    public QsRecycleAdapterItem(LayoutInflater inflater, ViewGroup parent) {
        this.mInflater = inflater;
        mItemView = inflater.inflate(itemViewLayoutId(), parent, false);
        mParentContext = parent == null ? null : parent.getContext();
        mViewHolder = new MyViewHolder(mItemView);
    }

    protected abstract int itemViewLayoutId();

    protected abstract void onBindItemData(T datum, int position, int totalCount);

    protected boolean isDisPlayItemAnimation() {
        return false;
    }

    protected Interpolator getAnimationInterpolator() {
        return null;
    }

    protected int getAnimationDuration() {
        return 500;
    }

    protected void onAnimation(float value) {
        //for child
    }

    protected void startValueAnimation() {
        if (getViewHolder() != null) {
            getViewHolder().startAnimation();
        }
    }

    public Context getParentContext() {
        return mParentContext;
    }

    public View getItemView() {
        return mItemView;
    }

    public MyViewHolder getViewHolder() {
        return mViewHolder;
    }

    public class MyViewHolder extends MyRecycleViewHolder<T> {

        MyViewHolder(View itemView) {
            super(itemView);
            QsHelper.getInstance().getViewBindHelper().bind(QsRecycleAdapterItem.this,itemView);
//            ButterKnife.bind(QsRecycleAdapterItem.this, itemView);
        }

        @Override public void onBindData(T s, int position, int totalCount) {
            onBindItemData(s, position, totalCount);
        }

        @Override protected boolean shouldDisplayAnimation() {
            return isDisPlayItemAnimation();
        }

        @Override protected Interpolator getInterpolator() {
            return getAnimationInterpolator();
        }

        @Override protected int getDuration() {
            return getAnimationDuration();
        }

        @Override protected void applyAnimation(float interpolatedTime) {
            onAnimation(interpolatedTime);
        }
    }
}