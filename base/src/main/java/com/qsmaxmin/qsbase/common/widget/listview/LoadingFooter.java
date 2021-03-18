package com.qsmaxmin.qsbase.common.widget.listview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.common.log.L;

/**
 * ListView/GridView/RecyclerView 分页加载时使用到的FooterView
 */
public class LoadingFooter extends FrameLayout {
    private static final String TAG = "LoadingFooter";
    private              State  mState;
    private              View   mNormalView;
    private              View   mLoadingView;
    private              View   mNetworkErrorView;
    private              View   mTheEndView;

    public LoadingFooter(Context context) {
        super(context);
        init();
    }

    public LoadingFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoadingFooter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        inflate(getContext(), R.layout.qs_layout_footer_main, this);
        setState(State.Normal);
    }

    @Override protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mState != null) {
            setViewState(mState);
        }
    }

    public State getState() {
        return mState;
    }

    public void setState(State status) {
        L.i(TAG, "setState  state :" + status);
        if (status != null) {
            this.mState = status;
            post(new Runnable() {
                @Override public void run() {
                    setViewState(mState);
                }
            });
        }
    }

    private void setViewState(State status) {
        mLoadingView = showView(mLoadingView, status == State.Loading, R.layout.qs_layout_footer_loading);

        mNormalView = showView(mNormalView, status == State.Normal, R.layout.qs_layout_footer_init);

        mTheEndView = showView(mTheEndView, status == State.TheEnd, R.layout.qs_layout_footer_end);

        mNetworkErrorView = showView(mNetworkErrorView, status == State.NetWorkError, R.layout.qs_layout_footer_error);
    }

    private View showView(View view, boolean show, int layoutId) {
        if (show) {
            if (view == null) {
                view = inflate(getContext(), layoutId, null);
                addView(view);
            } else {
                view.setVisibility(VISIBLE);
            }
        } else {
            if (view != null) {
                view.setVisibility(GONE);
            }
        }
        return view;
    }

    public enum State {
        Normal,
        TheEnd,
        Loading,
        NetWorkError
    }
}