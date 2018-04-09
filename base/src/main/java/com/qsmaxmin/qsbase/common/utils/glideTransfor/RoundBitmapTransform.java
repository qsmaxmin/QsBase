package com.qsmaxmin.qsbase.common.utils.glideTransfor;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
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

public class RoundBitmapTransform extends BitmapTransformation {
    private static final String CUSTOM_ID       = "com.qsmaxmin.qsbase.common.utils.glideTransfor.RoundBitmapTransform";
    private static final byte[] CUSTOM_ID_BYTES = CUSTOM_ID.getBytes(CHARSET);
    private final int radius;

    public RoundBitmapTransform(int radius) {
        this.radius = radius;
    }

    @Override protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        return circleCrop(pool, toTransform);
    }

    private Bitmap circleCrop(BitmapPool pool, Bitmap source) {
        if (source == null) return null;
        Bitmap result = pool.get(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        paint.setShader(new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        paint.setAntiAlias(true);
        RectF rectF = new RectF(0f, 0f, source.getWidth(), source.getHeight());
        canvas.drawRoundRect(rectF, radius, radius, paint);
        return result;
    }

    @Override public void updateDiskCacheKey(MessageDigest messageDigest) {
        messageDigest.update(CUSTOM_ID_BYTES);
        byte[] radiusData = ByteBuffer.allocate(4).putInt(radius).array();
        messageDigest.update(radiusData);
    }
}
