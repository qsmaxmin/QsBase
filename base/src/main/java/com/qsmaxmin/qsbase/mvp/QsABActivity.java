package com.qsmaxmin.qsbase.mvp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ViewAnimator;

import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.common.aspect.ThreadPoint;
import com.qsmaxmin.qsbase.common.aspect.ThreadType;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.utils.PresenterUtils;
import com.qsmaxmin.qsbase.common.utils.QsHelper;
import com.qsmaxmin.qsbase.common.utils.permission.PermissionUtils;
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
public abstract class QsABActivity<P extends QsPresenter> extends AppCompatActivity implements QsIABActivity {
    private   P                presenter;
    protected QsProgressDialog mProgressDialog;
    private   ViewAnimator     mViewAnimator;
    private   boolean          hasInitData;

    @Override public String initTag() {
        return getClass().getSimpleName();
    }

    @Override public void setActivityTitle(Object value, int code) {

    }

    @Override public int layoutId() {
        return R.layout.qs_framelayout;
    }


    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        QsHelper.getInstance().getScreenHelper().pushActivity(this);
        QsHelper.getInstance().getApplication().onActivityCreate(this);
        initStatusBar();
        View view = initView();
        setContentView(view);
        QsHelper.getInstance().getViewBindHelper().bind(this, view);
        if (isOpenEventBus() && !EventBus.getDefault().isRegistered(this)) EventBus.getDefault().register(this);
        if (!isDelayData()) {
            hasInitData = true;
            initData(savedInstanceState);
        }
    }

    @Override protected void onStart() {
        super.onStart();
        QsHelper.getInstance().getApplication().onActivityStart(this);
    }

    @Override protected void onResume() {
        super.onResume();
        QsHelper.getInstance().getApplication().onActivityResume(this);
    }

    @Override protected void onPause() {
        super.onPause();
        QsHelper.getInstance().getApplication().onActivityPause(this);
    }

    @Override protected void onStop() {
        super.onStop();
        QsHelper.getInstance().getApplication().onActivityStop(this);
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.setDetach();
            presenter = null;
        }
        if (isOpenEventBus() && EventBus.getDefault().isRegistered(this)) EventBus.getDefault().unregister(this);
        QsHelper.getInstance().getApplication().onActivityDestroy(this);
        QsHelper.getInstance().getScreenHelper().popActivity(this);
    }


    protected View initView() {
        View rootView;
        Toolbar mToolbar;
        if (isOpenViewState() && loadingLayoutId() > 0 && emptyLayoutId() > 0 && errorLayoutId() > 0) {
            rootView = View.inflate(this, rootViewLayoutId(), null);
            mViewAnimator = (ViewAnimator) rootView.findViewById(android.R.id.home);
            mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
            View.inflate(this, loadingLayoutId(), mViewAnimator);
            View.inflate(this, layoutId(), mViewAnimator);
            View.inflate(this, emptyLayoutId(), mViewAnimator);
            View.inflate(this, errorLayoutId(), mViewAnimator);
            initDefaultView();
        } else {
            rootView = View.inflate(this, R.layout.qs_activity_ab, null);
            mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
            ViewGroup mainView = (ViewGroup) rootView.findViewById(android.R.id.home);
            View.inflate(this, layoutId(), mainView);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) mToolbar.setElevation(0);
        if (actionbarLayoutId() > 0) {
            ViewGroup actionbarContainer = (ViewGroup) mToolbar.findViewById(R.id.vg_toolbar);
            View.inflate(getContext(), actionbarLayoutId(), actionbarContainer);
        }
        setSupportActionBar(mToolbar);
        return rootView;
    }

    protected int rootViewLayoutId() {
        return R.layout.qs_activity_ab_state;
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

    @Override public boolean isDelayData() {
        return false;
    }

    @Override public void activityFinish() {
        activityFinish(false);
    }

    @Override public void activityFinish(boolean finishAfterTransition) {
        if (finishAfterTransition) ActivityCompat.finishAfterTransition(this);
        else finish();
    }

    @Override public int loadingLayoutId() {
        return QsHelper.getInstance().getApplication().loadingLayoutId();
    }

    @Override public int emptyLayoutId() {
        return QsHelper.getInstance().getApplication().emptyLayoutId();
    }

    @Override public int errorLayoutId() {
        return QsHelper.getInstance().getApplication().errorLayoutId();
    }

    @Override public void loading() {
        loading(true);
    }

    @Override public void loading(boolean cancelAble) {
        loading(getString(R.string.loading), cancelAble);
    }

    @Override public void loading(int resId) {
        loading(resId, true);
    }

    @Override public void loading(int resId, boolean cancelAble) {
        loading(getResources().getString(resId), cancelAble);
    }

    @Override public void loading(String message) {
        loading(message, true);
    }

    @ThreadPoint(ThreadType.MAIN) @Override public void loading(String message, boolean cancelAble) {
        if (mProgressDialog == null) mProgressDialog = QsHelper.getInstance().getApplication().getCommonProgressDialog();
        if (mProgressDialog != null) {
            mProgressDialog.setMessage(message);
            mProgressDialog.setCancelable(cancelAble);
            QsHelper.getInstance().commitDialogFragment(mProgressDialog);
        } else {
            L.e(initTag(), "you should override the method 'Application.getCommonProgressDialog' and return a dialog when called the method : loading(...) ");
        }
    }

    @ThreadPoint(ThreadType.MAIN) @Override public void loadingClose() {
        if (mProgressDialog != null) mProgressDialog.dismissAllowingStateLoss();
    }

    @Override public void showLoadingView() {
        setViewState(QsConstants.VIEW_STATE_LOADING);
    }

    @Override public void showContentView() {
        setViewState(QsConstants.VIEW_STATE_CONTENT);
    }

    @Override public void showEmptyView() {
        setViewState(QsConstants.VIEW_STATE_EMPTY);
    }

    @Override public void showErrorView() {
        setViewState(QsConstants.VIEW_STATE_ERROR);
    }

    @Override public int currentViewState() {
        if (isOpenViewState() && mViewAnimator != null) {
            return mViewAnimator.getDisplayedChild();
        }
        return -1;
    }

    @Override public void intent2Activity(Class clazz) {
        intent2Activity(clazz, null, 0, null);
    }

    @Override public void intent2Activity(Class clazz, int requestCode) {
        intent2Activity(clazz, null, requestCode, null);
    }

    @Override public void intent2Activity(Class clazz, Bundle bundle) {
        intent2Activity(clazz, bundle, 0, null);
    }

    @Override public void intent2Activity(Class clazz, Bundle bundle, ActivityOptionsCompat optionsCompat) {
        intent2Activity(clazz, bundle, 0, optionsCompat);
    }

    @Override public void intent2Activity(Class clazz, Bundle bundle, int requestCode, ActivityOptionsCompat optionsCompat) {
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
        QsHelper.getInstance().commitFragment(getSupportFragmentManager(), layoutId, fragment, tag);
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
        QsHelper.getInstance().commitFragment(getSupportFragmentManager(), old, layoutId, fragment, tag);
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

    @Override public void commitBackStackFragment(int layoutId, Fragment fragment, String tag) {
        QsHelper.getInstance().commitBackStackFragment(getSupportFragmentManager(), layoutId, fragment, tag);
    }

    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionUtils.getInstance().parsePermissionResultData(requestCode, permissions, grantResults, this);
    }

    @ThreadPoint(ThreadType.MAIN) protected void setViewState(int showState) {
        L.i(initTag(), "setViewState() showState=" + showState);
        if (!isOpenViewState()) {
            L.i(initTag(), "当前Activity没有打开状态模式! isOpenViewState() = false");
            return;
        }
        if (mViewAnimator == null) {
            return;
        }
        int displayedChild = mViewAnimator.getDisplayedChild();
        if (displayedChild == showState) {
            return;
        }
        mViewAnimator.setDisplayedChild(showState);
    }

    @ThreadPoint(ThreadType.MAIN) @Override public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * 将onKeyDown事件传递到当前展示的Fragment
     */
    @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
        @SuppressLint("RestrictedApi") List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        if (fragmentList != null && !fragmentList.isEmpty()) {
            int size = fragmentList.size();
            for (int i = size - 1; i >= 0; i--) {
                Fragment fragment = fragmentList.get(i);
                if (fragment != null && !fragment.isDetached() && fragment.isResumed() && fragment.isAdded() && fragment instanceof QsIFragment) {
                    L.i(initTag(), "onKeyDown... Fragment:" + fragment.getClass().getSimpleName() + "  isDetach:" + fragment.isDetached() + "  isAdded:" + fragment.isAdded() + "  isResumed:" + fragment.isResumed());
                    boolean isIntercept = ((QsIFragment) fragment).onKeyDown(keyCode, event);
                    if (isIntercept) {
                        L.i(initTag(), "onKeyDown... Fragment:" + fragment.getClass().getSimpleName() + " 已拦截onKeyDown事件...");
                        return true;
                    }
                    break;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initDefaultView() {
        if (mViewAnimator != null && mViewAnimator.getChildCount() >= 4) {
            setDefaultViewClickListener(mViewAnimator.getChildAt(0));
            setDefaultViewClickListener(mViewAnimator.getChildAt(2));
            setDefaultViewClickListener(mViewAnimator.getChildAt(3));
        }
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
        if (isTransparentStatusBar()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && isBlackIconStatusBar()) {
                    window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                }
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
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
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }
        }
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
}
