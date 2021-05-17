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
import android.view.ViewConfiguration;

import androidx.annotation.Nullable;
import androidx.core.view.GestureDetectorCompat;

/**
 * @CreateBy qsmaxmin
 * @Date 2021/5/14 13:31
 * @Description
 */
final class ImageData {
    final         FunctionImageView     imageView;
    final         RectF                 cutRect;
    private final RectF                 originalRect;
    final         Matrix                originalMatrix;
    final         RectF                 initRect;
    final         RectF                 currentRect;
    final         Matrix                currentMatrix;
    private final ScaleGestureDetector  scaleDetector;
    private final GestureDetectorCompat touchDetector;
    boolean isIdle;
    private int              functionMode;
    private float            touchBeginScale;
    //
    private RecoverExecutor  recoverExecutor;
    private FlingExecutor    flingExecutor;
    private ResetExecutor    resetExecutor;
    private TapScaleExecutor tapScaleExecutor;
    //
    float           lastAngle;
    GestureListener gestureListener;
    //
    Bitmap          originalBitmap;

    ImageData(FunctionImageView view) {
        this.imageView = view;
        this.cutRect = new RectF();
        this.originalRect = new RectF();
        this.originalMatrix = new Matrix();
        this.initRect = new RectF();
        this.currentRect = new RectF();
        this.currentMatrix = new Matrix();
        this.isIdle = true;
        this.touchBeginScale = -1f;

        final Runnable singleTapCallback = new Runnable() {
            @Override public void run() {
                if (gestureListener != null) gestureListener.onSingleTap();
            }
        };

        this.scaleDetector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.OnScaleGestureListener() {
            @Override public boolean onScale(ScaleGestureDetector detector) {
                float scaleFactor = detector.getScaleFactor();
                currentMatrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
                mapWithOriginalRect(currentMatrix, currentRect);
                invalidate();
                return true;
            }

            @Override public boolean onScaleBegin(ScaleGestureDetector detector) {
                return true;
            }

            @Override public void onScaleEnd(ScaleGestureDetector detector) {
                startRecover();
            }
        });

        this.touchDetector = new GestureDetectorCompat(getContext(), new GestureDetector.SimpleOnGestureListener() {
            private final float[] matrixValues = new float[9];

            @Override public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                boolean active = e2.getPointerCount() == 1;
                if (active) {
                    currentMatrix.postTranslate(-distanceX, -distanceY);
                    if (isPreviewFunction() && currentRect.top > initRect.top) {
                        currentMatrix.getValues(matrixValues);
                        float scaleCurrent = matrixValues[Matrix.MSCALE_X];
                        if (touchBeginScale == -1f) {
                            touchBeginScale = scaleCurrent;
                        } else {
                            float scaleBegin = touchBeginScale;
                            float scaleEnd = 0.3f;
                            float scaleTarget = scaleBegin + (scaleEnd - scaleBegin) * calculateTouchScale();
                            float ps = scaleTarget / scaleCurrent;
                            currentMatrix.postScale(ps, ps, e2.getX(), e2.getY());
                            callbackTouchScaleChanged();
                        }
                    }
                    mapWithOriginalRect(currentMatrix, currentRect);
                    invalidate();
                }
                return active;
            }


            @Override public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (canFling() && (velocityX != 0 || velocityY != 0)) {
                    startFling(velocityX, velocityY);
                } else {
                    startRecover();
                }
                return true;
            }

            @Override public boolean onDown(MotionEvent e) {
                isIdle = false;
                stopFling();
                return true;
            }

            @Override public boolean onSingleTapUp(MotionEvent e) {
                removeCallbacks(singleTapCallback);
                postDelayed(singleTapCallback, ViewConfiguration.getDoubleTapTimeout());
                return false;
            }

            @Override public boolean onDoubleTap(MotionEvent e) {
                removeCallbacks(singleTapCallback);
                float scale = currentRect.width() / initRect.width();
                float maxScale = 4f;
                float x = e.getX();
                float y = e.getY();
                if (currentRect.contains(x, y)) {
                    if (scale < maxScale) {
                        scale *= 1.5f;
                        if (scale > maxScale) scale = maxScale;
                        startTapScale(scale, x, y);
                    } else {
                        reset(true);
                    }
                }
                return true;
            }
        });
    }

    private float calculateTouchScale() {
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

    private void startFling(float velocityX, float velocityY) {
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
        if (!available() || touchDetector.onTouchEvent(event)) return true;
        scaleDetector.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_UP) {
            touchBeginScale = -1f;
            if (!isInFling() && !isInTapScaling()) startRecover();
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
            originalMatrix.reset();
            originalMatrix.postScale(scale, scale);
            originalMatrix.postTranslate(initRect.left, initRect.top);
            currentMatrix.set(originalMatrix);
            mapWithOriginalRect(currentMatrix, currentRect);
        }
    }

    final void mapWithOriginalRect(Matrix matrix, RectF rectF) {
        matrix.mapRect(rectF, originalRect);
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
            mapWithOriginalRect(currentMatrix, currentRect);
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

    final boolean isInTapScaling() {
        return tapScaleExecutor != null && tapScaleExecutor.isScaling();
    }

    final void startTapScale(float scaleFactor, float px, float py) {
        if (tapScaleExecutor == null) tapScaleExecutor = new TapScaleExecutor(this);
        tapScaleExecutor.tapScale(scaleFactor, px, py);
    }

    final void reset(boolean anim) {
        if (resetExecutor == null) resetExecutor = new ResetExecutor(this);
        resetExecutor.resetMatrix(anim);
    }

    void setGestureListener(GestureListener listener) {
        this.gestureListener = listener;
    }
}
