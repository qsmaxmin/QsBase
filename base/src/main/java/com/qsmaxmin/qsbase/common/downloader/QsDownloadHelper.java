package com.qsmaxmin.qsbase.common.downloader;

import com.qsmaxmin.qsbase.common.log.L;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * @CreateBy qsmaxmin
 * @Date 2020/3/16 9:40
 * @Description download helper
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

    @SuppressWarnings({"unchecked", "CastCanBeRemovedNarrowingVariableType"})
    public static <M extends QsDownloadModel<K>, K> QsDownloader<M, K> getDownloader(Class<M> clazz) {
        if (clazz == null) return null;
        Object object = getInstance().downloaderHolder.get(clazz);
        if (object == null) {
            QsDownloader<M, K> downloader = new QsDownloader<>(getInstance().httpClient, clazz);
            synchronized (getInstance().downloaderHolder) {
                getInstance().downloaderHolder.put(clazz, downloader);
            }
            if (L.isEnable()) {
                String name = downloader.getClass().getSimpleName();
                String className = clazz.getSimpleName();
                int size = getInstance().downloaderHolder.size();
                L.i("QsDownloadHelper", "getDownloader(no cached)....clazz:" + className + ", downloader:" + name + ", cache size:" + size);
            }
            return downloader;
        } else {
            if (L.isEnable()) {
                String name = object.getClass().getSimpleName();
                String className = clazz.getSimpleName();
                int size = getInstance().downloaderHolder.size();
                L.i("QsDownloadHelper", "getDownloader(cached)....clazz:" + className + ", downloader:" + name + ", cache size:" + size);
            }
            return (QsDownloader<M, K>) object;
        }
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
