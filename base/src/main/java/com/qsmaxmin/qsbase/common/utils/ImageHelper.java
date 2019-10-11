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
import com.bumptech.glide.load.model.Headers;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.qsmaxmin.qsbase.common.aspect.ThreadPoint;
import com.qsmaxmin.qsbase.common.aspect.ThreadType;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.utils.glide.transform.PhotoFrameTransform;
import com.qsmaxmin.qsbase.common.utils.glide.transform.RoundCornersTransformation;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/6/20 17:35
 * @Description 图片加载帮助类
 */

public class ImageHelper {
    public Builder createRequest() {
        return new Builder(QsHelper.getApplication());
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
        Glide.get(QsHelper.getApplication()).clearMemory();
    }

    public void clearDiskCache() {
        Glide.get(QsHelper.getApplication()).clearDiskCache();
    }

    public long getCacheSize() {
        try {
            File photoCacheDir = Glide.getPhotoCacheDir(QsHelper.getApplication());
            return getFolderSize(photoCacheDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String getCacheFormatSize() {
        return Formatter.formatFileSize(QsHelper.getApplication(), getCacheSize());
    }

    public class Builder {
        private boolean                 enableHolder = true;
        private RequestManager          manager;
        private Object                  mObject;
        private Drawable                placeholderDrawable;
        private Drawable                errorDrawable;
        private int                     scaleType;
        private int[]                   mCorners;
        private int                     mWidth;
        private int                     mHeight;
        private boolean                 noMemoryCache;
        private DiskCacheStrategy       diskCacheStrategy;
        private BitmapTransformation    mTransformation;
        private boolean                 mIsCircleCrop;
        private HashMap<String, String> headers;
        private String                  mCacheKey;

        public Object getLoadObject() {
            return mObject;
        }

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
            return load(url, url);
        }

        public Builder load(String url, boolean ignoreParamsKey) {
            if (TextUtils.isEmpty(url)) return this;
            if (ignoreParamsKey) {
                String urlWithoutKey = filterOutUrlParams(url);
                return load(url, urlWithoutKey);
            } else {
                return load(url, url);
            }
        }

        public Builder load(String url, String cacheKey) {
            this.mObject = url;
            this.mCacheKey = cacheKey;
            return this;
        }

        public Builder load(Object object) {
            this.mObject = object;
            return this;
        }

        public Builder resize(int width, int height) {
            this.mWidth = width;
            this.mHeight = height;
            return this;
        }

        public Builder placeholder(int resourceId) {
            this.placeholderDrawable = QsHelper.getDrawable(resourceId);
            return this;
        }

        public Builder placeholder(Drawable drawable) {
            this.placeholderDrawable = drawable;
            return this;
        }

        public Drawable getPlaceholder() {
            return placeholderDrawable;
        }

        public Builder error(int resourceId) {
            this.errorDrawable = QsHelper.getDrawable(resourceId);
            return this;
        }

        public Builder error(Drawable drawable) {
            this.errorDrawable = drawable;
            return this;
        }

        public Drawable getErrorDrawable() {
            return errorDrawable;
        }

        public Builder centerCrop() {
            this.scaleType = 1;
            return this;
        }

        public Builder fitCenter() {
            this.scaleType = 2;
            return this;
        }

        public Builder centerInside() {
            this.scaleType = 3;
            return this;
        }

        public Builder roundedCorners(int... corners) {
            this.mCorners = corners;
            return this;
        }

        public Builder circleCrop() {
            this.mIsCircleCrop = true;
            return this;
        }

        public boolean isCircleCrop() {
            return mIsCircleCrop;
        }

        public Builder addFrame(int frameId) {
            if (frameId != 0) {
                this.mTransformation = new PhotoFrameTransform(frameId);
            }
            return this;
        }

        public Builder addFrame(Bitmap frameBitmap) {
            if (frameBitmap != null) {
                this.mTransformation = new PhotoFrameTransform(frameBitmap);
            }
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

        public boolean isNoMemoryCache() {
            return noMemoryCache;
        }

        public Builder noDiskCache() {
            diskCacheStrategy = DiskCacheStrategy.NONE;
            return this;
        }

        public boolean isNoDiskCache() {
            return diskCacheStrategy == DiskCacheStrategy.NONE;
        }

        public Builder enableHolder(boolean enable) {
            enableHolder = enable;
            return this;
        }

        public boolean isEnableHolder() {
            return enableHolder;
        }

        public Builder addHeader(String key, String value) {
            if (headers == null) headers = new HashMap<>();
            headers.put(key, value);
            return this;
        }

        public HashMap<String, String> getHeaders() {
            return headers;
        }

        public void into(ImageView view) {
            into(view, null);
        }

        public void into(final ImageView view, final ImageRequestListener listener) {
            onLoadImageBefore(this);
            if (mObject instanceof String) {
                String url = (String) this.mObject;
                mObject = createGlideUrl(url, mCacheKey);
            }
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
                            listener.onLoadFailed("unknown error!");
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
                QsHelper.post(new Runnable() {
                    @Override public void run() {
                        finalRequestBuilder.into(view);
                    }
                });
            }
        }

        public Bitmap getBitmap(Object object) {
            return getBitmap(object, Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL, false);
        }

        public Bitmap getBitmap(Object object, boolean ignoreParamsKey) {
            return getBitmap(object, Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL, ignoreParamsKey);
        }

        public Bitmap getBitmap(Object object, int width, int height) {
            return getBitmap(object, width, height, false);
        }

        public Bitmap getBitmap(Object object, int width, int height, boolean ignoreParamsKey) {
            if (object == null) return null;
            onLoadImageBefore(this);
            if (object instanceof String) {
                String url = (String) object;
                object = createGlideUrl(url, ignoreParamsKey ? filterOutUrlParams(url) : url);
            }
            RequestBuilder<Bitmap> requestBuilder = setRequestOptionsIfNeed(manager.asBitmap());
            FutureTarget<Bitmap> submit = requestBuilder.load(object).submit(width, height);
            try {
                return submit.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            return null;
        }

        public Drawable getDrawable(Object object) {
            return getDrawable(object, Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL, false);
        }

        public Drawable getDrawable(Object object, boolean ignoreParamsKey) {
            return getDrawable(object, Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL, ignoreParamsKey);
        }

        public Drawable getDrawable(Object object, int width, int height) {
            return getDrawable(object, width, height, false);
        }

        public Drawable getDrawable(Object object, int width, int height, boolean ignoreParamsKey) {
            if (object == null) return null;
            onLoadImageBefore(this);
            if (object instanceof String) {
                String url = (String) object;
                object = createGlideUrl(url, ignoreParamsKey ? filterOutUrlParams(url) : url);
            }
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
            return getImageFile(url, Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL, false);
        }

        public File getImageFile(String url, boolean ignoreParamsKey) {
            return getImageFile(url, Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL, ignoreParamsKey);
        }

        public File getImageFile(String url, int width, int height) {
            return getImageFile(url, width, height, false);
        }

        public File getImageFile(String url, int width, int height, boolean ignoreParamsKey) {
            if (TextUtils.isEmpty(url)) return null;
            onLoadImageBefore(this);
            QsGlideUrl object = createGlideUrl(url, ignoreParamsKey ? filterOutUrlParams(url) : url);
            RequestBuilder<File> requestBuilder = setRequestOptionsIfNeed(manager.asFile());
            FutureTarget<File> submit = requestBuilder.load(object).submit(width, height);
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
            return placeholderDrawable != null
                    || errorDrawable != null
                    || scaleType > 0
                    || mCorners != null
                    || (mWidth > 0 && mHeight > 0)
                    || noMemoryCache
                    || diskCacheStrategy != null
                    || mIsCircleCrop
                    || mTransformation != null;
        }

        @NonNull private RequestOptions createRequestOptions() {
            RequestOptions requestOptions = new RequestOptions();
            if (enableHolder) {
                if (placeholderDrawable != null) requestOptions = requestOptions.placeholder(placeholderDrawable);
                if (errorDrawable != null) requestOptions = requestOptions.error(errorDrawable);
            }

            if (scaleType == 1) {
                requestOptions = requestOptions.optionalCenterCrop();
            } else if (scaleType == 2) {
                requestOptions = requestOptions.optionalFitCenter();
            } else if (scaleType == 3) {
                requestOptions = requestOptions.optionalCenterInside();
            }

            if (mTransformation != null) {
                if (mIsCircleCrop) {
                    requestOptions = requestOptions.transforms(new CircleCrop(), mTransformation);
                } else if (mCorners != null) {
                    if (mCorners.length == 1) {
                        if (mCorners[0] > 0) requestOptions = requestOptions.transforms(new RoundedCorners(mCorners[0]), mTransformation);
                    } else {
                        requestOptions = requestOptions.transforms(new RoundCornersTransformation(mCorners), mTransformation);
                    }
                } else {
                    requestOptions = requestOptions.optionalTransform(mTransformation);
                }
            } else if (mIsCircleCrop) {
                requestOptions = requestOptions.optionalCircleCrop();
            } else if (mCorners != null) {
                if (mCorners.length == 1) {
                    if (mCorners[0] > 0) requestOptions = requestOptions.optionalTransform(new RoundedCorners(mCorners[0]));
                } else {
                    requestOptions = requestOptions.optionalTransform(new RoundCornersTransformation(mCorners));
                }
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

        private QsGlideUrl createGlideUrl(String url, String cacheKey) {
            if (TextUtils.isEmpty(url)) return null;
            QsGlideUrl qsGlideUrl;
            if (this.headers != null && !headers.isEmpty()) {
                LazyHeaders.Builder builder = new LazyHeaders.Builder();
                for (String key : this.headers.keySet()) {
                    builder.addHeader(key, headers.get(key));
                }
                LazyHeaders lazyHeaders = builder.build();
                qsGlideUrl = new QsGlideUrl(url, lazyHeaders);
            } else {
                qsGlideUrl = new QsGlideUrl(url);
            }
            qsGlideUrl.setCacheKey(cacheKey);
            return qsGlideUrl;
        }
    }

    @NonNull private String filterOutUrlParams(String url) {
        Uri uri = Uri.parse(url);
        return uri.getScheme() + "://" + uri.getHost() + uri.getPath();
    }

    private void onLoadImageBefore(Builder builder) {
        QsHelper.getAppInterface().onCommonLoadImage(builder);
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

    public static class QsGlideUrl extends GlideUrl {
        private final String url;
        private       String cacheKey;

        public QsGlideUrl(String url) {
            super(url);
            this.url = url;
        }

        public QsGlideUrl(String url, Headers headers) {
            super(url, headers);
            this.url = url;
        }

        void setCacheKey(String cacheKey) {
            this.cacheKey = cacheKey;
        }

        public String getUrl() {
            return url;
        }

        @Override public String getCacheKey() {
            return TextUtils.isEmpty(cacheKey) ? super.getCacheKey() : cacheKey;
        }
    }

}
