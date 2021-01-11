package com.qsmaxmin.qsbase.common.downloader;

/**
 * @CreateBy qsmaxmin
 * @Date 2021/1/11 17:13
 * @Description
 */
public class DownloadListenerAdapter implements DownloadListener {
    @Override public void onDownloadStart(QsDownloadModel model) {
        
    }

    @Override public void onDownloading(QsDownloadModel model, long size, long totalSize) {

    }

    @Override public void onDownloadComplete(QsDownloadModel model) {

    }

    @Override public void onDownloadFailed(QsDownloadModel model, String message) {

    }
}
