package com.qsmaxmin.qsbase.mvp.presenter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;

import com.qsmaxmin.annotation.QsNotProguard;
import com.qsmaxmin.qsbase.common.exception.QsException;
import com.qsmaxmin.qsbase.common.exception.QsExceptionType;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.model.QsModel;
import com.qsmaxmin.qsbase.common.utils.QsHelper;
import com.qsmaxmin.qsbase.common.widget.listview.LoadingFooter;
import com.qsmaxmin.qsbase.common.widget.toast.QsToast;
import com.qsmaxmin.qsbase.mvp.QsIPullToRefreshView;
import com.qsmaxmin.qsbase.mvp.QsIView;
import com.qsmaxmin.qsbase.mvp.model.QsConstants;
import com.qsmaxmin.qsbase.plugin.permission.QsIPermission;

import java.util.ArrayList;

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
public class QsPresenter<V extends QsIView> implements QsIPermission, QsNotProguard {
    private final ArrayList<String> tagList = new ArrayList<>();
    private       boolean           isAttach;
    private       V                 mView;

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

    public V getView() {
        if (isViewDetach()) {
            String threadName = Thread.currentThread().getName();
            switch (threadName) {
                case QsConstants.NAME_HTTP_THREAD:
                case QsConstants.NAME_WORK_THREAD:
                case QsConstants.NAME_SINGLE_THREAD:
                    throw new QsException(QsExceptionType.CANCEL, null, "current thread:" + threadName + " execute " + initTag() + ".getView() return null, maybe view is destroy...");
                default:
                    throw new QsException(QsExceptionType.UNEXPECTED, null, "请不要在非@ThreadPoint注解的线程或其他回调里直接执行getView()方法，必须先确定isViewDetach()返回值为false再调用getView方法");
            }
        }
        return mView;
    }

    public void setDetach() {
        isAttach = false;
        mView = null;
        cancelAllHttpRequest();
    }

    public boolean isViewDetach() {
        return !isAttach || mView == null;
    }

    /**
     * 发起http请求
     */
    protected <T> T createHttpRequest(Class<T> clazz) {
        return createHttpRequest(clazz, String.valueOf(System.nanoTime()));
    }

    protected <T> T createHttpRequest(Class<T> clazz, String requestTag) {
        synchronized (tagList) {
            if (!tagList.contains(requestTag)) {
                tagList.add(requestTag);
            } else {
                L.e(initTag(), "createHttpRequest Repeated tag:" + requestTag);
            }
            return QsHelper.getHttpHelper().create(clazz, requestTag);
        }
    }

    /**
     * 取消由当前presenter发起的http请求
     */
    protected void cancelAllHttpRequest() {
        synchronized (tagList) {
            for (String tag : tagList) {
                try {
                    QsHelper.getHttpHelper().cancelRequest(tag);
                } catch (Exception e) {
                    L.e(initTag(), "cancel http request failed :" + e.getMessage());
                }
            }
            tagList.clear();
        }
    }

    protected void cancelHttpRequest(String requestTag) {
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
            if (qsIView == null) return;
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

    public void methodError(QsException exception) {
        L.e(initTag(), "methodError... errorType:" + exception.getExceptionType() + " requestTag:" + exception.getRequestTag());
        exception.printStackTrace();
        switch (exception.getExceptionType()) {
            case HTTP_ERROR:
            case NETWORK_ERROR:
            case UNEXPECTED:
            case CANCEL:
                break;
        }
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
            if (qsIview.currentViewState() != QsIView.VIEW_STATE_CONTENT) {
                qsIview.showErrorView();
            }
            qsIview.loadingClose();
        }
    }

    @Nullable public FragmentActivity getActivity() {
        if (!isViewDetach()) return mView.getActivity();
        return null;
    }

    public String getString(@StringRes int stringId) {
        return QsHelper.getString(stringId);
    }

    public String getString(@StringRes int resId, Object... formatArgs) {
        return QsHelper.getString(resId, formatArgs);
    }

    public Drawable getDrawable(@DrawableRes int resId) {
        return QsHelper.getDrawable(resId);
    }

    public int getColor(@ColorRes int resId) {
        return QsHelper.getColor(resId);
    }

    public float getDimension(@DimenRes int resId) {
        return QsHelper.getDimension(resId);
    }

    public boolean isSdCardAvailable() {
        return QsHelper.isSdCardAvailable();
    }

    public boolean isNetworkAvailable() {
        return QsHelper.isNetworkAvailable();
    }

    @Override public Activity getActivityForPermission() {
        return getActivity();
    }
}
