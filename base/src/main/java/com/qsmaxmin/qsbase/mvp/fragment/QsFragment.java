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
import com.qsmaxmin.qsbase.common.utils.ViewHelper;
import com.qsmaxmin.qsbase.common.viewbind.OnActivityResultListener;
import com.qsmaxmin.qsbase.common.widget.headerview.ScrollerProvider;
import com.qsmaxmin.qsbase.common.widget.ptr.PtrFrameLayout;
import com.qsmaxmin.qsbase.mvp.QsIActivity;
import com.qsmaxmin.qsbase.mvp.presenter.QsPresenter;
import com.qsmaxmin.qsbase.mvvm.IView;
import com.qsmaxmin.qsbase.plugin.threadpoll.QsThreadPollHelper;

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
public abstract class QsFragment<P extends QsPresenter> extends Fragment implements QsIFragment, ScrollerProvider, View.OnTouchListener {
    private   P                        presenter;
    private   boolean                  hasInitData;
    protected ViewAnimator             mViewAnimator;
    private   OnActivityResultListener activityResultListener;

    @Override public String initTag() {
        return L.isEnable() ? getClass().getSimpleName() : "QsFragment";
    }

    @Override public int rootViewLayoutId() {
        return isOpenViewState() ? R.layout.qs_view_animator : R.layout.qs_frame_layout;
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        bindBundleByQsPlugin(getArguments());
        View rootView = initView(inflater, container);
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
        mViewAnimator = null;
        unbindEventByQsPlugin();
    }

    @Override public void onViewCreated(View view) {
        //custom your logic
    }

    protected View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        long s0 = 0;
        if (L.isEnable()) s0 = System.nanoTime();
        View rootView = inflater.inflate(rootViewLayoutId(), container, false);
        if (isOpenViewState()) {
            mViewAnimator = rootView.findViewById(R.id.qs_view_animator);
            ViewHelper.initViewAnimator(mViewAnimator, this);

            if (loadingLayoutId() != 0) {
                View loadingView = inflater.inflate(loadingLayoutId(), mViewAnimator, false);
                addToParent(loadingView, mViewAnimator, VIEW_STATE_LOADING);
                setDefaultViewClickListener(loadingView);
                onLoadingViewCreated(loadingView);
            }

            if (layoutId() != 0) {
                View contentView = inflater.inflate(layoutId(), mViewAnimator, false);
                addToParent(contentView, mViewAnimator, VIEW_STATE_CONTENT);
                if (contentViewBackgroundColor() != 0) contentView.setBackgroundColor(contentViewBackgroundColor());
                onContentViewCreated(contentView);
            }

            if (L.isEnable()) {
                long s1 = System.nanoTime();
                L.i(initTag(), "initView...view inflate complete(viewState is open), use time:" + (s1 - s0) / 1000000f + "ms");
            }

        } else if (layoutId() != 0) {
            ViewGroup customView = rootView.findViewById(android.R.id.custom);
            View contentView = inflater.inflate(layoutId(), customView, false);
            if (contentViewBackgroundColor() != 0) contentView.setBackgroundColor(contentViewBackgroundColor());
            customView.addView(contentView);
            onContentViewCreated(contentView);

            if (L.isEnable()) {
                long s1 = System.nanoTime();
                L.i(initTag(), "initView...view inflate complete(viewState not open), use time:" + (s1 - s0) / 1000000f + "ms");
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

    @Override public void initDataWhenDelay() {
        if (!hasInitData && isDelayData()) {
            initData(getArguments());
            hasInitData = true;
        }
    }

    @Override public void onViewClick(@NonNull View view) {
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

    @Override public void onBackPressed() {
        FragmentManager manager = getFragmentManager();
        if (manager != null) {
            FragmentTransaction ft = manager.beginTransaction();
            ft.remove(this).commitAllowingStateLoss();
        }
    }

    @Override public final void onViewClicked(@NonNull View view) {
        onViewClicked(view, 400);
    }

    @Override public final void onViewClicked(@NonNull View view, long interval) {
        if (interval > 0 && ViewHelper.isFastClick(interval)) return;
        onViewClick(view);
    }

    @Override public final void loading() {
        if (getActivity() instanceof IView) {
            ((IView) getActivity()).loading();
        }
    }

    @Override public final void loading(int resId) {
        if (getActivity() instanceof IView) {
            ((IView) getActivity()).loading(resId);
        }
    }

    @Override public final void loading(String message) {
        if (getActivity() instanceof IView) {
            ((IView) getActivity()).loading(message);
        }
    }

    @Override public final void loading(boolean cancelAble) {
        if (getActivity() instanceof IView) {
            ((IView) getActivity()).loading(cancelAble);
        }
    }

    @Override public final void loading(int resId, boolean cancelAble) {
        if (getActivity() instanceof IView) {
            ((IView) getActivity()).loading(resId, cancelAble);
        }
    }

    @Override public final void loading(String message, boolean cancelAble) {
        if (getActivity() instanceof IView) {
            ((IView) getActivity()).loading(message, cancelAble);
        }
    }

    @Override public final void loadingClose() {
        if (getActivity() instanceof IView) {
            ((IView) getActivity()).loadingClose();
        }
    }


    @Override public final void showLoadingView() {
        if (mViewAnimator != null) {
            if (L.isEnable()) L.i(initTag(), "showLoadingView.........childCount:" + mViewAnimator.getChildCount());
            int index = findViewIndexByState(VIEW_STATE_LOADING);
            if (index >= 0) setViewState(index);
        }
    }

    @Override public final void showContentView() {
        if (mViewAnimator != null) {
            if (L.isEnable()) L.i(initTag(), "showContentView.........childCount:" + mViewAnimator.getChildCount());
            int index = findViewIndexByState(VIEW_STATE_CONTENT);
            if (index >= 0) setViewState(index);
        }
    }

    @Override public final void showEmptyView() {
        if (mViewAnimator != null) {
            if (L.isEnable()) L.i(initTag(), "showErrorView.........childCount:" + mViewAnimator.getChildCount());
            int index = findViewIndexByState(VIEW_STATE_EMPTY);
            if (index >= 0) {
                setViewState(index);
            } else {
                post(new Runnable() {
                    @Override public void run() {
                        if (L.isEnable()) L.i(initTag(), "showEmptyView.........inflate emptyLayoutId()");
                        View emptyView = getLayoutInflater().inflate(emptyLayoutId(), mViewAnimator, false);
                        addToParent(emptyView, mViewAnimator, VIEW_STATE_EMPTY);
                        setDefaultViewClickListener(emptyView);
                        onEmptyViewCreated(emptyView);
                        setViewState(mViewAnimator.getChildCount() - 1);
                    }
                });
            }
        }
    }

    @Override public final void showErrorView() {
        if (isOpenViewState() && mViewAnimator != null) {
            if (L.isEnable()) L.i(initTag(), "showErrorView.........childCount:" + mViewAnimator.getChildCount());
            int index = findViewIndexByState(VIEW_STATE_ERROR);
            if (index >= 0) {
                setViewState(index);
            } else {
                post(new Runnable() {
                    @Override public void run() {
                        if (L.isEnable()) L.i(initTag(), "showErrorView.........inflate errorLayoutId()");
                        View errorView = getLayoutInflater().inflate(errorLayoutId(), mViewAnimator, false);
                        addToParent(errorView, mViewAnimator, VIEW_STATE_ERROR);
                        setDefaultViewClickListener(errorView);
                        onErrorViewCreated(errorView);
                        setViewState(mViewAnimator.getChildCount() - 1);
                    }
                });
            }
        }
    }

    private int findViewIndexByState(int state) {
        if (mViewAnimator != null) {
            int childCount = mViewAnimator.getChildCount();
            for (int index = 0; index < childCount; index++) {
                Object tag = mViewAnimator.getChildAt(index).getTag(R.id.qs_view_state_key);
                if (tag != null && (int) tag == state) {
                    return index;
                }
            }
        }
        return -1;
    }

    private void addToParent(@NonNull View view, @NonNull ViewGroup parent, int tag) {
        if (view != parent) {
            view.setTag(R.id.qs_view_state_key, tag);
            parent.addView(view);
        } else {
            View current = parent.getChildAt(parent.getChildCount() - 1);
            current.setTag(R.id.qs_view_state_key, tag);
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

    @Override public final void intent2Activity(Class clazz, Bundle bundle, int requestCode) {
        intent2Activity(clazz, bundle, requestCode, null, 0, 0);
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
        ViewHelper.intent2Activity(this, clazz, bundle, requestCode, optionsCompat, inAnimId, outAnimId);
    }

    @Override public final void commitFragment(Fragment fragment) {
        QsHelper.commitFragment(getChildFragmentManager(), android.R.id.custom, fragment);
    }

    @Override public final void commitFragment(Fragment fragment, String tag) {
        QsHelper.commitFragment(getChildFragmentManager(), android.R.id.custom, fragment, tag);
    }

    @Override public void commitFragment(Fragment fragment, int enterAnim, int existAnim) {
        QsHelper.commitFragment(getChildFragmentManager(), android.R.id.custom, fragment, fragment.getClass().getSimpleName(), enterAnim, existAnim);
    }

    @Override public final void commitFragment(Fragment fragment, String tag, int enterAnim, int existAnim) {
        QsHelper.commitFragment(getChildFragmentManager(), android.R.id.custom, fragment, tag, enterAnim, existAnim);
    }

    @Override public final void commitFragment(int layoutId, Fragment fragment) {
        QsHelper.commitFragment(getChildFragmentManager(), layoutId, fragment);
    }

    @Override public final void commitFragment(int layoutId, Fragment fragment, String tag) {
        QsHelper.commitFragment(getChildFragmentManager(), layoutId, fragment, tag);
    }

    @Override public void commitFragment(int layoutId, Fragment fragment, int enterAnim, int existAnim) {
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
        if (view != null) {
            ViewHelper.setDefaultViewClickListener(view, this);
        }
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
        L.i(initTag(), "onFragmentSelectedInViewPager..... isSelected:" + isSelected + "  currentPosition:" + currentPosition + "  totalCount:" + totalCount);
    }

    @Override public boolean onTouch(View v, MotionEvent event) {
        return interceptTouchEvent();
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

    @Override public boolean interceptTouchEvent() {
        return true;
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
