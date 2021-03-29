package com.qsmaxmin.qsbase.common.downloader;

import android.util.Base64;

import com.qsmaxmin.qsbase.common.utils.QsHelper;

import androidx.annotation.NonNull;
import okhttp3.Request;

/**
 * @CreateBy qsmaxmin
 * @Date 2020/3/16 9:16
 * @Description 一个简单的下载功能实体类，只需要提供一个下载链接即可下载
 * 注意：
 * 1，文件保存路径：当前app在sd卡上的cache目录 / 当前类名的Base64值 / 下载链接的Base64值
 * 2，下载链接必须固定不变，同时继承该类的子类类名也不可改变，否则会导致重复下载和重复保存
 */
public abstract class DownloadModelSimple extends QsDownloadModel<String> {

    @Override public String getId() {
        return encodeStr(getDownloadUrl());
    }

    @NonNull @Override public Request.Builder getRequest() {
        return new Request.Builder().url(getDownloadUrl());
    }

    @Override public String getFilePath() {
        return QsHelper.getApplication().getExternalCacheDir() + "/" + encodeStr(getClass().getName()) + "/" + getId();
    }

    public abstract String getDownloadUrl();

    private String encodeStr(String str) {
        byte[] encode = Base64.encode(str.getBytes(), Base64.NO_WRAP | Base64.NO_PADDING);
        return new String(encode);
    }
}
