package com.qsmaxmin.qsbase.mvp.adapter;

import android.animation.ValueAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.AdapterView;

/**
 * @CreateBy qsmaxmin
 * @Date 16/8/5
 * @Description RecyclerView holder
 */
public abstract class MyRecycleViewHolder<T> extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
    private int                                 mPosition;
    public  int                                 mTotalCount;
    private ValueAnimator                       valueAnimator;
    private AdapterView.OnItemClickListener     mClickListener;
    private AdapterView.OnItemLongClickListener mLongClickListener;

    public MyRecycleViewHolder(View itemView) {
        super(itemView);
        if (shouldDisplayAnimation()) {
            initAnimation();
        }
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    private void initAnimation() {
        valueAnimator = ValueAnimator.ofFloat(0f, 1f).setDuration(getDuration());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                applyAnimation(value);
            }
        });
        if (getInterpolator() != null) {
            valueAnimator.setInterpolator(getInterpolator());
        }
    }

    protected void startAnimation() {
        if (valueAnimator == null) {
            initAnimation();
        }
        valueAnimator.start();
    }


    public abstract void onBindData(T t, int position, int totalCount);

    public void setPosition(int position, int mTotalCount) {
        this.mPosition = position;
        this.mTotalCount = mTotalCount;
    }

    protected abstract boolean shouldDisplayAnimation();

    protected abstract int getDuration();

    protected abstract Interpolator getInterpolator();

    /**
     * 默认Alpha动画
     */
    protected abstract void applyAnimation(float interpolatedTime);

    @Override public void onClick(View v) {
        if (mClickListener != null) mClickListener.onItemClick(null, v, mPosition, -1);
    }

    @Override public boolean onLongClick(View v) {
        return mClickListener != null && mLongClickListener.onItemLongClick(null, v, mPosition, -1);
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.mClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener onItemLongClickListener) {
        this.mLongClickListener = onItemLongClickListener;
    }
}