package com.qsmaxmin.qsbase.common.widget.progress;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;

import com.qsmaxmin.qsbase.R;

/**
 * @CreateBy qsmaxmin
 * @Date 2021/5/17 11:51
 * @Description
 */
public class QsSeekBar extends View {
    private static final int                     HORIZONTAL = 0;
    private static final int                     VERTICAL   = 1;
    private              int                     orientation;
    private              int                     pl;
    private              int                     pt;
    private              float                   contentW;
    private              float                   contentH;
    private              float                   horEmptySpace;
    private              float                   verEmptySpace;
    private              Drawable                backgroundDrawable;
    private              Drawable                progressDrawable;
    private              Drawable                thumbDrawable;
    private              Drawable                originDrawable;
    private              float                   pThickness;
    private              float                   max;
    private              float                   min;
    private              float                   progress;
    private              float                   origin;
    private              OnSeekBarChangeListener listener;

    public QsSeekBar(Context context) {
        super(context);
        init(null);
    }

    public QsSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public QsSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        float density = getResources().getDisplayMetrics().density;

        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.QsSeekBar);
            orientation = typedArray.getInt(R.styleable.QsSeekBar_qsb_orientation, 0);
            backgroundDrawable = typedArray.getDrawable(R.styleable.QsSeekBar_qsb_backgroundDrawable);
            progressDrawable = typedArray.getDrawable(R.styleable.QsSeekBar_qsb_progressDrawable);
            thumbDrawable = typedArray.getDrawable(R.styleable.QsSeekBar_qsb_thumbDrawable);
            originDrawable = typedArray.getDrawable(R.styleable.QsSeekBar_qsb_originDrawable);
            pThickness = typedArray.getDimension(R.styleable.QsSeekBar_qsb_progressThickness, 3 * density);
            max = typedArray.getFloat(R.styleable.QsSeekBar_qsb_max, 100);
            min = typedArray.getFloat(R.styleable.QsSeekBar_qsb_min, 0);
            progress = typedArray.getFloat(R.styleable.QsSeekBar_qsb_progress, 0);
            origin = typedArray.getFloat(R.styleable.QsSeekBar_qsb_origin, 0);
            typedArray.recycle();
        }

        if (progress < min) progress = min;
        else if (progress > max) progress = max;
        if (origin < min) origin = min;
        else if (origin > max) origin = max;

        if (backgroundDrawable == null) {
            backgroundDrawable = new ColorDrawable(0xFFE0E0E0);
        }
        if (progressDrawable == null) {
            progressDrawable = new ColorDrawable(0xFFFF0000);
        }
        if (thumbDrawable == null) {
            OvalShape ovalShape = new OvalShape();
            ShapeDrawable d = new ShapeDrawable(ovalShape);
            d.getPaint().setColor(0xFF00FF00);
            thumbDrawable = d;
        }
    }

    public void setProgress(float progress) {
        if (progress < min) {
            progress = min;
        } else if (progress > max) {
            progress = max;
        }
        if (this.progress == progress) return;
        this.progress = progress;
        updateDrawable();
        invalidate();
        if (listener != null) {
            listener.onProgressChanged(this, getProgress(), false);
        }
    }

    public float getProgress() {
        return progress;
    }

    public void setMax(float max) {
        this.max = max;
    }

    public float getMax() {
        return max;
    }

    public void setMin(float min) {
        this.min = min;
    }

    public float getMin() {
        return min;
    }

    public void setOnSeekBarChangeListener(OnSeekBarChangeListener listener) {
        this.listener = listener;
    }

    private float getRatio() {
        return (progress - min) / getRange();
    }

    private float getRange() {
        return max - min;
    }

    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);
        pl = getPaddingLeft();
        pt = getPaddingTop();
        int pr = getPaddingRight();
        int pb = getPaddingBottom();
        if (isVertical()) {
            verEmptySpace = (width - pl - pr) / 2f;
            horEmptySpace = 0;
            contentW = width - pl - pr;
            contentH = height - horEmptySpace * 2;
        } else {
            horEmptySpace = (height - pt - pb) / 2f;
            verEmptySpace = 0;
            contentW = width - horEmptySpace * 2;
            contentH = height - pt - pb;
        }
        updateDrawable();
    }

    private void updateDrawable() {
        if (isHorizontal()) {
            float originPos = horEmptySpace + contentW * (origin - min) / (max - min);
            float len = contentW * (progress - origin) / (max - min);
            int progressLeft = (int) Math.min(originPos, originPos + len);
            int progressRight = (int) Math.max(originPos, originPos + len);
            int progressTop = (int) (pt + (contentH - pThickness) / 2f);
            int progressBottom = (int) (pt + (contentH + pThickness) / 2);
            backgroundDrawable.setBounds((int) horEmptySpace, progressTop, (int) (horEmptySpace + contentW), progressBottom);
            progressDrawable.setBounds(progressLeft, progressTop, progressRight, progressBottom);
            thumbDrawable.setBounds((int) (horEmptySpace + contentW * getRatio() - contentH / 2f), (int) pt, (int) (horEmptySpace + contentW * getRatio() + contentH / 2f), (int) (pt + contentH));

            if (originDrawable != null) {
                float size = pThickness * 2f;
                originDrawable.setBounds((int) (originPos - size / 2f), (int) (pt + (contentH - size) / 2f), (int) (originPos + size / 2f), (int) (pt + (contentH + size) / 2f));
            }
        } else {
            float originPos = verEmptySpace + contentH - contentH * (origin - min) / (max - min);
            float len = contentH * (progress - origin) / (max - min);
            int top = (int) Math.min(originPos, originPos - len);
            int bottom = (int) Math.max(originPos, originPos - len);

            int progressLeft = (int) (pl + (contentW - pThickness) / 2f);
            int progressRight = (int) (pl + (contentW + pThickness) / 2f);
            backgroundDrawable.setBounds(progressLeft, (int) verEmptySpace, progressRight, (int) (verEmptySpace + contentH));
            progressDrawable.setBounds(progressLeft, top, progressRight, bottom);

            thumbDrawable.setBounds((int) pl, (int) (verEmptySpace + contentH - contentH * getRatio() - contentW / 2f), (int) (pl + contentW), (int) (verEmptySpace + contentH - contentH * getRatio() + contentW / 2f));

            if (originDrawable != null) {
                float size = pThickness * 2f;
                originDrawable.setBounds((int) (pl + (contentW - size) / 2f), (int) (originPos - size / 2f), (int) (pl + (contentW + size) / 2f), (int) (originPos + size / 2f));
            }
        }
    }

    @Override protected void onDraw(Canvas canvas) {
        backgroundDrawable.draw(canvas);
        progressDrawable.draw(canvas);
        if (originDrawable != null) originDrawable.draw(canvas);
        thumbDrawable.draw(canvas);
    }

    private boolean isHorizontal() {
        return orientation == HORIZONTAL;
    }

    private boolean isVertical() {
        return orientation == VERTICAL;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override public boolean onTouchEvent(MotionEvent event) {
        if (isHorizontal()) {
            float x = event.getX();
            if (x < horEmptySpace) {
                x = horEmptySpace;
            } else if (x > horEmptySpace + contentW) {
                x = horEmptySpace + contentW;
            }
            progress = (x - horEmptySpace) / contentW * getRange() + getMin();
        } else {
            float y = event.getY();
            if (y < verEmptySpace) {
                y = verEmptySpace;
            } else if (y > verEmptySpace + contentH) {
                y = verEmptySpace + contentH;
            }
            progress = (1f - (y - verEmptySpace) / contentH) * getRange() + getMin();
        }
        if (Math.abs(progress - origin) < 0.02f) progress = origin;
        updateDrawable();
        invalidate();
        if (listener != null) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    listener.onStartTrackingTouch(this);
                    listener.onProgressChanged(this, getProgress(), true);
                    break;
                case MotionEvent.ACTION_UP:
                    listener.onProgressChanged(this, getProgress(), true);
                    listener.onStopTrackingTouch(this);
                    break;
                case MotionEvent.ACTION_MOVE:
                    listener.onProgressChanged(this, getProgress(), true);
                    break;
            }
        }
        return true;
    }


    public interface OnSeekBarChangeListener {

        /**
         * Notification that the progress level has changed. Clients can use the fromUser parameter
         * to distinguish user-initiated changes from those that occurred programmatically.
         *
         * @param seekBar  The SeekBar whose progress has changed
         * @param progress The current progress level. This will be in the range min..max where min
         *                 and max were set by {@link ProgressBar#setMin(int)} and
         *                 {@link ProgressBar#setMax(int)}, respectively. (The default values for
         *                 min is 0 and max is 100.)
         * @param fromUser True if the progress change was initiated by the user.
         */
        void onProgressChanged(QsSeekBar seekBar, float progress, boolean fromUser);

        /**
         * Notification that the user has started a touch gesture. Clients may want to use this
         * to disable advancing the seekbar.
         *
         * @param seekBar The SeekBar in which the touch gesture began
         */
        void onStartTrackingTouch(QsSeekBar seekBar);

        /**
         * Notification that the user has finished a touch gesture. Clients may want to use this
         * to re-enable advancing the seekbar.
         *
         * @param seekBar The SeekBar in which the touch gesture began
         */
        void onStopTrackingTouch(QsSeekBar seekBar);
    }
}
