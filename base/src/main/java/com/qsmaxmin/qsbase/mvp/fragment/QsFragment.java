package com.qsmaxmin.qsbase.mvp.fragment;

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
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.utils.QsHelper;
import com.qsmaxmin.qsbase.common.widget.dialog.QsProgressDialog;
import com.qsmaxmin.qsbase.common.widget.headerview.ScrollerProvider;
import com.qsmaxmin.qsbase.common.widget.ptr.PtrFrameLayout;
import com.qsmaxmin.qsbase.mvp.OnActivityResultListener;
import com.qsmaxmin.qsbase.mvp.QsIActivity;
import com.qsmaxmin.qsbase.mvp.presenter.QsPresenter;
import com.qsmaxmin.qsbase.plugin.threadpoll.QsThreadPollHelper;

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
 * @Date 2017/6/21 11:40
 * @Description
 */
public abstract class QsFragment<P extends QsPresenter> extends Fragment implements QsIFragment, ScrollerProvider, View.OnTouchListener {
    private   P                        presenter;
    private   boolean                  hasInitData;
    protected QsProgressDialog         mProgressDialog;
    protected ViewAnimator             mViewAnimator;
    private   OnActivityResultListener activityResultListener;

    @Override public String initTag() {
        return L.isEnable() ? getClass().getSimpleName() : "QsFragment";
    }

    @Override public int rootViewLayoutId() {
        return isOpenViewState() ? R.layout.qs_view_animator : R.layout.qs_frame_layout;
    }

    @Override @Nullable @CallSuper
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bindBundleByQsPlugin(getArguments());
        View rootView = initView(inflater);
        bindViewByQsPlugin(rootView);
        rootView.setOnTouchListener(this);
        onViewCreated(rootView);
        bindEventByQsPlugin();
        return rootView;
    }

    @CallSuper @Override public void bindBundleByQsPlugin(Bundle bundle) {
    }

    @CallSuper @Override public void bindViewByQsPlugin(View view) {
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
        if (presenter != null) {
            presenter.setDetach();
            presenter = null;
        }
        if (mProgressDialog != null) {
            mProgressDialog.dismissAllowingStateLoss();
            mProgressDialog = null;
        }
        mViewAnimator = null;
        unbindEventByQsPlugin();
    }

    @Override public void onViewCreated(View view) {
        //custom your logic
    }

    protected View initView(LayoutInflater inflater) {
        long s0 = 0;
        if (L.isEnable()) s0 = System.nanoTime();
        View rootView = inflater.inflate(rootViewLayoutId(), null);
        if (isOpenViewState()) {
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
                L.i(initTag(), "initView...view inflate complete(viewState is open), use time:" + (s1 - s0) / 1000000f + "ms");
            }

        } else {
            ViewGroup customView = rootView.findViewById(android.R.id.custom);
            View contentView = inflater.inflate(layoutId(), customView, false);
            customView.addView(contentView);
            onCreateContentView(contentView);

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

    @Override public void initDataWhenDelay() {
        if (!hasInitData && isDelayData()) {
            initData(getArguments());
            hasInitData = true;
        }
    }

    @Override public void onViewClick(View view) {
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

    @Override public void onBackPressed() {
        FragmentActivity activity = getActivity();
        if (activity != null) activity.onBackPressed();
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
                QsHelper.commitDialogFragment(getFragmentManager(), mProgressDialog);
            }
        } else {
            L.e(initTag(), "you should override the method 'Application.getLoadingDialog() or this.getLoadingDialog()' and return a dialog when called the method : loading(...) ");
        }
    }

    @Override public final void loadingClose() {
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

    @Override public final void intent2Activity(Class clazz, Bundle bundle, int requestCode, ActivityOptionsCompat optionsCompat, int inAnimId, int outAnimId) {
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
                if (inAnimId != 0 || outAnimId != 0) activity.overridePendingTransition(inAnimId, outAnimId);
            } else {
                if (requestCode > 0) {
                    ActivityCompat.startActivityForResult(activity, intent, requestCode, optionsCompat.toBundle());
                } else {
                    ActivityCompat.startActivity(activity, intent, optionsCompat.toBundle());
                }
            }
        }
    }

    @Override public final void commitFragment(Fragment fragment) {
        commitFragment(fragment, fragment.getClass().getSimpleName());
    }

    @Override public final void commitFragment(Fragment fragment, String tag) {
        commitFragment(android.R.id.custom, fragment, tag);
    }

    @Override public final void commitFragment(int layoutId, Fragment fragment) {
        commitFragment(layoutId, fragment, fragment.getClass().getSimpleName());
    }

    @Override public final void commitFragment(int layoutId, Fragment fragment, String tag) {
        QsHelper.commitFragment(getFragmentManager(), layoutId, fragment, tag);
    }

    @Override public final void commitFragment(Fragment old, Fragment fragment) {
        commitFragment(old, fragment, fragment.getClass().getSimpleName());
    }

    @Override public final void commitFragment(Fragment old, Fragment fragment, String tag) {
        commitFragment(old, android.R.id.custom, fragment, tag);
    }

    @Override public final void commitFragment(Fragment old, int layoutId, Fragment fragment) {
        commitFragment(old, layoutId, fragment, fragment.getClass().getSimpleName());
    }

    @Override public final void commitFragment(Fragment old, int layoutId, Fragment fragment, String tag) {
        QsHelper.commitFragment(getFragmentManager(), old, layoutId, fragment, tag);
    }

    @Override public final void commitBackStackFragment(Fragment fragment) {
        commitBackStackFragment(fragment, fragment.getClass().getSimpleName());
    }

    @Override public final void commitBackStackFragment(Fragment fragment, String tag) {
        commitBackStackFragment(android.R.id.custom, fragment, tag);
    }

    @Override public final void commitBackStackFragment(int layoutId, Fragment fragment) {
        commitBackStackFragment(layoutId, fragment, fragment.getClass().getSimpleName());
    }

    @Override public final void commitBackStackFragment(Fragment fragment, int enterAnim, int exitAnim) {
        QsHelper.commitBackStackFragment(fragment, enterAnim, exitAnim);
    }

    @Override public final void commitBackStackFragment(int layoutId, Fragment fragment, String tag) {
        QsHelper.commitBackStackFragment(getFragmentManager(), layoutId, fragment, tag);
    }

    @Override public final void commitDialogFragment(DialogFragment fragment) {
        QsHelper.commitDialogFragment(getFragmentManager(), fragment);
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
        return shouldInterceptTouchEvent();
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

    @Override public boolean shouldInterceptTouchEvent() {
        return true;
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
}
