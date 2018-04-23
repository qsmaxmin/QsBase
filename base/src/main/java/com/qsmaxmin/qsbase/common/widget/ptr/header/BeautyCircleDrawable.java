package com.qsmaxmin.qsbase.common.widget.ptr.header;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.animation.LinearInterpolator;

import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.common.utils.QsHelper;

import java.util.ArrayList;


/**
 * @CreateBy qsmaxmin
 * @Date 2017/1/16 17:46
 * @Description refresh header 小点转圈圈
 */
public class BeautyCircleDrawable extends Drawable {

    private boolean                isBegin;
    private boolean                isReachPoint;
    private float                  percent;
    private Paint                  mPaint;
    private ArrayList<CirclePoint> list;
    private ValueAnimator          valueAnimator;
    private boolean                isRefreshComplete;

    BeautyCircleDrawable(int radius) {
        mPaint = new Paint();
        mPaint.setColor(QsHelper.getInstance().getApplication().getResources().getColor(R.color.colorAccent));
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        list = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            CirclePoint circlePoint = new CirclePoint(radius, i, 8);
            list.add(circlePoint);
        }
    }

    private void beginInvalidate() {
        if (valueAnimator == null) {
            valueAnimator = ValueAnimator.ofFloat(30000);
            valueAnimator.setDuration(30000);
            valueAnimator.setRepeatCount(Integer.MAX_VALUE);
            valueAnimator.setRepeatMode(ValueAnimator.RESTART);
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override public void onAnimationUpdate(ValueAnimator animation) {
                    invalidateSelf();
                }
            });
            valueAnimator.start();
        } else if (!valueAnimator.isRunning()) {
            valueAnimator.start();
        }
    }

    private void cancelInvalidate() {
        if (valueAnimator != null && valueAnimator.isRunning()) {
            valueAnimator.cancel();
        }
    }

    @Override public void draw(@NonNull Canvas canvas) {
        drawBeautyCircle(canvas);
    }

    private void drawBeautyCircle(Canvas canvas) {
        if (isReachPoint) {
            for (CirclePoint point : list) {
                if (point != null) point.rotateOnce(canvas);
            }
            beginInvalidate();
        } else {
            if (isBegin) {
                for (CirclePoint point : list) {
                    if (point != null) point.rotateOnce(canvas);
                }
                beginInvalidate();
            } else {
                for (CirclePoint point : list) {
                    if (point != null) point.dispatchDrag(canvas);
                }
            }
        }
    }

    void onRefreshComplete() {
        this.isRefreshComplete = true;
    }

    void onBegin() {
        this.isBegin = true;
    }

    void setIsReachCriticalPoint(boolean isReachPoint) {
        if (this.isReachPoint != isReachPoint) {
            this.isReachPoint = isReachPoint;
            invalidateSelf();
        }
    }

    void onPrepare() {
        onReset();
    }

    void onReset() {
        this.isBegin = false;
        this.isRefreshComplete = false;
        percent = 0f;
        mPaint.setAlpha(255);
        for (CirclePoint point : list) {
            if (point != null) point.reset();
        }
        cancelInvalidate();
    }

    void setPercent(float percent) {
        if (this.percent != percent) {
            this.percent = percent;
            invalidateSelf();
        }
    }

    private class CirclePoint {
        boolean hasGrowUp;
        float   bigRadius;
        float   centerY;
        float   centerX;
        float   radius;
        float   currentRadius;
        float   maxRadius;
        int     index;
        int     totalCount;
        double currentAngle = -1f;
        int    alpha        = 255;

        CirclePoint(float radius, int index, int totalCount) {
            this.radius = radius;
            this.maxRadius = radius * 1.3f;
            this.currentRadius = radius * 0.2f;
            this.index = index;
            this.totalCount = totalCount;
        }

        void dispatchDrag(Canvas canvas) {
            float v = 1.2f / (float) totalCount;
            if (percent < v * (index + .5f)) {
                return;
            }
            initPosition();
            mPaint.setColor(getColorByIndex(index));
            if (!hasGrowUp) {
                currentRadius += 0.3f;
                if (currentRadius > maxRadius) {
                    currentRadius = maxRadius;
                    hasGrowUp = true;
                }
                invalidateSelf();
            } else if (currentRadius > radius && currentRadius <= maxRadius) {
                currentRadius -= 0.15f;
                invalidateSelf();
            } else {
                currentRadius = radius;
            }
            canvas.drawCircle(centerX, centerY, currentRadius, mPaint);
        }

        void rotateOnce(Canvas canvas) {
            initPosition();
            if (mPaint != null && canvas != null) {
                mPaint.setColor(getColorByIndex(index));
                if (isRefreshComplete) {
                    if (alpha > 30) {
                        alpha = (int) (alpha / 1.08);
                        bigRadius += .05f;
                        if (currentRadius < radius) {
                            this.currentRadius = radius;
                        }
                        currentRadius += .2f;
                    }
                    currentAngle += .03f;
                    mPaint.setAlpha(alpha);
                } else {
                    currentRadius = radius;
                    currentAngle += .1f;
                }
                calculatePosition();
                canvas.drawCircle(centerX, centerY, currentRadius, mPaint);
            }
        }

        private void calculatePosition() {
            centerY = (float) (getBounds().centerY() + bigRadius * Math.sin(currentAngle));
            centerX = (float) (getBounds().centerX() + bigRadius * Math.cos(currentAngle));
        }

        private void initPosition() {
            if (bigRadius == 0) {
                bigRadius = ((getBounds().width() >> 1) - radius) * 8 / 10;
            }
            if (currentAngle == -1) {
                double angle = (2 * Math.PI / totalCount);
                currentAngle = Math.PI / 2 + angle * index;
            }
            if (bigRadius > 0 && currentAngle != -1) {
                calculatePosition();
            }
        }

        void reset() {
            this.currentAngle = -1;
            this.currentRadius = radius * 0.2f;
            this.hasGrowUp = false;
            this.bigRadius = 0;
            this.alpha = 255;
        }
    }

    private int getColorByIndex(int index) {//1:E4B94A  2:A0CB51 3:EA624C 4:7F5FCF 5:65BEB4 6:74A5CE 7:B166D0 8:F17A4E
        switch (index % 8) {
            case 0:
            default:
                return 0xFFE4B94A;
            case 1:
                return 0xFFA0CB51;
            case 2:
                return 0xFFE06565;
            case 3:
                return 0xFF7F5FCF;
            case 4:
                return 0xFF65BEB4;
            case 5:
                return 0xFF74A5CE;
            case 6:
                return 0xFFB166D0;
            case 7:
                return 0xFFF17A4E;
        }
    }

    @Override public void setAlpha(int alpha) {

    }

    @Override public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
