package com.qsmaxmin.qsbase.mvp.presenter;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.qsmaxmin.annotation.QsNotProguard;
import com.qsmaxmin.qsbase.common.exception.QsException;
import com.qsmaxmin.qsbase.common.http.HttpHelper;
import com.qsmaxmin.qsbase.common.http.NetworkErrorReceiver;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.model.QsIModel;
import com.qsmaxmin.qsbase.common.utils.QsHelper;
import com.qsmaxmin.qsbase.common.widget.listview.LoadingFooter;
import com.qsmaxmin.qsbase.common.widget.toast.QsToast;
import com.qsmaxmin.qsbase.mvvm.MvIPullToRefreshView;
import com.qsmaxmin.qsbase.mvvm.MvIView;

import java.util.HashSet;

import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/6/21 16:27
 * @Description base presenter
 */
public class QsPresenter<V extends MvIView> implements NetworkErrorReceiver, QsNotProguard, LifecycleEventObserver {
    private final HashSet<Object> tagList = new HashSet<>();
    private       V               vLayer;

    /**
     * 该方法由QsTransform唤起，不可修改
     */
    public final void initPresenter(V v) {
        this.vLayer = v;
        v.getLifecycle().addObserver(this);
    }

    protected final String initTag() {
        return L.isEnable() ? getClass().getSimpleName() : "QsPresenter";
    }

    public final Context getContext() {
        return vLayer.getContext();
    }

    public final FragmentActivity getActivity() {
        return vLayer.getActivity();
    }

    @Override public final void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        if (L.isEnable()) L.i(initTag(), "onStateChanged.........." + source.getLifecycle().getCurrentState().name() + ", " + event.name());
        switch (event) {
            case ON_CREATE:
                onCreate();
                break;
            case ON_START:
                onStart();
                break;
            case ON_RESUME:
                onResume();
                break;
            case ON_PAUSE:
                onPause();
                break;
            case ON_STOP:
                onStop();
                break;
            case ON_DESTROY:
                cancelAllHttpRequest();
                onDestroy();
                break;
        }
    }

    protected void onCreate() {
    }

    protected void onStart() {
    }

    protected void onResume() {
    }

    protected void onPause() {
    }

    protected void onStop() {
    }

    protected void onDestroy() {
    }

    @NonNull public final V getView() {
        return vLayer;
    }

    public final boolean isViewDetach() {
        return vLayer.isViewDestroyed();
    }

    public final boolean isViewAttach() {
        return !vLayer.isViewDestroyed();
    }

    /**
     * 发起http请求
     */
    @NonNull protected final <T> T createHttpRequest(Class<T> clazz) {
        return createHttpRequest(clazz, System.nanoTime());
    }

    @NonNull protected final <T> T createHttpRequest(Class<T> clazz, Object requestTag) {
        synchronized (tagList) {
            if (!tagList.contains(requestTag)) {
                tagList.add(requestTag);
            } else {
                L.e(initTag(), "createHttpRequest Repeated tag:" + requestTag);
            }
        }
        return HttpHelper.getInstance().create(clazz, requestTag, this);
    }

    /**
     * 取消由当前presenter发起的http请求
     */
    protected final void cancelAllHttpRequest() {
        synchronized (tagList) {
            for (Object tag : tagList) {
                try {
                    QsHelper.getHttpHelper().cancelRequest(tag);
                } catch (Exception e) {
                    L.e(initTag(), "cancel http request failed :" + e.getMessage());
                }
            }
            tagList.clear();
        }
    }

    protected final void cancelHttpRequest(Object requestTag) {
        synchronized (tagList) {
            if (tagList.contains(requestTag)) {
                tagList.remove(requestTag);
                try {
                    QsHelper.getHttpHelper().cancelRequest(requestTag);
                } catch (Exception e) {
                    L.e(initTag(), "cancel http request failed :" + e.getMessage());
                }
            } else {
                L.i(initTag(), "The current HTTP request has been cancelled! requestTag:" + requestTag);
            }
        }
    }

    public boolean isSuccess(QsIModel baseModel) {
        return isSuccess(baseModel, false);
    }

    public boolean isSuccess(QsIModel model, boolean shouldToast) {
        if (model != null && model.isResponseOk()) {
            return true;
        } else if (isViewAttach()) {
            resetViewState();
            if (model != null && shouldToast) QsToast.show(model.getMessage());
        }
        return false;
    }

    /**
     * 分页
     *
     * @param model 分页数据持有
     */
    public void paging(QsIModel model) {
        if (model != null && isViewAttach()) {
            V view = getView();
            if (view instanceof MvIPullToRefreshView) {
                if (model.isLastPage()) {
                    ((MvIPullToRefreshView) view).setLoadingState(LoadingFooter.State.TheEnd);
                } else {
                    ((MvIPullToRefreshView) view).setLoadingState(LoadingFooter.State.Normal);
                }
            } else {
                L.e(initTag(), "not QsPullListFragment or QsPullRecyclerFragment view, so invalid paging(...)");
            }
        }
    }

    /**
     * 当前Presenter请求网络出错时，都会回调该方法
     */
    @Override public void methodError(@NonNull QsException e) {
        if (L.isEnable()) L.e(initTag(), "methodError..." + e.getMessage());
        resetViewState();
    }

    private void resetViewState() {
        if (isViewDetach()) return;
        V view = getView();
        if (view instanceof MvIPullToRefreshView) {
            MvIPullToRefreshView refreshView = (MvIPullToRefreshView) view;
            refreshView.stopRefreshing();
            refreshView.setLoadingState(LoadingFooter.State.NetWorkError);
        }
        if (!view.isShowContentView()) {
            view.showErrorView();
        }
        view.loadingClose();
    }

    public final String getString(@StringRes int stringId) {
        return QsHelper.getString(stringId);
    }

    public final String getString(@StringRes int resId, Object... formatArgs) {
        return QsHelper.getString(resId, formatArgs);
    }

    public final Drawable getDrawable(@DrawableRes int resId) {
        return QsHelper.getDrawable(resId);
    }

    public final int getColor(@ColorRes int resId) {
        return QsHelper.getColor(resId);
    }

    public final float getDimension(@DimenRes int resId) {
        return QsHelper.getDimension(resId);
    }

    public final boolean isSdCardAvailable() {
        return QsHelper.isSdCardAvailable();
    }

    public final boolean isNetworkAvailable() {
        return QsHelper.isNetworkAvailable();
    }
}
