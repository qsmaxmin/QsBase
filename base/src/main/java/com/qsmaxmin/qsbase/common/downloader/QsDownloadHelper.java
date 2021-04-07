package com.qsmaxmin.qsbase.common.downloader;

import com.qsmaxmin.qsbase.common.log.L;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * @CreateBy qsmaxmin
 * @Date 2020/3/16 9:40
 * @Description 文件下载管理器，统一管理所有类型的下载任务
 * 根据下载实体(QsDownloadModel的子类)的不同，创建不同的QsDownload对象。
 * @see QsDownloader
 * @see QsDownloadModel
 */
public class QsDownloadHelper {
    private static QsDownloadHelper             helper;
    private final  OkHttpClient                 httpClient;
    private final  HashMap<Class, QsDownloader> downloaderHolder;

    private QsDownloadHelper() {
        downloaderHolder = new HashMap<>();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(45, TimeUnit.SECONDS);
        builder.readTimeout(45, TimeUnit.SECONDS);
        builder.writeTimeout(45, TimeUnit.SECONDS);
        httpClient = builder.build();
    }

    /**
     * 下载步骤：
     * 1，新建一个类继承QsDownloadModel并定义好id的泛型(String,Long等)，实现抽象方法。例如{@link CommonDownloadModel}
     * 2，调用此方法，传入刚新建的类的Class对象，可返回一个QsDownloader对象
     * 3，如果需要的话，给QsDownloader设置全局监听{@link QsDownloader#registerGlobalDownloadListener(DownloadListener),QsDownloader#removeGlobalDownloadListener(DownloadListener)}
     * 4，调用QsDownloader的enqueueDownload或executeDownload方法开始下载{@link QsDownloader#enqueueDownload(QsDownloadModel),QsDownloader#executeDownload(QsDownloadModel)}
     *
     * @param clazz QsDownloadModel的子类Class，相同的Class被认为是同一类任务，同类任务需保证id唯一
     */
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
                    if (L.isEnable()) L.i("QsDownloadHelper", "getDownloader....create new QsDownloader by '" + clazz.getSimpleName()
                            + "', cached size:" + instance.downloaderHolder.size());
                }
            }
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

    /**
     * 清空所有下载任务，释放内存
     */
    public static void releaseAll() {
        if (helper != null) {
            if (L.isEnable()) L.i("QsDownloadHelper", "release........");
            if (!helper.downloaderHolder.isEmpty()) {
                synchronized (helper.downloaderHolder) {
                    for (QsDownloader downloader : helper.downloaderHolder.values()) {
                        if (downloader != null) downloader.release();
                    }
                    helper.downloaderHolder.clear();
                }
            }
            helper.httpClient.dispatcher().cancelAll();
            helper = null;
        }
    }

    /**
     * 清空指定类型的下载任务
     */
    public static <M extends QsDownloadModel> void release(Class<M> clazz) {
        synchronized (getInstance().downloaderHolder) {
            QsDownloader downloader = getInstance().downloaderHolder.remove(clazz);
            if (downloader != null) {
                downloader.release();
            }
        }
    }
}
