package com.qsmaxmin.qsbase.common.downloader;

import android.text.TextUtils;

import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.plugin.threadpoll.QsThreadPollHelper;
import com.qsmaxmin.qsbase.plugin.threadpoll.SafeRunnable;

import java.io.File;
import java.util.HashMap;

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
    private final OkHttpClient                       httpClient;
    private       DownloadListener[]                 globeListeners;
    private       boolean                            printResponseHeader;

    QsDownloader(OkHttpClient client, Class<M> tag) {
        this.httpClient = client;
        this.TAG = "QsDownloader-" + tag.getSimpleName();
        this.executorMap = new HashMap<>();
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
        } catch (final Exception e) {
            if (listener != null) {
                listener.onDownloadFailed(model, e.getMessage());
            }
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
                executor.addListener(listener);
                callbackDownloading(executor);
            }
        } else {
            callbackDownloadStart(executor);
            final DownloadExecutor<M, K> finalExecutor = executor;
            QsThreadPollHelper.runOnHttpThread(new SafeRunnable() {
                @Override protected void safeRun() {
                    try {
                        finalExecutor.start(builder);
                        callbackDownloadComplete(finalExecutor);
                    } catch (Exception e) {
                        callbackDownloadFailed(finalExecutor, e);
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
    public final void executeDownload(final M model, final DownloadListener<M> listener) throws Exception {
        if (QsThreadPollHelper.isMainThread()) {
            throw new Exception("cannot execute method:startDownloadSync() in MAIN Thread!!!");
        }
        boolean isTaskExecuting = false;
        DownloadExecutor<M, K> executor;
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
                executor.addListener(listener);
                callbackDownloading(executor);

                if (L.isEnable()) L.i(TAG, "executeDownload....相同的任务正在下载中，将当前线程置为等待中状态.........");
                executor.applyWait();
                if (L.isEnable()) L.i(TAG, "executeDownload....该任务在其它线程执行完毕，唤醒当前线程.........");

                if (!executor.isDownloadSuccess()) {
                    throw new Exception("download file failed in other thread !!");
                }
            }
        } else {
            try {
                callbackDownloadStart(executor);
                Request.Builder builder = getBuilder(model);
                executor.start(builder);
                callbackDownloadComplete(executor);
            } catch (Exception e) {
                callbackDownloadFailed(executor, e);
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
        globeListeners = null;
    }

    final OkHttpClient getClient() {
        return httpClient;
    }

    @SuppressWarnings("unchecked")
    private void callbackDownloadStart(final DownloadExecutor<M, K> executor) {
        executor.getModel().onDownloadStart();
        post(new SafeRunnable() {
            @Override protected void safeRun() {
                DownloadListener[] listeners = executor.collectCallbacks();
                if (listeners != null && listeners.length > 0) {
                    for (DownloadListener l : listeners) {
                        if (l != null) l.onDownloadStart(executor.getModel());
                    }
                }
                if (globeListeners != null && globeListeners.length > 0) {
                    for (DownloadListener l : globeListeners) {
                        if (l != null) l.onDownloadStart(executor.getModel());
                    }
                }
            }
        });
    }

    @SuppressWarnings("unchecked") final void callbackDownloading(final DownloadExecutor<M, K> executor) {
        final M model = executor.getModel();
        model.onDownloading(model.getDownloadedLength(), model.getTotalLength());
        post(new SafeRunnable() {
            @Override protected void safeRun() {
                DownloadListener[] listeners = executor.collectCallbacks();
                if (listeners != null && listeners.length > 0) {
                    for (DownloadListener l : listeners) {
                        if (l != null) l.onDownloading(model, model.getDownloadedLength(), model.getTotalLength());
                    }
                }
                if (globeListeners != null && globeListeners.length > 0) {
                    for (DownloadListener l : globeListeners) {
                        if (l != null) l.onDownloading(model, model.getDownloadedLength(), model.getTotalLength());
                    }
                }
            }
        });
    }


    @SuppressWarnings("unchecked")
    private void callbackDownloadComplete(final DownloadExecutor<M, K> executor) throws Exception {
        executor.getModel().onDownloadComplete();
        post(new SafeRunnable() {
            @Override protected void safeRun() {
                DownloadListener[] listeners = executor.collectCallbacks();
                if (listeners != null && listeners.length > 0) {
                    for (DownloadListener l : listeners) {
                        if (l != null) l.onDownloadComplete(executor.getModel());
                    }
                }
                if (globeListeners != null && globeListeners.length > 0) {
                    for (DownloadListener l : globeListeners) {
                        if (l != null) l.onDownloadComplete(executor.getModel());
                    }
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void callbackDownloadFailed(final DownloadExecutor<M, K> executor, final Exception e) {
        executor.getModel().onDownloadFailed(e.getMessage());
        post(new SafeRunnable() {
            @Override protected void safeRun() {
                DownloadListener[] listeners = executor.collectCallbacks();
                if (listeners != null && listeners.length > 0) {
                    for (DownloadListener l : listeners) {
                        if (l != null) l.onDownloadFailed(executor.getModel(), e.getMessage());
                    }
                }
                if (globeListeners != null && globeListeners.length > 0) {
                    for (DownloadListener l : globeListeners) {
                        if (l != null) l.onDownloadFailed(executor.getModel(), e.getMessage());
                    }
                }
            }
        });
    }

    private void removeExecutorFromTask(M model) {
        synchronized (executorMap) {
            executorMap.remove(model.getId());
        }
    }

    public final void registerGlobalDownloadListener(DownloadListener<M> l) {
        if (l != null) {
            if (globeListeners == null) {
                DownloadListener[] listeners = new DownloadListener[2];
                listeners[0] = l;
                globeListeners = listeners;
            } else {
                DownloadListener[] listeners = this.globeListeners;
                for (int i = 0; i < listeners.length; i++) {
                    if (listeners[i] == null) {
                        listeners[i] = l;
                        return;
                    } else if (listeners[i] == l) {
                        return;
                    }
                }
                DownloadListener[] tempArray = new DownloadListener[listeners.length * 2];
                System.arraycopy(listeners, 0, tempArray, 0, listeners.length);
                tempArray[listeners.length] = l;
                globeListeners = tempArray;
            }
        }
    }

    public final void removeGlobalDownloadListener(DownloadListener<M> l) {
        DownloadListener[] listeners = this.globeListeners;
        if (listeners != null) {
            for (int i = 0; i < listeners.length; i++) {
                if (listeners[i] == l) listeners[i] = null;
            }
        }
    }

    private void post(SafeRunnable action) {
        QsThreadPollHelper.post(action);
    }

}
