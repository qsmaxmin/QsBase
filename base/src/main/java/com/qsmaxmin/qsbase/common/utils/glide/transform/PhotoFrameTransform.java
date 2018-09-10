package com.qsmaxmin.qsbase.common.utils.glide.transform;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.qsmaxmin.qsbase.common.utils.QsHelper;

import java.nio.ByteBuffer;
import java.security.MessageDigest;

/**
 * @CreateBy qsmaxmin
 * @Date 2018/4/9 11:23
 * @Description
 */

public class PhotoFrameTransform extends BitmapTransformation {
    private static final String CUSTOM_ID       = "com.qsmaxmin.qsbase.common.utils.glide.transformation.PhotoFrameTransform";
    private static final byte[] CUSTOM_ID_BYTES = CUSTOM_ID.getBytes(CHARSET);
    private int    sourceId;
    private Bitmap mBitmap;

    public PhotoFrameTransform(@DrawableRes int sourceId) {
        this.sourceId = sourceId;
    }

    public PhotoFrameTransform(@NonNull Bitmap frameBitmap) {
        this.mBitmap = frameBitmap;
    }

    @Override protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        return addLabel(pool, toTransform);
    }

    private Bitmap addLabel(BitmapPool pool, Bitmap source) {
        if (source == null) return null;
        Bitmap result = pool.get(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(source, 0, 0, paint);

        Bitmap scaledBitmap = getFrameBitmap(result.getWidth(), result.getHeight());
        if (scaledBitmap != null) canvas.drawBitmap(scaledBitmap, 0, 0, paint);
        return result;
    }

    private Bitmap getFrameBitmap(int width, int height) {
        if (sourceId > 0) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(QsHelper.getInstance().getApplication().getResources(), sourceId, options);
            int halfWidth = options.outWidth / 2;
            int halfHeight = options.outHeight / 2;
            int scaleRatio = 1;
            while (halfWidth > width && halfHeight > height) {
                scaleRatio *= 2;
                halfWidth /= 2;
                halfHeight /= 2;
            }
            options.inJustDecodeBounds = false;
            options.inSampleSize = scaleRatio;
            Bitmap frameBitmap = BitmapFactory.decodeResource(QsHelper.getInstance().getApplication().getResources(), sourceId, options);
            return Bitmap.createScaledBitmap(frameBitmap, width, height, false);
        } else if (mBitmap != null) {
            if (mBitmap.getWidth() != width || mBitmap.getHeight() != height) {
                return Bitmap.createScaledBitmap(mBitmap, width, height, false);
            }
            return mBitmap;
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof PhotoFrameTransform) && ((PhotoFrameTransform) o).sourceId == sourceId;
    }

    @Override
    public int hashCode() {
        return CUSTOM_ID.hashCode() + sourceId;
    }

    @Override public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update(CUSTOM_ID_BYTES);
        byte[] idData = ByteBuffer.allocate(4).putInt(sourceId).array();
        messageDigest.update(idData);
    }
}
