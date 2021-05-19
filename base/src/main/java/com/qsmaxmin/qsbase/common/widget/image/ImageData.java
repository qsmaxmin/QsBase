package com.qsmaxmin.qsbase.common.widget.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Build;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import androidx.annotation.Nullable;

/**
 * @CreateBy qsmaxmin
 * @Date 2021/5/17 13:10
 * @Description
 */
final class ImageData {
    private final FunctionImageView     imageView;
    private final ScaleGestureDetector  scaleDetector;
    private final GestureDetector       gestureDetector;
    private final TransformMatrix       mMatrix;
    private final SimpleGestureListener gestureListenerImpl;
    private       int                   functionMode;
    private       boolean               enableTouchScaleDown;
    private       Bitmap                originalBitmap;
    private       ExecutorReset         resetExecutor;
    private       GestureListener       listener;
    private       ExecutorRecover       recoverExecutor;
    private       ExecutorFling         flingExecutor;
    private       ExecutorTapScale      tapScaleExecutor;
    private       OnTransformListener   transformListener;
    private       ExecutorTransform     transformExecutor;
    private       float[]               initTransformFromCoordinate;
    private       int                   initTransformFromDuration;

    ImageData(FunctionImageView imageView) {
        this.imageView = imageView;
        this.gestureListenerImpl = new SimpleGestureListener(this);
        this.gestureDetector = new GestureDetector(getContext(), gestureListenerImpl);
        this.scaleDetector = new ScaleGestureDetector(getContext(), new ScaleGestureListener(this));
        this.mMatrix = new TransformMatrix();
    }

    void setBitmap(Bitmap bitmap) {
        this.originalBitmap = bitmap;
        setViewSize(imageView.getWidth(), imageView.getHeight());
    }

    Bitmap getBitmap() {
        return originalBitmap;
    }

    void setFunction(int functionMode) {
        this.functionMode = functionMode;
    }

    int getFunction() {
        return functionMode;
    }

    void setEnableTouchScaleDown(boolean enableTouchScaleDown) {
        this.enableTouchScaleDown = enableTouchScaleDown;
    }

    void setViewSize(int viewWidth, int viewHeight) {

        if (viewWidth > 0 && viewHeight > 0 && originalBitmap != null) {
            int bitmapWidth = originalBitmap.getWidth();
            int bitmapHeight = originalBitmap.getHeight();
            float scaleX = (float) viewWidth / bitmapWidth;
            float scaleY = (float) viewHeight / bitmapHeight;
            float scale = isPreviewFunction() ? Math.min(scaleX, scaleY) : Math.max(scaleX, scaleY);
            float newW = bitmapWidth * scale;
            float newH = bitmapHeight * scale;
            float left = ((float) viewWidth - newW) / 2f;
            float top = ((float) viewHeight - newH) / 2f;
            float right = left + newW;
            float bottom = top + newH;
            mMatrix.init(viewWidth, viewHeight, bitmapWidth, bitmapHeight, left, top, right, bottom);
            if (initTransformFromCoordinate != null) {
                getTransformExecutor().transformFrom(initTransformFromCoordinate, initTransformFromDuration);
                initTransformFromCoordinate = null;
                initTransformFromDuration = 0;
            }
        }
    }

    boolean isPreviewFunction() {
        return functionMode == 0;
    }

    /**
     * 手指向下滑动时，缩放显示
     */
    boolean canTouchScaleDown() {
        return isPreviewFunction() && enableTouchScaleDown;
    }

    void setAngle(float angle) {
        mMatrix.setAngle(angle);
        invalidate();
    }

    void startRecover() {
        if (recoverExecutor == null) recoverExecutor = new ExecutorRecover(this);
        recoverExecutor.recover();
    }

    void startReset(boolean anim) {
        if (resetExecutor == null) resetExecutor = new ExecutorReset(this);
        resetExecutor.startReset(anim);
    }

    void startFling(int velocityX, int velocityY) {
        if (flingExecutor == null) flingExecutor = new ExecutorFling(this);
        flingExecutor.fling(velocityX, velocityY);
    }

    void stopFling() {
        if (flingExecutor != null) flingExecutor.stopFling();
    }

    private boolean isInFling() {
        return flingExecutor != null && flingExecutor.isAnimating();
    }

    void startTapScale(float scaleFactor, float px, float py) {
        if (tapScaleExecutor == null) tapScaleExecutor = new ExecutorTapScale(this);
        tapScaleExecutor.startTapScale(scaleFactor, px, py);
    }

    boolean isTapScaling() {
        return tapScaleExecutor != null && tapScaleExecutor.isAnimating();
    }

    void setGestureListener(GestureListener listener) {
        this.listener = listener;
    }


    @Nullable Bitmap getCropBitmap() {
        if (originalBitmap != null && mMatrix.hasInit()) {
            Coordinate coordinate = mMatrix.getViewCoordinate();
            int w = (int) coordinate.getWidth();
            int h = (int) coordinate.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            Matrix m = mMatrix.updateMatrix();
            canvas.drawBitmap(originalBitmap, m, null);
            return bitmap;
        }
        return null;
    }

    boolean onTouchEvent(MotionEvent event) {
        if (!available()) return true;
        boolean isTouchUp = event.getAction() == MotionEvent.ACTION_UP;
        if (isTouchUp) {
            gestureListenerImpl.resetTouchBeginScale();
        }
        if (gestureDetector.onTouchEvent(event)) return true;
        scaleDetector.onTouchEvent(event);
        if (isTouchUp && !isInFling() && !isTapScaling()) {
            startRecover();
        }
        return true;
    }

    private boolean available() {
        return originalBitmap != null
                && originalBitmap.getWidth() > 0
                && originalBitmap.getHeight() > 0;
    }

    private ExecutorTransform getTransformExecutor() {
        if (transformExecutor == null) transformExecutor = new ExecutorTransform(this);
        return transformExecutor;
    }

    void draw(Canvas canvas) {
        if (available()) {
            Matrix m = mMatrix.updateMatrix();
            canvas.drawBitmap(originalBitmap, m, null);
        }
    }

    TransformMatrix getMatrix() {
        return mMatrix;
    }

    void postAnimation(Runnable action) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            imageView.postOnAnimation(action);
        } else {
            imageView.postDelayed(action, 16L);
        }
    }

    void invalidate() {
        imageView.invalidate();
    }

    void post(Runnable action) {
        imageView.post(action);
    }

    void removeCallbacks(Runnable action) {
        imageView.removeCallbacks(action);
    }

    Context getContext() {
        return imageView.getContext();
    }

    GestureListener getGestureListener() {
        return listener;
    }

    void callbackTouchScaleChanged(float ratio) {
        if (listener != null && isPreviewFunction()) {
            listener.onTouchScaleChanged(ratio);
        }
    }

    void postDelayed(Runnable action, long delayed) {
        imageView.postDelayed(action, delayed);
    }

    void callbackTransformChanged(float progress, boolean end) {
        if (transformListener != null) transformListener.onTransform(progress, end);
    }

    void setTransformListener(OnTransformListener listener) {
        this.transformListener = listener;
    }

    void transformTo(RectF rectF, boolean anim) {
        getTransformExecutor().transformTo(Coordinate.getCoordinate(rectF), anim);
    }

    void transformTo(float[] coordinate, boolean anim) {
        getTransformExecutor().transformTo(coordinate, anim);
    }

    void setInitTransformFrom(RectF rectF, int duration) {
        setInitTransformFrom(Coordinate.getCoordinate(rectF), duration);
    }

    void setInitTransformFrom(float[] coordinate, int duration) {
        this.initTransformFromCoordinate = coordinate;
        this.initTransformFromDuration = duration;
    }
}
