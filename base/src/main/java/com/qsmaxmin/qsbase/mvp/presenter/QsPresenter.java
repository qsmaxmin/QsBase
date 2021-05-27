package com.qsmaxmin.qsbase.mvp.presenter;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.qsmaxmin.annotation.QsNotProguard;
import com.qsmaxmin.qsbase.common.http.HttpCall;
import com.qsmaxmin.qsbase.common.http.HttpCallback;
import com.qsmaxmin.qsbase.common.http.HttpHelper;
import com.qsmaxmin.qsbase.common.http.NetworkErrorReceiver;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.model.QsIModel;
import com.qsmaxmin.qsbase.common.utils.QsHelper;
import com.qsmaxmin.qsbase.common.widget.listview.LoadingFooter;
import com.qsmaxmin.qsbase.common.widget.toast.QsToast;
import com.qsmaxmin.qsbase.mvvm.MvIPullToRefreshView;
import com.qsmaxmin.qsbase.mvvm.MvIView;

import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
    private V      vLayer;
    private Object requestTag;

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
        if (L.isEnable()) L.i(initTag(), "onStateChanged..........Event:" + event.name());
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
     *
     * @see #createHttpRequest2(Class) 使用该api创建Http接口代理对象
     * @see #executeSafely(HttpCall)  使用该api同步请求网络
     * @see #execute(HttpCall) 使用该api同步请求网络
     * @see #enqueue(HttpCall, HttpCallback)  使用该api异步请求网络
     * @deprecated
     */
    @NonNull protected final <T> T createHttpRequest(Class<T> clazz) {
        return HttpHelper.getInstance().create(clazz, this);
    }

    @NonNull protected final <T> T createHttpRequest2(Class<T> clazz) {
        return HttpHelper.createHttp(clazz);
    }

    protected final <D> D execute(@NonNull HttpCall<D> call) throws Exception {
        if (requestTag == null) requestTag = new Object();
        return call.execute(requestTag);
    }

    @Nullable protected final <D> D executeSafely(@NonNull HttpCall<D> call) {
        if (requestTag == null) requestTag = new Object();
        return call.executeSafely(requestTag);
    }

    protected final <D> void enqueue(@NonNull HttpCall<D> call, @NonNull HttpCallback<D> callback) {
        if (requestTag == null) requestTag = new Object();
        call.enqueue(requestTag, callback);
    }

    /**
     * 取消由当前presenter发起的http请求
     */
    protected final void cancelHttpRequest(Object requestTag) {
        if (requestTag != null) {
            QsHelper.getHttpHelper().cancelRequest(requestTag);
        }
    }

    private void cancelAllHttpRequest() {
        cancelHttpRequest(requestTag);
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
     *
     * @see #createHttpRequest(Class) 该方法创建的Htt接口才会回调该方法，不再推荐使用
     * @see #createHttpRequest2(Class) 不再回调该方法
     * @deprecated
     */
    @Override public void methodError(@NonNull Throwable t) {
        if (L.isEnable()) L.e(initTag(), "methodError..." + t.getMessage());
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
