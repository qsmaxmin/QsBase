package com.qsmaxmin.qsbase.mvp.presenter;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.qsmaxmin.annotation.QsNotProguard;
import com.qsmaxmin.qsbase.common.exception.QsException;
import com.qsmaxmin.qsbase.common.http.HttpHelper;
import com.qsmaxmin.qsbase.common.http.NetworkErrorCallback;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.model.QsModel;
import com.qsmaxmin.qsbase.common.utils.QsHelper;
import com.qsmaxmin.qsbase.common.widget.listview.LoadingFooter;
import com.qsmaxmin.qsbase.common.widget.toast.QsToast;
import com.qsmaxmin.qsbase.mvp.QsIPullToRefreshView;
import com.qsmaxmin.qsbase.mvp.QsIView;

import java.util.HashSet;

import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.FragmentActivity;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/6/21 16:27
 * @Description
 */
public class QsPresenter<V extends QsIView> implements NetworkErrorCallback, QsNotProguard {
    private final HashSet<Object> tagList = new HashSet<>();
    private       boolean         isAttach;
    private       V               mView;

    protected String initTag() {
        return L.isEnable() ? getClass().getSimpleName() : "QsPresenter";
    }

    @Nullable public Context getContext() {
        if (!isViewDetach()) return mView.getContext();
        return null;
    }

    public void initPresenter(V view) {
        mView = view;
        isAttach = true;
    }

    public final V getView() {
        return mView;
    }

    public final void setDetach() {
        isAttach = false;
        mView = null;
        cancelAllHttpRequest();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public final boolean isViewDetach() {
        return !isAttach || mView == null;
    }

    /**
     * 发起http请求
     */
    protected final <T> T createHttpRequest(Class<T> clazz) {
        return createHttpRequest(clazz, System.nanoTime());
    }

    protected final <T> T createHttpRequest(Class<T> clazz, Object requestTag) {
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

    protected final void cancelHttpRequest(String requestTag) {
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

    public boolean isSuccess(QsModel baseModel) {
        return isSuccess(baseModel, false);
    }

    public boolean isSuccess(QsModel model, boolean shouldToast) {
        if (model != null && model.isResponseOk()) {
            return true;
        } else if (!isViewDetach()) {
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
    public void paging(QsModel model) {
        if (model != null && !isViewDetach()) {
            QsIView qsIView = getView();
            if (qsIView instanceof QsIPullToRefreshView) {
                if (model.isLastPage()) {
                    ((QsIPullToRefreshView) qsIView).setLoadingState(LoadingFooter.State.TheEnd);
                } else {
                    ((QsIPullToRefreshView) qsIView).setLoadingState(LoadingFooter.State.Normal);
                }
            } else {
                L.e(initTag(), "not QsPullListFragment or QsPullRecyclerFragment view, so invalid paging(...)");
            }
        }
    }

    @Override public void methodError(QsException e) {
        resetViewState();
    }

    private void resetViewState() {
        if (!isViewDetach()) {
            QsIView qsIview = getView();
            if (qsIview instanceof QsIPullToRefreshView) {
                QsIPullToRefreshView view = (QsIPullToRefreshView) qsIview;
                view.stopRefreshing();
                view.setLoadingState(LoadingFooter.State.NetWorkError);
            }
            if (!qsIview.isShowContentView()) {
                qsIview.showErrorView();
            }
            qsIview.loadingClose();
        }
    }

    @Nullable public final FragmentActivity getActivity() {
        if (!isViewDetach()) return mView.getActivity();
        return null;
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
