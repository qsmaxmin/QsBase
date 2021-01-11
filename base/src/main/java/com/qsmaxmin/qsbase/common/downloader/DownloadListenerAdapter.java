package com.qsmaxmin.qsbase.common.downloader;

/**
 * @CreateBy qsmaxmin
 * @Date 2021/1/11 17:13
 * @Description
 */
public class DownloadListenerAdapter<T extends QsDownloadModel> implements DownloadListener<T> {

    @Override public void onDownloadStart(T model) {

    }

    @Override public void onDownloading(T model, long size, long totalSize) {

    }

    @Override public void onDownloadComplete(T model) {

    }

    @Override public void onDownloadFailed(T model, String message) {

    }
}
