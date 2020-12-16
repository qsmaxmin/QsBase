package com.qsmaxmin.qsbase.mvp;

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
import com.qsmaxmin.qsbase.mvp.presenter.QsPresenter;
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
 * @CreateBy qsmaxmin
 * @Date 2017/6/20 16:40
 * @Description
 */
public abstract class QsActivity<P extends QsPresenter> extends FragmentActivity implements QsIActivity {
    private   View                     contentView;
    protected P                        presenter;
    protected QsProgressDialog         mProgressDialog;
    protected ViewAnimator             mViewAnimator;
    private   OnKeyDownListener        onKeyDownListener;
    private   OnActivityResultListener activityResultListener;

    @Override public String initTag() {
        return L.isEnable() ? getClass().getSimpleName() : "QsActivity";
    }

    @Override public int rootViewLayoutId() {
        if (isOpenViewState()) {
            return actionbarLayoutId() == 0 ? R.layout.qs_view_animator : R.layout.qs_view_animator_ab;
        } else {
            return actionbarLayoutId() == 0 ? R.layout.qs_frame_layout : R.layout.qs_frame_layout_ab;
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

    @CallSuper @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        QsHelper.getScreenHelper().pushActivity(this);
        QsHelper.getAppInterface().onActivityCreate(this);
        bindBundleByQsPlugin(getIntent().getExtras());
        initStatusBar();
        contentView = initView(getLayoutInflater());
        setContentView(contentView);
        bindViewByQsPlugin(contentView);
        onViewCreated(contentView);

        bindEventByQsPlugin();
        initData(savedInstanceState);
    }

    @Override public void onViewCreated(View view) {
        //custom your logic
    }

    @CallSuper @Override public void bindBundleByQsPlugin(Bundle bundle) {
    }

    @CallSuper @Override public void bindViewByQsPlugin(View view) {
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
        if (presenter != null) {
            presenter.setDetach();
            presenter = null;
        }
        if (mProgressDialog != null) {
            mProgressDialog.dismissAllowingStateLoss();
            mProgressDialog = null;
        }
        onKeyDownListener = null;
        activityResultListener = null;
        contentView = null;
        unbindEventByQsPlugin();
        QsHelper.getAppInterface().onActivityDestroy(this);
        QsHelper.getScreenHelper().popActivity(this);
    }

    /**
     * 初始化View
     */
    protected View initView(LayoutInflater inflater) {
        long s0 = 0;
        if (L.isEnable()) s0 = System.nanoTime();
        ViewGroup rootView = (ViewGroup) inflater.inflate(rootViewLayoutId(), null);

        if (actionbarLayoutId() != 0) {
            ViewGroup actionbarContainer = rootView.findViewById(R.id.qs_actionbar_parent);
            inflater.inflate(actionbarLayoutId(), actionbarContainer, true);
        }

        if (isOpenViewState()) {
            mViewAnimator = rootView.findViewById(R.id.qs_view_animator);
            ViewHelper.initViewAnimator(mViewAnimator, this);

            View loadingView = inflater.inflate(loadingLayoutId(), mViewAnimator, false);
            loadingView.setTag(R.id.qs_view_state_key, VIEW_STATE_LOADING);
            setDefaultViewClickListener(loadingView);
            mViewAnimator.addView(loadingView);
            onLoadingViewCreated(loadingView);

            if (layoutId() != 0) {
                View targetView = inflater.inflate(layoutId(), mViewAnimator, false);
                targetView.setTag(R.id.qs_view_state_key, VIEW_STATE_CONTENT);
                if (contentViewBackgroundColor() != 0) targetView.setBackgroundColor(contentViewBackgroundColor());
                mViewAnimator.addView(targetView);
                onContentViewCreated(targetView);
            }

            if (L.isEnable()) {
                long s1 = System.nanoTime();
                L.i(initTag(), "initView...view inflate complete(viewState is open), use time:" + (s1 - s0) / 1000_000f + "ms");
            }
        } else {
            if (layoutId() != 0) {
                ViewGroup customView = rootView.findViewById(android.R.id.custom);
                View targetView = inflater.inflate(layoutId(), customView, false);
                if (contentViewBackgroundColor() != 0) targetView.setBackgroundColor(contentViewBackgroundColor());
                customView.addView(targetView);
                onContentViewCreated(targetView);
            }

            if (L.isEnable()) {
                long s1 = System.nanoTime();
                L.i(initTag(), "initView...view inflate complete(viewState not open), use time:" + (s1 - s0) / 1000_000f + "ms");
            }
        }
        return rootView;
    }

    @SuppressWarnings("unchecked")
    @Override public final P getPresenter() {
        if (presenter == null) {
            presenter = (P) createPresenter();
            presenter.initPresenter(this);
        }
        return presenter;
    }

    @Override public Object createPresenter() {
        return null;
    }

    @Override public void onViewClick(View view) {
    }

    @Override public final Context getContext() {
        return this;
    }

    @Override public boolean isOpenViewState() {
        return false;
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

    @Override public void onLoadingViewCreated(@NonNull View loadingView) {
        //custom your logic
    }

    @Override public void onContentViewCreated(@NonNull View contentView) {
        //custom your logic
    }

    @Override public void onEmptyViewCreated(@NonNull View emptyView) {
        //custom your logic
    }

    @Override public void onErrorViewCreated(@NonNull View errorView) {
        //custom your logic
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

    @Override public final void loadingClose() {
        if (mProgressDialog != null && mProgressDialog.isAdded()) {
            mProgressDialog.dismissAllowingStateLoss();
        }
    }

    @Override public final void showLoadingView() {
        if (mViewAnimator != null) {
            if (L.isEnable()) L.i(initTag(), "showLoadingView.........");
            setViewState(0);
        }
    }

    @Override public final void showContentView() {
        if (mViewAnimator != null) {
            if (L.isEnable()) L.i(initTag(), "showContentView.........");
            setViewState(1);
        }
    }

    @Override public final void showEmptyView() {
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
                    if (L.isEnable()) L.i(initTag(), "showEmptyView.........inflate emptyLayoutId()");
                    View emptyView = getLayoutInflater().inflate(emptyLayoutId(), mViewAnimator, false);
                    emptyView.setTag(R.id.qs_view_state_key, VIEW_STATE_EMPTY);
                    setDefaultViewClickListener(emptyView);
                    mViewAnimator.addView(emptyView);
                    onEmptyViewCreated(emptyView);
                    setViewState(mViewAnimator.getChildCount() - 1);
                }
            });
        }
    }

    @Override public final void showErrorView() {
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
                    if (L.isEnable()) L.i(initTag(), "showErrorView.........inflate errorLayoutId()");
                    View errorView = getLayoutInflater().inflate(errorLayoutId(), mViewAnimator, false);
                    errorView.setTag(R.id.qs_view_state_key, VIEW_STATE_ERROR);
                    setDefaultViewClickListener(errorView);
                    mViewAnimator.addView(errorView);
                    onErrorViewCreated(errorView);
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

    @Override public void onBackPressed() {
        if (QsHelper.isMainThread()) {
            super.onBackPressed();
        } else {
            post(new Runnable() {
                @Override public void run() {
                    QsActivity.super.onBackPressed();
                }
            });
        }
    }

    @Override public final boolean onKeyDown(int keyCode, KeyEvent event) {
        if (onKeyDownListener != null && onKeyDownListener.onKeyDown(keyCode, event)) return true;
        return onKeyDown(event, keyCode);
    }

    @Override public boolean onKeyDown(KeyEvent event, int keyCode) {
        return super.onKeyDown(keyCode, event);
    }

    private void setDefaultViewClickListener(View view) {
        ViewHelper.setDefaultViewClickListener(view, this);
    }

    protected void initStatusBar() {
        ViewHelper.initStatusBar(this, isTransparentStatusBar(), isTransparentNavigationBar(), isBlackIconStatusBar());
    }

    @Override public final void post(Runnable action) {
        runOnUiThread(action);
    }

    @Override public final void postDelayed(Runnable action, long delayed) {
        if (!isFinishing()) {
            QsHelper.postDelayed(action, delayed);
        }
    }

    @Override public final void runOnWorkThread(Runnable action) {
        QsHelper.executeInWorkThread(action);
    }

    @Override public final void runOnHttpThread(Runnable action) {
        QsHelper.executeInHttpThread(action);
    }

    @Override public final FragmentActivity getActivity() {
        return this;
    }

    @Override public void smoothScrollToTop(boolean autoRefresh) {
        View view = contentView;
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

    @Override protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (activityResultListener != null) {
            activityResultListener.onActivityResult(this, requestCode, resultCode, data);
        }
    }

    @Override public final void setOnActivityResultListener(OnActivityResultListener listener) {
        this.activityResultListener = listener;
    }

    @Override public final void setOnKeyDownListener(OnKeyDownListener listener) {
        this.onKeyDownListener = listener;
    }

    @Override public void setActivityTitle(CharSequence title, int type) {
        //custom your logic
    }

    @Override public boolean isShowBackButtonInDefaultView() {
        return false;
    }

    @Override public boolean isTransparentStatusBar() {
        return false;
    }

    @Override public boolean isBlackIconStatusBar() {
        return false;
    }

    @Override public boolean isTransparentNavigationBar() {
        return false;
    }

    @Override public int contentViewBackgroundColor() {
        return 0;
    }
}
