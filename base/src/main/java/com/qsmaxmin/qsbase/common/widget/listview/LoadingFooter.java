package com.qsmaxmin.qsbase.common.widget.listview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewStub;
import android.widget.RelativeLayout;

import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.widget.recyclerview.HeaderFooterRecyclerAdapter;

/**
 * ListView/GridView/RecyclerView 分页加载时使用到的FooterView
 */
public class LoadingFooter extends RelativeLayout implements HeaderFooterRecyclerAdapter.OnRecyclerViewAdapterBindViewHolder {

    private static final String TAG = "LoadingFooter";

    private State mState;
    private View  mNormalView;
    private View  mLoadingView;
    private View  mNetworkErrorView;
    private View  mTheEndView;


    public LoadingFooter(Context context) {
        this(context, null);
    }

    public LoadingFooter(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingFooter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        inflate(context, R.layout.qs_layout_footer_main, this);
        setState(State.Normal);
    }

    @Override protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    public State getState() {
        return mState;
    }

    public void setState(State status) {
        L.i(TAG, "setState  state:" + status);
        if (status == null) {
            return;
        }
        this.mState = status;
        setViewState(status);
    }

    /**
     * 当recyclerView回调
     * {@link HeaderFooterRecyclerAdapter#onBindViewHolder(RecyclerView.ViewHolder, int)}
     */
    @Override public void onAdapterBindViewHolder() {
        L.i(TAG, "onAdapterBindViewHolder  state:" + mState);
    }

    /**
     * 设置状态
     */
    private void setViewState(State status) {
        switch (status) {
            case Normal:
                if (mTheEndView != null) mTheEndView.setVisibility(GONE);
                if (mNetworkErrorView != null) mNetworkErrorView.setVisibility(GONE);
                if (mLoadingView != null) mLoadingView.setVisibility(GONE);
                if (mNormalView != null) {
                    mNormalView.setVisibility(VISIBLE);
                } else {
                    ViewStub viewStub = (ViewStub) findViewById(R.id.normal_viewstub);
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
                    ViewStub viewStub = (ViewStub) findViewById(R.id.loading_viewstub);
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
                    ViewStub viewStub = (ViewStub) findViewById(R.id.end_viewstub);
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
                    ViewStub viewStub = (ViewStub) findViewById(R.id.network_error_viewstub);
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