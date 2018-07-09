/*
 * Copyright (C) 2011 Patrik Akerfeldt
 * Copyright (C) 2011 Jake Wharton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.qsmaxmin.qsbase.common.widget.viewpager.autoscroll;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import com.qsmaxmin.qsbase.R;

import java.util.ArrayList;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/2/15 13:32
 * @Description 无限viewpager指示器
 */
public class CirclePageIndicator extends View implements PageIndicator {

    private final Paint                mPaintUnSelectedFill = new Paint(ANTI_ALIAS_FLAG);
    private final Paint                mPaintOutside        = new Paint(ANTI_ALIAS_FLAG);
    private final Paint                mPaintSelectedFill   = new Paint(ANTI_ALIAS_FLAG);
    private       ArrayList<Indicator> indicators           = new ArrayList<>();
    private ViewPager.OnPageChangeListener mListener;
    private int                            transformMode;
    private float                          mRadius;
    private Drawable                       fillDrawable;
    private ViewPager                      mViewPager;
    private int                            mCurrentPage;
    private int                            mSnapPage;
    private float                          mPageOffset;
    private int                            mScrollState;
    private boolean                        mCentered;
    private float                          strokeWidth;
    private float                          outsideSize;
    private Path                           path;
    private float                          longOffset;
    private float                          centerY;
    private float                          centerMargin;
    private float                          selectedWidth;

    public CirclePageIndicator(Context context) {
        this(context, null);
    }

    public CirclePageIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CirclePageIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (isInEditMode()) return;
        final Resources res = getResources();
        final int defaultPageColor = res.getColor(android.R.color.white);
        final int defaultFillColor = res.getColor(R.color.colorAccent);
        final int defaultOutsideColor = res.getColor(android.R.color.darker_gray);
        final float defaultRadius = res.getDisplayMetrics().density * 3;
        final boolean defaultCentered = true;

        // Retrieve styles attributes
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CirclePageIndicator, defStyle, 0);
        transformMode = a.getInt(R.styleable.CirclePageIndicator_cpi_Transform_mode, 3);
        mCentered = a.getBoolean(R.styleable.CirclePageIndicator_cpi_Centered, defaultCentered);
        strokeWidth = a.getDimension(R.styleable.CirclePageIndicator_cpi_StrokeWidth, 0);
        mPaintUnSelectedFill.setStyle(strokeWidth == 0 ? Style.FILL : Style.STROKE);
        mPaintUnSelectedFill.setColor(a.getColor(R.styleable.CirclePageIndicator_cpi_UnSelectedColor, defaultPageColor));
        mPaintUnSelectedFill.setStrokeWidth(strokeWidth);

        mPaintOutside.setStyle(Style.STROKE);
        mPaintOutside.setColor(a.getColor(R.styleable.CirclePageIndicator_cpi_OutsideColor, defaultOutsideColor));
        outsideSize = a.getDimension(R.styleable.CirclePageIndicator_cpi_OutsideSize, 1);
        mPaintOutside.setStrokeWidth(outsideSize);

        mPaintSelectedFill.setStyle(strokeWidth == 0 ? Style.FILL : Style.STROKE);
        mPaintSelectedFill.setColor(a.getColor(R.styleable.CirclePageIndicator_cpi_SelectedColor, defaultFillColor));
        mPaintSelectedFill.setStrokeWidth(strokeWidth);

        fillDrawable = a.getDrawable(R.styleable.CirclePageIndicator_cpi_SelectedDrawable);
        mRadius = a.getDimension(R.styleable.CirclePageIndicator_cpi_Radius, defaultRadius);
        centerMargin = a.getFloat(R.styleable.CirclePageIndicator_cpi_ItemMarginRatio, 3.5f) * mRadius;
        selectedWidth = a.getFloat(R.styleable.CirclePageIndicator_cpi_SelectedWidthRatio, 3.0f) * mRadius;
        Drawable background = a.getDrawable(R.styleable.CirclePageIndicator_android_background);
        if (background != null) {
            setBackgroundDrawable(background);
        }
        a.recycle();
    }

    public void setCentered(boolean centered) {
        mCentered = centered;
        invalidate();
    }

    public boolean isCentered() {
        return mCentered;
    }

    public void setPageColor(int pageColor) {
        mPaintUnSelectedFill.setColor(pageColor);
        invalidate();
    }

    public int getPageColor() {
        return mPaintUnSelectedFill.getColor();
    }

    public void setFillColor(int fillColor) {
        mPaintSelectedFill.setColor(fillColor);
        invalidate();
    }

    public int getFillColor() {
        return mPaintSelectedFill.getColor();
    }


    public void setOutsideColor(int outsideColor) {
        mPaintOutside.setColor(outsideColor);
        invalidate();
    }

    public int getOutsideColor() {
        return mPaintOutside.getColor();
    }

    public void setOutsideWidth(float outsideWidth) {
        this.outsideSize = outsideWidth;
        mPaintOutside.setStrokeWidth(outsideWidth);
        invalidate();
    }

    public float getStrokeWidth() {
        return mPaintOutside.getStrokeWidth();
    }

    public void setRadius(float radius) {
        mRadius = radius;
        invalidate();
    }

    public float getRadius() {
        return mRadius;
    }

    public void setIndicatorMarginRatio(float ratio) {
        centerMargin = mRadius * ratio;
        selectedWidth = centerMargin * .75f;
    }

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mViewPager == null) {
            return;
        }
        PagerAdapter adapter = mViewPager.getAdapter();
        if (adapter == null) return;
        final int count;
        if (adapter instanceof InfinitePagerAdapter) {
            count = ((InfinitePagerAdapter) adapter).getRealCount();
        } else {
            count = adapter.getCount();
        }
        if (count == 0) return;
        if (mCurrentPage >= count) {
            setCurrentItem(count - 1);
            return;
        }
        centerY = getPaddingTop() + mRadius;
        longOffset = getPaddingLeft() + mRadius;
        if (mCentered) {
            longOffset = ((getWidth() - getPaddingRight() + getPaddingLeft()) / 2.0f) - (((count - 1) * centerMargin) / 2.0f);
        }
        switch (transformMode) {
            case 1:
                drawNormalUnSelected(canvas, count);
                drawNormalSelected(canvas);
                break;
            case 2:
                drawNormalUnSelected(canvas, count);
                drawTranslateSelected(canvas, count);
                break;
            case 3:
                drawScale(canvas, count);
                break;
        }
    }

    private void drawScale(Canvas canvas, int count) {
        longOffset -= selectedWidth / 2;
        if (indicators.size() != count) {
            indicators.clear();
            for (int i = 0; i < count; i++) {
                indicators.add(new Indicator(i));
            }
        }
        for (int index = 0; index < indicators.size(); index++) {
            /*draw normal circle*/
            indicators.get(index).onDraw(canvas);
        }
    }

    private class Indicator {
        private int   index;
        private float leftX;
        private float rightX;

        Indicator(int index) {
            this.index = index;
        }

        void onDraw(Canvas canvas) {
            if (mCurrentPage == indicators.size() - 1) {//第一个选中左滑，最后一个选中右滑执行这里
                if (index == 0) {
                    leftX = longOffset + index * centerMargin;
                    rightX = longOffset + index * centerMargin + selectedWidth * mPageOffset;
                } else if (index == indicators.size() - 1) {
                    rightX = longOffset + index * centerMargin + selectedWidth;
                    leftX = rightX - selectedWidth * (1 - mPageOffset);
                } else {
                    leftX = longOffset + index * centerMargin + selectedWidth * mPageOffset;
                    rightX = leftX;
                }
            } else {
                if (index == mCurrentPage) {//右滑
                    leftX = longOffset + index * centerMargin;
                    rightX = leftX + selectedWidth * (1 - mPageOffset);
                } else if (index == mCurrentPage + 1) {//左滑
                    rightX = longOffset + index * centerMargin + selectedWidth;
                    leftX = rightX - selectedWidth * mPageOffset;
                } else {
                    if (index < mCurrentPage) {
                        leftX = longOffset + index * centerMargin;
                        rightX = leftX;
                    } else if (index > mCurrentPage + 1) {
                        leftX = longOffset + index * centerMargin + selectedWidth;
                        rightX = leftX;
                    }
                }
            }

            if (rightX != leftX) {
                canvas.drawRoundRect(getRectF(leftX - mRadius, centerY - mRadius, rightX + mRadius, centerY + mRadius), mRadius, mRadius, mPaintSelectedFill);
                if (outsideSize > 0) {
                    canvas.drawRoundRect(getRectF(leftX - mRadius + strokeWidth / 2, centerY - mRadius + strokeWidth / 2, rightX + mRadius - strokeWidth / 2, centerY + mRadius - strokeWidth / 2), mRadius, mRadius, mPaintOutside);
                }
            } else {
                canvas.drawCircle(leftX, centerY, mRadius, mPaintSelectedFill);
                if (outsideSize > 0) {
                    canvas.drawCircle(leftX, centerY, mRadius - strokeWidth / 2, mPaintOutside);
                }
            }
        }
    }

    private void drawPath(Canvas canvas, float leftX, float rightX) {
        if (path == null) {
            path = new Path();
        }
        path.reset();
        path.moveTo(leftX, getPaddingTop());
        path.lineTo(rightX, getPaddingTop());
        path.lineTo(rightX, mRadius * 2 + getPaddingTop());
        path.lineTo(leftX, mRadius * 2 + getPaddingTop());
        path.close();
        canvas.drawPath(path, mPaintSelectedFill);
    }


    private void drawNormalUnSelected(Canvas canvas, int count) {
        for (int iLoop = 0; iLoop < count; iLoop++) {
            float dX = longOffset + (iLoop * centerMargin);
            canvas.drawCircle(dX, centerY, strokeWidth > 0 ? mRadius - strokeWidth / 2 : mRadius, mPaintUnSelectedFill);
            if (outsideSize > 0) {
                canvas.drawCircle(dX, centerY, mRadius - strokeWidth / 2, mPaintOutside);
            }
        }
    }

    private void drawNormalSelected(Canvas canvas) {
        float dX = longOffset + mSnapPage * centerMargin;
        if (fillDrawable != null) {
            int width = fillDrawable.getIntrinsicWidth();
            int height = fillDrawable.getIntrinsicHeight();
            fillDrawable.setBounds((int) (dX - width / 2), (int) (centerY - height / 2), (int) (dX + width / 2), (int) (centerY + height / 2));
            fillDrawable.draw(canvas);
        } else {
            canvas.drawCircle(dX, centerY, strokeWidth > 0 ? mRadius - strokeWidth / 2 : mRadius, mPaintSelectedFill);
            if (outsideSize > 0) {
                canvas.drawCircle(dX, centerY, mRadius - strokeWidth / 2, mPaintOutside);
            }
        }
    }

    private void drawTranslateSelected(Canvas canvas, int count) {
        float dX;
        float cx = mCurrentPage * centerMargin;
        if (mCurrentPage == count - 1) {//最后一页，循环
            cx -= mPageOffset * (centerMargin * (count - 1));
        } else {
            cx += mPageOffset * centerMargin;
        }
        dX = longOffset + cx;
        if (fillDrawable != null) {
            int width = fillDrawable.getIntrinsicWidth();
            int height = fillDrawable.getIntrinsicHeight();
            fillDrawable.setBounds((int) (dX - width / 2), (int) (centerY - height / 2), (int) (dX + width / 2), (int) (centerY + height / 2));
            fillDrawable.draw(canvas);
        } else {
            float left = dX - (selectedWidth / 2) - mRadius;
            float right = dX + (selectedWidth / 2) + mRadius;
            canvas.drawRoundRect(getRectF(left, centerY - mRadius, right, centerY + mRadius), mRadius, mRadius, mPaintSelectedFill);
            if (outsideSize > 0) {
                canvas.drawRoundRect(getRectF(left + strokeWidth / 2, centerY - mRadius + strokeWidth / 2, right - strokeWidth / 2, centerY + mRadius - strokeWidth / 2), mRadius, mRadius, mPaintOutside);
            }
        }
    }

    private RectF rectF = new RectF();

    private RectF getRectF(float left, float top, float right, float bottom) {
        rectF.set(left, top, right, bottom);
        return rectF;
    }

    @Override public void setViewPager(ViewPager view) {
        if (mViewPager == view) {
            return;
        }
        if (mViewPager != null) {
            mViewPager.removeOnPageChangeListener(this);
        }
        if (view.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }
        mViewPager = view;
        mViewPager.addOnPageChangeListener(this);
        invalidate();
    }

    @Override public void setViewPager(ViewPager view, int initialPosition) {
        setViewPager(view);
        setCurrentItem(initialPosition);
    }

    @Override public void setCurrentItem(int item) {
        if (mViewPager == null) {
            throw new IllegalStateException("ViewPager has not been bound.");
        }
        mViewPager.setCurrentItem(item);
        mCurrentPage = item;
        invalidate();
    }

    @Override public void notifyDataSetChanged() {
        if (mViewPager.getAdapter() != null) {
            mViewPager.getAdapter().notifyDataSetChanged();
            invalidate();
        }
    }

    @Override public void onPageScrollStateChanged(int state) {
        mScrollState = state;
        if (mListener != null) mListener.onPageScrollStateChanged(state);
    }

    @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        PagerAdapter adapter = mViewPager.getAdapter();
        if (adapter == null) return;
        if (adapter instanceof InfinitePagerAdapter) {
            mCurrentPage = ((InfinitePagerAdapter) adapter).getVirtualPosition(position);
        } else {
            mCurrentPage = position;
        }
        mPageOffset = positionOffset;
        invalidate();
        if (mListener != null) mListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
    }

    @Override public void onPageSelected(int position) {
        if (transformMode == 1 || mScrollState == ViewPager.SCROLL_STATE_IDLE) {
            PagerAdapter adapter = mViewPager.getAdapter();
            if (adapter instanceof InfinitePagerAdapter) {
                mCurrentPage = ((InfinitePagerAdapter) adapter).getVirtualPosition(position);
            } else {
                mCurrentPage = position;
            }
            mSnapPage = mCurrentPage;
            invalidate();
        }
        if (mListener != null) mListener.onPageSelected(position);
    }

    @Override public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        mListener = listener;
    }

    /*
     * (non-Javadoc)
     * @see android.view.View#onMeasure(int, int)
     */
    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureLong(widthMeasureSpec), measureShort(heightMeasureSpec));
    }

    /**
     * Determines the width of this view
     *
     * @param measureSpec A measureSpec packed into an int
     * @return The width of the view, honoring constraints from measureSpec
     */
    private int measureLong(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if ((specMode == MeasureSpec.EXACTLY) || (mViewPager == null)) {
            // We were told how big to be
            result = specSize;
        } else {
            // Calculate the width according the views count
            PagerAdapter adapter = mViewPager.getAdapter();
            if (adapter == null) return result;
            final int count;
            if (adapter instanceof InfinitePagerAdapter) {
                count = ((InfinitePagerAdapter) adapter).getRealCount();
            } else {
                count = adapter.getCount();
            }
            result = (int) (getPaddingLeft() + getPaddingRight() + (count * 2 * mRadius) + (count - 1) * mRadius + +selectedWidth + mRadius * 2 + 1);
            // Respect AT_MOST value if that was what is called for by
            // measureSpec
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    /**
     * Determines the height of this view
     *
     * @param measureSpec A measureSpec packed into an int
     * @return The height of the view, honoring constraints from measureSpec
     */
    private int measureShort(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
        } else {
            // Measure the height
            result = (int) (2 * mRadius + getPaddingTop() + getPaddingBottom() + 1);
            // Respect AT_MOST value if that was what is called for by
            // measureSpec
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    @Override public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        mCurrentPage = savedState.currentPage;
        mSnapPage = savedState.currentPage;
        requestLayout();
    }

    @Override public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.currentPage = mCurrentPage;
        return savedState;
    }

    private static class SavedState extends BaseSavedState {

        int currentPage;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            currentPage = in.readInt();
        }

        @Override public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(currentPage);
        }

        @SuppressWarnings("UnusedDeclaration") public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {

            @Override public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
