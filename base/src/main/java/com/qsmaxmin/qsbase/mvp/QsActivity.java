package com.qsmaxmin.qsbase.mvp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ScrollView;
import android.widget.ViewAnimator;

import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.utils.QsHelper;
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
    private   boolean                  hasInitData;
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
        return R.layout.qs_frame_layout;
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

    @Override @CallSuper protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        QsHelper.getScreenHelper().pushActivity(this);
        QsHelper.getAppInterface().onActivityCreate(this);
        bindBundleByQsPlugin(getIntent().getExtras());
        initStatusBar();
        contentView = initView(getLayoutInflater());
        setContentView(contentView);
        bindViewByQsPlugin(contentView);

        if (isOpenEventBus()) {
            bindEventByQsPlugin();
        }
        if (!isDelayData()) {
            hasInitData = true;
            initData(savedInstanceState);
        }
    }

    @CallSuper @Override public void bindBundleByQsPlugin(Bundle bundle) {
    }

    @CallSuper @Override public void bindViewByQsPlugin(View view) {
    }

    @Override public boolean isOpenEventBus() {
        return true;
    }

    @Override public void bindEventByQsPlugin() {
    }

    @Override public void unbindEventByQsPlugin() {
    }

    @Override protected void onStart() {
        super.onStart();
        QsHelper.getAppInterface().onActivityStart(this);
    }

    @CallSuper @Override protected void onResume() {
        super.onResume();
        QsHelper.getAppInterface().onActivityResume(this);
    }

    @Override protected void onPause() {
        super.onPause();
        QsHelper.getAppInterface().onActivityPause(this);
    }

    @Override protected void onStop() {
        super.onStop();
        QsHelper.getAppInterface().onActivityStop(this);
    }

    @Override @CallSuper protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.setDetach();
            presenter = null;
        }
        if (mProgressDialog != null) {
            mProgressDialog.dismissAllowingStateLoss();
            mProgressDialog = null;
        }
        mViewAnimator = null;
        onKeyDownListener = null;
        contentView = null;
        if (isOpenEventBus()) {
            unbindEventByQsPlugin();
        }
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

        if (isOpenViewState()) {
            if (actionbarLayoutId() != 0) {
                ViewGroup actionbarContainer = rootView.findViewById(R.id.qs_actionbar_parent);
                inflater.inflate(actionbarLayoutId(), actionbarContainer, true);
            }
            mViewAnimator = rootView.findViewById(R.id.qs_view_animator);
            initViewAnimator(mViewAnimator);

            View loadingView = inflater.inflate(loadingLayoutId(), mViewAnimator, false);
            loadingView.setTag(R.id.qs_view_state_key, VIEW_STATE_LOADING);
            setDefaultViewClickListener(loadingView);
            mViewAnimator.addView(loadingView);
            onCreateLoadingView(loadingView);

            View contentView = inflater.inflate(layoutId(), mViewAnimator, false);
            contentView.setTag(R.id.qs_view_state_key, VIEW_STATE_CONTENT);
            if (contentViewBackgroundColor() != 0) contentView.setBackgroundColor(contentViewBackgroundColor());
            mViewAnimator.addView(contentView);
            onCreateContentView(contentView);

            if (L.isEnable()) {
                long s1 = System.nanoTime();
                L.i(initTag(), "initView...view inflate complete(viewState is open), use time:" + (s1 - s0) / 1000_000f + "ms");
            }
        } else {
            if (actionbarLayoutId() != 0) {
                ViewGroup actionbarContainer = rootView.findViewById(R.id.qs_actionbar_parent);
                inflater.inflate(actionbarLayoutId(), actionbarContainer, true);
            }
            ViewGroup customView = rootView.findViewById(android.R.id.custom);
            View contentView = inflater.inflate(layoutId(), customView, false);
            customView.addView(contentView);
            onCreateContentView(contentView);

            if (L.isEnable()) {
                long s1 = System.nanoTime();
                L.i(initTag(), "initView...view inflate complete(viewState not open), use time:" + (s1 - s0) / 1000_000f + "ms");
            }
        }
        return rootView;
    }


    private void initViewAnimator(ViewAnimator viewAnimator) {
        Animation inAnimation = viewStateInAnimation();
        if (inAnimation != null) {
            viewAnimator.setInAnimation(inAnimation);
        } else if (viewStateInAnimationId() != 0) {
            viewAnimator.setInAnimation(getContext(), viewStateInAnimationId());
        }
        Animation outAnimation = viewStateOutAnimation();
        if (outAnimation != null) {
            viewAnimator.setOutAnimation(outAnimation);
        } else if (viewStateOutAnimationId() != 0) {
            viewAnimator.setOutAnimation(getContext(), viewStateOutAnimationId());
        }
        viewAnimator.setAnimateFirstView(viewStateAnimateFirstView());
    }

    @SuppressWarnings("unchecked")
    @Override public P getPresenter() {
        if (presenter == null) {
            presenter = (P) createPresenter();
            presenter.initPresenter(this);
        }
        return presenter;
    }

    @Override public Object createPresenter() {
        return null;
    }

    @Override public void initDataWhenDelay() {
        if (!hasInitData && isDelayData()) {
            initData(getIntent().getExtras());
            hasInitData = true;
        }
    }

    @Override public void onViewClick(View view) {

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
        overridePendingTransition(enterAnim, exitAnim);
    }

    @Override public void activityFinish(boolean finishAfterTransition) {
        if (finishAfterTransition) ActivityCompat.finishAfterTransition(this);
        else finish();
    }

    @Override public void onCreateLoadingView(@NonNull View loadingView) {
        //custom your logic
    }

    @Override public void onCreateContentView(@NonNull View contentView) {
        //custom your logic
    }

    @Override public void onCreateEmptyView(@NonNull View emptyView) {
        //custom your logic
    }

    @Override public void onCreateErrorView(@NonNull View errorView) {
        //custom your logic
    }

    @Override public QsProgressDialog getLoadingDialog() {
        return QsHelper.getAppInterface().getLoadingDialog();
    }

    @Override public void loading() {
        loading(true);
    }

    @Override public void loading(boolean cancelAble) {
        loading(getString(R.string.loading), cancelAble);
    }

    @Override public void loading(String message) {
        loading(message, true);
    }

    @Override public void loading(int resId) {
        loading(resId, true);
    }

    @Override public void loading(int resId, boolean cancelAble) {
        loading(QsHelper.getString(resId), cancelAble);
    }

    @Override public void loading(String message, boolean cancelAble) {
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
        if (QsHelper.isMainThread()) {
            if (mProgressDialog != null && mProgressDialog.isAdded()) {
                mProgressDialog.dismissAllowingStateLoss();
            }
        } else {
            post(new Runnable() {
                @Override public void run() {
                    if (mProgressDialog != null && mProgressDialog.isAdded()) {
                        mProgressDialog.dismissAllowingStateLoss();
                    }
                }
            });
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
                    if (L.isEnable()) L.i(initTag(), "showEmptyView.........inflate emptyLayoutId()");
                    View emptyView = getLayoutInflater().inflate(emptyLayoutId(), mViewAnimator, false);
                    emptyView.setTag(R.id.qs_view_state_key, VIEW_STATE_EMPTY);
                    setDefaultViewClickListener(emptyView);
                    mViewAnimator.addView(emptyView);
                    onCreateEmptyView(emptyView);
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
                    if (L.isEnable()) L.i(initTag(), "showErrorView.........inflate errorLayoutId()");
                    View errorView = getLayoutInflater().inflate(errorLayoutId(), mViewAnimator, false);
                    errorView.setTag(R.id.qs_view_state_key, VIEW_STATE_ERROR);
                    setDefaultViewClickListener(errorView);
                    mViewAnimator.addView(errorView);
                    onCreateErrorView(errorView);
                    setViewState(mViewAnimator.getChildCount() - 1);
                }
            });
        }
    }

    /**
     * return current showing view
     */
    @Override public int currentViewState() {
        if (isOpenViewState() && mViewAnimator != null) {
            int displayedIndex = mViewAnimator.getDisplayedChild();
            View childView = mViewAnimator.getChildAt(displayedIndex);
            return (int) childView.getTag(R.id.qs_view_state_key);
        }
        return -1;
    }

    @Override public void intent2Activity(Class clazz) {
        intent2Activity(clazz, null, 0, null, 0, 0);
    }

    @Override public void intent2Activity(Class clazz, int requestCode) {
        intent2Activity(clazz, null, requestCode, null, 0, 0);
    }

    @Override public void intent2Activity(Class clazz, Bundle bundle) {
        intent2Activity(clazz, bundle, 0, null, 0, 0);
    }

    @Override public void intent2Activity(Class clazz, Bundle bundle, int inAnimId, int outAnimId) {
        intent2Activity(clazz, bundle, 0, null, inAnimId, outAnimId);
    }

    @Override public void intent2Activity(Class clazz, Bundle bundle, ActivityOptionsCompat optionsCompat) {
        intent2Activity(clazz, bundle, 0, optionsCompat, 0, 0);
    }

    @Override public void intent2Activity(Class clazz, Bundle bundle, int requestCode, ActivityOptionsCompat optionsCompat) {
        intent2Activity(clazz, bundle, requestCode, optionsCompat, 0, 0);
    }

    @Override public void intent2Activity(Class clazz, Bundle bundle, int requestCode, ActivityOptionsCompat optionsCompat, int inAnimId, int outAnimId) {
        if (clazz != null) {
            Intent intent = new Intent();
            intent.setClass(this, clazz);
            if (bundle != null) intent.putExtras(bundle);
            if (optionsCompat == null) {
                if (requestCode > 0) {
                    startActivityForResult(intent, requestCode);
                } else {
                    startActivity(intent);
                }
                if (inAnimId != 0 || outAnimId != 0) overridePendingTransition(inAnimId, outAnimId);
            } else {
                if (requestCode > 0) {
                    ActivityCompat.startActivityForResult(this, intent, requestCode, optionsCompat.toBundle());
                } else {
                    ActivityCompat.startActivity(this, intent, optionsCompat.toBundle());
                }
            }
        }
    }

    @Override public void commitFragment(Fragment fragment) {
        commitFragment(fragment, fragment.getClass().getSimpleName());
    }

    @Override public void commitFragment(Fragment fragment, String tag) {
        commitFragment(android.R.id.custom, fragment, tag);
    }

    @Override public void commitFragment(int layoutId, Fragment fragment) {
        commitFragment(layoutId, fragment, fragment.getClass().getSimpleName());
    }

    @Override public void commitFragment(int layoutId, Fragment fragment, String tag) {
        QsHelper.commitFragment(getSupportFragmentManager(), layoutId, fragment, tag);
    }

    @Override public void commitFragment(Fragment old, Fragment fragment) {
        commitFragment(old, fragment, fragment.getClass().getSimpleName());
    }

    @Override public void commitFragment(Fragment old, Fragment fragment, String tag) {
        commitFragment(old, android.R.id.custom, fragment, tag);
    }

    @Override public void commitFragment(Fragment old, int layoutId, Fragment fragment) {
        commitFragment(old, layoutId, fragment, fragment.getClass().getSimpleName());
    }

    @Override public void commitFragment(Fragment old, int layoutId, Fragment fragment, String tag) {
        QsHelper.commitFragment(getSupportFragmentManager(), old, layoutId, fragment, tag);
    }

    @Override public void commitBackStackFragment(Fragment fragment) {
        commitBackStackFragment(fragment, fragment.getClass().getSimpleName());
    }

    @Override public void commitBackStackFragment(Fragment fragment, String tag) {
        commitBackStackFragment(android.R.id.custom, fragment, tag);
    }

    @Override public void commitBackStackFragment(int layoutId, Fragment fragment) {
        commitBackStackFragment(layoutId, fragment, fragment.getClass().getSimpleName());
    }

    @Override public void commitBackStackFragment(Fragment fragment, int enterAnim, int exitAnim) {
        QsHelper.commitBackStackFragment(fragment, enterAnim, exitAnim);
    }

    @Override public void commitBackStackFragment(int layoutId, Fragment fragment, String tag) {
        QsHelper.commitBackStackFragment(getSupportFragmentManager(), layoutId, fragment, tag);
    }

    @Override public void commitDialogFragment(DialogFragment fragment) {
        QsHelper.commitDialogFragment(getSupportFragmentManager(), fragment);
    }

    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionHelper.parsePermissionResultData(requestCode, permissions, grantResults);
    }

    protected void setViewState(final int index) {
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
        if (view != null) {
            View backView = view.findViewById(R.id.qs_back_in_default_view);
            if (backView != null) {
                if (isShowBackButtonInDefaultView()) {
                    backView.setVisibility(View.VISIBLE);
                    backView.setOnClickListener(new View.OnClickListener() {
                        @Override public void onClick(View v) {
                            onBackPressed();
                        }
                    });
                } else {
                    backView.setVisibility(View.GONE);
                }
            }
            View reloadView = view.findViewById(R.id.qs_reload_in_default_view);
            if (reloadView != null) reloadView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    showLoadingView();
                    initData(getIntent().getExtras());
                }
            });
        }
    }

    protected void initStatusBar() {
        if (isTransparentStatusBar() || isTransparentNavigationBar()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                if (isTransparentNavigationBar()) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                    window.setNavigationBarColor(Color.TRANSPARENT);
                }

                if (isTransparentStatusBar()) {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(Color.TRANSPARENT);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && isBlackIconStatusBar()) {
                        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                    } else {
                        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                    }
                }

            } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                Window window = getWindow();
                WindowManager.LayoutParams winParams = window.getAttributes();
                final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
                winParams.flags |= bits;
                window.setAttributes(winParams);
            }
        } else {
            if (isBlackIconStatusBar() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Window window = getWindow();
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
    }

    @Override public void post(Runnable action) {
        runOnUiThread(action);
    }

    @Override public void postDelayed(Runnable action, long delayed) {
        if (!isFinishing()) {
            QsHelper.postDelayed(action, delayed);
        }
    }

    @Override public void runOnWorkThread(Runnable action) {
        QsHelper.executeInWorkThread(action);
    }

    @Override public void runOnHttpThread(Runnable action) {
        QsHelper.executeInHttpThread(action);
    }

    @Override public FragmentActivity getActivity() {
        return this;
    }

    @Override public void smoothScrollToTop(boolean autoRefresh) {
        if (contentView == null) return;
        final ScrollView scrollView = (ScrollView) tryGetTargetView(ScrollView.class, contentView);
        if (scrollView != null) {
            scrollView.post(new Runnable() {
                @Override public void run() {
                    scrollView.smoothScrollTo(0, 0);
                }
            });
        }

        if (autoRefresh) {
            final PtrFrameLayout frameLayout = (PtrFrameLayout) tryGetTargetView(PtrFrameLayout.class, contentView);
            if (frameLayout != null) {
                frameLayout.post(new Runnable() {
                    @Override public void run() {
                        frameLayout.autoRefresh();
                    }
                });
            }
        }
    }

    private View tryGetTargetView(Class clazz, View parentView) {
        View targetView = null;
        if (parentView.getClass() == clazz) {
            targetView = parentView;
        } else {
            if (parentView instanceof ViewGroup) {
                int childCount = ((ViewGroup) parentView).getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View childAt = ((ViewGroup) parentView).getChildAt(i);
                    targetView = tryGetTargetView(clazz, childAt);
                    if (targetView != null) break;
                }
            }
        }
        return targetView;
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (activityResultListener != null) {
            activityResultListener.onActivityResult(this, requestCode, resultCode, data);
        }
    }

    @Override public void setOnActivityResultListener(OnActivityResultListener listener) {
        this.activityResultListener = listener;
    }

    @Override public void setOnKeyDownListener(OnKeyDownListener listener) {
        this.onKeyDownListener = listener;
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
