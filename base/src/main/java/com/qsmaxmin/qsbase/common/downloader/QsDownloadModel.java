package com.qsmaxmin.qsbase.common.downloader;

import okhttp3.Request;

/**
 * @CreateBy qsmaxmin
 * @Date 2020/3/16 9:16
 * @Description model层接口，必须全部实现
 * 其中，K可以是Long，Integer，String等任意类型，一般是数据库唯一主键类型
 */
public interface QsDownloadModel<K> {
    /**
     * 同一类型的该对象表示同一类型的下载任务。
     * 同一类型的下载任务，id必须唯一
     * 可以和其它类型的id重复
     */
    K getId();

    Request.Builder getRequest();

    String getFilePath();
}
