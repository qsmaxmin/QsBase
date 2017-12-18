package com.qsmaxmin.qsbase.common.utils;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.qsmaxmin.qsbase.common.aspect.ThreadPoint;
import com.qsmaxmin.qsbase.common.aspect.ThreadType;
import com.qsmaxmin.qsbase.common.log.L;

import java.io.File;
import java.util.concurrent.ExecutionException;

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

    @ThreadPoint(ThreadType.MAIN) public void clearMemoryCache() {
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
        private Object         mObject;
        private Drawable       placeholderDrawable;
        private int            placeholderId;
        private int            errorId;
        private Drawable       errorDrawable;
        private boolean        centerCrop;
        private boolean        fitCenter;
        private boolean        centerInside;
        private int            mCorners;
        private int            mWidth;
        private int            mHeight;

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

        public Builder load(String url) {
            this.mObject = url;
            return this;
        }

        public Builder load(Object object) {
            this.mObject = object;
            return this;
        }

        public Builder load(String url, boolean ignoreParamsKey) {
            if (TextUtils.isEmpty(url)) return this;
            if (ignoreParamsKey) {
                Uri uri = Uri.parse(url);
                String urlWithoutKey = uri.getScheme() + "://" + uri.getHost() + uri.getPath();
                this.mObject = new MyGlideUrl(url, urlWithoutKey);
            } else {
                this.mObject = url;
            }
            return this;
        }

        public Builder load(String url, String cacheKey) {
            this.mObject = new MyGlideUrl(url, cacheKey);
            return this;
        }

        /**
         * @deprecated 已过时，用{@link #load(String, boolean)}替代
         */
        public Builder loadIgnoreParamsKey(String url) {
            if (TextUtils.isEmpty(url)) return this;
            Uri uri = Uri.parse(url);
            String urlWithoutKey = uri.getScheme() + "://" + uri.getHost() + uri.getPath();
            this.mObject = new MyGlideUrl(url, urlWithoutKey);
            return this;
        }

        public Builder resize(int width, int height) {
            this.mWidth = width;
            this.mHeight = height;
            return this;
        }

        public Builder placeholder(int resourceId) {
            this.placeholderId = resourceId;
            return this;
        }

        public Builder placeholder(Drawable drawable) {
            this.placeholderDrawable = drawable;
            return this;
        }

        public Builder error(int resourceId) {
            this.errorId = resourceId;
            return this;
        }

        public Builder error(Drawable drawable) {
            this.errorDrawable = drawable;
            return this;
        }

        public Builder centerCrop() {
            this.centerCrop = true;
            return this;
        }

        public Builder fitCenter() {
            this.fitCenter = true;
            return this;
        }

        public Builder centerInside() {
            this.centerInside = true;
            return this;
        }

        public Builder RoundedCorners(int corners) {
            this.mCorners = corners;
            return this;
        }

        public void into(ImageView view) {
            into(view, null);
        }

        public void into(ImageView view, final ImageRequestListener listener) {
            RequestBuilder<Drawable> requestBuilder;
            if (mObject != null) {
                requestBuilder = manager.load(mObject);
            } else {
                L.e("ImageHelper", "method error, load(...) params is empty....");
                return;
            }
            setRequestOptionsIfNeed(requestBuilder);
            if (listener != null) {
                requestBuilder.listener(new RequestListener<Drawable>() {
                    @Override public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        listener.onLoadFailed(e == null ? "" : e.getMessage());
                        return false;
                    }

                    @Override public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        listener.onSuccess(resource);
                        return false;
                    }
                });
            }
            requestBuilder.into(view);
        }

        public Bitmap getBitmap(String url) {
            return getBitmap(url, Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
        }

        public Drawable getDrawable(String url) {
            return getDrawable(url, Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
        }

        public File getImageFile(String url) {
            return getImageFile(url, Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
        }

        public Bitmap getBitmap(String url, int width, int height) {
            if (TextUtils.isEmpty(url)) return null;
            RequestBuilder<Bitmap> requestBuilder = manager.asBitmap();
            setRequestOptionsIfNeed(requestBuilder);
            FutureTarget<Bitmap> submit = requestBuilder.load(url).submit(width, height);
            try {
                return submit.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            return null;
        }

        public Drawable getDrawable(String url, int width, int height) {
            if (TextUtils.isEmpty(url)) return null;
            RequestBuilder<Drawable> requestBuilder = manager.asDrawable();
            setRequestOptionsIfNeed(requestBuilder);
            FutureTarget<Drawable> submit = requestBuilder.load(url).submit(width, height);
            try {
                return submit.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            return null;
        }

        public File getImageFile(String url, int width, int height) {
            if (TextUtils.isEmpty(url)) return null;
            RequestBuilder<File> requestBuilder = manager.asFile();
            setRequestOptionsIfNeed(requestBuilder);
            FutureTarget<File> submit = requestBuilder.load(url).submit(width, height);
            try {
                return submit.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            return null;
        }

        private void setRequestOptionsIfNeed(RequestBuilder requestBuilder) {
            if (shouldCreateRequestOptions()) {
                RequestOptions requestOptions = createRequestOptions();
                requestBuilder.apply(requestOptions);
            }
        }

        /**
         * private Drawable       placeholderDrawable;
         * private int            placeholderId;
         * private int            errorId;
         * private Drawable       errorDrawable;
         * private boolean        centerCrop;
         * private boolean        fitCenter;
         * private boolean        centerInside;
         * private int            mCorners;
         * private int            mWidth;
         * private int            mHeight;
         */
        private boolean shouldCreateRequestOptions() {
            return placeholderId > 0
                    || placeholderDrawable != null
                    || errorId > 0
                    || errorDrawable != null
                    || centerCrop
                    || fitCenter
                    || centerInside
                    || mCorners > 0
                    || (mWidth > 0 && mHeight > 0);
        }

        @NonNull private RequestOptions createRequestOptions() {
            RequestOptions requestOptions = new RequestOptions();
            if (placeholderId > 0) {
                requestOptions.placeholder(placeholderId);
            } else if (placeholderDrawable != null) {
                requestOptions.placeholder(placeholderDrawable);
            }
            if (errorId > 0) {
                requestOptions.error(placeholderId);
            } else if (errorDrawable != null) {
                requestOptions.error(placeholderId);
            }
            if (centerCrop) requestOptions.centerCrop();
            if (fitCenter) requestOptions.fitCenter();
            if (centerInside) requestOptions.centerInside();
            if (mCorners > 0) requestOptions.transform(new RoundedCorners(mCorners));
            if (mWidth > 0 && mHeight > 0) requestOptions.override(mWidth, mHeight);
            return requestOptions;
        }
    }

    /**
     * 将图片的url除去param后作为缓存key，避免同一张图片缓存key不一致的问题
     */
    private class MyGlideUrl extends GlideUrl {
        private String mCacheKey;

        MyGlideUrl(String url, String cacheKey) {
            super(url);
            if (!TextUtils.isEmpty(cacheKey)) {
                this.mCacheKey = cacheKey;
            }
        }

        @Override public String getCacheKey() {
            return mCacheKey;
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


    public interface ImageRequestListener {

        void onLoadFailed(String message);

        void onSuccess(Drawable drawable);
    }

}
