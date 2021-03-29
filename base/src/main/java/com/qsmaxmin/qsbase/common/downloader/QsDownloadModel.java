package com.qsmaxmin.qsbase.common.downloader;

import java.io.File;

import androidx.annotation.NonNull;
import okhttp3.Request;

/**
 * @CreateBy qsmaxmin
 * @Date 2020/3/16 9:16
 * @Description model层接口，必须全部实现
 * 其中，K可以是Long，Integer，String等任意类型，一般是数据库唯一主键类型
 */
public abstract class QsDownloadModel<K> {
    private long downloadedLength;
    private long totalLength;
    private File targetFile;

    /**
     * 同一类型的该对象表示同一类型的下载任务。
     * 同一类型的下载任务，id必须唯一
     * 可以和其它类型的id重复
     */
    public abstract K getId();

    /**
     * 创建http请求所需参数的封装类，包括下载链接，自定义请求头等
     * 例如：return new Request.Builder().url(downloadUrl).header("Referer", "customReferer");
     */
    @NonNull public abstract Request.Builder getRequest();

    /**
     * 下载文件保存的路径
     */
    public abstract String getFilePath();

    /**
     * 获取文件已下载的大小，下载中以后才能获取到正确的值
     *
     * @see DownloadListener#onDownloading(QsDownloadModel, long, long)
     * @see DownloadListener#onDownloadComplete(QsDownloadModel)
     */
    public final long getDownloadedLength() {
        return downloadedLength;
    }

    /**
     * 获取文件已总大小，下载中以后才能获取到正确的值
     *
     * @see DownloadListener#onDownloading(QsDownloadModel, long, long)
     * @see DownloadListener#onDownloadComplete(QsDownloadModel)
     */
    public final long getTotalLength() {
        return totalLength;
    }

    /**
     * 获取文件下载进度，下载中以后才能获取到正确的值
     *
     * @see DownloadListener#onDownloading(QsDownloadModel, long, long)
     * @see DownloadListener#onDownloadComplete(QsDownloadModel)
     */
    public final int getDownloadProgress() {
        return totalLength == 0 ? 0 : (int) (downloadedLength * 100 / totalLength);
    }

    /**
     * 下载的文件是否已存在
     */
    public final boolean isFileExists() {
        return getFile().exists();
    }

    /**
     * 下载的文件
     */
    public final File getFile() {
        if (targetFile == null) targetFile = new File(getFilePath());
        return targetFile;
    }

    /**
     * 设置文件总长度
     * 该api不对外开放
     *
     * @see DownloadExecutor
     */
    final void setTotalLength(long len) {
        this.totalLength = len;
    }

    /**
     * 下载时实时更新进度
     * 该api不对外开放
     *
     * @see DownloadExecutor
     */
    final void addDownloadedLength(long added) {
        this.downloadedLength += added;
    }

    /**
     * 获取临时文件路径
     * 该api不对外开放
     *
     * @see DownloadExecutor
     */
    final String getDownloadTempFilePath() {
        return getFilePath() + "_qs_temp";
    }

}
