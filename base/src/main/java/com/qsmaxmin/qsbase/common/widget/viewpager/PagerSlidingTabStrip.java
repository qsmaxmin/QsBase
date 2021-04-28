package com.qsmaxmin.qsbase.common.widget.viewpager;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qsmaxmin.qsbase.R;

import androidx.viewpager.widget.ViewPager;

/**
 * Created by sky on 14-11-20.ViewPager Tab
 */
public class PagerSlidingTabStrip extends HorizontalScrollView {
    private       OnTabItemClickListener    mClickListener;
    private       OnTabItemClickListener    mLongClickListener;
    private final PageListener              pageListener           = new PageListener();
    private       int                       currentPosition        = 0;
    private       int                       selectedPosition       = 0;
    private       float                     currentPositionOffset  = 0f;
    private       int                       indicatorColor         = 0xFF0000FF;
    private       float                     indicatorWidth         = 0;
    private       float                     indicatorHeight        = 3;
    private       float                     indicatorCorner        = 0;
    private       Drawable                  indicatorDrawable;
    private       int                       indicatorStyle;
    private       int                       underlineColor         = 0x1A000000;
    private       int                       dividerColor;
    private       boolean                   shouldExpand           = false;
    private       boolean                   textAllCaps            = false;
    private       float                     scrollOffset           = 52;
    private       float                     underlineHeight;
    private       float                     dividerPadding         = 12;
    private       float                     tabPaddingLR           = 10;
    private       float                     dividerWidth;
    private       float                     tabTextSize            = 14;
    private       int                       tabTextColor           = 0xFF999999;
    private       int                       selectedTabTextColor   = 0xFF0000FF;
    private       Typeface                  tabTypeface            = null;
    private       int                       tabTypefaceStyle       = Typeface.NORMAL;
    private       int                       lastScrollX            = 0;
    private       boolean                   isCurrentItemAnimation = false;
    private       Paint                     rectPaint;
    private       Paint                     dividerPaint;
    public        LinearLayout              tabsContainer;
    private       ViewPager                 pager;
    private       RectF                     indicateRectF;
    private       LinearLayout.LayoutParams defaultTabLayoutParams;
    private       LinearLayout.LayoutParams expandedTabLayoutParams;

    public interface IconTabProvider {
        int getPageIconResId(int position);
    }

    public interface TitleCountTabProvider {
        String getPageCount(int position);
    }

    public interface CustomTabProvider {
        View getCustomTabView(LayoutInflater inflater, ViewGroup parent, int position);

        void initTabsItem(View view, int position);
    }

    public PagerSlidingTabStrip(Context context) {
        super(context);
        initView(context, null);
    }

    public PagerSlidingTabStrip(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public PagerSlidingTabStrip(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        setFillViewport(true);
        setWillNotDraw(false);
        setHorizontalScrollBarEnabled(false);

        tabsContainer = new LinearLayout(context);
        tabsContainer.setOrientation(LinearLayout.HORIZONTAL);
        tabsContainer.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(tabsContainer);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        scrollOffset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, scrollOffset, dm);
        indicatorHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, indicatorHeight, dm);
        underlineHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, underlineHeight, dm);
        dividerPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerPadding, dm);
        tabPaddingLR = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, tabPaddingLR, dm);
        dividerWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerWidth, dm);
        tabTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, tabTextSize, dm);

        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PagerSlidingTabStrip);
            tabTextSize = typedArray.getDimension(R.styleable.PagerSlidingTabStrip_psts_TextSize, tabTextSize);
            tabTextColor = typedArray.getColor(R.styleable.PagerSlidingTabStrip_psts_UnSelectedTextColor, tabTextColor);
            selectedTabTextColor = typedArray.getColor(R.styleable.PagerSlidingTabStrip_psts_SelectedTextColor, getResources().getColor(R.color.colorAccent));
            indicatorColor = typedArray.getColor(R.styleable.PagerSlidingTabStrip_psts_IndicatorColor, indicatorColor);
            indicatorWidth = typedArray.getDimension(R.styleable.PagerSlidingTabStrip_psts_IndicatorWidth, 0);
            indicatorHeight = typedArray.getDimension(R.styleable.PagerSlidingTabStrip_psts_IndicatorHeight, indicatorHeight);
            indicatorCorner = typedArray.getDimension(R.styleable.PagerSlidingTabStrip_psts_IndicatorCorner, 0);
            indicatorStyle = typedArray.getInt(R.styleable.PagerSlidingTabStrip_psts_IndicatorStyle, 0);
            indicatorDrawable = typedArray.getDrawable(R.styleable.PagerSlidingTabStrip_psts_IndicatorDrawable);
            underlineColor = typedArray.getColor(R.styleable.PagerSlidingTabStrip_psts_UnderlineColor, underlineColor);
            dividerColor = typedArray.getColor(R.styleable.PagerSlidingTabStrip_psts_DividerColor, dividerColor);
            dividerWidth = typedArray.getDimension(R.styleable.PagerSlidingTabStrip_psts_DividerWidth, dividerWidth);
            underlineHeight = typedArray.getDimension(R.styleable.PagerSlidingTabStrip_psts_UnderlineHeight, underlineHeight);
            dividerPadding = typedArray.getDimension(R.styleable.PagerSlidingTabStrip_psts_DividerPadding, dividerPadding);
            tabPaddingLR = typedArray.getDimension(R.styleable.PagerSlidingTabStrip_psts_TabPaddingLeftRight, tabPaddingLR);
            shouldExpand = typedArray.getBoolean(R.styleable.PagerSlidingTabStrip_psts_ShouldExpand, shouldExpand);
            scrollOffset = typedArray.getDimension(R.styleable.PagerSlidingTabStrip_psts_ScrollOffset, scrollOffset);
            textAllCaps = typedArray.getBoolean(R.styleable.PagerSlidingTabStrip_psts_TextAllCaps, textAllCaps);
            typedArray.recycle();
        }

        rectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rectPaint.setStyle(Style.FILL);

        defaultTabLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        expandedTabLayoutParams = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f);
    }

    public LinearLayout getTabsContainer() {
        return tabsContainer;
    }

    public void setViewPager(ViewPager pager) {
        this.pager = pager;
        pager.removeOnPageChangeListener(pageListener);
        pager.addOnPageChangeListener(pageListener);
        notifyDataSetChanged();
    }

    public void setOnTabClickListener(OnTabItemClickListener listener) {
        this.mClickListener = listener;
    }

    public void setOnTabLongClickListener(OnTabItemClickListener listener) {
        this.mLongClickListener = listener;
    }

    public void notifyDataSetChanged() {
        if (pager.getAdapter() == null) return;
        tabsContainer.removeAllViews();

        int tabCount = pager.getAdapter().getCount();
        for (int i = 0; i < tabCount; i++) {
            if (pager.getAdapter() instanceof TitleCountTabProvider) {
                addTextIconTab(i, String.valueOf(pager.getAdapter().getPageTitle(i)), ((TitleCountTabProvider) pager.getAdapter()).getPageCount(i));
            } else if (pager.getAdapter() instanceof IconTabProvider) {
                addIconTab(i, ((IconTabProvider) pager.getAdapter()).getPageIconResId(i));
            } else if (pager.getAdapter() instanceof CustomTabProvider) {
                addCustomTab(i);
            } else {
                addTextTab(i, String.valueOf(pager.getAdapter().getPageTitle(i)));
            }
        }
        updateTabStyles();

        getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @Override public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                currentPosition = pager.getCurrentItem();
                scrollToChild(currentPosition, 0);
            }
        });

    }

    private void addCustomTab(final int position) {
        CustomTabProvider adapter = (CustomTabProvider) pager.getAdapter();
        if (adapter == null) return;
        View tab = adapter.getCustomTabView(LayoutInflater.from(getContext()), tabsContainer, position);
        adapter.initTabsItem(tab, position);
        tab.setFocusable(true);
        tab.setOnClickListener(new OnClickListener() {

            @Override public void onClick(View v) {
                if (mClickListener != null) {
                    mClickListener.onClick(position);
                } else {
                    pager.setCurrentItem(position, isCurrentItemAnimation);
                }
            }
        });

        tab.setOnLongClickListener(new OnLongClickListener() {
            @Override public boolean onLongClick(View v) {
                if (mLongClickListener != null) {
                    mLongClickListener.onClick(position);
                    return true;
                }
                return false;
            }
        });
        tabsContainer.addView(tab, position, shouldExpand ? expandedTabLayoutParams : defaultTabLayoutParams);
    }

    private void addTextIconTab(final int position, String title, String count) {
        TextView tab = new TextView(getContext());
        if (count != null) {
            String stringBuilder = title + "(" + count + ")";
            tab.setText(stringBuilder);
        } else {
            tab.setText(title);
        }
        tab.setGravity(Gravity.CENTER);
        tab.setSingleLine();
        addTab(position, tab);
    }

    private void addTextTab(final int position, String title) {
        TextView tab = new TextView(getContext());
        tab.setText(title);
        tab.setGravity(Gravity.CENTER);
        tab.setSingleLine();
        addTab(position, tab);
    }

    private void addIconTab(final int position, int resId) {
        ImageButton tab = new ImageButton(getContext());
        tab.setImageResource(resId);
        addTab(position, tab);
    }

    private void addTab(final int position, View tab) {
        tab.setFocusable(true);
        tab.setOnClickListener(new OnClickListener() {
            @Override public void onClick(View v) {
                if (mClickListener != null) {
                    mClickListener.onClick(position);
                } else {
                    pager.setCurrentItem(position, isCurrentItemAnimation);
                }
            }
        });
        tab.setPadding((int) tabPaddingLR, 0, (int) tabPaddingLR, 0);
        tabsContainer.addView(tab, position, shouldExpand ? expandedTabLayoutParams : defaultTabLayoutParams);
    }

    private void updateTabStyles() {
        int tabCount = getTabCount();
        for (int i = 0; i < tabCount; i++) {
            View v = tabsContainer.getChildAt(i);
            if (v instanceof TextView) {
                TextView tab = (TextView) v;
                tab.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabTextSize);
                tab.setTypeface(tabTypeface, tabTypefaceStyle);
                tab.setTextColor(tabTextColor);
                if (textAllCaps) {
                    tab.setAllCaps(true);
                }
                if (i == selectedPosition) {
                    tab.setTextColor(selectedTabTextColor);
                }
            }
        }
    }

    private void scrollToChild(int position, int offset) {
        if (getTabCount() <= position) return;
        int newScrollX = tabsContainer.getChildAt(position).getLeft() + offset;
        if (position > 0 || offset > 0) newScrollX -= scrollOffset;
        if (newScrollX != lastScrollX) {
            lastScrollX = newScrollX;
            smoothScrollTo(newScrollX, 0);
        }
    }

    private int getTabCount() {
        return tabsContainer.getChildCount();
    }

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isInEditMode() || getTabCount() == 0) return;

        drawIndicator(canvas);

        drawUnderLine(canvas);

        drawDivider(canvas);
    }

    private void drawUnderLine(Canvas canvas) {
        if (underlineColor != Color.TRANSPARENT && underlineHeight > 0) {
            rectPaint.setColor(underlineColor);
            canvas.drawRect(0, getHeight() - underlineHeight, tabsContainer.getWidth(), getHeight(), rectPaint);
        }
    }

    private void drawDivider(Canvas canvas) {
        if (dividerColor != Color.TRANSPARENT && dividerWidth > 0) {
            if (dividerPaint == null) {
                dividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            }
            dividerPaint.setColor(dividerColor);
            dividerPaint.setStrokeWidth(dividerWidth);
            for (int i = 0; i < getTabCount() - 1; i++) {
                View tab = tabsContainer.getChildAt(i);
                canvas.drawLine(tab.getRight(), dividerPadding, tab.getRight(), getHeight() - dividerPadding, dividerPaint);
            }
        }
    }

    private void drawIndicator(Canvas canvas) {
        if (indicatorDrawable != null) {
            int height = getHeight();
            float drawableW = indicatorWidth != 0 ? indicatorWidth : indicatorDrawable.getIntrinsicWidth();
            float drawableH = indicatorHeight != 0 ? indicatorHeight : indicatorDrawable.getIntrinsicHeight();

            View currentTab = tabsContainer.getChildAt(currentPosition);
            if (drawableW == 0) drawableW = currentTab.getWidth();
            if (drawableH == 0) drawableH = currentTab.getHeight();
            float left = (currentTab.getRight() + currentTab.getLeft()) / 2f - drawableW / 2f;
            float right = (currentTab.getRight() + currentTab.getLeft()) / 2f + drawableW / 2f;

            if (currentPositionOffset > 0f && currentPosition < getTabCount() - 1) {
                View nextTab = tabsContainer.getChildAt(currentPosition + 1);
                float nextLeft = (nextTab.getRight() + nextTab.getLeft()) / 2f - drawableW / 2f;
                float nextRight = (nextTab.getRight() + nextTab.getLeft()) / 2f + drawableW / 2f;
                left = (currentPositionOffset * nextLeft + (1f - currentPositionOffset) * left);
                right = (currentPositionOffset * nextRight + (1f - currentPositionOffset) * right);
            }

            if (indicatorStyle == 0) {
                indicatorDrawable.setBounds((int) left, (int) (height - drawableH), (int) right, height);
            } else if (indicatorStyle == 1) {
                indicatorDrawable.setBounds((int) left, (int) ((height - drawableH) / 2f), (int) right, (int) ((height + drawableH) / 2f));
            }
            indicatorDrawable.draw(canvas);

        } else if (indicatorColor != Color.TRANSPARENT && indicatorHeight > 0) {
            int height = getHeight();
            rectPaint.setColor(indicatorColor);
            View currentTab = tabsContainer.getChildAt(currentPosition);
            float left;
            float right;
            if (indicatorWidth > 0) {
                left = (currentTab.getRight() + currentTab.getLeft()) / 2f - indicatorWidth / 2f;
                right = (currentTab.getRight() + currentTab.getLeft()) / 2f + indicatorWidth / 2f;
            } else {
                left = currentTab.getLeft();
                right = currentTab.getRight();
            }
            if (currentPositionOffset > 0f && currentPosition < getTabCount() - 1) {
                View nextTab = tabsContainer.getChildAt(currentPosition + 1);
                float nextLeft;
                float nextRight;
                if (indicatorWidth > 0) {
                    nextLeft = (nextTab.getRight() + nextTab.getLeft()) / 2f - indicatorWidth / 2f;
                    nextRight = (nextTab.getRight() + nextTab.getLeft()) / 2f + indicatorWidth / 2f;
                } else {
                    nextLeft = nextTab.getLeft();
                    nextRight = nextTab.getRight();
                }
                left = (currentPositionOffset * nextLeft + (1f - currentPositionOffset) * left);
                right = (currentPositionOffset * nextRight + (1f - currentPositionOffset) * right);
            }

            if (indicatorCorner > 0) {
                if (indicateRectF == null) {
                    indicateRectF = new RectF(left, height - indicatorHeight, right, height);
                } else {
                    indicateRectF.set(left, height - indicatorHeight, right, height);
                }
                canvas.drawRoundRect(indicateRectF, indicatorCorner, indicatorCorner, rectPaint);
            } else {
                canvas.drawRect(left, height - indicatorHeight, right, height, rectPaint);
            }
        }
    }


    private class PageListener implements ViewPager.OnPageChangeListener {

        @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            currentPosition = position;
            currentPositionOffset = positionOffset;
            if (getTabCount() > 0) {
                scrollToChild(position, (int) (positionOffset * tabsContainer.getChildAt(position).getWidth()));
            }
            invalidate();
        }

        @Override public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                scrollToChild(pager.getCurrentItem(), 0);
            }
        }

        @Override public void onPageSelected(int position) {
            selectedPosition = position;
            updateTabStyles();
        }
    }

    public void setIndicatorColor(int indicatorColor) {
        if (this.indicatorColor != indicatorColor) {
            this.indicatorColor = indicatorColor;
            invalidate();
        }
    }

    public int getIndicatorColor() {
        return this.indicatorColor;
    }

    public void setIndicatorWidth(float indicatorWidth) {
        if (this.indicatorWidth != indicatorWidth) {
            this.indicatorWidth = indicatorWidth;
            invalidate();
        }
    }

    public float getIndicatorWidth() {
        return indicatorWidth;
    }

    public void setIndicatorHeight(float indicatorHeight) {
        if (this.indicatorHeight != indicatorHeight) {
            this.indicatorHeight = indicatorHeight;
            invalidate();
        }
    }

    public float getIndicatorHeight() {
        return indicatorHeight;
    }

    public void setIndicatorCorner(float indicatorCorner) {
        if (this.indicatorCorner != indicatorCorner) {
            this.indicatorCorner = indicatorCorner;
            invalidate();
        }
    }

    public float getIndicatorCorner() {
        return indicatorCorner;
    }

    public void setIndicatorDrawable(Drawable drawable) {
        if (this.indicatorDrawable != drawable) {
            this.indicatorDrawable = drawable;
            invalidate();
        }
    }

    public Drawable getIndicatorDrawable() {
        return indicatorDrawable;
    }

    /**
     * 目前支持0，1两种类型
     * 0，标题底部显示
     * 1，标题背后显示
     */
    public void setIndicatorStyle(int style) {
        if (this.indicatorStyle != style) {
            this.indicatorStyle = style;
            invalidate();
        }
    }

    public int getIndicatorStyle() {
        return indicatorStyle;
    }

    public void setUnderlineColor(int underlineColor) {
        if (this.underlineColor != underlineColor) {
            this.underlineColor = underlineColor;
            invalidate();
        }
    }

    public int getUnderlineColor() {
        return underlineColor;
    }

    public void setDividerColor(int dividerColor) {
        if (this.dividerColor != dividerColor) {
            this.dividerColor = dividerColor;
            invalidate();
        }
    }

    public void setDividerWidth(float dividerWidth) {
        if (this.dividerWidth != dividerWidth) {
            this.dividerWidth = dividerWidth;
            invalidate();
        }
    }

    public float getDividerWidth() {
        return dividerWidth;
    }

    public void setDividerColorResource(int resId) {
        if (this.dividerColor != getResources().getColor(resId)) {
            this.dividerColor = getResources().getColor(resId);
            invalidate();
        }
    }

    public int getDividerColor() {
        return dividerColor;
    }

    public void setUnderlineHeight(float underlineHeight) {
        if (this.underlineHeight != underlineHeight) {
            this.underlineHeight = underlineHeight;
            invalidate();
        }
    }

    public float getUnderlineHeight() {
        return underlineHeight;
    }

    public void setDividerPadding(float dividerPadding) {
        if (this.dividerPadding != dividerPadding) {
            this.dividerPadding = dividerPadding;
            invalidate();
        }
    }

    public float getDividerPadding() {
        return dividerPadding;
    }

    public void setScrollOffset(float scrollOffsetPx) {
        if (this.scrollOffset != scrollOffsetPx) {
            this.scrollOffset = scrollOffsetPx;
            invalidate();
        }
    }

    public float getScrollOffset() {
        return scrollOffset;
    }

    public void setShouldExpand(boolean shouldExpand) {
        this.shouldExpand = shouldExpand;
    }

    public boolean getShouldExpand() {
        return shouldExpand;
    }

    public boolean isTextAllCaps() {
        return textAllCaps;
    }

    public void setAllCaps(boolean textAllCaps) {
        this.textAllCaps = textAllCaps;
    }

    public void setTextSize(float textSize) {
        this.tabTextSize = textSize;
        updateTabStyles();
    }

    public float getTextSize() {
        return tabTextSize;
    }

    public void setTextColor(int textColor) {
        this.tabTextColor = textColor;
        updateTabStyles();
    }

    public int getTextColor() {
        return tabTextColor;
    }

    public void setSelectedTextColor(int textColor) {
        this.selectedTabTextColor = textColor;
        updateTabStyles();
    }

    public int getSelectedTextColor() {
        return selectedTabTextColor;
    }

    public void setTypeface(Typeface typeface, int style) {
        this.tabTypeface = typeface;
        this.tabTypefaceStyle = style;
        updateTabStyles();
    }

    public void setTabPaddingLeftRight(float padding) {
        this.tabPaddingLR = padding;
        updateTabStyles();
    }

    public float getTabPaddingLeftRight() {
        return tabPaddingLR;
    }

    public void setTabMarginsLeftRight(int margin) {
        defaultTabLayoutParams.setMargins(margin, 0, margin, 0);
        expandedTabLayoutParams.setMargins(margin, 0, margin, 0);
        updateTabStyles();
    }

    public void setIsCurrentItemAnimation(boolean isCurrentItemAnimation) {
        this.isCurrentItemAnimation = isCurrentItemAnimation;
    }

    @Override public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        currentPosition = savedState.currentPosition;
        requestLayout();
    }

    @Override public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.currentPosition = currentPosition;
        return savedState;
    }

    private static class SavedState extends BaseSavedState {

        private int currentPosition;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            currentPosition = in.readInt();
        }

        @Override public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(currentPosition);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {

            @Override public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    public interface OnTabItemClickListener {
        void onClick(int position);
    }
}