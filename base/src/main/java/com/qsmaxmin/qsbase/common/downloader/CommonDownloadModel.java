package com.qsmaxmin.qsbase.common.downloader;

import android.os.Environment;
import android.util.Base64;

import com.qsmaxmin.qsbase.common.utils.QsHelper;

import androidx.annotation.NonNull;
import okhttp3.Request;

/**
 * @CreateBy qsmaxmin
 * @Date 2020/3/16 9:16
 * @Description 一个公共的下载功能实体类（id是String类型）
 * 注意：
 * 1，文件默认保存路径：sdcard/Download/{packageName}/common/{id}
 * 2，如果仅设置下载链接则id使用下载链接的Base64值，此时需保证下载链接长期不变，否则会导致文件重复下载
 * 3，传入的id需要保证全局唯一，否则相同的id再次下载时会回调文件已下载
 * 次实现类仅适用于下载链接长期不变，或者id能全局唯一的任务。更多自定义功能请继承QsDownloadModel
 * QsDownloadHelper会以QsDownloadModel的子类Class分类下载任务，因此只需保证同一类任务的id唯一即可
 * @see QsDownloadHelper#getDownloader(Class) 根据QsDownloadModel的子类Class创建QsDownloader对象{@link QsDownloadModel}
 * @see QsDownloader#enqueueDownload(QsDownloadModel) 异步执行下载逻辑，
 * @see QsDownloader#executeDownload(QsDownloadModel) 同步执行下载逻辑
 */
public class CommonDownloadModel extends QsDownloadModel<String> {
    private final String id;
    private final String downloadUrl;
    private final String filePath;

    public CommonDownloadModel(@NonNull String downloadUrl) {
        byte[] encode = Base64.encode(downloadUrl.getBytes(), Base64.NO_WRAP | Base64.NO_PADDING);
        String packageName = QsHelper.getApplication().getPackageName();
        this.id = new String(encode);
        this.downloadUrl = downloadUrl;
        this.filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + packageName + "/common/" + getId();
    }

    public CommonDownloadModel(@NonNull String id, @NonNull String downloadUrl) {
        String packageName = QsHelper.getApplication().getPackageName();
        this.id = id;
        this.downloadUrl = downloadUrl;
        this.filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + packageName + "/common/" + getId();
    }

    public CommonDownloadModel(@NonNull String id, @NonNull String downloadUrl, @NonNull String filePath) {
        this.id = id;
        this.downloadUrl = downloadUrl;
        this.filePath = filePath;
    }

    @NonNull @Override public final String getId() {
        return id;
    }

    /**
     * 创建http请求所需参数的封装类，包括下载链接，自定义请求头等
     * 例如：return new Request.Builder().url(downloadUrl).header("Referer", "customReferer");
     */
    @NonNull @Override public Request.Builder getRequest() {
        return new Request.Builder().url(getDownloadUrl());
    }

    @NonNull @Override public final String getFilePath() {
        return filePath;
    }

    @NonNull public final String getDownloadUrl() {
        return downloadUrl;
    }
}
