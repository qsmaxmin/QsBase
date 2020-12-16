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
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.utils.QsHelper;
import com.qsmaxmin.qsbase.common.utils.ViewHelper;
import com.qsmaxmin.qsbase.common.viewbind.OnActivityResultListener;
import com.qsmaxmin.qsbase.common.viewbind.OnKeyDownListener;
import com.qsmaxmin.qsbase.common.widget.dialog.QsProgressDialog;
import com.qsmaxmin.qsbase.common.widget.ptr.PtrFrameLayout;
import com.qsmaxmin.qsbase.plugin.permission.PermissionHelper;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

/**
 * @CreateBy administrator
 * @Date 2020/12/8 14:01
 * @Description
 */
public abstract class MvActivity extends FragmentActivity implements MvIActivity {
    private ViewAnimator             mViewAnimator;
    private QsProgressDialog         mProgressDialog;
    private OnActivityResultListener activityResultListener;
    private OnKeyDownListener        onKeyDownListener;
    private View                     rootView;

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
        if (mProgressDialog != null) {
            mProgressDialog.dismissAllowingStateLoss();
            mProgressDialog = null;
        }
        onKeyDownListener = null;
        activityResultListener = null;
        unbindEventByQsPlugin();
        QsHelper.getAppInterface().onActivityDestroy(this);
        QsHelper.getScreenHelper().popActivity(this);
    }

    @CallSuper @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        QsHelper.getScreenHelper().pushActivity(this);
        QsHelper.getAppInterface().onActivityCreate(this);
        bindBundleByQsPlugin(getIntent().getExtras());
        initStatusBar();
        rootView = initView(getLayoutInflater());
        setContentView(rootView);

        bindEventByQsPlugin();
        initData(savedInstanceState);
    }

    @Override public void onCreateActionbarView(LayoutInflater inflater, ViewGroup parent) {
    }

    @Override public View onCreateLoadingView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(QsHelper.getAppInterface().loadingLayoutId(), parent, false);
    }

    @Override public View onCreateEmptyView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(QsHelper.getAppInterface().emptyLayoutId(), parent, false);
    }

    @Override public View onCreateErrorView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(QsHelper.getAppInterface().errorLayoutId(), parent, false);
    }

    protected View initView(LayoutInflater inflater) {
        long s0 = 0;
        if (L.isEnable()) s0 = System.currentTimeMillis();
        ViewGroup rootView = (ViewGroup) inflater.inflate(rootViewLayoutId(), null);

        ViewGroup actionbarContainer = rootView.findViewById(R.id.qs_actionbar_parent);
        if (actionbarContainer != null) {
            onCreateActionbarView(inflater, actionbarContainer);
        }

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
                if (contentViewBackgroundColor() != 0) {
                    contentView.setBackgroundColor(contentViewBackgroundColor());
                }
                if (contentView.getParent() == null) {
                    mViewAnimator.addView(contentView);
                }
            }

            if (L.isEnable()) {
                long s1 = System.currentTimeMillis();
                L.i(initTag(), "initView...view inflate complete(viewState is open), use time:" + (s1 - s0) + "ms");
            }
        } else {
            ViewGroup customView = rootView.findViewById(android.R.id.custom);
            View contentView = onCreateContentView(inflater, customView);
            if (contentView != null) {
                if (contentViewBackgroundColor() != 0) {
                    contentView.setBackgroundColor(contentViewBackgroundColor());
                }
                if (contentView.getParent() == null) {
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

    @Override public String initTag() {
        return L.isEnable() ? getClass().getSimpleName() : "MvActivity";
    }

    @Override public int rootViewLayoutId() {
        if (isOpenViewState()) {
            return R.layout.qs_view_animator_ab;
        } else {
            return R.layout.qs_frame_layout_ab;
        }
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

    @Override public void activityFinish() {
        activityFinish(false);
    }

    @Override public void activityFinish(int enterAnim, int exitAnim) {
        activityFinish();
        overridePendingTransition(enterAnim, exitAnim);
    }

    @Override public void activityFinish(boolean finishAfterTransition) {
        if (finishAfterTransition) {
            ActivityCompat.finishAfterTransition(this);
        } else {
            finish();
        }
    }


    @Override public QsProgressDialog getLoadingDialog() {
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
        if (mProgressDialog == null) mProgressDialog = getLoadingDialog();
        if (mProgressDialog != null) {
            mProgressDialog.setMessage(message);
            mProgressDialog.setCancelable(cancelAble);
            if (!mProgressDialog.isAdded() && !mProgressDialog.isShowing()) {
                mProgressDialog.setIsShowing(true);
                QsHelper.commitDialogFragment(getSupportFragmentManager(), mProgressDialog);
            }
        } else {
            L.e(initTag(), "you should override the method 'Application.getLoadingDialog() or this.getLoadingDialog()' and return a dialog when called the method : loading(...) ");
        }
    }

    @Override public void loadingClose() {
        if (mProgressDialog != null && mProgressDialog.isAdded()) {
            mProgressDialog.dismissAllowingStateLoss();
        }
    }

    @Override public void showLoadingView() {
        if (mViewAnimator != null) {
            if (L.isEnable()) L.i(initTag(), "showLoadingView.........");
            setViewState(0);
        }
    }

    @Override public void showContentView() {
        if (mViewAnimator != null) {
            if (L.isEnable()) L.i(initTag(), "showContentView.........");
            setViewState(1);
        }
    }

    @Override public void showEmptyView() {
        if (mViewAnimator != null) {
            if (L.isEnable()) L.i(initTag(), "showEmptyView.........");
            int childCount = mViewAnimator.getChildCount();
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
                    if (emptyView.getParent() == null) {
                        mViewAnimator.addView(emptyView);
                    }
                    setDefaultViewClickListener(emptyView);
                    setViewState(mViewAnimator.getChildCount() - 1);
                }
            });
        }
    }

    @Override public void showErrorView() {
        if (isOpenViewState() && mViewAnimator != null) {
            if (L.isEnable()) L.i(initTag(), "showErrorView.........");
            int childCount = mViewAnimator.getChildCount();
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
                    if (errorView.getParent() == null) {
                        mViewAnimator.addView(errorView);
                    }
                    setDefaultViewClickListener(errorView);
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

    @Override public int currentViewState() {
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
        QsHelper.commitFragment(getSupportFragmentManager(), android.R.id.custom, fragment);
    }

    @Override public final void commitFragment(Fragment fragment, String tag) {
        QsHelper.commitFragment(getSupportFragmentManager(), android.R.id.custom, fragment, tag);
    }

    @Override public final void commitFragment(Fragment fragment, int enterAnim, int existAnim) {
        QsHelper.commitFragment(getSupportFragmentManager(), android.R.id.custom, fragment, fragment.getClass().getSimpleName(), enterAnim, existAnim);
    }

    @Override public final void commitFragment(Fragment fragment, String tag, int enterAnim, int existAnim) {
        QsHelper.commitFragment(getSupportFragmentManager(), android.R.id.custom, fragment, tag, enterAnim, existAnim);
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
        if (!isFinishing()) {
            QsHelper.postDelayed(action, delayed);
        }
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

    @Override public void setOnActivityResultListener(OnActivityResultListener listener) {
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
        if (onKeyDownListener != null && onKeyDownListener.onKeyDown(keyCode, event)) return true;
        return onKeyDown(event, keyCode);
    }

    @Override public boolean onKeyDown(KeyEvent event, int keyCode) {
        return super.onKeyDown(keyCode, event);
    }

    @Override public void setOnKeyDownListener(OnKeyDownListener listener) {
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

    protected void setViewState(final int index) {
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
}
