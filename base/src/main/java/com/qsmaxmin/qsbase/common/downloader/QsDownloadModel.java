package com.qsmaxmin.qsbase.common.downloader;

import okhttp3.Request;

/**
 * @CreateBy qsmaxmin
 * @Date 2020/3/16 9:16
 * @Description model层接口，必须全部实现
 */
public interface QsDownloadModel {
    String getId();

    Request getRequest();

    String getFilePath();
}
