package com.qsmaxmin.qsbase.common.downloader;

import android.os.Looper;
import android.text.TextUtils;

import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.plugin.threadpoll.QsThreadPollHelper;
import com.qsmaxmin.qsbase.plugin.threadpoll.SafeRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * @CreateBy qsmaxmin
 * @Date 2020-03-17  15:25
 * @Description
 */
public final class QsDownloader<M extends QsDownloadModel<K>, K> {
    private final String                       TAG;
    private final HashMap<K, DownloadExecutor> executorMap;
    private final List<DownloadListener<M>>    globeListeners;
    private final OkHttpClient                 httpClient;
    private       boolean                      supportBreakPointTransmission;
    private       boolean                      forceDownload;
    private       boolean                      printResponseHeader;

    QsDownloader(OkHttpClient client, Class<M> tag) {
        this.httpClient = client;
        this.TAG = "QsDownloader-" + tag.getSimpleName();
        this.supportBreakPointTransmission = true;
        this.executorMap = new HashMap<>();
        this.globeListeners = new ArrayList<>();
    }

    /**
     * 设置是否开启断点续传功能，默认开启
     */
    public final void setSupportBreakPointTransmission(boolean supportBreakPointTransmission) {
        this.supportBreakPointTransmission = supportBreakPointTransmission;
    }

    public final boolean isSupportBreakPointTransmission() {
        return supportBreakPointTransmission;
    }

    /**
     * 是否强制下载
     * 强制下载会忽略本地已存在的文件
     */
    public final void setForceDownload(boolean forceDownload) {
        this.forceDownload = forceDownload;
    }

    public final boolean isForceDownload() {
        return forceDownload;
    }

    /**
     * 是否打印响应头信息
     */
    public final void sePrintResponseHeader(boolean printResponseHeader) {
        this.printResponseHeader = printResponseHeader;
    }

    public final boolean isPrintResponseHeader() {
        return printResponseHeader;
    }

    /**
     * 异步执行下载动作
     *
     * @see #enqueueDownload(QsDownloadModel)
     */
    public final void startDownload(final M model) {
        enqueueDownload(model);
    }

    /**
     * 异步执行下载动作
     */
    public final void enqueueDownload(final M model) {
        final Request.Builder builder;
        try {
            builder = getBuilder(model);
        } catch (Exception e) {
            postDownloadFailed(model, e.getMessage());
            L.e(TAG, e);
            return;
        }
        postDownloadStart(model);
        final DownloadExecutor<M, K> executor;
        synchronized (executorMap) {
            if (executorMap.get(model.getId()) == null) {
                executor = new DownloadExecutor<>(this, model, TAG);
                executorMap.put(model.getId(), executor);
            } else {
                return;
            }
        }
        QsThreadPollHelper.runOnHttpThread(new SafeRunnable() {
            @Override protected void safeRun() {
                try {
                    executor.start(builder);
                    postDownloadComplete(model);
                } catch (Exception e) {
                    postDownloadFailed(model, e.getMessage());
                    L.e(TAG, e);
                } finally {
                    executor.applyNotify();
                    removeExecutorFromTask(model);
                }
            }
        });
    }

    /**
     * 同步执行下载动作
     */
    @SuppressWarnings("unchecked")
    public final void executeDownload(final M model) throws Exception {
        if (QsThreadPollHelper.isMainThread()) {
            throw new Exception("cannot execute method:startDownloadSync() in MAIN Thread!!!");
        }
        boolean shouldWait = false;
        DownloadExecutor executor;
        synchronized (executorMap) {
            executor = executorMap.get(model.getId());
            if (executor == null) {
                executor = new DownloadExecutor<>(this, model, TAG);
                executorMap.put(model.getId(), executor);
            } else {
                shouldWait = true;
            }
        }

        if (shouldWait) {
            if (executor.isDownloadSuccess()) {
                postDownloadComplete((M) executor.getModel());
            } else {
                M downloadingModel = (M) executor.getModel();
                postDownloading(downloadingModel, downloadingModel.getDownloadedLength(), model.getTotalLength());

                if (L.isEnable()) L.i(TAG, "executeDownload....相同的任务正在下载中，将当前线程置为等待中状态.........");
                executor.applyWait();
                if (L.isEnable()) L.i(TAG, "executeDownload....该任务在其它线程执行完毕，唤醒当前线程.........");

                if (executor.isDownloadSuccess()) {
                    postDownloadComplete(downloadingModel);
                } else {
                    postDownloadFailed(downloadingModel, "download file failed in other thread !!");
                    throw new Exception("download file failed in other thread !!");
                }
            }

        } else {
            try {
                postDownloadStart(model);
                Request.Builder builder = getBuilder(model);
                executor.start(builder);
                postDownloadComplete(model);
            } catch (Exception e) {
                postDownloadFailed(model, e.getMessage());
                throw e;
            } finally {
                removeExecutorFromTask(model);
                executor.applyNotify();
            }
        }
    }


    @NonNull private Request.Builder getBuilder(M model) throws Exception {
        if (model == null) {
            throw new Exception("startDownload...param error");
        }
        if (model.getId() == null) {
            throw new Exception("startDownload..." + model.getClass().getSimpleName() + ".getId() return empty");
        }
        if (TextUtils.isEmpty(model.getFilePath())) {
            throw new Exception("startDownload..." + model.getClass().getSimpleName() + ".getFilePath() return empty");
        }
        return model.getRequest();
    }

    public final boolean isDownloading(M m) {
        return executorMap.get(m.getId()) != null;
    }

    public final boolean isDownloading(K k) {
        return executorMap.get(k) != null;
    }

    public final void cancelDownload(M m) {
        synchronized (executorMap) {
            DownloadExecutor executor = executorMap.get(m.getId());
            if (executor != null) {
                executor.cancel();
            }
        }
    }

    /**
     * 清除指定下载文件的缓存
     */
    public final boolean cleanDownloadCache(M m) {
        if (isDownloading(m)) {
            if (L.isEnable()) L.e(TAG, "cleanDownloadCache failed, The cache cannot be deleted while downloading.....");
            return false;
        }
        File cacheFile = new File(m.getTempFilePath());
        if (cacheFile.exists()) {
            return cacheFile.delete();
        }
        return true;
    }

    public final void release() {
        if (L.isEnable()) L.i(TAG, "release........");
        if (executorMap.size() > 0) {
            synchronized (executorMap) {
                for (DownloadExecutor executor : executorMap.values()) {
                    executor.cancel();
                }
                executorMap.clear();
            }
        }
        if (globeListeners.size() > 0) {
            synchronized (globeListeners) {
                globeListeners.clear();
            }
        }
    }

    final OkHttpClient getClient() {
        return httpClient;
    }

    private void removeExecutorFromTask(M model) {
        synchronized (executorMap) {
            executorMap.remove(model.getId());
        }
    }

    public final void registerGlobalDownloadListener(DownloadListener<M> listener) {
        if (listener != null) {
            synchronized (globeListeners) {
                if (!globeListeners.contains(listener)) {
                    globeListeners.add(listener);
                }
            }
        }
    }

    public final void removeGlobalDownloadListener(DownloadListener<M> listener) {
        synchronized (globeListeners) {
            globeListeners.remove(listener);
        }
    }

    private void postDownloadStart(final M model) {
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

    final void postDownloading(final M model, final long size, final long totalSize) {
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

    private void postDownloadComplete(final M model) {
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

    private void postDownloadFailed(final M model, final String msg) {
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
