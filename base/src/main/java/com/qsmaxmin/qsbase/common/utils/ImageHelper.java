package com.qsmaxmin.qsbase.common.utils;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Looper;
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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.qsmaxmin.qsbase.common.aspect.ThreadPoint;
import com.qsmaxmin.qsbase.common.aspect.ThreadType;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.utils.glide.transform.RoundCornerBitmapTransform;

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
        if (helper == null) helper = new ImageHelper();
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
        private boolean enableDefaultHolder = true;
        private RequestManager       manager;
        private Object               mObject;
        private int                  placeholderId;
        private int                  errorId;
        private int                  defaultHolderId;
        private Drawable             placeholderDrawable;
        private Drawable             errorDrawable;
        private boolean              centerCrop;
        private boolean              fitCenter;
        private boolean              centerInside;
        private int                  mCorners;
        private int                  mWidth;
        private int                  mHeight;
        private boolean              noMemoryCache;
        private DiskCacheStrategy    diskCacheStrategy;
        private BitmapTransformation mTransformation;
        private boolean              mIsCircleCrop;

        Builder(Context context) {
            manager = Glide.with(context);
            defaultHolderId = QsHelper.getInstance().getApplication().defaultImageHolder();
        }

        Builder(Activity context) {
            manager = Glide.with(context);
            defaultHolderId = QsHelper.getInstance().getApplication().defaultImageHolder();
        }

        Builder(Fragment context) {
            manager = Glide.with(context);
            defaultHolderId = QsHelper.getInstance().getApplication().defaultImageHolder();
        }

        Builder(android.support.v4.app.Fragment context) {
            manager = Glide.with(context);
            defaultHolderId = QsHelper.getInstance().getApplication().defaultImageHolder();
        }

        Builder(FragmentActivity context) {
            manager = Glide.with(context);
            defaultHolderId = QsHelper.getInstance().getApplication().defaultImageHolder();
        }

        Builder(View context) {
            manager = Glide.with(context);
            defaultHolderId = QsHelper.getInstance().getApplication().defaultImageHolder();
        }

        public RequestManager getManager() {
            return manager;
        }

        public Builder load(String url) {
            if (!TextUtils.isEmpty(url)) this.mObject = createGlideUrl(url, url);
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
                return load(url, urlWithoutKey);
            } else {
                this.mObject = createGlideUrl(url, url);
            }
            return this;
        }

        public Builder load(String url, String cacheKey) {
            if (!TextUtils.isEmpty(url)) this.mObject = createGlideUrl(url, cacheKey);
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

        public Builder roundedCorners(int corners) {
            this.mCorners = corners;
            return this;
        }

        public Builder circleCrop() {
            this.mIsCircleCrop = true;
            return this;
        }

        public Builder transform(BitmapTransformation transformation) {
            this.mTransformation = transformation;
            return this;
        }

        public Builder noMemoryCache() {
            this.noMemoryCache = true;
            return this;
        }

        public Builder noDiskCache() {
            diskCacheStrategy = DiskCacheStrategy.NONE;
            return this;
        }

        public Builder enableDefaultHolder(boolean enable) {
            enableDefaultHolder = enable;
            return this;
        }

        public void into(ImageView view) {
            into(view, null);
        }

        public void into(final ImageView view, final ImageRequestListener listener) {
            RequestBuilder<Drawable> requestBuilder = setRequestOptionsIfNeed(manager.load(mObject));
            if (listener != null) {
                requestBuilder = requestBuilder.listener(new RequestListener<Drawable>() {
                    @Override public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        if (e != null) {
                            listener.onLoadFailed(e.getMessage());
                            for (Throwable t : e.getRootCauses()) {
                                L.e("ImageHelper", "Caused by " + t.getMessage());
                            }
                        } else {
                            listener.onLoadFailed("");
                        }
                        return false;
                    }

                    @Override public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        listener.onSuccess(resource, model);
                        return false;
                    }
                });
            }
            if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
                requestBuilder.into(view);
            } else {
                final RequestBuilder<Drawable> finalRequestBuilder = requestBuilder;
                QsHelper.getInstance().getThreadHelper().getMainThread().execute(new Runnable() {
                    @Override public void run() {
                        finalRequestBuilder.into(view);
                    }
                });
            }
        }


        public Bitmap getBitmap(String url) {
            return getBitmap(url, Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
        }

        public Bitmap getBitmap(Object object) {
            return getBitmap(object, Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
        }

        public Bitmap getBitmap(String url, int width, int height) {
            if (TextUtils.isEmpty(url)) return null;
            return getBitmap(new MyGlideUrl(url), width, height);
        }

        public Bitmap getBitmap(Object object, int width, int height) {
            if (object == null) return null;
            RequestBuilder<Bitmap> requestBuilder = setRequestOptionsIfNeed(manager.asBitmap());
            FutureTarget<Bitmap> submit = requestBuilder.load(object).submit(width, height);
            try {
                return submit.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            return null;
        }

        public Drawable getDrawable(String url) {
            return getDrawable(url, Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
        }

        public Drawable getDrawable(Object object) {
            return getDrawable(object, Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
        }

        public Drawable getDrawable(String url, int width, int height) {
            if (TextUtils.isEmpty(url)) return null;
            return getDrawable(new MyGlideUrl(url), width, height);
        }

        public Drawable getDrawable(Object object, int width, int height) {
            if (object == null) return null;
            RequestBuilder<Drawable> requestBuilder = setRequestOptionsIfNeed(manager.asDrawable());
            FutureTarget<Drawable> submit = requestBuilder.load(object).submit(width, height);
            try {
                return submit.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            return null;
        }


        public File getImageFile(String url) {
            return getImageFile(url, Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
        }

        public File getImageFile(String url, int width, int height) {
            if (TextUtils.isEmpty(url)) return null;
            RequestBuilder<File> requestBuilder = setRequestOptionsIfNeed(manager.asFile());
            FutureTarget<File> submit = requestBuilder.load(new MyGlideUrl(url)).submit(width, height);
            try {
                return submit.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            return null;
        }

        private <T> RequestBuilder<T> setRequestOptionsIfNeed(RequestBuilder<T> requestBuilder) {
            if (shouldCreateRequestOptions()) {
                RequestOptions requestOptions = createRequestOptions();
                return requestBuilder.apply(requestOptions);
            }
            return requestBuilder;
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
                    || errorId > 0
                    || placeholderDrawable != null
                    || errorDrawable != null
                    || (enableDefaultHolder && defaultHolderId > 0)
                    || centerCrop
                    || fitCenter
                    || centerInside
                    || mCorners > 0
                    || (mWidth > 0 && mHeight > 0)
                    || noMemoryCache
                    || diskCacheStrategy != null
                    || mIsCircleCrop
                    || mTransformation != null;
        }

        @NonNull private RequestOptions createRequestOptions() {
            RequestOptions requestOptions = new RequestOptions();
            if (placeholderId > 0) {
                requestOptions = requestOptions.placeholder(placeholderId);
            } else if (placeholderDrawable != null) {
                requestOptions = requestOptions.placeholder(placeholderDrawable);
            } else if (enableDefaultHolder && defaultHolderId > 0) {
                requestOptions = requestOptions.placeholder(defaultHolderId);
            }
            if (errorId > 0) {
                requestOptions = requestOptions.error(placeholderId);
            } else if (errorDrawable != null) {
                requestOptions = requestOptions.error(placeholderId);
            } else if (enableDefaultHolder && defaultHolderId > 0) {
                requestOptions = requestOptions.error(defaultHolderId);
            }
            if (centerCrop) {
                requestOptions = requestOptions.optionalCenterCrop();
            } else if (fitCenter) {
                requestOptions = requestOptions.optionalFitCenter();
            } else if (centerInside) {
                requestOptions = requestOptions.optionalCenterInside();
            }
            if (mIsCircleCrop) {
                requestOptions = requestOptions.optionalCircleCrop();
            }
            if (mCorners > 0) {
                requestOptions = requestOptions.optionalTransform(new RoundCornerBitmapTransform(mCorners));
            }
            if (mTransformation != null) {
                requestOptions = requestOptions.optionalTransform(mTransformation);
            }

            if (mWidth > 0 && mHeight > 0) {
                requestOptions = requestOptions.override(mWidth, mHeight);
            }
            if (noMemoryCache) {
                requestOptions = requestOptions.skipMemoryCache(true);
            }
            if (diskCacheStrategy != null) {
                requestOptions = requestOptions.diskCacheStrategy(diskCacheStrategy);
            }
            return requestOptions;
        }

        private MyGlideUrl createGlideUrl(String url, String cacheKey) {
            MyGlideUrl myGlideUrl = new MyGlideUrl(url);
            myGlideUrl.setCacheKey(cacheKey);
            return myGlideUrl;
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

        void onSuccess(Drawable drawable, Object model);
    }

    public class MyGlideUrl extends GlideUrl {

        private String mCacheKey;

        MyGlideUrl(String url) {
            super(url);
        }

        void setCacheKey(String cacheKey) {
            this.mCacheKey = cacheKey;
        }

        @Override public String getCacheKey() {
            return TextUtils.isEmpty(mCacheKey) ? super.getCacheKey() : mCacheKey;
        }
    }

}
