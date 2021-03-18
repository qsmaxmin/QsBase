package com.qsmaxmin.qsbase.common.downloader;

import com.qsmaxmin.qsbase.common.log.L;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * @CreateBy qsmaxmin
 * @Date 2020/3/16 9:40
 * @Description 文件下载管理器，统一管理所有类型的下载任务
 * 根据下载实体(QsDownloadModel)的不同，创建不同的QsDownload对象。
 * @see QsDownloader
 * @see QsDownloadModel
 */
public class QsDownloadHelper {
    private final  HashMap<Class, QsDownloader> downloaderHolder = new HashMap<>();
    private static QsDownloadHelper             helper;
    private final  OkHttpClient                 httpClient;

    private QsDownloadHelper() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(45, TimeUnit.SECONDS);
        builder.readTimeout(45, TimeUnit.SECONDS);
        builder.writeTimeout(45, TimeUnit.SECONDS);
        httpClient = builder.build();
    }

    @SuppressWarnings({"unchecked"})
    public static <M extends QsDownloadModel<K>, K> QsDownloader<M, K> getDownloader(Class<M> clazz) {
        if (clazz == null) return null;
        QsDownloadHelper instance = getInstance();
        QsDownloader<M, K> downloader = instance.downloaderHolder.get(clazz);
        if (downloader == null) {
            synchronized (instance.downloaderHolder) {
                downloader = instance.downloaderHolder.get(clazz);
                if (downloader == null) {
                    downloader = new QsDownloader<>(instance.httpClient, clazz);
                    instance.downloaderHolder.put(clazz, downloader);
                    if (L.isEnable()) L.i("QsDownloadHelper", "getDownloader(no cached)....clazz:" + clazz.getSimpleName()
                            + ", downloader:" + downloader.getClass().getSimpleName() + ", cache size:" + instance.downloaderHolder.size());
                } else {
                    if (L.isEnable()) L.i("QsDownloadHelper", "getDownloader(cached)....clazz:" + clazz.getSimpleName()
                            + ", downloader:" + downloader.getClass().getSimpleName() + ", cache size:" + instance.downloaderHolder.size());
                }
            }
        } else {
            if (L.isEnable()) L.i("QsDownloadHelper", "getDownloader(cached)....clazz:" + clazz.getSimpleName()
                    + ", downloader:" + downloader.getClass().getSimpleName() + ", cache size:" + instance.downloaderHolder.size());
        }
        return downloader;
    }

    private static QsDownloadHelper getInstance() {
        if (helper == null) {
            synchronized (QsDownloadHelper.class) {
                if (helper == null) helper = new QsDownloadHelper();
            }
        }
        return helper;
    }

    public static <M extends QsDownloadModel> void releaseAll() {
        if (helper != null) {
            if (L.isEnable()) L.i("QsDownloadHelper", "release........");
            if (!helper.downloaderHolder.isEmpty()) {
                synchronized (helper.downloaderHolder) {
                    for (Class clazz : helper.downloaderHolder.keySet()) {
                        QsDownloader downloader = helper.downloaderHolder.get(clazz);
                        if (downloader != null) downloader.release();
                    }
                    helper.downloaderHolder.clear();
                }
            }
            helper.httpClient.dispatcher().cancelAll();
            helper = null;
        }
    }

    public static <M extends QsDownloadModel> void release(Class<M> clazz) {
        Object object = getInstance().downloaderHolder.get(clazz);
        if (object != null) {
            QsDownloader downloader = (QsDownloader) object;
            downloader.release();
        }
    }

}
