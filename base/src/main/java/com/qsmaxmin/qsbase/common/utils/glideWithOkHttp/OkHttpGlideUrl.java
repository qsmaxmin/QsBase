package com.qsmaxmin.qsbase.common.utils.glideWithOkHttp;

import android.text.TextUtils;

import com.bumptech.glide.load.model.GlideUrl;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/12/19 14:12
 * @Description
 */

public class OkHttpGlideUrl extends GlideUrl {

    private String mCacheKey;

    public OkHttpGlideUrl(String url, String cacheKey) {
        super(url);
        if (!TextUtils.isEmpty(cacheKey)) {
            this.mCacheKey = cacheKey;
        } else {
            mCacheKey = url;
        }
    }

    @Override public String getCacheKey() {
        return mCacheKey;
    }
}
