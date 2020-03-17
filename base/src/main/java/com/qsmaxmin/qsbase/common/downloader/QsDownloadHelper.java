package com.qsmaxmin.qsbase.common.downloader;

import com.qsmaxmin.qsbase.common.log.L;

import java.util.HashMap;

/**
 * @CreateBy qsmaxmin
 * @Date 2020/3/16 9:40
 * @Description 下载帮助类
 */
public class QsDownloadHelper {
    private final  HashMap<Class, QsDownloader> downloaderHolder = new HashMap<>();
    private static QsDownloadHelper             helper;

    private QsDownloadHelper() {
    }

    @SuppressWarnings({"unchecked", "CastCanBeRemovedNarrowingVariableType"})
    public static <M extends QsDownloadModel> QsDownloader<M> getDownloader(Class<M> clazz) {
        Object object = getInstance().downloaderHolder.get(clazz);
        if (object == null) {
            QsDownloader<M> downloader = new QsDownloader<>();
            getInstance().downloaderHolder.put(clazz, downloader);
            if (L.isEnable()) {
                String downloaderName = downloader.getClass().getSimpleName();
                L.i("QsDownloadHelper", "getDownloader(no cached)....clazz:" + clazz.getSimpleName() + ", downloader:" + downloaderName);
            }
            return downloader;
        } else {
            if (L.isEnable()) {
                String downloader = object.getClass().getSimpleName();
                L.i("QsDownloadHelper", "getDownloader(cached)....clazz:" + clazz.getSimpleName() + ", downloader:" + downloader);
            }
            return (QsDownloader<M>) object;
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
            if (helper.downloaderHolder.isEmpty()) {
                synchronized (helper.downloaderHolder) {
                    for (Class clazz : helper.downloaderHolder.keySet()) {
                        QsDownloader downloader = helper.downloaderHolder.get(clazz);
                        if (downloader != null) downloader.release();
                    }
                }
            }
            helper.downloaderHolder.clear();
            helper = null;
        }
    }

}
