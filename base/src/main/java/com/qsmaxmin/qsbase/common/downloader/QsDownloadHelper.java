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
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * @CreateBy qsmaxmin
 * @Date 2020/3/16 9:40
 * @Description 下载帮助类
 */
public class QsDownloadHelper {
    private static QsDownloadHelper                    helper;
    private final  String                              TAG            = "BaseDownloadHelper";
    private final  HashMap<String, QsDownloadExecutor> executorMap    = new HashMap<>();
    private final  List<DownloadListener>              globeListeners = new ArrayList<>();
    private        OkHttpClient                        httpClient;

    private QsDownloadHelper() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(45, TimeUnit.SECONDS);
        builder.readTimeout(45, TimeUnit.SECONDS);
        builder.writeTimeout(45, TimeUnit.SECONDS);
        httpClient = builder.build();
    }

    public static QsDownloadHelper getInstance() {
        if (helper == null) {
            synchronized (QsDownloadHelper.class) {
                if (helper == null) helper = new QsDownloadHelper();
            }
        }
        return helper;
    }

    /**
     * 执行下载动作
     */
    @Permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void startDownload(final QsDownloadModel model) {
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

        Request request = model.getRequest();
        if (request == null) {
            if (L.isEnable()) L.e(TAG, "startDownload..." + model.getClass().getSimpleName() + ".getRequest() return null");
            return;
        }

        if (isDownloading(model)) {
            if (L.isEnable()) L.e(TAG, "startDownload...do not download again，id:" + model.getId());
            return;
        }

        L.i(TAG, "startDownload...started, id:" + model.toString());
        File zipFile = new File(model.getFilePath());
        if (zipFile.exists()) {
            boolean delete = zipFile.delete();
            L.i(TAG, "delete old file..........delete:" + delete);
        } else {
            File parentFile = zipFile.getParentFile();
            if (!parentFile.exists() && !parentFile.mkdirs()) {
                L.e(TAG, "create dir failed...dir:" + parentFile.getPath());
                postDownloadFailed(model, "create dir failed, dir:" + parentFile.getPath());
                return;
            }
        }
        QsDownloadExecutor executor = new QsDownloadExecutor(model);
        executorMap.put(model.getId(), executor);
        executor.start(request);
    }

    /**
     * 是否正在下载
     */
    private boolean isDownloading(QsDownloadModel m) {
        return executorMap.get(m.getId()) != null;
    }

    public void cancelAll() {
        if (L.isEnable()) L.i(TAG, "cancelAll........");
        httpClient.dispatcher().cancelAll();
    }

    public static void release() {
        if (helper != null) {
            if (L.isEnable()) L.i(helper.TAG, "release........");
            helper.executorMap.clear();
            helper.globeListeners.clear();
            helper.httpClient.dispatcher().cancelAll();
            helper = null;
        }
    }


    OkHttpClient getClient() {
        return httpClient;
    }

    void removeExecutorFromTask(QsDownloadModel model) {
        executorMap.remove(model.getId());
    }

    /**
     * 添加全局监听
     * 因为是全局监听，所以需要做好QsDownloadModel id的唯一性
     *
     * @see QsDownloadModel#getId()
     * @see DownloadListener#onDownloadStart(QsDownloadModel) ...
     */
    public void registerGlobalDownloadListener(DownloadListener listener) {
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
     *
     * @see QsDownloadModel#getId()
     * @see DownloadListener#onDownloadStart(QsDownloadModel) ...
     */
    public void removeGlobalDownloadListener(DownloadListener listener) {
        synchronized (globeListeners) {
            globeListeners.remove(listener);
        }
    }

    void postDownloadStart(final QsDownloadModel model) {
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

    void postDownloading(final QsDownloadModel model, final long size, final long totalSize) {
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

    void postDownloadComplete(final QsDownloadModel model) {
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

    void postDownloadFailed(final QsDownloadModel model, final String msg) {
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

    private void callbackDownloadStart(QsDownloadModel model) {
        Object[] callbacks = collectCallbacks();
        if (callbacks != null) {
            for (Object callback : callbacks) {
                if (callback != null) {
                    ((DownloadListener) callback).onDownloadStart(model);
                }
            }
        }
    }

    private void callbackDownloading(QsDownloadModel model, long size, long totalSize) {
        Object[] callbacks = collectCallbacks();
        if (callbacks != null) {
            for (Object callback : callbacks) {
                if (callback != null) {
                    ((DownloadListener) callback).onDownloading(model, size, totalSize);
                }
            }
        }
    }

    private void callbackDownloadComplete(QsDownloadModel model) {
        Object[] callbacks = collectCallbacks();
        if (callbacks != null) {
            for (Object callback : callbacks) {
                if (callback != null) {
                    ((DownloadListener) callback).onDownloadComplete(model);
                }
            }
        }
    }

    private void callbackDownloadFailed(QsDownloadModel model, String msg) {
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
