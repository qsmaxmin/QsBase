package com.qsmaxmin.qsbase.common.widget.listview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewStub;
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


    private View showView(View view, int layoutId) {
        if (view == null) {
            view = inflate(getContext(), layoutId, null);
            addView(view);
        } else {
            view.setVisibility(VISIBLE);
        }
        return view;
    }

    private void hideView(View view) {
        if (view != null) view.setVisibility(GONE);
    }

    private void setViewState(State status) {
        switch (status) {
            case Normal:
                hideView(mTheEndView);
                hideView(mNetworkErrorView);
                hideView(mLoadingView);
                if (mNormalView != null) {
                    mNormalView.setVisibility(VISIBLE);
                } else {
                    ViewStub viewStub = findViewById(R.id.normal_viewstub);
                    mNormalView = viewStub.inflate();
                }
                break;

            case Loading:
                if (mTheEndView != null) mTheEndView.setVisibility(GONE);
                if (mNetworkErrorView != null) mNetworkErrorView.setVisibility(GONE);
                if (mNormalView != null) mNormalView.setVisibility(GONE);
                if (mLoadingView != null) {
                    mLoadingView.setVisibility(VISIBLE);
                } else {
                    ViewStub viewStub = findViewById(R.id.loading_viewstub);
                    mLoadingView = viewStub.inflate();
                }
                break;

            case TheEnd:
                if (mLoadingView != null) mLoadingView.setVisibility(GONE);
                if (mNetworkErrorView != null) mNetworkErrorView.setVisibility(GONE);
                if (mNormalView != null) mNormalView.setVisibility(GONE);
                if (mTheEndView != null) {
                    mTheEndView.setVisibility(VISIBLE);
                } else {
                    ViewStub viewStub = findViewById(R.id.end_viewstub);
                    mTheEndView = viewStub.inflate();
                }
                break;

            case NetWorkError:
                if (mLoadingView != null) mLoadingView.setVisibility(GONE);
                if (mTheEndView != null) mTheEndView.setVisibility(GONE);
                if (mNormalView != null) mNormalView.setVisibility(GONE);
                if (mNetworkErrorView != null) {
                    mNetworkErrorView.setVisibility(VISIBLE);
                } else {
                    ViewStub viewStub = findViewById(R.id.network_error_viewstub);
                    mNetworkErrorView = viewStub.inflate();
                }
                break;
            default:
                break;
        }
    }

    public enum State {
        Normal,
        TheEnd,
        Loading,
        NetWorkError
    }
}