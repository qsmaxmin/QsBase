package com.qsmaxmin.qsbase.common.downloader;

import android.Manifest;
import android.os.Looper;
import android.text.TextUtils;

import com.qsmaxmin.qsbase.common.aspect.Permission;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.utils.QsHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * @CreateBy qsmaxmin
 * @Date 2020-03-17  15:25
 * @Description
 */
public class QsDownloader<M extends QsDownloadModel> {
    private final String                            TAG;
    private final HashMap<String, DownloadExecutor> executorMap    = new HashMap<>();
    private final List<DownloadListener<M>>         globeListeners = new ArrayList<>();
    private       OkHttpClient                      httpClient;
    private       Class                             httpTag;

    QsDownloader(OkHttpClient client, Class<M> tag) {
        this.httpClient = client;
        this.httpTag = tag;
        this.TAG = "QsDownloader-" + httpTag.getSimpleName();
    }

    /**
     * 执行下载动作
     */
    @Permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void startDownload(final M model) {
        if (model == null) {
            if (L.isEnable()) L.e(TAG, "startDownload...param error");
            return;
        }

        if (TextUtils.isEmpty(model.getId())) {
            if (L.isEnable()) L.e(TAG, "startDownload..." + model.getClass().getSimpleName() + ".getId() return empty");
            return;
        }

        if (TextUtils.isEmpty(model.getFilePath())) {
            if (L.isEnable()) L.e(TAG, "startDownload..." + model.getClass().getSimpleName() + ".getFilePath() return empty");
            return;
        }

        Request.Builder builder = model.getRequest();
        if (builder == null) {
            if (L.isEnable()) L.e(TAG, "startDownload..." + model.getClass().getSimpleName() + ".getRequest() return null");
            return;
        }

        if (isDownloading(model)) {
            if (L.isEnable()) L.e(TAG, "startDownload...do not download again，id:" + model.getId());
            return;
        }
        DownloadExecutor<M> executor = new DownloadExecutor<>(this, model);
        executorMap.put(model.getId(), executor);

        builder.tag(httpTag);
        executor.start(builder);
    }


    /**
     * 是否正在下载
     */
    @SuppressWarnings("WeakerAccess")
    public boolean isDownloading(M m) {
        return executorMap.get(m.getId()) != null;
    }

    public void release() {
        if (L.isEnable()) L.i(TAG, "release........");
        try {
            executorMap.clear();
            globeListeners.clear();
            Dispatcher dispatcher = getClient().dispatcher();
            List<Call> runningCalls = dispatcher.runningCalls();
            for (Call call : runningCalls) {
                if (call.request().tag() == httpTag) {
                    call.cancel();
                    if (L.isEnable()) {
                        L.i(TAG, "cancel runningCalls.....tag:" + httpTag);
                    }
                }
            }
            List<Call> queuedCalls = dispatcher.queuedCalls();
            for (Call call : queuedCalls) {
                if (httpTag == call.request().tag()) {
                    if (L.isEnable()) {
                        L.i(TAG, "cancel queuedCalls.....tag:" + httpTag);
                    }
                    call.cancel();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    OkHttpClient getClient() {
        return httpClient;
    }

    void removeExecutorFromTask(M model) {
        executorMap.remove(model.getId());
    }

    /**
     * 添加全局监听
     * 因为是全局监听，所以需要做好QsDownloadModel id的唯一性
     */
    public void registerGlobalDownloadListener(DownloadListener<M> listener) {
        if (listener != null) {
            synchronized (globeListeners) {
                if (!globeListeners.contains(listener)) {
                    globeListeners.add(listener);
                }
            }
        }
    }

    /**
     * 移除指定全局回调
     * 因为是全局监听，所以需要做好QsDownloadModel id的唯一性
     */
    public void removeGlobalDownloadListener(DownloadListener<M> listener) {
        synchronized (globeListeners) {
            globeListeners.remove(listener);
        }
    }

    void postDownloadStart(final M model) {
        if (isMainThread()) {
            callbackDownloadStart(model);
        } else {
            post(new Runnable() {
                @Override public void run() {
                    callbackDownloadStart(model);
                }
            });
        }
    }

    void postDownloading(final M model, final long size, final long totalSize) {
        if (isMainThread()) {
            callbackDownloading(model, size, totalSize);
        } else {
            post(new Runnable() {
                @Override public void run() {
                    callbackDownloading(model, size, totalSize);
                }
            });
        }
    }

    void postDownloadComplete(final M model) {
        if (isMainThread()) {
            callbackDownloadComplete(model);
        } else {
            post(new Runnable() {
                @Override public void run() {
                    callbackDownloadComplete(model);
                }
            });
        }
    }

    void postDownloadFailed(final M model, final String msg) {
        if (isMainThread()) {
            callbackDownloadFailed(model, msg);
        } else {
            post(new Runnable() {
                @Override public void run() {
                    callbackDownloadFailed(model, msg);
                }
            });
        }
    }

    private void post(Runnable action) {
        QsHelper.getThreadHelper().getMainThread().execute(action);
    }

    @SuppressWarnings({"unchecked"})
    private void callbackDownloadStart(M model) {
        Object[] callbacks = collectCallbacks();
        if (callbacks != null) {
            for (Object callback : callbacks) {
                if (callback != null) {
                    ((DownloadListener) callback).onDownloadStart(model);
                }
            }
        }
    }

    @SuppressWarnings({"unchecked"})
    private void callbackDownloading(M model, long size, long totalSize) {
        Object[] callbacks = collectCallbacks();
        if (callbacks != null) {
            for (Object callback : callbacks) {
                if (callback != null) {
                    ((DownloadListener) callback).onDownloading(model, size, totalSize);
                }
            }
        }
    }

    @SuppressWarnings({"unchecked"})
    private void callbackDownloadComplete(M model) {
        Object[] callbacks = collectCallbacks();
        if (callbacks != null) {
            for (Object callback : callbacks) {
                if (callback != null) {
                    ((DownloadListener) callback).onDownloadComplete(model);
                }
            }
        }
    }

    @SuppressWarnings({"unchecked"})
    private void callbackDownloadFailed(M model, String msg) {
        Object[] callbacks = collectCallbacks();
        if (callbacks != null) {
            for (Object callback : callbacks) {
                if (callback != null) {
                    ((DownloadListener) callback).onDownloadFailed(model, msg);
                }
            }
        }
    }

    private boolean isMainThread() {
        return Thread.currentThread() == Looper.getMainLooper().getThread();
    }

    private Object[] collectCallbacks() {
        synchronized (globeListeners) {
            if (globeListeners.size() > 0) {
                return globeListeners.toArray();
            }
            return null;
        }
    }
}
