package com.qsmaxmin.qsbase.common.widget.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Build;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import androidx.annotation.Nullable;
import androidx.core.view.GestureDetectorCompat;

/**
 * @CreateBy qsmaxmin
 * @Date 2021/5/14 13:31
 * @Description
 */
final class ImageData {
    private final FunctionImageView           imageView;
    private final RectF                       cutRect;
    private final RectF                       originalRect;
    private final float[]                     initValues;
    private final RectF                       initRect;
    private final RectF                       currentRect;
    private final Matrix                      currentMatrix;
    private final ScaleGestureDetector        scaleDetector;
    private final GestureDetectorCompat       touchDetector;
    private final SimpleOnGestureListenerImpl gestureListenerImpl;
    private       boolean                     isIdle;
    private       int                         functionMode;
    private       float                       touchBeginScale;
    //
    private       RecoverExecutor             recoverExecutor;
    private       FlingExecutor               flingExecutor;
    private       ResetExecutor               resetExecutor;
    //
    private       float                       lastAngle;
    private       GestureListener             gestureListener;
    //
    private       Bitmap                      originalBitmap;

    RectF getCutRect() {
        return cutRect;
    }

    float[] getInitValues() {
        return initValues;
    }

    RectF getInitRect() {
        return initRect;
    }

    RectF getCurrentRect() {
        return currentRect;
    }

    Matrix getCurrentMatrix() {
        return currentMatrix;
    }

    boolean isIdle() {
        return isIdle;
    }

    void setIdle(boolean idle) {
        this.isIdle = idle;
    }

    void resetLastAngle() {
        this.lastAngle = 0f;
    }

    float getTouchBeginScale() {
        return touchBeginScale;
    }

    void setTouchBeginScale(float touchBeginScale) {
        this.touchBeginScale = touchBeginScale;
    }

    GestureListener getGestureListener() {
        return gestureListener;
    }

    ImageData(FunctionImageView view) {
        this.imageView = view;
        this.cutRect = new RectF();
        this.originalRect = new RectF();
        this.initValues = new float[9];
        this.initRect = new RectF();
        this.currentRect = new RectF();
        this.currentMatrix = new Matrix();
        this.isIdle = true;
        this.gestureListenerImpl = new SimpleOnGestureListenerImpl(this);
        this.scaleDetector = new ScaleGestureDetector(getContext(), new OnScaleGestureListenerImpl(this));
        this.touchDetector = new GestureDetectorCompat(getContext(), gestureListenerImpl);
    }

    float calculateTouchScale() {
        float ratio = (currentRect.top - initRect.top) / initRect.height();
        if (ratio < 0f) ratio = 0f;
        else if (ratio > 1f) ratio = 1f;
        return ratio;
    }

    protected void callbackTouchScaleChanged() {
        if (gestureListener != null) {
            gestureListener.onTouchScaleChanged(calculateTouchScale());
        }
    }

    void startFling(float velocityX, float velocityY) {
        if (flingExecutor == null) flingExecutor = new FlingExecutor(this);
        flingExecutor.fling((int) velocityX, (int) velocityY);
    }

    boolean canFling() {
        return currentRect.contains(cutRect);
    }

    void postDelayed(Runnable action, long delayMillis) {
        imageView.postDelayed(action, delayMillis);
    }

    boolean onTouchEvent(MotionEvent event) {
        if (!available()) return true;
        boolean isTouchUp = event.getAction() == MotionEvent.ACTION_UP;
        if (isTouchUp) {
            touchBeginScale = 0f;
        }
        if (touchDetector.onTouchEvent(event)) return true;
        scaleDetector.onTouchEvent(event);
        if (isTouchUp && !isInFling() && !gestureListenerImpl.isInTapScaling()) {
            startRecover();
        }
        return true;
    }

    final void setBitmap(Bitmap bitmap) {
        this.originalBitmap = bitmap;
        setViewSize(imageView.getWidth(), imageView.getHeight());
    }

    final void setFunction(int function) {
        this.functionMode = function;
    }

    final Context getContext() {
        return imageView.getContext();
    }

    final boolean available() {
        return originalBitmap != null && originalBitmap.getWidth() > 0 && originalBitmap.getHeight() > 0;
    }

    final void setViewSize(int viewWidth, int viewHeight) {
        if (viewWidth > 0 && viewHeight > 0) {
            cutRect.set(0, 0, viewWidth, viewHeight);
            int bitmapWidth = originalBitmap.getWidth();
            int bitmapHeight = originalBitmap.getHeight();
            float scaleX = (float) viewWidth / bitmapWidth;
            float scaleY = (float) viewHeight / bitmapHeight;
            float scale = isPreviewFunction() ? Math.min(scaleX, scaleY) : Math.max(scaleX, scaleY);
            float newW = bitmapWidth * scale;
            float newH = bitmapHeight * scale;
            float left = ((float) viewWidth - newW) / 2f;
            float top = ((float) viewHeight - newH) / 2f;
            initRect.set(left, top, left + newW, top + newH);

            originalRect.set(0, 0, originalBitmap.getWidth(), originalBitmap.getHeight());
            Matrix initMatrix = new Matrix();
            initMatrix.postScale(scale, scale);
            initMatrix.postTranslate(initRect.left, initRect.top);
            initMatrix.getValues(initValues);
            currentMatrix.set(initMatrix);
            mapCurrentRect();
        }
    }

    final void mapWithOriginalRect(Matrix matrix, RectF rectF) {
        matrix.mapRect(rectF, originalRect);
    }

    final void mapCurrentRect() {
        currentMatrix.mapRect(currentRect, originalRect);
    }

    final void draw(Canvas canvas) {
        if (originalBitmap != null) {
            canvas.drawBitmap(originalBitmap, currentMatrix, null);
        }
    }

    final void setAngle(float angle) {
        if (originalBitmap == null) return;
        if (angle != lastAngle) {
            currentMatrix.postRotate(angle - lastAngle, getWidth() / 2f, getHeight() / 2f);
            mapCurrentRect();
            lastAngle = angle;
            invalidate();
        }
    }

    @Nullable public final Bitmap getBitmap() {
        if (originalBitmap != null && isIdle) {
            Bitmap bitmap = Bitmap.createBitmap((int) getWidth(), (int) getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(originalBitmap, currentMatrix, null);
            return bitmap;
        }
        return null;
    }

    final void invalidate() {
        imageView.invalidate();
    }

    final void removeCallbacks(Runnable action) {
        imageView.removeCallbacks(action);
    }

    final float getHeight() {
        return cutRect.height();
    }

    final float getWidth() {
        return cutRect.width();
    }

    final void post(Runnable action) {
        imageView.post(action);
    }

    final void postAnimation(Runnable action) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            imageView.postOnAnimation(action);
        } else {
            postDelayed(action, 16L);
        }
    }

    final void startRecover() {
        if (recoverExecutor == null) {
            recoverExecutor = new RecoverExecutor(this);
        }
        recoverExecutor.recover();
    }

    final void stopFling() {
        if (flingExecutor != null) flingExecutor.stopFling();
    }

    final boolean isPreviewFunction() {
        return functionMode == 0;
    }


    final boolean isInFling() {
        return flingExecutor != null && flingExecutor.isFling();
    }

    final void reset(boolean anim) {
        if (resetExecutor == null) resetExecutor = new ResetExecutor(this);
        resetExecutor.resetMatrix(anim);
    }

    void setGestureListener(GestureListener listener) {
        this.gestureListener = listener;
    }
}
