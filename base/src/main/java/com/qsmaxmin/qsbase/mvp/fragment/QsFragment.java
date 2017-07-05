package com.qsmaxmin.qsbase.mvp.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewAnimator;

import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.utils.PresenterUtils;
import com.qsmaxmin.qsbase.common.utils.QsHelper;
import com.qsmaxmin.qsbase.mvp.QsIABActivity;
import com.qsmaxmin.qsbase.mvp.model.QsConstants;
import com.qsmaxmin.qsbase.mvp.presenter.QsPresenter;

import org.greenrobot.eventbus.EventBus;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/6/21 11:40
 * @Description
 */

public abstract class QsFragment<P extends QsPresenter> extends Fragment implements QsIFragment {

    private   P              presenter;
    private   boolean        hasInitData;
    protected ProgressDialog mProgressDialog;
    protected ViewAnimator   mViewAnimator;


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
        View view = initView(inflater);
//        ButterKnife.bind(this, view);
        QsHelper.getInstance().getViewBindHelper().bind(this, view);
        if (isOpenEventBus() && !EventBus.getDefault().isRegistered(this)) EventBus.getDefault().register(this);
        return view;
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
        if (presenter != null) presenter.setDetach();
        if (isOpenEventBus() && EventBus.getDefault().isRegistered(this)) EventBus.getDefault().unregister(this);
//        ButterKnife.unbind(this);
    }

    protected View initView(LayoutInflater inflater) {
        View rootView;
        if (isOpenViewState() && loadingLayoutId() > 0 && emptyLayoutId() > 0 && errorLayoutId() > 0) {
            rootView = inflater.inflate(R.layout.qs_activity_state, null);
            mViewAnimator = (ViewAnimator) rootView.findViewById(android.R.id.home);
            inflater.inflate(loadingLayoutId(), mViewAnimator);
            inflater.inflate(layoutId(), mViewAnimator);
            inflater.inflate(emptyLayoutId(), mViewAnimator);
            inflater.inflate(errorLayoutId(), mViewAnimator);
        } else {
            rootView = inflater.inflate(layoutId(), null);
        }
        return rootView;
    }

    @Override public P getPresenter() {
        if (presenter == null) {
            synchronized (this) {
                if (presenter == null) {
                    presenter = PresenterUtils.createPresenter(getClass(), this);
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

    @Override public void loading(String message, boolean cancelAble) {
        if (mProgressDialog == null) mProgressDialog = QsHelper.getInstance().getApplication().getCommonProgressDialog(getContext());
        mProgressDialog.setMessage(message);
        mProgressDialog.setCancelable(cancelAble);
        mProgressDialog.show();
    }

    @Override public void loadingClose() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) mProgressDialog.cancel();
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
        intent2Activity(clazz, null, 0);
    }

    @Override public void intent2Activity(Class clazz, int requestCode) {
        intent2Activity(clazz, null, requestCode);
    }

    @Override public void intent2Activity(Class clazz, Bundle bundle) {
        intent2Activity(clazz, bundle, 0);
    }

    @Override public void intent2Activity(Class clazz, Bundle bundle, int requestCode) {
        Context context = getContext();
        if (context != null && clazz != null) {
            Intent intent = new Intent();
            intent.setClass(context, clazz);
            if (bundle != null) intent.putExtras(bundle);
            if (requestCode > 0) {
                startActivityForResult(intent, requestCode);
            } else {
                startActivity(intent);
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
        if (fragment != null && fragment.isAdded()) {
            return;
        }
        getFragmentManager().beginTransaction().add(layoutId, fragment, tag).setTransition(FragmentTransaction.TRANSIT_NONE).commitAllowingStateLoss();
        if (!isOpenViewState()) getFragmentManager().executePendingTransactions();
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
        if (layoutId == 0) return;
        if (fragment != null && fragment.isAdded()) return;
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        if (old != null) fragmentTransaction.detach(old);
        fragmentTransaction.add(layoutId, fragment, tag).setTransition(FragmentTransaction.TRANSIT_NONE).commitAllowingStateLoss();
        if (!isOpenViewState()) getFragmentManager().executePendingTransactions();
    }

    @Override public void commitBackStackFragment(Fragment fragment) {
        commitBackStackFragment(fragment, fragment.getClass().getSimpleName());
    }

    @Override public void commitBackStackFragment(Fragment fragment, String tag) {
        if (fragment != null && fragment.isAdded()) return;
        getFragmentManager().beginTransaction().add(android.R.id.custom, fragment, tag).addToBackStack(null).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commitAllowingStateLoss();
        if (!isOpenViewState()) getFragmentManager().executePendingTransactions();
    }

    @Override public void commitBackStackFragment(int layoutId, Fragment fragment) {
        commitBackStackFragment(layoutId, fragment, fragment.getClass().getSimpleName());

    }

    @Override public void commitBackStackFragment(int layoutId, Fragment fragment, String tag) {
        if (layoutId == 0) return;
        if (fragment != null && fragment.isAdded()) return;
        getFragmentManager().beginTransaction().add(layoutId, fragment, tag).addToBackStack(null).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commitAllowingStateLoss();
        if (!isOpenViewState()) getFragmentManager().executePendingTransactions();
    }

    private void setViewState(int showState) {
        L.i(initTag(), "setViewState() showState=" + showState);
        if (!isOpenViewState()) {
            L.i(initTag(), "当前activity没有打开状态模式! isOpenViewState() = false");
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
        if (showState == QsConstants.VIEW_STATE_ERROR) {
            mViewAnimator.getCurrentView().setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    showLoadingView();
                    initData(getArguments());
                }
            });
        }
    }
}
