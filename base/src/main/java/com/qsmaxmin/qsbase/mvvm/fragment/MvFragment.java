package com.qsmaxmin.qsbase.mvvm.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ScrollView;
import android.widget.ViewAnimator;

import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.common.http.HttpCall;
import com.qsmaxmin.qsbase.common.http.HttpCallback;
import com.qsmaxmin.qsbase.common.http.HttpHelper;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.utils.QsHelper;
import com.qsmaxmin.qsbase.common.utils.ViewHelper;
import com.qsmaxmin.qsbase.common.viewbind.OnActivityResultListener;
import com.qsmaxmin.qsbase.common.widget.dialog.ProgressView;
import com.qsmaxmin.qsbase.common.widget.dialog.QsProgressDialog;
import com.qsmaxmin.qsbase.common.widget.headerview.ScrollerProvider;
import com.qsmaxmin.qsbase.common.widget.ptr.PtrFrameLayout;
import com.qsmaxmin.qsbase.mvvm.MvIActivity;
import com.qsmaxmin.qsbase.plugin.threadpoll.QsThreadPollHelper;

import java.util.ArrayList;
import java.util.List;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/6/21 11:40
 * @Description
 */
public abstract class MvFragment extends Fragment implements MvIFragment, ScrollerProvider {
    private   boolean                        hasInitData;
    protected ViewAnimator                   mViewAnimator;
    private   OnActivityResultListener       activityResultListener;
    private   List<OnActivityResultListener> resultListenerList;
    private   ProgressView                   progressView;
    private   boolean                        isViewCreated;

    @Override public final String initTag() {
        return L.isEnable() ? getClass().getSimpleName() : "QsFragment";
    }

    @Override public int rootViewLayoutId() {
        if (isActionbarAtTheTopLevel()) {
            return isOpenViewState() ? R.layout.qs_fragment_animator : R.layout.qs_fragment;
        } else {
            return isOpenViewState() ? R.layout.qs_fragment_animator_full_fragment : R.layout.qs_fragment_full_fragment;
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

    @Override public View onCreateContentView(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent) {
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

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        isViewCreated = true;
        super.onCreate(savedInstanceState);
        bindBundleByQsPlugin(getArguments());
        bindEventByQsPlugin();
        if (interceptBackPressed()) {
            requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
                @Override public void handleOnBackPressed() {
                    if (L.isEnable()) L.i(initTag(), "handleOnBackPressed..........");
                    onBackPressed();
                }
            });
        } else if (L.isEnable()) {
            L.i(initTag(), "interceptBackPressed() return false, if you want to intercept back pressed event, override it and return true!");
        }
    }

    @Override @Nullable @CallSuper
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = initView(inflater, container);
        onViewCreated(rootView);
        return rootView;
    }

    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!isDelayData()) {
            hasInitData = true;
            initData(savedInstanceState);
        }
    }

    @CallSuper @Override public void onDestroy() {
        isViewCreated = false;
        super.onDestroy();
        unbindEventByQsPlugin();
    }

    @CallSuper @Override public void bindBundleByQsPlugin(Bundle bundle) {
    }

    @CallSuper @Override public void bindEventByQsPlugin() {
    }

    @CallSuper @Override public void unbindEventByQsPlugin() {
    }

    protected View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        long s0 = 0;
        if (L.isEnable()) s0 = System.nanoTime();
        View rootView = onCreateRootView(inflater, container);
        if (rootView == null) {
            return onCreateContentView(inflater, container);
        }

        ViewGroup actionbarContainer = rootView.findViewById(R.id.qs_actionbar_parent);
        if (actionbarContainer != null) {
            View actionbarView = onCreateActionbarView(inflater, actionbarContainer);
            if (actionbarView != null && actionbarView.getParent() == null) {
                actionbarContainer.addView(actionbarView);
            }
        }

        if (isOpenViewState()) {
            mViewAnimator = rootView.findViewById(R.id.qs_layout_container);
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
                long s1 = System.nanoTime();
                L.i(initTag(), "initView...view inflate complete(viewState is open), use time:" + (s1 - s0) / 1000000f + "ms");
            }

        } else {
            ViewGroup customViewContainer = rootView.findViewById(R.id.qs_layout_container);
            if (customViewContainer == null) {
                customViewContainer = (ViewGroup) rootView;
                L.e(initTag(), "initView...Layout with id[R.id.qs_layout_container] were not found, so attach to root view!");
            }
            View contentView = onCreateContentView(inflater, customViewContainer);
            if (contentView != null) {
                if (contentViewBackgroundColor() != 0) {
                    contentView.setBackgroundColor(contentViewBackgroundColor());
                }
                if (customViewContainer != contentView && contentView.getParent() == null) {
                    customViewContainer.addView(contentView);
                }
            }

            if (L.isEnable()) {
                long s1 = System.nanoTime();
                L.i(initTag(), "initView...view inflate complete(viewState not open), use time:" + (s1 - s0) / 1000000f + "ms");
            }
        }
        return rootView;
    }

    @Override public final void initDataWhenDelay() {
        if (!hasInitData && isDelayData()) {
            hasInitData = true;
            initData(null);
        }
    }

    @Override public boolean isOpenViewState() {
        return true;
    }

    @Override public Animation viewStateInAnimation() {
        return QsHelper.getAppInterface().viewStateInAnimation();
    }

    @Override public Animation viewStateOutAnimation() {
        return QsHelper.getAppInterface().viewStateOutAnimation();
    }

    @Override public int viewStateInAnimationId() {
        return QsHelper.getAppInterface().viewStateInAnimationId();
    }

    @Override public int viewStateOutAnimationId() {
        return QsHelper.getAppInterface().viewStateOutAnimationId();
    }

    @Override public boolean viewStateAnimateFirstView() {
        return QsHelper.getAppInterface().viewStateAnimateFirstView();
    }

    @Override public boolean isDelayData() {
        return false;
    }

    @Override public final void activityFinish() {
        activityFinish(false);
    }

    @Override public final void activityFinish(int enterAnim, int exitAnim) {
        activityFinish();
        FragmentActivity activity = getActivity();
        if (activity != null) activity.overridePendingTransition(enterAnim, exitAnim);
    }

    @Override public final void activityFinish(boolean finishAfterTransition) {
        FragmentActivity activity = getActivity();
        if (activity == null) return;
        if (finishAfterTransition) {
            ActivityCompat.finishAfterTransition(activity);
        } else {
            activity.finish();
        }
    }

    @Override public void onBackPressed() {
        onBackPressed(0, 0, 0, 0);
    }

    @Override public void onBackPressed(int enterAnim, int exitAnim) {
        onBackPressed(enterAnim, exitAnim, 0, 0);
    }

    @Override public void onBackPressed(int enterAnim, int exitAnim, int popEnterAnim, int popExitAnim) {
        FragmentManager manager = getFragmentManager();
        if (manager != null) {
            FragmentTransaction ft = manager.beginTransaction();
            ft.setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim);
            ft.remove(this).commitAllowingStateLoss();
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
            progressView = new ProgressView(getContext());
            progressView.initView(getLoadingDialog());
        }
        progressView.setMessage(message);
        progressView.setCancelable(cancelAble);
        progressView.show(getActivity());
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

    @Override public final void intent2Activity(Class<?> clazz) {
        intent2Activity(clazz, null, 0, null, 0, 0);
    }

    @Override public final void intent2Activity(Class<?> clazz, int requestCode) {
        intent2Activity(clazz, null, requestCode, null, 0, 0);
    }

    @Override public final void intent2Activity(Class<?> clazz, Bundle bundle, int requestCode) {
        intent2Activity(clazz, bundle, requestCode, null, 0, 0);
    }

    @Override public final void intent2Activity(Class<?> clazz, Bundle bundle) {
        intent2Activity(clazz, bundle, 0, null, 0, 0);
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
        QsHelper.commitFragment(getChildFragmentManager(), R.id.qs_default_container_for_fragment, fragment);
    }

    @Override public final void commitFragment(Fragment fragment, String tag) {
        QsHelper.commitFragment(getChildFragmentManager(), R.id.qs_default_container_for_fragment, fragment, tag);
    }

    @Override public final void commitFragment(Fragment fragment, int enterAnim, int existAnim) {
        QsHelper.commitFragment(getChildFragmentManager(), R.id.qs_default_container_for_fragment, fragment, fragment.getClass().getSimpleName(), enterAnim, existAnim);
    }

    @Override public final void commitFragment(Fragment fragment, String tag, int enterAnim, int existAnim) {
        QsHelper.commitFragment(getChildFragmentManager(), R.id.qs_default_container_for_fragment, fragment, tag, enterAnim, existAnim);
    }

    @Override public final void commitFragment(int layoutId, Fragment fragment) {
        QsHelper.commitFragment(getChildFragmentManager(), layoutId, fragment);
    }

    @Override public final void commitFragment(int layoutId, Fragment fragment, String tag) {
        QsHelper.commitFragment(getChildFragmentManager(), layoutId, fragment, tag);
    }

    @Override public final void commitFragment(int layoutId, Fragment fragment, int enterAnim, int existAnim) {
        QsHelper.commitFragment(getChildFragmentManager(), layoutId, fragment, fragment.getClass().getSimpleName(), enterAnim, existAnim);
    }

    @Override public final void commitFragment(int layoutId, Fragment fragment, String tag, int enterAnim, int existAnim) {
        QsHelper.commitFragment(getChildFragmentManager(), layoutId, fragment, tag, enterAnim, existAnim);
    }

    @Override public final void commitDialogFragment(DialogFragment fragment) {
        QsHelper.commitDialogFragment(getChildFragmentManager(), fragment);
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
        ViewHelper.setDefaultViewClickListener(view, this);
    }

    @Override public void smoothScrollToTop(boolean autoRefresh) {
        View view = getView();
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
        //当该fragment的父控件时ViewPager时，滑动ViewPager会触发该回调
    }

    @Override public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (activityResultListener != null) {
            activityResultListener.onActivityResult(getActivity(), requestCode, resultCode, data);
        }
        List<OnActivityResultListener> list = resultListenerList;
        if (list != null && list.size() > 0) {
            int size = list.size();
            OnActivityResultListener[] listeners = list.toArray(new OnActivityResultListener[size]);
            for (OnActivityResultListener l : listeners) {
                l.onActivityResult(getActivity(), requestCode, resultCode, data);
            }
        }
    }

    @Override public final void setOnActivityResultListener(OnActivityResultListener listener) {
        this.activityResultListener = listener;
    }

    @Override public void addOnActivityResultListener(OnActivityResultListener listener) {
        if (listener == null) return;
        if (resultListenerList == null) {
            resultListenerList = new ArrayList<>();
        }
        if (!resultListenerList.contains(listener)) {
            resultListenerList.add(listener);
        }
    }

    @Override public void removeOnActivityResultListener(OnActivityResultListener listener) {
        if (listener != null && resultListenerList != null) {
            resultListenerList.remove(listener);
        }
    }

    /**
     * if return true, this fragment will intercept onBackPressed event,
     * and {@link #onBackPressed()}will be execute.
     */
    @Override public boolean interceptBackPressed() {
        return false;
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

    @Override public final void setActivityTitle(CharSequence title) {
        setActivityTitle(title, 0);
    }

    @Override public final void setActivityTitle(final CharSequence title, final int type) {
        final FragmentActivity activity = getActivity();
        if (activity instanceof MvIActivity) {
            if (QsThreadPollHelper.isMainThread()) {
                ((MvIActivity) activity).setActivityTitle(title, type);
            } else {
                post(new Runnable() {
                    @Override public void run() {
                        ((MvIActivity) activity).setActivityTitle(title, type);
                    }
                });
            }
        }
    }

    /**
     * 发送事件给当前Activity
     */
    @Override public void sendEventToActivity(int eventType, Bundle data) {
        final FragmentActivity activity = getActivity();
        if (activity instanceof MvIActivity) {
            ((MvIActivity) activity).onReceivedEventFromFragment(eventType, data);
        }
    }

    @Override @NonNull public final <T> T createHttpRequest(Class<T> clazz) {
        return HttpHelper.createHttp(clazz);
    }

    @Override public final <D> D execute(@NonNull HttpCall<D> call) throws Exception {
        return call.as(this).execute();
    }

    @Override @Nullable public final <D> D executeSafely(@NonNull HttpCall<D> call) {
        return call.as(this).executeSafely();
    }

    @Override public final <D> void enqueue(@NonNull HttpCall<D> call, @NonNull HttpCallback<D> callback) {
        call.as(this).enqueue(callback);
    }

    /**
     * 取消http请求
     *
     * @see HttpCall#requestTag(Object)
     */
    @Override public final void cancelHttpRequest(Object requestTag) {
        if (requestTag != null) {
            QsHelper.getHttpHelper().cancelRequest(requestTag);
        }
    }

    @Override public final boolean isViewDestroyed() {
        return !isViewCreated;
    }

    /**
     * actionbar是否在最顶层
     *
     * @return true会遮挡fragment
     * @see #commitFragment(Fragment)
     */
    @Override public boolean isActionbarAtTheTopLevel() {
        return true;
    }
}
