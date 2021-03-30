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
 * @Description 管理同一分类的下载任务
 * @see QsDownloadHelper#getDownloader(Class)
 */
public final class QsDownloader<M extends QsDownloadModel<K>, K> {
    private final String                             TAG;
    private final HashMap<K, DownloadExecutor<M, K>> executorMap;
    private final List<DownloadListener<M>>          globeListeners;
    private final OkHttpClient                       httpClient;
    private       boolean                            printResponseHeader;

    QsDownloader(OkHttpClient client, Class<M> tag) {
        this.httpClient = client;
        this.TAG = "QsDownloader-" + tag.getSimpleName();
        this.executorMap = new HashMap<>();
        this.globeListeners = new ArrayList<>();
    }

    /**
     * 是否打印响应头信息,下载前设置，默认不打印
     */
    public final void sePrintResponseHeader(boolean printResponseHeader) {
        this.printResponseHeader = printResponseHeader;
    }

    public final boolean isPrintResponseHeader() {
        return printResponseHeader;
    }

    /**
     * 异步执行下载动作
     * 根据服务器是否支持断点续传以及是否是大文件来确定是否开启断点续传功能
     * 根据文件大小确定开启的线程数
     *
     * @see #enqueueDownload(QsDownloadModel)
     * @deprecated
     */
    public final void startDownload(final M model) {
        enqueueDownload(model, null);
    }

    public final void enqueueDownload(final M model) {
        enqueueDownload(model, null);
    }

    /**
     * 异步执行下载动作
     * 根据服务器是否支持断点续传以及是否是大文件来确定是否开启断点续传功能
     * 根据文件大小确定开启的线程数
     * 任务种类用QsDownloadModel{@link QsDownloadModel}的子类区分
     * 同一任务种类的不同任务由Model的id{@link QsDownloadModel#getId()}区分
     * 若该任务正在执行时，再次执行会立即收到{@link DownloadListener#onDownloading(QsDownloadModel, long, long)}回调
     */
    public final void enqueueDownload(final M model, final DownloadListener<M> listener) {
        final Request.Builder builder;
        try {
            builder = getBuilder(model);
        } catch (Exception e) {
            postDownloadFailed(listener, model, e.getMessage());
            postDownloadFailedGlobal(model, e.getMessage());
            L.e(TAG, e);
            return;
        }

        DownloadExecutor<M, K> executor;
        boolean isTaskExecuting = false;
        synchronized (executorMap) {
            executor = executorMap.get(model.getId());
            if (executor == null) {
                executor = createDownloadExecutor(model, listener);
                executorMap.put(model.getId(), executor);
            } else {
                isTaskExecuting = true;
            }
        }
        if (isTaskExecuting) {
            if (executor.isDownloading()) {
                postDownloading(listener, model);
                postDownloadingGlobal(executor.getModel());
            }
        } else {
            postDownloadStart(listener, model);
            postDownloadStartGlobal(model);
            final DownloadExecutor<M, K> finalExecutor = executor;
            QsThreadPollHelper.runOnHttpThread(new SafeRunnable() {
                @Override protected void safeRun() {
                    try {
                        finalExecutor.start(builder);
                        postDownloadComplete(listener, model);
                        postDownloadCompleteGlobal(model);
                    } catch (Exception e) {
                        postDownloadFailed(listener, model, e.getMessage());
                        postDownloadFailedGlobal(model, e.getMessage());
                        if (L.isEnable()) e.printStackTrace();
                    } finally {
                        removeExecutorFromTask(model);
                        finalExecutor.applyNotify();
                    }
                }
            });
        }
    }

    public final void executeDownload(final M model) throws Exception {
        executeDownload(model, null);
    }

    /**
     * 同步执行下载动作
     * 根据服务器是否支持断点续传以及是否是大文件来确定是否开启断点续传功能
     * 根据文件大小确定开启的线程数
     * 任务种类用QsDownloadModel{@link QsDownloadModel}的子类区分
     * 同一任务种类的不同任务由Model的id{@link QsDownloadModel#getId()}区分
     * 若该任务正在执行时，再次执行会立即收到{@link DownloadListener#onDownloading(QsDownloadModel, long, long)}回调
     */
    @SuppressWarnings("unchecked")
    public final void executeDownload(final M model, final DownloadListener listener) throws Exception {
        if (QsThreadPollHelper.isMainThread()) {
            throw new Exception("cannot execute method:startDownloadSync() in MAIN Thread!!!");
        }
        boolean isTaskExecuting = false;
        DownloadExecutor executor;
        synchronized (executorMap) {
            executor = executorMap.get(model.getId());
            if (executor == null) {
                executor = createDownloadExecutor(model, listener);
                executorMap.put(model.getId(), executor);
            } else {
                isTaskExecuting = true;
            }
        }

        if (isTaskExecuting) {
            if (executor.isDownloading()) {
                M m = (M) executor.getModel();
                postDownloading(listener, m);
                postDownloadingGlobal(m);

                if (L.isEnable()) L.i(TAG, "executeDownload....相同的任务正在下载中，将当前线程置为等待中状态.........");
                executor.applyWait();
                if (L.isEnable()) L.i(TAG, "executeDownload....该任务在其它线程执行完毕，唤醒当前线程.........");

                if (!executor.isDownloadSuccess()) {
                    throw new Exception("download file failed in other thread !!");
                }
            }
        } else {
            try {
                postDownloadStart(listener, model);
                postDownloadStartGlobal(model);
                Request.Builder builder = getBuilder(model);
                executor.start(builder);
                postDownloadComplete(listener, model);
                postDownloadCompleteGlobal(model);
            } catch (Exception e) {
                postDownloadFailed(listener, model, e.getMessage());
                postDownloadFailedGlobal(model, e.getMessage());
                throw e;
            } finally {
                removeExecutorFromTask(model);
                executor.applyNotify();
            }
        }
    }

    private DownloadExecutor<M, K> createDownloadExecutor(M model, DownloadListener<M> listener) {
        DownloadExecutor<M, K> executor = new DownloadExecutor<>(this, model, listener, TAG);
        executor.setPrintRespHeader(printResponseHeader);
        return executor;
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
        File cacheFile = new File(m.getDownloadTempFilePath());
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

    private void postDownloadStart(final DownloadListener<M> listener, final M model) {
        if (listener == null) return;
        if (isMainThread()) {
            listener.onDownloadStart(model);
        } else {
            post(new SafeRunnable() {
                @Override protected void safeRun() {
                    listener.onDownloadStart(model);
                }
            });
        }
    }

    void postDownloading(final DownloadListener<M> listener, final M model) {
        if (listener == null) return;
        if (isMainThread()) {
            listener.onDownloading(model, model.getDownloadedLength(), model.getTotalLength());
        } else {
            post(new SafeRunnable() {
                @Override protected void safeRun() {
                    listener.onDownloading(model, model.getDownloadedLength(), model.getTotalLength());
                }
            });
        }
    }

    private void postDownloadComplete(final DownloadListener<M> listener, final M model) {
        if (listener == null) return;
        if (isMainThread()) {
            listener.onDownloadComplete(model);
        } else {
            post(new SafeRunnable() {
                @Override protected void safeRun() {
                    listener.onDownloadComplete(model);
                }
            });
        }
    }

    private void postDownloadFailed(final DownloadListener<M> listener, final M model, final String msg) {
        if (listener == null) return;
        if (isMainThread()) {
            listener.onDownloadFailed(model, msg);
        } else {
            post(new SafeRunnable() {
                @Override protected void safeRun() {
                    listener.onDownloadFailed(model, msg);
                }
            });
        }
    }

    @SuppressWarnings("unchecked")
    private void postDownloadStartGlobal(final M model) {
        final Object[] callbacks = collectCallbacks();
        if (callbacks == null) return;
        if (isMainThread()) {
            for (Object callback : callbacks) {
                if (callback != null) {
                    ((DownloadListener) callback).onDownloadStart(model);
                }
            }
        } else {
            post(new SafeRunnable() {
                @Override public void safeRun() {
                    for (Object callback : callbacks) {
                        if (callback != null) {
                            ((DownloadListener) callback).onDownloadStart(model);
                        }
                    }
                }
            });
        }
    }

    @SuppressWarnings("unchecked") final void postDownloadingGlobal(final M model) {
        final Object[] callbacks = collectCallbacks();
        if (callbacks == null) return;
        if (isMainThread()) {
            for (Object callback : callbacks) {
                if (callback != null) {
                    ((DownloadListener) callback).onDownloading(model, model.getDownloadedLength(), model.getTotalLength());
                }
            }
        } else {
            post(new SafeRunnable() {
                @Override public void safeRun() {
                    for (Object callback : callbacks) {
                        if (callback != null) {
                            ((DownloadListener) callback).onDownloading(model, model.getDownloadedLength(), model.getTotalLength());
                        }
                    }
                }
            });
        }
    }

    @SuppressWarnings("unchecked")
    private void postDownloadCompleteGlobal(final M model) {
        final Object[] callbacks = collectCallbacks();
        if (callbacks == null) return;
        if (isMainThread()) {
            for (Object callback : callbacks) {
                if (callback != null) {
                    ((DownloadListener) callback).onDownloadComplete(model);
                }
            }
        } else {
            post(new SafeRunnable() {
                @Override public void safeRun() {
                    for (Object callback : callbacks) {
                        if (callback != null) {
                            ((DownloadListener) callback).onDownloadComplete(model);
                        }
                    }
                }
            });
        }
    }

    @SuppressWarnings("unchecked")
    private void postDownloadFailedGlobal(final M model, final String msg) {
        final Object[] callbacks = collectCallbacks();
        if (callbacks == null) return;
        if (isMainThread()) {
            for (Object callback : callbacks) {
                if (callback != null) {
                    ((DownloadListener) callback).onDownloadFailed(model, msg);
                }
            }
        } else {
            post(new SafeRunnable() {
                @Override public void safeRun() {
                    for (Object callback : callbacks) {
                        if (callback != null) {
                            ((DownloadListener) callback).onDownloadFailed(model, msg);
                        }
                    }
                }
            });
        }
    }

    private void post(SafeRunnable action) {
        QsThreadPollHelper.post(action);
    }


    private boolean isMainThread() {
        return Thread.currentThread() == Looper.getMainLooper().getThread();
    }

    private Object[] collectCallbacks() {
        if (globeListeners.size() == 0) return null;
        synchronized (globeListeners) {
            if (globeListeners.size() > 0) {
                return globeListeners.toArray();
            }
            return null;
        }
    }

}
