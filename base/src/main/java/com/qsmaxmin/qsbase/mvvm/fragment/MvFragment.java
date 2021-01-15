package com.qsmaxmin.qsbase.mvvm.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ScrollView;
import android.widget.ViewAnimator;

import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.common.exception.QsException;
import com.qsmaxmin.qsbase.common.http.HttpHelper;
import com.qsmaxmin.qsbase.common.http.NetworkErrorReceiver;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.utils.QsHelper;
import com.qsmaxmin.qsbase.common.utils.ViewHelper;
import com.qsmaxmin.qsbase.common.viewbind.OnActivityResultListener;
import com.qsmaxmin.qsbase.common.widget.dialog.QsProgressDialog;
import com.qsmaxmin.qsbase.common.widget.headerview.ScrollerProvider;
import com.qsmaxmin.qsbase.common.widget.listview.LoadingFooter;
import com.qsmaxmin.qsbase.common.widget.ptr.PtrFrameLayout;
import com.qsmaxmin.qsbase.mvp.QsIActivity;
import com.qsmaxmin.qsbase.mvp.QsIPullToRefreshView;
import com.qsmaxmin.qsbase.plugin.threadpoll.QsThreadPollHelper;

import java.util.HashSet;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/6/21 11:40
 * @Description
 */
public abstract class MvFragment extends Fragment implements MvIFragment, ScrollerProvider, View.OnTouchListener {
    private   boolean                  hasInitData;
    protected QsProgressDialog         mProgressDialog;
    protected ViewAnimator             mViewAnimator;
    private   OnActivityResultListener activityResultListener;
    private   HashSet<Object>          requestTags;

    @Override public String initTag() {
        return L.isEnable() ? getClass().getSimpleName() : "QsFragment";
    }

    @Override public int rootViewLayoutId() {
        return isOpenViewState() ? R.layout.qs_view_animator : R.layout.qs_frame_layout;
    }

    @Override public final void onViewClicked(View view) {
        onViewClicked(view, 400);
    }

    @Override public final void onViewClicked(View view, long interval) {
        if (interval > 0) {
            if (ViewHelper.isFastClick(interval)) return;
        }
        onViewClick(view);
    }

    @Override public void onViewClick(View view) {
        //custom your logic
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (interceptBackPressed()) {
            requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
                @Override public void handleOnBackPressed() {
                    if (L.isEnable()) L.i(initTag(), "handleOnBackPressed..........");
                    onBackPressed();
                }
            });
        } else if (L.isEnable()) {
            L.i(initTag(), "interceptBackPressed() return false, if you want to intercept back pressed event, override it and return true!");
        }
    }

    @Override @Nullable @CallSuper
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bindBundleByQsPlugin(getArguments());
        View rootView = initView(inflater, container);
        rootView.setOnTouchListener(this);
        bindEventByQsPlugin();
        return rootView;
    }

    @CallSuper @Override public void bindBundleByQsPlugin(Bundle bundle) {
    }

    @CallSuper @Override public void bindEventByQsPlugin() {
    }

    @CallSuper @Override public void unbindEventByQsPlugin() {
    }

    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!isDelayData()) {
            hasInitData = true;
            initData(savedInstanceState);
        }
    }

    @CallSuper @Override public void onDestroyView() {
        super.onDestroyView();
        if (mProgressDialog != null) {
            mProgressDialog.dismissAllowingStateLoss();
            mProgressDialog = null;
        }
        mViewAnimator = null;
        unbindEventByQsPlugin();
        cancelAllHttpRequest();
    }

    protected View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        long s0 = 0;
        if (L.isEnable()) s0 = System.nanoTime();
        View rootView = inflater.inflate(rootViewLayoutId(), container, false);
        if (isOpenViewState()) {
            mViewAnimator = rootView.findViewById(R.id.qs_view_animator);
            ViewHelper.initViewAnimator(mViewAnimator, this);

            View loadingView = onCreateLoadingView(inflater, mViewAnimator);
            if (loadingView != null) {
                loadingView.setTag(R.id.qs_view_state_key, VIEW_STATE_LOADING);
                setDefaultViewClickListener(loadingView);
                if (loadingView.getParent() == null) {
                    mViewAnimator.addView(loadingView);
                }
            }

            View contentView = onCreateContentView(inflater, mViewAnimator);
            if (contentView != null) {
                contentView.setTag(R.id.qs_view_state_key, VIEW_STATE_CONTENT);
                if (contentViewBackgroundColor() != 0) contentView.setBackgroundColor(contentViewBackgroundColor());
                if (contentView.getParent() == null) {
                    mViewAnimator.addView(contentView);
                }
            }

            if (L.isEnable()) {
                long s1 = System.nanoTime();
                L.i(initTag(), "initView...view inflate complete(viewState is open), use time:" + (s1 - s0) / 1000000f + "ms");
            }

        } else {
            ViewGroup customView = rootView.findViewById(android.R.id.custom);
            View contentView = onCreateContentView(inflater, customView);
            if (contentView != null) {
                if (contentViewBackgroundColor() != 0) contentView.setBackgroundColor(contentViewBackgroundColor());
                if (contentView.getParent() == null) {
                    customView.addView(contentView);
                }
            }

            if (L.isEnable()) {
                long s1 = System.nanoTime();
                L.i(initTag(), "initView...view inflate complete(viewState not open), use time:" + (s1 - s0) / 1000000f + "ms");
            }
        }
        return rootView;
    }

    @Override public void initDataWhenDelay() {
        if (!hasInitData && isDelayData()) {
            initData(getArguments());
            hasInitData = true;
        }
    }

    @Override public boolean isOpenViewState() {
        return true;
    }

    @Override public Animation viewStateInAnimation() {
        return null;
    }

    @Override public int viewStateInAnimationId() {
        return 0;
    }

    @Override public Animation viewStateOutAnimation() {
        return null;
    }

    @Override public int viewStateOutAnimationId() {
        return 0;
    }

    @Override public boolean viewStateAnimateFirstView() {
        return true;
    }

    @Override public boolean isDelayData() {
        return false;
    }

    @Override public void activityFinish() {
        activityFinish(false);
    }

    @Override public void activityFinish(int enterAnim, int exitAnim) {
        activityFinish();
        FragmentActivity activity = getActivity();
        if (activity != null) activity.overridePendingTransition(enterAnim, exitAnim);
    }

    @Override public void activityFinish(boolean finishAfterTransition) {
        FragmentActivity activity = getActivity();
        if (activity == null) return;
        if (finishAfterTransition) {
            ActivityCompat.finishAfterTransition(activity);
        } else {
            activity.finish();
        }
    }

    @Override public View onCreateLoadingView(@NonNull LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(QsHelper.getAppInterface().loadingLayoutId(), parent, false);
    }

    @Override public View onCreateEmptyView(@NonNull LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(QsHelper.getAppInterface().emptyLayoutId(), parent, false);
    }

    @Override public View onCreateErrorView(@NonNull LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(QsHelper.getAppInterface().errorLayoutId(), parent, false);
    }

    @Override public QsProgressDialog getLoadingDialog() {
        return QsHelper.getAppInterface().getLoadingDialog();
    }

    @Override public void onBackPressed() {
        FragmentManager manager = getFragmentManager();
        if (manager != null) {
            FragmentTransaction ft = manager.beginTransaction();
            ft.remove(this).commitAllowingStateLoss();
        }
    }

    @Override public final void loading() {
        loading(true);
    }

    @Override public final void loading(boolean cancelAble) {
        loading(getString(R.string.loading), cancelAble);
    }

    @Override public final void loading(String message) {
        loading(message, true);
    }

    @Override public final void loading(int resId) {
        loading(resId, true);
    }

    @Override public final void loading(int resId, boolean cancelAble) {
        loading(QsHelper.getString(resId), cancelAble);
    }

    @Override public final void loading(String message, boolean cancelAble) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.setMessage(message);
            mProgressDialog.setCancelable(cancelAble);
        } else {
            mProgressDialog = getLoadingDialog();
            if (mProgressDialog != null) {
                mProgressDialog.setMessage(message);
                mProgressDialog.setCancelable(cancelAble);
                mProgressDialog.show(this);
            }
        }
    }

    @Override public final void loadingClose() {
        if (mProgressDialog != null) {
            mProgressDialog.dismissAllowingStateLoss();
        }
    }

    @Override public final void showLoadingView() {
        if (mViewAnimator != null && !isDetached()) {
            if (L.isEnable()) L.i(initTag(), "showLoadingView.........");
            setViewState(0);
        }
    }

    @Override public final void showContentView() {
        if (mViewAnimator != null && !isDetached()) {
            if (L.isEnable()) L.i(initTag(), "showContentView.........");
            setViewState(1);
        }
    }

    @Override public final void showEmptyView() {
        if (mViewAnimator != null && !isDetached()) {
            int childCount = mViewAnimator.getChildCount();
            if (L.isEnable()) L.i(initTag(), "showEmptyView.........childCount:" + childCount);
            for (int index = 0; index < childCount; index++) {
                View childAt = mViewAnimator.getChildAt(index);
                int stateValue = (int) childAt.getTag(R.id.qs_view_state_key);
                if (stateValue == VIEW_STATE_EMPTY) {
                    setViewState(index);
                    return;
                }
            }
            post(new Runnable() {
                @Override public void run() {
                    if (L.isEnable()) L.i(initTag(), "showEmptyView.........create empty view by 'onCreateEmptyView(...)' method~");
                    View emptyView = onCreateEmptyView(getLayoutInflater(), mViewAnimator);
                    emptyView.setTag(R.id.qs_view_state_key, VIEW_STATE_EMPTY);
                    setDefaultViewClickListener(emptyView);
                    if (emptyView.getParent() == null) {
                        mViewAnimator.addView(emptyView);
                    }
                    setViewState(mViewAnimator.getChildCount() - 1);
                }
            });
        }
    }

    @Override public final void showErrorView() {
        if (mViewAnimator != null && !isDetached()) {
            int childCount = mViewAnimator.getChildCount();
            if (L.isEnable()) L.i(initTag(), "showErrorView.........childCount:" + childCount);
            for (int index = 0; index < childCount; index++) {
                View childAt = mViewAnimator.getChildAt(index);

                int stateValue = (int) childAt.getTag(R.id.qs_view_state_key);
                if (stateValue == VIEW_STATE_ERROR) {
                    setViewState(index);
                    return;
                }
            }
            post(new Runnable() {
                @Override public void run() {
                    if (L.isEnable()) L.i(initTag(), "showErrorView.........create error view by 'onCreateErrorView(...)' method~");
                    View errorView = onCreateErrorView(getLayoutInflater(), mViewAnimator);
                    errorView.setTag(R.id.qs_view_state_key, VIEW_STATE_ERROR);
                    setDefaultViewClickListener(errorView);
                    mViewAnimator.addView(errorView);
                    if (errorView.getParent() == null) {
                        mViewAnimator.addView(errorView);
                    }
                    setViewState(mViewAnimator.getChildCount() - 1);
                }
            });
        }
    }

    @Override public boolean isShowLoadingView() {
        return currentViewState() == VIEW_STATE_LOADING;
    }

    @Override public boolean isShowContentView() {
        return mViewAnimator == null || currentViewState() == VIEW_STATE_CONTENT;
    }

    @Override public boolean isShowEmptyView() {
        return currentViewState() == VIEW_STATE_EMPTY;
    }

    @Override public boolean isShowErrorView() {
        return currentViewState() == VIEW_STATE_ERROR;
    }


    /**
     * return current showing view
     */
    @Override public final int currentViewState() {
        if (isOpenViewState() && mViewAnimator != null) {
            int displayedIndex = mViewAnimator.getDisplayedChild();
            View childView = mViewAnimator.getChildAt(displayedIndex);
            return (int) childView.getTag(R.id.qs_view_state_key);
        }
        return -1;
    }

    @Override public final void intent2Activity(Class clazz) {
        intent2Activity(clazz, null, 0, null, 0, 0);
    }

    @Override public final void intent2Activity(Class clazz, int requestCode) {
        intent2Activity(clazz, null, requestCode, null, 0, 0);
    }

    @Override public final void intent2Activity(Class clazz, Bundle bundle, int requestCode) {
        intent2Activity(clazz, bundle, requestCode, null, 0, 0);
    }

    @Override public final void intent2Activity(Class clazz, Bundle bundle) {
        intent2Activity(clazz, bundle, 0, null, 0, 0);
    }

    @Override public final void intent2Activity(Class clazz, Bundle bundle, int inAnimId, int outAnimId) {
        intent2Activity(clazz, bundle, 0, null, inAnimId, outAnimId);
    }

    @Override public final void intent2Activity(Class clazz, Bundle bundle, ActivityOptionsCompat optionsCompat) {
        intent2Activity(clazz, bundle, 0, optionsCompat, 0, 0);
    }

    @Override public final void intent2Activity(Class clazz, Bundle bundle, int requestCode, ActivityOptionsCompat optionsCompat) {
        intent2Activity(clazz, bundle, requestCode, optionsCompat, 0, 0);
    }

    @Override public final void intent2Activity(Class clazz, Bundle bundle, int requestCode, ActivityOptionsCompat optionsCompat, int enterAnim, int existAnim) {
        ViewHelper.intent2Activity(this, clazz, bundle, requestCode, optionsCompat, enterAnim, existAnim);
    }

    @Override public final void commitFragment(Fragment fragment) {
        QsHelper.commitFragment(getChildFragmentManager(), android.R.id.custom, fragment);
    }

    @Override public final void commitFragment(Fragment fragment, String tag) {
        QsHelper.commitFragment(getChildFragmentManager(), android.R.id.custom, fragment, tag);
    }

    @Override public void commitFragment(Fragment fragment, int enterAnim, int existAnim) {
        QsHelper.commitFragment(getChildFragmentManager(), android.R.id.custom, fragment, fragment.getClass().getSimpleName(), enterAnim, existAnim);
    }

    @Override public final void commitFragment(Fragment fragment, String tag, int enterAnim, int existAnim) {
        QsHelper.commitFragment(getChildFragmentManager(), android.R.id.custom, fragment, tag, enterAnim, existAnim);
    }

    @Override public final void commitFragment(int layoutId, Fragment fragment) {
        QsHelper.commitFragment(getChildFragmentManager(), layoutId, fragment);
    }

    @Override public final void commitFragment(int layoutId, Fragment fragment, String tag) {
        QsHelper.commitFragment(getChildFragmentManager(), layoutId, fragment, tag);
    }

    @Override public void commitFragment(int layoutId, Fragment fragment, int enterAnim, int existAnim) {
        QsHelper.commitFragment(getChildFragmentManager(), layoutId, fragment, fragment.getClass().getSimpleName(), enterAnim, existAnim);
    }

    @Override public final void commitFragment(int layoutId, Fragment fragment, String tag, int enterAnim, int existAnim) {
        QsHelper.commitFragment(getChildFragmentManager(), layoutId, fragment, tag, enterAnim, existAnim);
    }

    @Override public final void commitDialogFragment(DialogFragment fragment) {
        QsHelper.commitDialogFragment(getChildFragmentManager(), fragment);
    }

    private void setViewState(final int index) {
        if (QsHelper.isMainThread()) {
            if (mViewAnimator.getDisplayedChild() != index) {
                mViewAnimator.setDisplayedChild(index);
            }
        } else {
            post(new Runnable() {
                @Override public void run() {
                    if (mViewAnimator.getDisplayedChild() != index) {
                        mViewAnimator.setDisplayedChild(index);
                    }
                }
            });
        }
    }

    private void setDefaultViewClickListener(View view) {
        if (view != null) {
            ViewHelper.setDefaultViewClickListener(view, this);
        }
    }

    @Override public void smoothScrollToTop(boolean autoRefresh) {
        View view = getView();
        if (view == null) return;
        final ScrollView scrollView = ViewHelper.tryGetTargetView(ScrollView.class, view);
        if (scrollView != null) {
            scrollView.post(new Runnable() {
                @Override public void run() {
                    scrollView.smoothScrollTo(0, 0);
                }
            });
        }
        if (autoRefresh) {
            final PtrFrameLayout frameLayout = ViewHelper.tryGetTargetView(PtrFrameLayout.class, view);
            if (frameLayout != null) {
                frameLayout.post(new Runnable() {
                    @Override public void run() {
                        frameLayout.autoRefresh();
                    }
                });
            }
        }
    }

    @Override public final void post(Runnable action) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(action);
        }
    }

    @Override public final void postDelayed(Runnable action, long delayed) {
        if (getActivity() != null) {
            QsHelper.postDelayed(action, delayed);
        }
    }

    @Override public final void runOnWorkThread(Runnable action) {
        QsHelper.executeInWorkThread(action);
    }

    @Override public final void runOnHttpThread(Runnable action) {
        QsHelper.executeInHttpThread(action);
    }

    @Override public void onFragmentSelectedInViewPager(boolean isSelected, int currentPosition, int totalCount) {
        L.i(initTag(), "onFragmentSelectedInViewPager..... isSelected:" + isSelected + "  currentPosition:" + currentPosition + "  totalCount:" + totalCount);
    }

    @Override public boolean onTouch(View v, MotionEvent event) {
        return interceptTouchEvent();
    }

    @Override public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (activityResultListener != null) {
            activityResultListener.onActivityResult(getActivity(), requestCode, resultCode, data);
        }
    }

    @Override public final void setOnActivityResultListener(OnActivityResultListener listener) {
        this.activityResultListener = listener;
    }

    @Override public boolean interceptTouchEvent() {
        return true;
    }

    /**
     * if return true, this fragment will intercept onBackPressed event,
     * and {@link #onBackPressed()}will be execute.
     */
    @Override public boolean interceptBackPressed() {
        return false;
    }

    @Override public boolean isShowBackButtonInDefaultView() {
        return false;
    }

    @Override public View getScrollableView() {
        return getView();
    }

    @Override public int contentViewBackgroundColor() {
        return 0;
    }

    @Override public void setActivityTitle(CharSequence title) {
        setActivityTitle(title, 0);
    }

    @Override public void setActivityTitle(final CharSequence title, final int type) {
        final FragmentActivity activity = getActivity();
        if (activity instanceof QsIActivity) {
            if (QsThreadPollHelper.isMainThread()) {
                ((QsIActivity) activity).setActivityTitle(title, type);
            } else {
                post(new Runnable() {
                    @Override public void run() {
                        ((QsIActivity) activity).setActivityTitle(title, type);
                    }
                });
            }
        }
    }

    @Override @Nullable public final <T> T createHttpRequest(Class<T> clazz) {
        return createHttpRequest(clazz, System.nanoTime(), this);
    }

    @Override @Nullable public final <T> T createHttpRequest(Class<T> clazz, Object tag) {
        return createHttpRequest(clazz, tag, this);
    }

    @Override @Nullable public final <T> T createHttpRequest(Class<T> clazz, NetworkErrorReceiver receiver) {
        return createHttpRequest(clazz, System.nanoTime(), receiver);
    }

    @Override @Nullable public final <T> T createHttpRequest(Class<T> clazz, Object requestTag, NetworkErrorReceiver receiver) {
        synchronized (this) {
            if (requestTags == null) requestTags = new HashSet<>();
            if (!requestTags.contains(requestTag)) {
                requestTags.add(requestTag);
            } else {
                L.e(initTag(), "createHttpRequest Repeated tag:" + requestTag);
            }
        }
        return HttpHelper.getInstance().create(clazz, requestTag, receiver);
    }

    /**
     * 取消由当前Fragment发起的http请求
     */
    protected final void cancelAllHttpRequest() {
        if (requestTags != null) {
            synchronized (this) {
                QsHelper.getHttpHelper().cancelRequest(requestTags);
                requestTags.clear();
            }
        }
    }

    @Override public void methodError(QsException e) {
        if (isAdded() && !isDetached()) {
            if (this instanceof QsIPullToRefreshView) {
                QsIPullToRefreshView view = (QsIPullToRefreshView) this;
                view.stopRefreshing();
                view.setLoadingState(LoadingFooter.State.NetWorkError);
            }
            if (!isShowContentView()) {
                showErrorView();
            }
            loadingClose();
        }
    }
}
