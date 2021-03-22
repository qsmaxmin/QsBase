package com.qsmaxmin.qsbase.common.downloader;

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

    final void update(long downloadedLength, long totalLength) {
        this.downloadedLength = downloadedLength;
        this.totalLength = totalLength;
    }

    public final long getDownloadedLength() {
        return downloadedLength;
    }

    public final long getTotalLength() {
        return totalLength;
    }

    public final int getDownloadProgress() {
        return totalLength == 0 ? 0 : (int) ((float) downloadedLength / totalLength * 100);
    }

    public final String getTempFilePath() {
        return getFilePath() + "_temp";
    }
}
