package com.qsmaxmin.qsbase.mvp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewAnimator;

import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.common.aspect.ThreadPoint;
import com.qsmaxmin.qsbase.common.aspect.ThreadType;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.utils.PresenterUtils;
import com.qsmaxmin.qsbase.common.utils.QsHelper;
import com.qsmaxmin.qsbase.common.widget.dialog.QsProgressDialog;
import com.qsmaxmin.qsbase.mvp.QsIABActivity;
import com.qsmaxmin.qsbase.mvp.model.QsConstants;
import com.qsmaxmin.qsbase.mvp.presenter.QsPresenter;

import org.greenrobot.eventbus.EventBus;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/6/21 11:40
 * @Description
 */

public abstract class QsFragment<P extends QsPresenter> extends Fragment implements QsIFragment, View.OnTouchListener {

    private   P                presenter;
    private   boolean          hasInitData;
    protected QsProgressDialog mProgressDialog;
    protected ViewAnimator     mViewAnimator;

    @Override public String initTag() {
        return getClass().getSimpleName();
    }

    @Override public void setActivityTitle(Object value) {
        setActivityTitle(value, -1);
    }

    @Override public void setActivityTitle(Object value, int code) {
        FragmentActivity activity = getActivity();
        if (activity instanceof QsIABActivity) {
            ((QsIABActivity) activity).setActivityTitle(value, code);
            L.i(initTag(), "setActivityTitle()  title=" + String.valueOf(value) + "  code=" + code);
        }
    }

    @Override public void onActionBar() {
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = initView(inflater);
        QsHelper.getInstance().getViewBindHelper().bind(this, rootView);
        rootView.setOnTouchListener(this);
        if (isOpenEventBus() && !EventBus.getDefault().isRegistered(this)) EventBus.getDefault().register(this);
        return rootView;
    }

    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!isDelayData()) {
            hasInitData = true;
            initData(savedInstanceState);
        }
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        onActionBar();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        if (presenter != null) {
            presenter.setDetach();
            presenter = null;
        }
        if (mProgressDialog != null) {
            mProgressDialog.dismissAllowingStateLoss();
            mProgressDialog = null;
        }
        mViewAnimator = null;
        if (isOpenEventBus() && EventBus.getDefault().isRegistered(this)) EventBus.getDefault().unregister(this);
    }

    protected View initView(LayoutInflater inflater) {
        View view;
        if (isOpenViewState() && loadingLayoutId() > 0 && emptyLayoutId() > 0 && errorLayoutId() > 0) {
            view = inflater.inflate(rootViewLayoutId(), null);
            mViewAnimator = (ViewAnimator) view.findViewById(android.R.id.home);
            inflater.inflate(loadingLayoutId(), mViewAnimator);
            inflater.inflate(layoutId(), mViewAnimator);
            inflater.inflate(emptyLayoutId(), mViewAnimator);
            inflater.inflate(errorLayoutId(), mViewAnimator);
            initDefaultView();
        } else {
            view = inflater.inflate(layoutId(), null);
        }
        return view;
    }

    protected int rootViewLayoutId() {
        return R.layout.qs_fragment_state;
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
            initData(getArguments());
            hasInitData = true;
        }
    }

    @Override public void onViewClick(View view) {

    }

    @Override public boolean isOpenEventBus() {
        return false;
    }

    @Override public boolean isOpenViewState() {
        return true;
    }

    @Override public boolean isDelayData() {
        return false;
    }

    @Override public void activityFinish() {
        activityFinish(false);
    }

    @Override public void activityFinish(boolean finishAfterTransition) {
        if (finishAfterTransition) {
            ActivityCompat.finishAfterTransition(getActivity());
        } else {
            getActivity().finish();
        }
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

    @Override public void loading(String message) {
        loading(message, true);
    }

    @Override public void loading(int resId) {
        loading(resId, true);
    }

    @Override public void loading(int resId, boolean cancelAble) {
        loading(getResources().getString(resId), cancelAble);
    }

    @ThreadPoint(ThreadType.MAIN) @Override public void loading(String message, boolean cancelAble) {
        if (mProgressDialog == null) mProgressDialog = QsHelper.getInstance().getApplication().getCommonProgressDialog();
        if (mProgressDialog != null) {
            mProgressDialog.setMessage(message);
            mProgressDialog.setCancelable(cancelAble);
            if (!mProgressDialog.isAdded()) {
                QsHelper.getInstance().commitDialogFragment(getFragmentManager(), mProgressDialog);
            }
        } else {
            L.e(initTag(), "you should override the method 'Application.getCommonProgressDialog' and return a dialog when called the method : loading(...) ");
        }
    }

    @ThreadPoint(ThreadType.MAIN) @Override public void loadingClose() {
        if (mProgressDialog != null && mProgressDialog.isAdded()) mProgressDialog.dismissAllowingStateLoss();
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
        FragmentActivity activity = getActivity();
        if (clazz != null && activity != null) {
            Intent intent = new Intent();
            intent.setClass(activity, clazz);
            if (bundle != null) intent.putExtras(bundle);
            if (optionsCompat == null) {
                if (requestCode > 0) {
                    startActivityForResult(intent, requestCode);
                } else {
                    startActivity(intent);
                }
            } else {
                if (requestCode > 0) {
                    ActivityCompat.startActivityForResult(activity, intent, requestCode, optionsCompat.toBundle());
                } else {
                    ActivityCompat.startActivity(activity, intent, optionsCompat.toBundle());
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

    @ThreadPoint(ThreadType.MAIN) @Override public void commitFragment(int layoutId, Fragment fragment, String tag) {
        QsHelper.getInstance().commitFragment(getFragmentManager(), layoutId, fragment, tag);
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

    @ThreadPoint(ThreadType.MAIN) @Override public void commitFragment(Fragment old, int layoutId, Fragment fragment, String tag) {
        QsHelper.getInstance().commitFragment(getFragmentManager(), old, layoutId, fragment, tag);
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

    @ThreadPoint(ThreadType.MAIN) @Override public void commitBackStackFragment(int layoutId, Fragment fragment, String tag) {
        QsHelper.getInstance().commitBackStackFragment(getFragmentManager(), layoutId, fragment, tag);
    }

    @ThreadPoint(ThreadType.MAIN) protected void setViewState(int showState) {
        L.i(initTag(), "setViewState() showState=" + showState);
        if (!isOpenViewState()) {
            L.i(initTag(), "当前Fragment没有打开状态模式! isOpenViewState() = false");
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
                            getActivity().onBackPressed();
                        }
                    });
                } else {
                    backView.setVisibility(View.GONE);
                }
            }
            View reloadView = view.findViewById(R.id.qs_reload_in_default_view);
            if (reloadView != null) {
                reloadView.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        showLoadingView();
                        initData(getArguments());
                    }
                });
            }
        }
    }

    @Override public void onFragmentSelectedInViewPager(boolean isSelected, int currentPosition, int totalCount) {
        L.i(initTag(), "onFragmentSelectedInViewPager..... isSelected:" + isSelected + "  currentPosition:" + currentPosition + "  totalCount:" + totalCount);
    }

    @Override public boolean onTouch(View v, MotionEvent event) {
        return shouldInterceptTouchEvent();
    }

    @Override public boolean shouldInterceptTouchEvent() {
        return true;
    }

    @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    @Override public boolean isShowBackButtonInDefaultView() {
        return false;
    }
}
