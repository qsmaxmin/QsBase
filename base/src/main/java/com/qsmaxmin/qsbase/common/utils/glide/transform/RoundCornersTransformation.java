package com.qsmaxmin.qsbase.common.utils.glide.transform;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.security.MessageDigest;
import java.util.Arrays;

/**
 * @CreateBy qsmaxmin
 * @Date 2018/9/18 10:40
 * @Description
 */

public class RoundCornersTransformation extends BitmapTransformation {

    private static final String CUSTOM_ID = "com.qsmaxmin.qsbase.common.utils.glide.transform.RoundCornersTransformation";

    private int[] mCorners;

    public RoundCornersTransformation(@NonNull int... corners) {
        mCorners = new int[4];
        for (int i = 0; i < 4; i++) {
            mCorners[i] = i < corners.length ? corners[i] : 0;
        }
    }

    @Override protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        int width = toTransform.getWidth();
        int height = toTransform.getHeight();
        Bitmap bitmap = pool.get(width, height, Bitmap.Config.ARGB_8888);
        RectF rectf = new RectF(0, 0, width, height);

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        canvas.drawARGB(0, 0, 0, 0);

        Path path = new Path();
        float[] radiusArray = new float[8];
        radiusArray[0] = mCorners[0];
        radiusArray[1] = mCorners[0];
        radiusArray[2] = mCorners[1];
        radiusArray[3] = mCorners[1];
        radiusArray[4] = mCorners[2];
        radiusArray[5] = mCorners[2];
        radiusArray[6] = mCorners[3];
        radiusArray[7] = mCorners[3];
        path.addRoundRect(rectf, radiusArray, Path.Direction.CW);
        canvas.drawPath(path, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        canvas.drawBitmap(toTransform, 0, 0, paint);

        return bitmap;
    }

    @Override public String toString() {
        return "RoundCornersTransformation(mCorners=" + Arrays.toString(mCorners) + ")";
    }

    @Override public boolean equals(Object o) {
        if (o instanceof RoundCornersTransformation) {
            RoundCornersTransformation transformation = (RoundCornersTransformation) o;
            if (transformation.mCorners != null && mCorners != null) {
                if (transformation.mCorners.length == mCorners.length) {
                    for (int i = 0; i < mCorners.length; i++) {
                        if (mCorners[i] != transformation.mCorners[i]) {
                            return false;
                        }
                    }
                    return true;
                }
            } else if (transformation.mCorners == null && mCorners == null) {
                return true;
            }
        }
        return false;
    }

    @Override public int hashCode() {
        int cornerInt = getCustomKey();
        return CUSTOM_ID.hashCode() + cornerInt;
    }

    @Override public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update((CUSTOM_ID + getCustomKey()).getBytes(CHARSET));
    }

    private int getCustomKey() {
        int cornerInt = 0;
        int index = 12;
        for (int corner : mCorners) {
            cornerInt += corner << index;
            index -= 4;
        }
        return cornerInt;
    }
}
