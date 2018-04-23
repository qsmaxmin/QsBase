package com.qsmaxmin.qsbase.common.utils.glide.transform;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.support.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.nio.ByteBuffer;
import java.security.MessageDigest;

/**
 * @CreateBy qsmaxmin
 * @Date 2018/4/9 11:23
 * @Description
 */

public class RoundCornerBitmapTransform extends BitmapTransformation {
    private static final String CUSTOM_ID       = "com.qsmaxmin.qsbase.common.utils.glide.transformation.RoundBitmapTransform";
    private static final byte[] CUSTOM_ID_BYTES = CUSTOM_ID.getBytes(CHARSET);
    private final int radius;

    public RoundCornerBitmapTransform(int radius) {
        this.radius = radius;
    }

    @Override protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        return circleCrop(pool, toTransform);
    }

    private Bitmap circleCrop(BitmapPool pool, Bitmap source) {
        if (source == null) return null;
        Bitmap result = pool.get(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        RectF rectF = new RectF(0f, 0f, source.getWidth(), source.getHeight());

        Canvas canvas = new Canvas(result);
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        canvas.drawRoundRect(rectF, radius, radius, paint);
        canvas.setBitmap(null);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof RoundCornerBitmapTransform) && ((RoundCornerBitmapTransform) o).radius == radius;
    }

    @Override
    public int hashCode() {
        return CUSTOM_ID.hashCode() + radius;
    }

    @Override public void updateDiskCacheKey(MessageDigest messageDigest) {
        messageDigest.update(CUSTOM_ID_BYTES);
        byte[] radiusData = ByteBuffer.allocate(4).putInt(radius).array();
        messageDigest.update(radiusData);
    }
}
