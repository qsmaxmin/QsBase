package com.qsmaxmin.qsbase.common.widget.listview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.utils.ViewHelper;

/**
 * ListView/GridView/RecyclerView 分页加载时使用到的FooterView
 */
public abstract class BaseLoadingFooter extends FrameLayout {
    private static final String            TAG = "LoadingFooter";
    private              LayoutInflater    inflater;
    private              LoadingState      mState;
    private              SparseArray<View> stateViews;

    public BaseLoadingFooter(Context context) {
        super(context);
        init();
    }

    public BaseLoadingFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseLoadingFooter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        inflater = LayoutInflater.from(getContext());
        stateViews = new SparseArray<>();
        setState(LoadingState.Normal);
    }

    @Override protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mState != null) {
            updateView();
        }
    }

    public final LoadingState getState() {
        return mState;
    }

    public final View getCurrentView() {
        return stateViews.get(mState.index);
    }

    public final void setState(final LoadingState state) {
        L.i(TAG, "setState... state:" + state + ", old state:" + mState);
        if (state != null && state != mState) {
            final LoadingState oldState = mState;
            this.mState = state;
            post(new Runnable() {
                @Override public void run() {
                    updateView();
                    onViewStateChanged(state, oldState);
                }
            });
        }
    }

    private void updateView() {
        View stateView = stateViews.get(mState.index);
        if (stateView == null) {
            View newView = onCreateStateView(mState, inflater, this);
            if (newView != null) {
                stateView = ViewHelper.addToParent(newView, this);
                stateViews.put(mState.index, stateView);
            }
        }
        int size = stateViews.size();
        for (int i = 0; i < size; i++) {
            int key = stateViews.keyAt(i);
            View value = stateViews.valueAt(i);
            value.setVisibility(key == mState.index ? View.VISIBLE : View.GONE);
        }
    }

    protected abstract View onCreateStateView(LoadingState state, LayoutInflater inflater, ViewGroup parent);

    protected void onViewStateChanged(LoadingState currentState, LoadingState oldState) {
    }
}