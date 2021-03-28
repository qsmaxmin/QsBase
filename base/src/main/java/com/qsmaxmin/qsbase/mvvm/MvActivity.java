package com.qsmaxmin.qsbase.mvvm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ScrollView;
import android.widget.ViewAnimator;

import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.common.http.HttpHelper;
import com.qsmaxmin.qsbase.common.http.NetworkErrorReceiver;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.utils.QsHelper;
import com.qsmaxmin.qsbase.common.utils.ViewHelper;
import com.qsmaxmin.qsbase.common.viewbind.OnActivityResultListener;
import com.qsmaxmin.qsbase.common.viewbind.OnKeyDownListener;
import com.qsmaxmin.qsbase.common.widget.dialog.ProgressView;
import com.qsmaxmin.qsbase.common.widget.dialog.QsProgressDialog;
import com.qsmaxmin.qsbase.common.widget.ptr.PtrFrameLayout;
import com.qsmaxmin.qsbase.common.widget.sliding.ISlidingViewGroup;
import com.qsmaxmin.qsbase.common.widget.sliding.SlidingListenerAdapter;
import com.qsmaxmin.qsbase.plugin.permission.PermissionHelper;

import java.util.HashSet;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

/**
 * @CreateBy qsmaxmin
 * @Date 2020/12/8 14:01
 * @Description
 */
public abstract class MvActivity extends FragmentActivity implements MvIActivity {
    private   ViewAnimator             mViewAnimator;
    protected ProgressView             progressView;
    private   OnActivityResultListener activityResultListener;
    private   OnKeyDownListener        onKeyDownListener;
    private   View                     rootView;
    private   HashSet<Object>          requestTags;
    private   boolean                  isDestroyed;
    private   ISlidingViewGroup        slidingView;

    @CallSuper @Override public void bindBundleByQsPlugin(Bundle bundle) {
    }

    @CallSuper @Override public void bindEventByQsPlugin() {
    }

    @CallSuper @Override public void unbindEventByQsPlugin() {
    }

    @CallSuper @Override protected void onStart() {
        super.onStart();
        QsHelper.getAppInterface().onActivityStart(this);
    }

    @CallSuper @Override protected void onResume() {
        super.onResume();
        QsHelper.getAppInterface().onActivityResume(this);
    }

    @CallSuper @Override protected void onPause() {
        super.onPause();
        QsHelper.getAppInterface().onActivityPause(this);
    }

    @CallSuper @Override protected void onStop() {
        super.onStop();
        QsHelper.getAppInterface().onActivityStop(this);
    }

    @CallSuper @Override protected void onDestroy() {
        super.onDestroy();
        isDestroyed = true;
        unbindEventByQsPlugin();
        cancelAllHttpRequest();

        QsHelper.getAppInterface().onActivityDestroy(this);
        QsHelper.getScreenHelper().popActivity(this);
    }

    @CallSuper @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isDestroyed = true;
        QsHelper.getScreenHelper().pushActivity(this);
        QsHelper.getAppInterface().onActivityCreate(this);
        bindBundleByQsPlugin(getIntent().getExtras());
        initStatusBar();
        rootView = initView(getLayoutInflater());
        setContentView(rootView);
        onViewCreated(rootView);
        bindEventByQsPlugin();
        initData(savedInstanceState);
    }

    @Override public final String initTag() {
        return L.isEnable() ? getClass().getSimpleName() : "MvActivity";
    }

    @Override public int rootViewLayoutId() {
        if (isOpenSlidingToClose()) {
            return isOpenViewState() ? R.layout.qs_activity_sliding_animator : R.layout.qs_activity_sliding;
        } else {
            return isOpenViewState() ? R.layout.qs_activity_animator : R.layout.qs_activity;
        }
    }

    @Override public int actionbarLayoutId() {
        return 0;
    }

    @Override public int layoutId() {
        return 0;
    }

    @Override public int loadingLayoutId() {
        return QsHelper.getAppInterface().loadingLayoutId();
    }

    @Override public int emptyLayoutId() {
        return QsHelper.getAppInterface().emptyLayoutId();
    }

    @Override public int errorLayoutId() {
        return QsHelper.getAppInterface().errorLayoutId();
    }

    @Override public View onCreateRootView(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent) {
        return inflater.inflate(rootViewLayoutId(), parent, false);
    }

    @Override public View onCreateActionbarView(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return actionbarLayoutId() == 0 ? null : inflater.inflate(actionbarLayoutId(), parent, true);
    }

    @Override public View onCreateLoadingView(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return loadingLayoutId() == 0 ? null : inflater.inflate(loadingLayoutId(), parent, false);
    }

    @Override public View onCreateContentView(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return layoutId() == 0 ? null : inflater.inflate(layoutId(), parent, false);
    }

    @Override public View onCreateEmptyView(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return emptyLayoutId() == 0 ? null : inflater.inflate(emptyLayoutId(), parent, false);
    }

    @Override public View onCreateErrorView(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return errorLayoutId() == 0 ? null : inflater.inflate(errorLayoutId(), parent, false);
    }

    @Override public void onViewCreated(@NonNull View rootView) {
    }

    @NonNull protected View initView(@NonNull LayoutInflater inflater) {
        long s0 = 0;
        if (L.isEnable()) s0 = System.currentTimeMillis();
        View rootView = onCreateRootView(inflater, null);

        if (isOpenSlidingToClose()) {
            if (rootView instanceof ISlidingViewGroup) {
                slidingView = (ISlidingViewGroup) rootView;
            } else {
                slidingView = rootView.findViewById(R.id.qs_sliding_view);
            }
            if (slidingView != null) {
                slidingView.setSlidingListener(new SlidingListenerAdapter() {
                    @Override public void onOpen() {
                        activityFinish();
                    }
                });
            }
        }

        ViewGroup actionbarContainer = rootView.findViewById(R.id.qs_actionbar_parent);
        if (actionbarContainer != null) {
            View actionbarView = onCreateActionbarView(inflater, actionbarContainer);
            if (actionbarView != null && actionbarView.getParent() == null) {
                actionbarContainer.addView(actionbarView);
            }
        }

        if (isOpenViewState()) {
            mViewAnimator = rootView.findViewById(R.id.qs_view_animator);
            ViewHelper.initViewAnimator(mViewAnimator, this);

            View loadingView = onCreateLoadingView(inflater, mViewAnimator);
            if (loadingView != null) {
                View view = ViewHelper.addToParent(loadingView, mViewAnimator, VIEW_STATE_LOADING);
                setDefaultViewClickListener(view);
            }

            View contentView = onCreateContentView(inflater, mViewAnimator);
            if (contentView != null) {
                View view = ViewHelper.addToParent(contentView, mViewAnimator, VIEW_STATE_CONTENT);
                if (contentViewBackgroundColor() != 0) {
                    view.setBackgroundColor(contentViewBackgroundColor());
                }
            }

            if (L.isEnable()) {
                long s1 = System.currentTimeMillis();
                L.i(initTag(), "initView...view inflate complete(viewState is open), use time:" + (s1 - s0) + "ms");
            }

        } else {
            ViewGroup customView = rootView.findViewById(R.id.qs_default_container_for_activity);
            View contentView = onCreateContentView(inflater, customView);
            if (contentView != null) {
                if (contentViewBackgroundColor() != 0) {
                    contentView.setBackgroundColor(contentViewBackgroundColor());
                }
                if (customView != contentView && contentView.getParent() == null) {
                    customView.addView(contentView);
                }
            }

            if (L.isEnable()) {
                long s1 = System.currentTimeMillis();
                L.i(initTag(), "initView...view inflate complete(viewState not open), use time:" + (s1 - s0) + "ms");
            }
        }
        return rootView;
    }

    @Override public final void onViewClicked(@NonNull View view) {
        onViewClicked(view, 400);
    }

    @Override public final void onViewClicked(@NonNull View view, long interval) {
        if (interval > 0 && ViewHelper.isFastClick(interval)) return;
        onViewClick(view);
    }

    @Override public void onViewClick(@NonNull View view) {
        //custom your logic
    }

    @Override public Context getContext() {
        return this;
    }

    @Override public boolean isOpenViewState() {
        return false;
    }

    @Override public Animation viewStateInAnimation() {
        return null;
    }

    @Override public Animation viewStateOutAnimation() {
        return null;
    }

    @Override public int viewStateInAnimationId() {
        return 0;
    }

    @Override public int viewStateOutAnimationId() {
        return 0;
    }

    @Override public boolean viewStateAnimateFirstView() {
        return true;
    }

    @Override public boolean isShowBackButtonInDefaultView() {
        return false;
    }

    @Override public final void activityFinish() {
        activityFinish(false);
    }

    @Override public final void activityFinish(int enterAnim, int exitAnim) {
        activityFinish();
        overridePendingTransition(enterAnim, exitAnim);
    }

    @Override public final void activityFinish(boolean finishAfterTransition) {
        if (finishAfterTransition) {
            ActivityCompat.finishAfterTransition(this);
        } else {
            finish();
        }
    }

    @NonNull @Override public QsProgressDialog getLoadingDialog() {
        return QsHelper.getAppInterface().getLoadingDialog();
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
        if (progressView == null) {
            progressView = new ProgressView(this);
            progressView.initView(getLoadingDialog());
        }
        progressView.setMessage(message);
        progressView.setCancelable(cancelAble);
        progressView.show(this);
    }

    @Override public final void loadingClose() {
        if (progressView != null) {
            progressView.hide();
        }
    }

    @Override public final void showLoadingView() {
        if (mViewAnimator != null) {
            if (L.isEnable()) L.i(initTag(), "showLoadingView.........childCount:" + mViewAnimator.getChildCount());
            int index = ViewHelper.findViewIndexByState(mViewAnimator, VIEW_STATE_LOADING);
            if (index >= 0) setViewState(index);
        }
    }

    @Override public final void showContentView() {
        if (mViewAnimator != null) {
            if (L.isEnable()) L.i(initTag(), "showContentView.........childCount:" + mViewAnimator.getChildCount());
            int index = ViewHelper.findViewIndexByState(mViewAnimator, VIEW_STATE_CONTENT);
            if (index >= 0) setViewState(index);
        }
    }

    @Override public final void showEmptyView() {
        if (mViewAnimator != null) {
            if (L.isEnable()) L.i(initTag(), "showEmptyView.........childCount:" + mViewAnimator.getChildCount());
            int index = ViewHelper.findViewIndexByState(mViewAnimator, VIEW_STATE_EMPTY);
            if (index >= 0) {
                setViewState(index);
            } else {
                post(new Runnable() {
                    @Override public void run() {
                        if (L.isEnable()) L.i(initTag(), "showEmptyView.........create empty view by 'onCreateEmptyView(...)' method~");
                        View emptyView = onCreateEmptyView(getLayoutInflater(), mViewAnimator);
                        View view = ViewHelper.addToParent(emptyView, mViewAnimator, VIEW_STATE_EMPTY);
                        setDefaultViewClickListener(view);
                        setViewState(mViewAnimator.getChildCount() - 1);
                    }
                });
            }
        }
    }

    @Override public final void showErrorView() {
        if (mViewAnimator != null) {
            if (L.isEnable()) L.i(initTag(), "showErrorView.........childCount:" + mViewAnimator.getChildCount());
            int index = ViewHelper.findViewIndexByState(mViewAnimator, VIEW_STATE_ERROR);
            if (index >= 0) {
                setViewState(index);
            } else {
                post(new Runnable() {
                    @Override public void run() {
                        if (L.isEnable()) L.i(initTag(), "showErrorView.........create error view by 'onCreateErrorView(...)' method~");
                        View errorView = onCreateErrorView(getLayoutInflater(), mViewAnimator);
                        View view = ViewHelper.addToParent(errorView, mViewAnimator, VIEW_STATE_ERROR);
                        setDefaultViewClickListener(view);
                        setViewState(mViewAnimator.getChildCount() - 1);
                    }
                });
            }
        }
    }

    @Override public final boolean isShowLoadingView() {
        return currentViewState() == VIEW_STATE_LOADING;
    }

    @Override public final boolean isShowContentView() {
        return mViewAnimator == null || currentViewState() == VIEW_STATE_CONTENT;
    }

    @Override public final boolean isShowEmptyView() {
        return currentViewState() == VIEW_STATE_EMPTY;
    }

    @Override public final boolean isShowErrorView() {
        return currentViewState() == VIEW_STATE_ERROR;
    }

    @Override public final int currentViewState() {
        if (isOpenViewState() && mViewAnimator != null) {
            int displayedIndex = mViewAnimator.getDisplayedChild();
            View childView = mViewAnimator.getChildAt(displayedIndex);
            return (int) childView.getTag(R.id.qs_view_state_key);
        }
        return -1;
    }

    @Override public final void intent2Activity(Class<?> clazz) {
        intent2Activity(clazz, null, 0, null, 0, 0);
    }

    @Override public final void intent2Activity(Class<?> clazz, int requestCode) {
        intent2Activity(clazz, null, requestCode, null, 0, 0);
    }

    @Override public final void intent2Activity(Class<?> clazz, Bundle bundle) {
        intent2Activity(clazz, bundle, 0, null, 0, 0);
    }

    @Override public final void intent2Activity(Class<?> clazz, Bundle bundle, int requestCode) {
        intent2Activity(clazz, bundle, requestCode, null, 0, 0);
    }

    @Override public final void intent2Activity(Class<?> clazz, Bundle bundle, int inAnimId, int outAnimId) {
        intent2Activity(clazz, bundle, 0, null, inAnimId, outAnimId);
    }

    @Override public final void intent2Activity(Class<?> clazz, Bundle bundle, ActivityOptionsCompat optionsCompat) {
        intent2Activity(clazz, bundle, 0, optionsCompat, 0, 0);
    }

    @Override public final void intent2Activity(Class<?> clazz, Bundle bundle, int requestCode, ActivityOptionsCompat optionsCompat) {
        intent2Activity(clazz, bundle, requestCode, optionsCompat, 0, 0);
    }

    @Override public final void intent2Activity(Class<?> clazz, Bundle bundle, int requestCode, ActivityOptionsCompat optionsCompat, int enterAnim, int existAnim) {
        ViewHelper.intent2Activity(this, clazz, bundle, requestCode, optionsCompat, enterAnim, existAnim);
    }

    @Override public final void commitFragment(Fragment fragment) {
        QsHelper.commitFragment(getSupportFragmentManager(), R.id.qs_default_container_for_activity, fragment);
    }

    @Override public final void commitFragment(Fragment fragment, String tag) {
        QsHelper.commitFragment(getSupportFragmentManager(), R.id.qs_default_container_for_activity, fragment, tag);
    }

    @Override public final void commitFragment(Fragment fragment, int enterAnim, int existAnim) {
        QsHelper.commitFragment(getSupportFragmentManager(), R.id.qs_default_container_for_activity, fragment, fragment.getClass().getSimpleName(), enterAnim, existAnim);
    }

    @Override public final void commitFragment(Fragment fragment, String tag, int enterAnim, int existAnim) {
        QsHelper.commitFragment(getSupportFragmentManager(), R.id.qs_default_container_for_activity, fragment, tag, enterAnim, existAnim);
    }

    @Override public final void commitFragment(int layoutId, Fragment fragment) {
        QsHelper.commitFragment(getSupportFragmentManager(), layoutId, fragment);
    }

    @Override public final void commitFragment(int layoutId, Fragment fragment, String tag) {
        QsHelper.commitFragment(getSupportFragmentManager(), layoutId, fragment, tag);
    }

    @Override public final void commitFragment(int layoutId, Fragment fragment, int enterAnim, int existAnim) {
        QsHelper.commitFragment(getSupportFragmentManager(), layoutId, fragment, fragment.getClass().getSimpleName(), enterAnim, existAnim);
    }

    @Override public final void commitFragment(int layoutId, Fragment fragment, String tag, int enterAnim, int existAnim) {
        QsHelper.commitFragment(getSupportFragmentManager(), layoutId, fragment, tag, enterAnim, existAnim);
    }

    @Override public final void commitDialogFragment(DialogFragment fragment) {
        QsHelper.commitDialogFragment(getSupportFragmentManager(), fragment);
    }

    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionHelper.parsePermissionResultData(requestCode, permissions, grantResults);
    }

    @Override public final void post(Runnable action) {
        runOnUiThread(action);
    }

    @Override public final void postDelayed(Runnable action, long delayed) {
        QsHelper.postDelayed(action, delayed);
    }

    @Override public final void runOnHttpThread(Runnable action) {
        QsHelper.executeInHttpThread(action);
    }

    @Override public final void runOnWorkThread(Runnable action) {
        QsHelper.executeInWorkThread(action);
    }

    @Override public FragmentActivity getActivity() {
        return this;
    }

    @Override public void smoothScrollToTop(boolean autoRefresh) {
        if (rootView == null) return;
        final ScrollView scrollView = ViewHelper.tryGetTargetView(ScrollView.class, rootView);
        if (scrollView != null) {
            scrollView.post(new Runnable() {
                @Override public void run() {
                    scrollView.smoothScrollTo(0, 0);
                }
            });
        }
        if (autoRefresh) {
            final PtrFrameLayout frameLayout = ViewHelper.tryGetTargetView(PtrFrameLayout.class, rootView);
            if (frameLayout != null) {
                frameLayout.post(new Runnable() {
                    @Override public void run() {
                        frameLayout.autoRefresh();
                    }
                });
            }
        }
    }

    @Override public final void setOnActivityResultListener(OnActivityResultListener listener) {
        this.activityResultListener = listener;
    }

    @Override public int contentViewBackgroundColor() {
        return 0;
    }

    protected void initStatusBar() {
        ViewHelper.initStatusBar(this, isTransparentStatusBar(), isTransparentNavigationBar(), isBlackIconStatusBar());
    }

    @Override public boolean isBlackIconStatusBar() {
        return false;
    }

    @Override public boolean isTransparentStatusBar() {
        return false;
    }

    @Override public boolean isTransparentNavigationBar() {
        return false;
    }

    @Override public final boolean onKeyDown(int keyCode, KeyEvent event) {
        if (onKeyDownListener != null && onKeyDownListener.onKeyDown(keyCode, event)) {
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK && getOnBackPressedDispatcher().hasEnabledCallbacks()) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        return onKeyDown(event, keyCode);
    }

    @Override public boolean onKeyDown(@NonNull KeyEvent event, int keyCode) {
        return super.onKeyDown(keyCode, event);
    }

    @Override public final void setOnKeyDownListener(OnKeyDownListener listener) {
        this.onKeyDownListener = listener;
    }

    @Override public void setActivityTitle(CharSequence title, int type) {
        //custom your logic
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (activityResultListener != null) {
            activityResultListener.onActivityResult(this, requestCode, requestCode, data);
        }
    }

    private void setDefaultViewClickListener(View view) {
        ViewHelper.setDefaultViewClickListener(view, this);
    }

    private void setViewState(final int index) {
        if (mViewAnimator != null) {
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
    }

    @Override @NonNull public final <T> T createHttpRequest(Class<T> clazz) {
        return createHttpRequest(clazz, System.nanoTime(), null);
    }

    @Override @NonNull public final <T> T createHttpRequest(Class<T> clazz, Object tag) {
        return createHttpRequest(clazz, tag, null);
    }

    @Override @NonNull public final <T> T createHttpRequest(Class<T> clazz, NetworkErrorReceiver receiver) {
        return createHttpRequest(clazz, System.nanoTime(), receiver);
    }

    @Override @NonNull public final <T> T createHttpRequest(Class<T> clazz, Object requestTag, NetworkErrorReceiver receiver) {
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
     * 取消由当前Activity发起的http请求
     */
    protected final void cancelAllHttpRequest() {
        if (requestTags != null) {
            synchronized (this) {
                QsHelper.getHttpHelper().cancelRequest(requestTags);
                requestTags.clear();
            }
        }
    }

    @Override public final boolean isViewDestroyed() {
        return isDestroyed;
    }

    /**
     * 是否打开滑动关闭Activity功能
     * 注意当前页面手势冲突
     *
     * @see ISlidingViewGroup
     * @see #rootViewLayoutId()
     */
    @Override public boolean isOpenSlidingToClose() {
        return false;
    }

    /**
     * @see ISlidingViewGroup
     * 是否允许滑动关闭Activity
     */
    @Override public final void setAllowSlidingToClose(boolean allow) {
        if (slidingView != null) slidingView.setCanSliding(allow);
    }

    @Override public final boolean isAllowSlidingToClose() {
        return slidingView != null && slidingView.isCanSliding();
    }
}
