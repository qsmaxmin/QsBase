package com.qsmaxmin.qsbase.common.downloader;

/**
 * @CreateBy qsmaxmin
 * @Date 2020/3/16 9:17
 * @Description 下载回调
 */
public interface DownloadListener {

    /**
     * 开始下载，更新progressbar
     */
    void onDownloadStart(QsDownloadModel model);

    /**
     * 正在下载，更新progressbar
     */
    void onDownloading(QsDownloadModel model, long size, long totalSize);

    /**
     * 结束下载，更新progressbar
     */
    void onDownloadComplete(QsDownloadModel model);

    /**
     * 下载失败
     */
    void onDownloadFailed(QsDownloadModel model, String message);
}
