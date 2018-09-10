package com.qsmaxmin.qsbase.common.utils.glide.transform;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
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
        return createBitmapWithFrame(pool, toTransform);
    }

    private Bitmap createBitmapWithFrame(BitmapPool pool, Bitmap source) {
        if (source == null) return null;
        Bitmap result = pool.get(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(source, 0, 0, paint);

        drawFrame(canvas, result.getWidth(), result.getHeight());
        return result;
    }

    private void drawFrame(Canvas canvas, int width, int height) {
        if (sourceId > 0) {
            Drawable drawable = QsHelper.getInstance().getDrawable(sourceId);
            if (drawable != null) {
                drawable.setBounds(0, 0, width, height);
                drawable.draw(canvas);
            }
        } else if (mBitmap != null) {
            if (mBitmap.getWidth() != width || mBitmap.getHeight() != height) {
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(mBitmap, width, height, false);
                canvas.drawBitmap(scaledBitmap, 0, 0, null);
            } else {
                canvas.drawBitmap(mBitmap, 0, 0, null);
            }
        }
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
