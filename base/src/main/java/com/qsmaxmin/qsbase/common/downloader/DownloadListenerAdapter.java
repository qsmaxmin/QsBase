package com.qsmaxmin.qsbase.common.downloader;

/**
 * @CreateBy qsmaxmin
 * @Date 2021/1/11 17:13
 * @Description
 */
public class DownloadListenerAdapter<M extends QsDownloadModel> implements DownloadListener<M> {

    @Override public void onDownloadStart(M model) {

    }

    @Override public void onDownloading(M model, long size, long totalSize) {

    }

    @Override public void onDownloadComplete(M model) {

    }

    @Override public void onDownloadFailed(M model, String message) {

    }
}
