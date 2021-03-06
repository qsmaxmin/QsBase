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
    @NonNull public abstract String getFilePath();

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
     * 下载的文件是否已下载
     * 下载时会先将流写入一个临时文件，下载成功后才会重命名为指定文件
     * 所以判断目标文件是否存在即可判断该文件是否已下载完成
     */
    public final boolean isFileDownloaded() {
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
     * 设置文件已下载长度
     *
     * @see DownloadExecutor
     */
    final void setDownloadedLength(long length) {
        this.downloadedLength = length;
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

    /**
     * 将要被下载的文件已存在时是否还执行下载
     *
     * @see #getFilePath()
     */
    protected boolean shouldDownloadWhenFileExist() {
        return false;
    }

    /**
     * 同一种任务，该回调仅执行一次
     * 该回调在下载线程中执行
     */
    protected void onDownloadStart() {
    }

    /**
     * 同一种任务，该回调仅执行一次
     * 该回调在下载线程中执行
     */
    protected void onDownloading(long size, long totalSize) {
    }

    /**
     * 同一种任务，该回调仅执行一次
     * 该回调在下载线程中执行
     * 特别适合下载完成后的解压，入库等只需要执行一次的操作
     */
    protected void onDownloadComplete() throws Exception {
    }

    /**
     * 同一种任务，该回调仅执行一次
     * 该回调在下载线程中执行
     */
    protected void onDownloadFailed(String message) {
    }

}
