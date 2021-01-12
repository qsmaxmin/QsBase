package com.qsmaxmin.qsbase.common.downloader;

import android.os.Looper;
import android.text.TextUtils;

import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.plugin.threadpoll.QsThreadPollHelper;

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
@SuppressWarnings("rawtypes")
public class QsDownloader<M extends QsDownloadModel<K>, K> {
    private final String                       TAG;
    private final HashMap<K, DownloadExecutor> executorMap    = new HashMap<>();
    private final List<DownloadListener<M>>    globeListeners = new ArrayList<>();
    private final OkHttpClient                 httpClient;
    private final Class                        httpTag;

    QsDownloader(OkHttpClient client, Class<M> tag) {
        this.httpClient = client;
        this.httpTag = tag;
        this.TAG = "QsDownloader-" + httpTag.getSimpleName();
    }

    public void startDownload(final M model) {
        if (model == null) {
            if (L.isEnable()) L.e(TAG, "startDownload...param error");
            return;
        }

        if (model.getId() == null) {
            if (L.isEnable()) L.e(TAG, "startDownload..." + model.getClass().getSimpleName() + ".getId() return null");
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
        DownloadExecutor<M, K> executor = new DownloadExecutor<>(this, model, TAG);
        executorMap.put(model.getId(), executor);

        builder.tag(httpTag);
        executor.start(builder);
    }

    public boolean isDownloading(M m) {
        return executorMap.get(m.getId()) != null;
    }

    public boolean isDownloading(K k) {
        return executorMap.get(k) != null;
    }

    /**
     * 清除指定下载文件的缓存
     */
    public boolean cleanDownloadCache(M m) {
        if (isDownloading(m)) {
            if (L.isEnable()) L.e(TAG, "cleanDownloadCache failed, The cache cannot be deleted while downloading.....");
            return false;
        }
        File cacheFile = DownloadExecutor.getCacheFile(m);
        if (cacheFile.exists()) {
            boolean delete = cacheFile.delete();
            L.i(TAG, "cleanDownloadCache......success:" + delete);
        }
        return true;
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

    public void registerGlobalDownloadListener(DownloadListener<M> listener) {
        if (listener != null) {
            synchronized (globeListeners) {
                if (!globeListeners.contains(listener)) {
                    globeListeners.add(listener);
                }
            }
        }
    }

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
        QsThreadPollHelper.post(action);
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
