package com.qsmaxmin.qsbase.common.downloader;

/**
 * @CreateBy qsmaxmin
 * @Date 2020/3/16 9:17
 * @Description download listener
 */
public interface DownloadListener<T extends QsDownloadModel> {

    void onDownloadStart(T model);

    void onDownloading(T model, long size, long totalSize);

    void onDownloadComplete(T model);

    void onDownloadFailed(T model, String message);
}
