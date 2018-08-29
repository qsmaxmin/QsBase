package com.qsmaxmin.qsbase.mvp.presenter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import com.qsmaxmin.qsbase.common.aspect.ThreadPoint;
import com.qsmaxmin.qsbase.common.aspect.ThreadType;
import com.qsmaxmin.qsbase.common.exception.QsException;
import com.qsmaxmin.qsbase.common.exception.QsExceptionType;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.model.QsModel;
import com.qsmaxmin.qsbase.common.utils.QsHelper;
import com.qsmaxmin.qsbase.common.widget.listview.LoadingFooter;
import com.qsmaxmin.qsbase.common.widget.toast.QsToast;
import com.qsmaxmin.qsbase.mvp.QsIView;
import com.qsmaxmin.qsbase.mvp.fragment.QsIPullToRefresh;
import com.qsmaxmin.qsbase.mvp.model.QsConstants;

import java.util.ArrayList;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/6/21 16:27
 * @Description
 */
public class QsPresenter<V extends QsIView> {
    private ArrayList<String> tagList = new ArrayList<>();
    private boolean isAttach;
    private V       mView;

    protected String initTag() {
        return QsHelper.getInstance().getApplication().isLogOpen() ? getClass().getSimpleName() : "QsPresenter";
    }

    public Context getContext() {
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
        if (!tagList.contains(requestTag)) {
            tagList.add(requestTag);
        } else {
            L.e(initTag(), "createHttpRequest Repeated tag:" + requestTag);
        }
        return QsHelper.getInstance().getHttpHelper().create(clazz, requestTag);
    }

    /**
     * 取消由当前presenter发起的http请求
     */
    protected void cancelAllHttpRequest() {
        try {
            for (String tag : tagList) {
                QsHelper.getInstance().getHttpHelper().cancelRequest(tag);
            }
            tagList.clear();
        } catch (Exception e) {
            L.e(initTag(), "cancel http request failed :" + e.getMessage());
        }
    }

    protected void cancelHttpRequest(String requestTag) {
        if (tagList.contains(requestTag)) {
            tagList.remove(requestTag);
            try {
                QsHelper.getInstance().getHttpHelper().cancelRequest(requestTag);
            } catch (Exception e) {
                L.e(initTag(), "cancel http request failed :" + e.getMessage());
            }
        } else {//当前http请求已经被取消
            L.i(initTag(), "The current HTTP request has been cancelled! requestTag:" + requestTag);
        }
    }

    public boolean isSuccess(QsModel baseModel) {
        return isSuccess(baseModel, false);
    }

    /**
     * 请求网络成功，判断数据的完整性
     */
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
    @ThreadPoint(ThreadType.MAIN) public void paging(QsModel model) {
        if (model != null && !isViewDetach()) {
            QsIView qsIView = getView();
            if (qsIView == null) return;
            if (qsIView instanceof QsIPullToRefresh) {
                if (model.isLastPage()) {
                    ((QsIPullToRefresh) qsIView).setLoadingState(LoadingFooter.State.TheEnd);
                } else {
                    ((QsIPullToRefresh) qsIView).setLoadingState(LoadingFooter.State.Normal);
                }
            } else {
                L.e(initTag(), "not QsPullListFragment or QsPullRecyclerFragment view, so invalid paging(...)");
            }
        }
    }

    /**
     * 自定义异常处理
     */
    public void methodError(QsException exception) {
        L.e(initTag(), "methodError... errorType:" + exception.getExceptionType() + " requestTag:" + exception.getRequestTag() + " errorMessage:" + exception.getMessage());
        switch (exception.getExceptionType()) {
            case HTTP_ERROR:
            case NETWORK_ERROR:
            case UNEXPECTED:
            case CANCEL:
                break;
        }
        resetViewState();
    }

    /**
     * 还原View状态
     */
    @ThreadPoint(ThreadType.MAIN) private void resetViewState() {
        if (!isViewDetach()) {
            QsIView qsIview = getView();
            if (qsIview instanceof QsIPullToRefresh) {
                QsIPullToRefresh view = (QsIPullToRefresh) qsIview;
                view.stopRefreshing();
                view.setLoadingState(LoadingFooter.State.NetWorkError);
            }
            if (qsIview.currentViewState() != QsConstants.VIEW_STATE_CONTENT) {
                qsIview.showErrorView();
            }
            qsIview.loadingClose();
        }
    }

    public String getString(@StringRes int stringId) {
        return QsHelper.getInstance().getString(stringId);
    }

    public String getString(@StringRes int resId, Object... formatArgs) {
        return QsHelper.getInstance().getString(resId, formatArgs);
    }

    public Drawable getDrawable(@DrawableRes int resId) {
        return QsHelper.getInstance().getDrawable(resId);
    }

    public int getColor(@ColorRes int resId) {
        return QsHelper.getInstance().getColor(resId);
    }

    public float getDimension(@DimenRes int resId) {
        return QsHelper.getInstance().getDimension(resId);
    }

    public boolean isSdCardAvailable() {
        return QsHelper.getInstance().isSdCardAvailable();
    }

    public boolean isNetworkAvailable() {
        return QsHelper.getInstance().isNetworkAvailable();
    }
}
