package com.qsmaxmin.qsbase.mvp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ViewAnimator;

import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.common.aspect.ThreadPoint;
import com.qsmaxmin.qsbase.common.aspect.ThreadType;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.utils.PresenterUtils;
import com.qsmaxmin.qsbase.common.utils.QsHelper;
import com.qsmaxmin.qsbase.common.viewbind.OnKeyDownListener;
import com.qsmaxmin.qsbase.common.viewbind.ViewBindHelper;
import com.qsmaxmin.qsbase.common.widget.dialog.QsProgressDialog;
import com.qsmaxmin.qsbase.mvp.fragment.QsIFragment;
import com.qsmaxmin.qsbase.mvp.model.QsConstants;
import com.qsmaxmin.qsbase.mvp.presenter.QsPresenter;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/6/20 16:40
 * @Description
 */
public abstract class QsActivity<P extends QsPresenter> extends FragmentActivity implements QsIActivity {
    private   P                 presenter;
    protected QsProgressDialog  mProgressDialog;
    protected ViewAnimator      mViewAnimator;
    private   boolean           hasInitData;
    private   OnKeyDownListener onKeyDownListener;

    @Override public String initTag() {
        return L.isEnable() ? getClass().getSimpleName() : "QsActivity";
    }


    @Override public int layoutId() {
        return R.layout.qs_framelayout;
    }

    @Override @CallSuper protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        QsHelper.getScreenHelper().pushActivity(this);
        QsHelper.getAppInterface().onActivityCreate(this);
        ViewBindHelper.bindBundle(this, getIntent().getExtras());
        initStatusBar();
        View view = initView();
        setContentView(view);
        ViewBindHelper.bindView(this, view);
        if (isOpenEventBus() && !EventBus.getDefault().isRegistered(this)) EventBus.getDefault().register(this);
        if (!isDelayData()) {
            hasInitData = true;
            initData(savedInstanceState);
        }
    }

    @Override protected void onStart() {
        super.onStart();
        QsHelper.getAppInterface().onActivityStart(this);
    }

    @CallSuper @Override protected void onResume() {
        super.onResume();
        QsHelper.getScreenHelper().bringActivityToTop(this);
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
        if (isOpenEventBus() && EventBus.getDefault().isRegistered(this)) EventBus.getDefault().unregister(this);
        QsHelper.getAppInterface().onActivityDestroy(this);
        QsHelper.getScreenHelper().popActivity(this);
    }


    protected View initView() {
        long s0 = 0;
        if (L.isEnable()) s0 = System.nanoTime();
        View rootView;
        LayoutInflater inflater = getLayoutInflater();
        if (isOpenViewState() && loadingLayoutId() != 0 && emptyLayoutId() != 0 && errorLayoutId() != 0) {
            rootView = View.inflate(this, rootViewLayoutId(), null);
            mViewAnimator = rootView.findViewById(android.R.id.home);
            initViewAnimator(mViewAnimator);

            View loadingView = onCreateLoadingView(inflater);
            loadingView.setTag(R.id.view_state_key, QsConstants.VIEW_STATE_LOADING);
            mViewAnimator.addView(loadingView, 0);

            View contentView = onCreateContentView(inflater);
            contentView.setTag(R.id.view_state_key, QsConstants.VIEW_STATE_CONTENT);
            mViewAnimator.addView(contentView, 1);
            if (L.isEnable()) {
                long s1 = System.nanoTime();
                L.i(initTag(), "initView...view inflate complete(viewState is open), use time:" + (s1 - s0) / 1000000f + "ms");
            }
        } else {
            rootView = onCreateContentView(inflater);
            if (L.isEnable()) {
                long s1 = System.nanoTime();
                L.i(initTag(), "initView...view inflate complete(viewState not open), use time:" + (s1 - s0) / 1000000f + "ms");
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

    protected int rootViewLayoutId() {
        return R.layout.qs_activity_state;
    }

    @Override public P getPresenter() {
        if (presenter == null) {
            synchronized (this) {
                if (presenter == null) {
                    presenter = PresenterUtils.createPresenter(this);
                    L.i(initTag(), "Presenter初始化完成...");
                }
            }
        }
        return presenter;
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

    @Override public boolean isOpenEventBus() {
        return false;
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

    @Override public int loadingLayoutId() {
        return QsHelper.getAppInterface().loadingLayoutId();
    }

    @Override public int emptyLayoutId() {
        return QsHelper.getAppInterface().emptyLayoutId();
    }

    @Override public int errorLayoutId() {
        return QsHelper.getAppInterface().errorLayoutId();
    }

    @Override public View onCreateLoadingView(@NonNull LayoutInflater inflater) {
        View view = inflater.inflate(loadingLayoutId(), null);
        setDefaultViewClickListener(view);
        return view;
    }

    @Override public View onCreateContentView(@NonNull LayoutInflater inflater) {
        return inflater.inflate(layoutId(), null);
    }

    @Override public View onCreateEmptyView(@NonNull LayoutInflater inflater) {
        View view = inflater.inflate(emptyLayoutId(), null);
        setDefaultViewClickListener(view);
        return view;
    }

    @Override public View onCreateErrorView(@NonNull LayoutInflater inflater) {
        View view = inflater.inflate(errorLayoutId(), null);
        setDefaultViewClickListener(view);
        return view;
    }

    /**
     * 重写该方法以便自定义进度条样式
     */
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

    @ThreadPoint(ThreadType.MAIN)
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

    @ThreadPoint(ThreadType.MAIN)
    @Override public void loadingClose() {
        if (mProgressDialog != null && mProgressDialog.isAdded()) mProgressDialog.dismissAllowingStateLoss();
    }

    @Override public void showLoadingView() {
        if (isOpenViewState() && mViewAnimator != null) {
            if (L.isEnable()) L.i(initTag(), "showLoadingView.........");
            setViewState(0);
        }
    }

    @Override public void showContentView() {
        if (isOpenViewState() && mViewAnimator != null) {
            if (L.isEnable()) L.i(initTag(), "showContentView.........");
            setViewState(1);
        }
    }

    @ThreadPoint(ThreadType.MAIN)
    @Override public void showEmptyView() {
        if (isOpenViewState() && mViewAnimator != null) {
            if (L.isEnable()) L.i(initTag(), "showEmptyView.........");
            int childCount = mViewAnimator.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = mViewAnimator.getChildAt(i);
                int index = (int) childAt.getTag(R.id.view_state_key);
                if (index == QsConstants.VIEW_STATE_EMPTY) {
                    setViewState(i);
                    return;
                }
            }
            View emptyView = onCreateEmptyView(getLayoutInflater());
            if (emptyView != null) {
                emptyView.setTag(R.id.view_state_key, QsConstants.VIEW_STATE_EMPTY);
                mViewAnimator.addView(emptyView);
                setViewState(childCount);
            }
        }
    }

    @ThreadPoint(ThreadType.MAIN)
    @Override public void showErrorView() {
        if (isOpenViewState() && mViewAnimator != null) {
            if (L.isEnable()) L.i(initTag(), "showErrorView.........");
            int childCount = mViewAnimator.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = mViewAnimator.getChildAt(i);
                int value = (int) childAt.getTag(R.id.view_state_key);
                if (value == QsConstants.VIEW_STATE_ERROR) {
                    setViewState(i);
                    return;
                }
            }
            View errorView = onCreateErrorView(getLayoutInflater());
            if (errorView != null) {
                errorView.setTag(R.id.view_state_key, QsConstants.VIEW_STATE_ERROR);
                mViewAnimator.addView(errorView);
                setViewState(childCount);
            }
        }
    }

    /**
     * return current showing view
     *
     * @return see {@link QsConstants#VIEW_STATE_LOADING ...}
     */
    @Override public int currentViewState() {
        if (isOpenViewState() && mViewAnimator != null) {
            int displayedIndex = mViewAnimator.getDisplayedChild();
            View childView = mViewAnimator.getChildAt(displayedIndex);
            return (int) childView.getTag(R.id.view_state_key);
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
                    if (inAnimId != 0 || outAnimId != 0) overridePendingTransition(inAnimId, outAnimId);
                } else {
                    startActivity(intent);
                    if (inAnimId != 0 || outAnimId != 0) overridePendingTransition(inAnimId, outAnimId);
                }
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
        QsHelper.getPermissionHelper().parsePermissionResultData(requestCode, permissions, grantResults, this);
    }

    @ThreadPoint(ThreadType.MAIN)
    protected void setViewState(int index) {
        if (mViewAnimator.getDisplayedChild() != index) {
            mViewAnimator.setDisplayedChild(index);
        }
    }

    @ThreadPoint(ThreadType.MAIN)
    @Override public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * onKeyDown事件处理顺序：
     * 1，优先onKeyDownListener处理
     * 2，将onKeyDown事件传递到当前展示的Fragment
     * 3，重写onKeyDown处理
     */
    @Override public final boolean onKeyDown(int keyCode, KeyEvent event) {
        if (onKeyDownListener != null && onKeyDownListener.onKeyDown(keyCode, event)) return true;
        @SuppressLint("RestrictedApi") List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        if (!fragmentList.isEmpty()) {
            int size = fragmentList.size();
            for (int i = size - 1; i >= 0; i--) {
                Fragment fragment = fragmentList.get(i);
                if (fragment != null && !fragment.isDetached() && fragment.isResumed() && fragment.isAdded() && fragment instanceof QsIFragment) {
                    L.i(initTag(), "onKeyDown... resume fragment:" + fragment.getClass().getSimpleName() + "  isDetach:" + fragment.isDetached() + "  isAdded:" + fragment.isAdded() + "  isResumed:" + fragment.isResumed());
                    boolean isIntercept = ((QsIFragment) fragment).onKeyDown(keyCode, event);
                    if (isIntercept) {
                        L.i(initTag(), "onKeyDown... Fragment:" + fragment.getClass().getSimpleName() + " 已拦截onKeyDown事件...");
                        return true;
                    }
                    break;
                }
            }
        }
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
            } else {
                L.e(initTag(), "当前Android SDK版本太低(" + Build.VERSION.SDK_INT + ")，只有SDK版本 >= KITKAT才支持透明状态栏，推荐在actionbarLayoutId()方法中根据该条件给出不同高度的布局");
            }
        } else {
            if (isBlackIconStatusBar() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Window window = getWindow();
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
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
}
