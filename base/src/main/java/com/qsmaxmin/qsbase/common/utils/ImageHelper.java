package com.qsmaxmin.qsbase.common.utils;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.model.GlideUrl;

import java.io.File;
import java.math.BigDecimal;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/6/20 17:35
 * @Description 图片加载帮助类
 */

public class ImageHelper {
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

    public long getCacheSize() {
        try {
            File photoCacheDir = Glide.getPhotoCacheDir(QsHelper.getInstance().getApplication());
            return getFolderSize(photoCacheDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String getCacheFormatSize() {
        return Formatter.formatFileSize(QsHelper.getInstance().getApplication(), getCacheSize());
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
            return manager.load(url);
        }

        public RequestBuilder<Drawable> load(Object object) {
            return manager.load(object);
        }

        public RequestBuilder<Drawable> loadIgnoreParamsKey(String url) {
            return manager.load(new MyGlideUrl(url));
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

    private long getFolderSize(File file) {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (File aFileList : fileList) {
                if (aFileList.isDirectory()) {
                    size = size + getFolderSize(aFileList);
                } else {
                    size = size + aFileList.length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }


    private static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return size + "Byte";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);

        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
    }

}
