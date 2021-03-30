package com.qsmaxmin.qsbase.common.downloader;

/**
 * @CreateBy qsmaxmin
 * @Date 2020/3/16 9:17
 * @Description download listener
 */
public interface DownloadListener<M extends QsDownloadModel> {

    void onDownloadStart(M model);

    void onDownloading(M model, long size, long totalSize);

    void onDownloadComplete(M model);

    void onDownloadFailed(M model, String message);
}
