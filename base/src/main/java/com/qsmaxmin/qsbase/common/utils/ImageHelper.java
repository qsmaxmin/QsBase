package com.qsmaxmin.qsbase.common.utils;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.model.GlideUrl;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/6/20 17:35
 * @Description 图片加载帮助类
 */

class ImageHelper {
    private static ImageHelper helper;

    private ImageHelper() {
    }

    static ImageHelper getInstance() {
        if (helper == null) {
            synchronized (ImageHelper.class) {
                if (helper == null) helper = new ImageHelper();
            }
        }
        return helper;
    }

    public Builder createRequest() {
        return new Builder(QsHelper.getInstance().getApplication());
    }

    public Builder createRequest(Context context) {
        return new Builder(context);
    }

    public Builder createRequest(Activity activity) {
        return new Builder(activity);
    }

    public Builder createRequest(FragmentActivity activity) {
        return new Builder(activity);
    }

    public Builder createRequest(android.support.v4.app.Fragment fragment) {
        return new Builder(fragment);
    }

    public Builder createRequest(Fragment fragment) {
        return new Builder(fragment);
    }

    public Builder createRequest(View view) {
        return new Builder(view);
    }

    public void clearMemoryCache() {
        Glide.get(QsHelper.getInstance().getApplication()).clearMemory();
    }

    public void clearDiskCache() {
        Glide.get(QsHelper.getInstance().getApplication()).clearDiskCache();
    }

    public class Builder {
        private RequestManager manager;

        Builder(Context context) {
            manager = Glide.with(context);
        }

        Builder(Activity context) {
            manager = Glide.with(context);
        }

        Builder(Fragment context) {
            manager = Glide.with(context);
        }

        Builder(android.support.v4.app.Fragment context) {
            manager = Glide.with(context);
        }

        Builder(FragmentActivity context) {
            manager = Glide.with(context);
        }

        Builder(View context) {
            manager = Glide.with(context);
        }

        public RequestManager getManager() {
            return manager;
        }

        public RequestBuilder<Drawable> load(String url) {
            return manager.load(new MyGlideUrl(url));
        }

        public RequestBuilder<Drawable> load(Object url) {
            return manager.load(url);
        }
    }

    /**
     * 将图片的url除去param后作为缓存key，避免同一张图片缓存key不一致的问题
     */
    private class MyGlideUrl extends GlideUrl {
        private String cacheKey;

        MyGlideUrl(String url) {
            super(url);
            if (!TextUtils.isEmpty(url)) {
                Uri uri = Uri.parse(url);
                cacheKey = uri.getScheme() + "://" + uri.getHost() + uri.getPath();
            }
        }

        @Override public String getCacheKey() {
            return cacheKey;
        }
    }
}
