package com.qsmaxmin.qsbase.mvp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ScrollView;
import android.widget.ViewAnimator;

import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.common.aspect.ThreadPoint;
import com.qsmaxmin.qsbase.common.aspect.ThreadType;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.utils.PresenterUtils;
import com.qsmaxmin.qsbase.common.utils.QsHelper;
import com.qsmaxmin.qsbase.common.viewbind.ViewBindHelper;
import com.qsmaxmin.qsbase.common.widget.dialog.QsProgressDialog;
import com.qsmaxmin.qsbase.common.widget.ptr.PtrFrameLayout;
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
        return L.isEnable() ? getClass().getSimpleName() : "QsFragment";
    }

    @Override public void setActivityTitle(Object value) {
        setActivityTitle(value, -1);
    }

    @Override @ThreadPoint(ThreadType.MAIN) public void setActivityTitle(Object value, int code) {
        FragmentActivity activity = getActivity();
        if (activity instanceof QsIABActivity) {
            L.i(initTag(), "setActivityTitle(" + value + ", " + code + ")");
            ((QsIABActivity) activity).setActivityTitle(value, code);
        }
    }

    @Override public void onActionBar() {
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override @Nullable @CallSuper public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewBindHelper.bindBundle(this, getArguments());
        View rootView = initView(inflater);
        ViewBindHelper.bindView(this, rootView);
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

    @Override @CallSuper public void onDestroyView() {
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
        long s0 = 0;
        if (L.isEnable()) s0 = System.nanoTime();
        View view;
        if (isOpenViewState() && loadingLayoutId() != 0 && emptyLayoutId() != 0 && errorLayoutId() != 0) {
            view = inflater.inflate(rootViewLayoutId(), null);
            mViewAnimator = view.findViewById(android.R.id.home);
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
            view = onCreateContentView(inflater);
            if (L.isEnable()) {
                long s1 = System.nanoTime();
                L.i(initTag(), "initView...view inflate complete(viewState not open), use time:" + (s1 - s0) / 1000000f + "ms");
            }
        }
        return view;
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

    @Override public void onBackPressed() {
        FragmentActivity activity = getActivity();
        if (activity != null) activity.onBackPressed();
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
                QsHelper.commitDialogFragment(getFragmentManager(), mProgressDialog);
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
        if (isOpenViewState() && mViewAnimator != null && !isDetached()) {
            if (L.isEnable()) L.i(initTag(), "showLoadingView.........");
            setViewState(0);
        }
    }

    @Override public void showContentView() {
        if (isOpenViewState() && mViewAnimator != null && !isDetached()) {
            if (L.isEnable()) L.i(initTag(), "showContentView.........");
            setViewState(1);
        }
    }

    @ThreadPoint(ThreadType.MAIN)
    @Override public void showEmptyView() {
        if (isOpenViewState() && mViewAnimator != null && !isDetached()) {
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
        if (isOpenViewState() && mViewAnimator != null && !isDetached()) {
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
        FragmentActivity activity = getActivity();
        if (clazz != null && activity != null) {
            Intent intent = new Intent();
            intent.setClass(activity, clazz);
            if (bundle != null) intent.putExtras(bundle);
            if (optionsCompat == null) {
                if (requestCode > 0) {
                    startActivityForResult(intent, requestCode);
                    if (inAnimId != 0 || outAnimId != 0) activity.overridePendingTransition(inAnimId, outAnimId);
                } else {
                    startActivity(intent);
                    if (inAnimId != 0 || outAnimId != 0) activity.overridePendingTransition(inAnimId, outAnimId);
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
        QsHelper.commitFragment(getFragmentManager(), layoutId, fragment, tag);
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

    @ThreadPoint(ThreadType.MAIN)
    @Override public void commitFragment(Fragment old, int layoutId, Fragment fragment, String tag) {
        QsHelper.commitFragment(getFragmentManager(), old, layoutId, fragment, tag);
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
        QsHelper.commitBackStackFragment(getFragmentManager(), layoutId, fragment, tag);
    }

    @Override public void commitDialogFragment(DialogFragment fragment) {
        QsHelper.commitDialogFragment(getFragmentManager(), fragment);
    }

    @ThreadPoint(ThreadType.MAIN)
    private void setViewState(int index) {
        if (mViewAnimator.getDisplayedChild() != index) {
            mViewAnimator.setDisplayedChild(index);
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
                            if (getActivity() != null) getActivity().onBackPressed();
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

    @Override public void smoothScrollToTop(boolean autoRefresh) {
        View view = getView();
        if (view == null) return;
        final ScrollView scrollView = (ScrollView) tryGetTargetView(ScrollView.class, view);
        if (scrollView != null) {
            scrollView.post(new Runnable() {
                @Override public void run() {
                    scrollView.smoothScrollTo(0, 0);
                }
            });
        }

        if (autoRefresh) {
            final PtrFrameLayout frameLayout = (PtrFrameLayout) tryGetTargetView(PtrFrameLayout.class, view);
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

    @Override public void post(Runnable action) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(action);
        }
    }

    @Override public void runOnWorkThread(Runnable action) {
        QsHelper.getThreadHelper().getWorkThreadPoll().execute(action);
    }

    @Override public void runOnHttpThread(Runnable action) {
        QsHelper.getThreadHelper().getHttpThreadPoll().execute(action);
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
