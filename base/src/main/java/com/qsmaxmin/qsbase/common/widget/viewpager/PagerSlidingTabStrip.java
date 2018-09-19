package com.qsmaxmin.qsbase.common.widget.viewpager;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qsmaxmin.qsbase.R;

import java.util.Locale;

/**
 * Created by sky on 14-11-20.ViewPager Tab
 */
public class PagerSlidingTabStrip extends HorizontalScrollView {

    private OnTabItemClickListener mClickListener;
    private OnTabItemClickListener mLongClickListener;

    public interface IconTabProvider {

        int getPageIconResId(int position);
    }

    public interface TitleCountTabProvider {

        String getPageCount(int position);

    }

    public interface CustomTabProvider {

        int getCustomTabView();

        void initTabsItem(View view, int position);
    }

    private static final int[] ATTRS = new int[]{android.R.attr.textSize, android.R.attr.textColor};

    private final PageListener pageListener           = new PageListener();
    private       int          currentPosition        = 0;
    private       int          selectedPosition       = 0;
    private       float        currentPositionOffset  = 0f;
    private       int          indicatorColor         = 0xFF666666;
    private       int          indicatorWidth         = 0;
    private       int          indicatorHeight        = 8;
    private       int          indicatorCorner        = 0;
    private       int          underlineColor         = 0x1A000000;
    private       int          dividerColor           = 0x1A000000;
    private       boolean      shouldExpand           = false;
    private       boolean      textAllCaps            = false;
    private       int          scrollOffset           = 52;
    private       int          underlineHeight        = 2;
    private       int          dividerPadding         = 12;
    private       int          tabPadding             = 20;
    private       int          tabMargins             = 0;
    private       int          dividerWidth           = 1;
    private       int          tabTextSize            = 12;
    private       int          tabTextColor           = 0xFF666666;
    private       int          selectedTabTextColor   = 0xFF666666;
    private       Typeface     tabTypeface            = null;
    private       int          tabTypefaceStyle       = Typeface.NORMAL;
    private       int          lastScrollX            = 0;
    private       boolean      isCurrentItemAnimation = false;
    private int                       tabBackgroundResId;
    private Paint                     rectPaint;
    private Paint                     dividerPaint;
    private Locale                    locale;
    private int                       tabWidth;
    private int                       rectPaintWidth;
    public  LinearLayout              tabsContainer;
    private ViewPager                 pager;
    private int                       tabCount;
    private RectF                     indicateRectF;
    private LinearLayout.LayoutParams defaultTabLayoutParams;
    private LinearLayout.LayoutParams expandedTabLayoutParams;

    public PagerSlidingTabStrip(Context context) {
        this(context, null);
    }

    public PagerSlidingTabStrip(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PagerSlidingTabStrip(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setFillViewport(true);
        setWillNotDraw(false);

        tabsContainer = new LinearLayout(context);
        tabsContainer.setOrientation(LinearLayout.HORIZONTAL);
        tabsContainer.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(tabsContainer);

        DisplayMetrics dm = getResources().getDisplayMetrics();

        scrollOffset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, scrollOffset, dm);
        indicatorHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, indicatorHeight, dm);
        underlineHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, underlineHeight, dm);
        dividerPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerPadding, dm);
        tabPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, tabPadding, dm);
        dividerWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerWidth, dm);
        tabTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, tabTextSize, dm);

        // get system attrs (android:textSize and android:textColor)

        // get custom attrs
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PagerSlidingTabStrip);

        tabTextSize = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_psts_TextSize, tabTextSize);
        tabTextColor = a.getColor(R.styleable.PagerSlidingTabStrip_psts_UnSelectedTextColor, tabTextColor);
        selectedTabTextColor = a.getColor(R.styleable.PagerSlidingTabStrip_psts_SelectedTextColor, getResources().getColor(R.color.colorAccent));

        indicatorColor = a.getColor(R.styleable.PagerSlidingTabStrip_psts_IndicatorColor, indicatorColor);
        indicatorWidth = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_psts_IndicatorWidth, 0);
        indicatorHeight = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_psts_IndicatorHeight, indicatorHeight);
        indicatorCorner = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_psts_IndicatorCorner, 0);

        underlineColor = a.getColor(R.styleable.PagerSlidingTabStrip_psts_UnderlineColor, underlineColor);
        dividerColor = a.getColor(R.styleable.PagerSlidingTabStrip_psts_DividerColor, dividerColor);
        underlineHeight = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_psts_UnderlineHeight, underlineHeight);
        dividerPadding = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_psts_DividerPadding, dividerPadding);
        tabPadding = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_psts_TabPaddingLeftRight, tabPadding);
        tabBackgroundResId = a.getResourceId(R.styleable.PagerSlidingTabStrip_psts_TabBackground, tabBackgroundResId);
        shouldExpand = a.getBoolean(R.styleable.PagerSlidingTabStrip_psts_ShouldExpand, shouldExpand);
        scrollOffset = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_psts_ScrollOffset, scrollOffset);
        textAllCaps = a.getBoolean(R.styleable.PagerSlidingTabStrip_psts_TextAllCaps, textAllCaps);

        a.recycle();

        rectPaint = new Paint();
        rectPaint.setAntiAlias(true);
        rectPaint.setStyle(Style.FILL);

        dividerPaint = new Paint();
        dividerPaint.setAntiAlias(true);
        dividerPaint.setStrokeWidth(dividerWidth);

        defaultTabLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        expandedTabLayoutParams = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f);
        if (locale == null) {
            locale = getResources().getConfiguration().locale;
        }
    }

    /**
     * 设置宽度
     */
    public void setTabWidth(int width) {
        tabWidth = width;
        if (tabWidth != 0) {
            getLayoutParams().width = LayoutParams.WRAP_CONTENT;
        } else {
            getLayoutParams().width = LayoutParams.MATCH_PARENT;
        }
    }

    public LinearLayout getTabsContainer() {
        return tabsContainer;
    }

    public void setRectPaintWidth(int width) {
        rectPaintWidth = width;
    }

    public void setViewPager(ViewPager pager) {
        this.pager = pager;
        if (pager.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }
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

        tabCount = pager.getAdapter().getCount();

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
        View tab = LayoutInflater.from(getContext()).inflate(adapter.getCustomTabView(), null);
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

        tab.setPadding(tabPadding, 0, tabPadding, 0);

        tabsContainer.addView(tab, position, shouldExpand ? expandedTabLayoutParams : defaultTabLayoutParams);
    }

    private void updateTabStyles() {

        for (int i = 0; i < tabCount; i++) {
            View v = tabsContainer.getChildAt(i);
            v.setBackgroundResource(tabBackgroundResId);

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

        if (tabCount == 0) {
            return;
        }

        int newScrollX = tabsContainer.getChildAt(position).getLeft() + offset;

        if (position > 0 || offset > 0) {
            newScrollX -= scrollOffset;
        }

        if (newScrollX != lastScrollX) {
            lastScrollX = newScrollX;
//            scrollTo(newScrollX, 0);
            smoothScrollTo(newScrollX, 0);
        }

    }

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isInEditMode() || tabCount == 0) {
            return;
        }

        final int height = getHeight();

        // draw indicator
        if (indicatorColor != Color.TRANSPARENT) {
            rectPaint.setColor(indicatorColor);
            View currentTab = tabsContainer.getChildAt(currentPosition);
            float lineLeft;
            float lineRight;
            if (indicatorWidth > 0) {
                lineLeft = (currentTab.getRight() + currentTab.getLeft()) / 2 - indicatorWidth / 2;
                lineRight = (currentTab.getRight() + currentTab.getLeft()) / 2 + indicatorWidth / 2;
            } else {
                lineLeft = currentTab.getLeft() + rectPaintWidth;
                lineRight = tabWidth == 0 ? currentTab.getRight() - rectPaintWidth : tabWidth;
            }
            if (currentPositionOffset > 0f && currentPosition < tabCount - 1) {
                View nextTab = tabsContainer.getChildAt(currentPosition + 1);
                float nextTabLeft;
                float nextTabRight;
                if (indicatorWidth > 0) {
                    nextTabLeft = (nextTab.getRight() + nextTab.getLeft()) / 2 - indicatorWidth / 2;
                    nextTabRight = (nextTab.getRight() + nextTab.getLeft()) / 2 + indicatorWidth / 2;
                } else {
                    nextTabLeft = nextTab.getLeft() + rectPaintWidth;
                    nextTabRight = tabWidth == 0 ? nextTab.getRight() - rectPaintWidth : tabWidth;
                }
                lineLeft = (currentPositionOffset * nextTabLeft + (1f - currentPositionOffset) * lineLeft);
                lineRight = (currentPositionOffset * nextTabRight + (1f - currentPositionOffset) * lineRight);
            }
            if (indicatorCorner > 0) {
                if (indicateRectF == null) {
                    indicateRectF = new RectF(lineLeft + indicatorMargin, height - indicatorHeight, lineRight - indicatorMargin, height);
                } else {
                    indicateRectF.set(lineLeft + indicatorMargin, height - indicatorHeight, lineRight - indicatorMargin, height);
                }
                canvas.drawRoundRect(indicateRectF, indicatorCorner, indicatorCorner, rectPaint);
            } else {
                canvas.drawRect(lineLeft + indicatorMargin, height - indicatorHeight, lineRight - indicatorMargin, height, rectPaint);
            }
        }

        // draw underline
        if (underlineColor != Color.TRANSPARENT) {
            rectPaint.setColor(underlineColor);
            canvas.drawRect(0, height - underlineHeight, tabWidth == 0 ? tabsContainer.getWidth() : tabWidth, height, rectPaint);
        }

        // draw divider
        if (dividerColor != Color.TRANSPARENT) {
            dividerPaint.setColor(dividerColor);
            for (int i = 0; i < tabCount - 1; i++) {
                View tab = tabsContainer.getChildAt(i);
                canvas.drawLine(tabWidth == 0 ? tab.getRight() : tabWidth, dividerPadding, tabWidth == 0 ? tab.getRight() : tabWidth, height - dividerPadding, dividerPaint);
            }
        }
    }

    int indicatorMargin;

    public void setIndicatorMargin(int marginPx) {
        this.indicatorMargin = marginPx;
    }

    private class PageListener implements OnPageChangeListener {

        @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            currentPosition = position;
            currentPositionOffset = positionOffset;
            scrollToChild(position, (int) (positionOffset * tabsContainer.getChildAt(position).getWidth()));
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

    public void setIndicatorWidth(int indicatorWidth) {
        if (this.indicatorWidth != indicatorWidth) {
            this.indicatorWidth = indicatorWidth;
            invalidate();
        }
    }

    public int getIndicatorWidth() {
        return indicatorWidth;
    }

    public void setIndicatorColorResource(int resId) {
        if (this.indicatorColor != getResources().getColor(resId)) {
            this.indicatorColor = getResources().getColor(resId);
            invalidate();
        }
    }

    public int getIndicatorColor() {
        return this.indicatorColor;
    }

    public void setIndicatorHeight(int indicatorLineHeightPx) {
        if (this.indicatorHeight != indicatorLineHeightPx) {
            this.indicatorHeight = indicatorLineHeightPx;
            invalidate();
        }
    }

    public int getIndicatorHeight() {
        return indicatorHeight;
    }

    public void setIndicatorCorner(int indicatorCorner) {
        if (this.indicatorCorner != indicatorCorner) {
            this.indicatorCorner = indicatorCorner;
            invalidate();
        }
    }

    public int getIndicatorCorner() {
        return indicatorCorner;
    }

    public void setUnderlineColor(int underlineColor) {
        if (this.underlineColor != underlineColor) {
            this.underlineColor = underlineColor;
            invalidate();
        }
    }

    public void setUnderlineColorResource(int resId) {
        this.underlineColor = getResources().getColor(resId);
        invalidate();
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

    public void setDividerColorResource(int resId) {
        if (this.dividerColor != getResources().getColor(resId)) {
            this.dividerColor = getResources().getColor(resId);
            invalidate();
        }
    }

    public int getDividerColor() {
        return dividerColor;
    }

    public void setUnderlineHeight(int underlineHeightPx) {
        if (this.underlineHeight != underlineHeightPx) {
            this.underlineHeight = underlineHeightPx;
            invalidate();
        }
    }

    public int getUnderlineHeight() {
        return underlineHeight;
    }

    public void setDividerPadding(int dividerPaddingPx) {
        if (this.dividerPadding != dividerPaddingPx) {
            this.dividerPadding = dividerPaddingPx;
            invalidate();
        }
    }

    public int getDividerPadding() {
        return dividerPadding;
    }

    public void setScrollOffset(int scrollOffsetPx) {
        if (this.scrollOffset != scrollOffsetPx) {
            this.scrollOffset = scrollOffsetPx;
            invalidate();
        }
    }

    public int getScrollOffset() {
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

    public void setTextSize(int textSizePx) {
        this.tabTextSize = textSizePx;
        updateTabStyles();
    }

    public int getTextSize() {
        return tabTextSize;
    }

    public void setTextColor(int textColor) {
        this.tabTextColor = textColor;
        updateTabStyles();
    }

    public void setTextColorResource(int resId) {
        this.tabTextColor = getResources().getColor(resId);
        updateTabStyles();
    }

    public int getTextColor() {
        return tabTextColor;
    }

    public void setSelectedTextColor(int textColor) {
        this.selectedTabTextColor = textColor;
        updateTabStyles();
    }

    public void setSelectedTextColorResource(int resId) {
        this.selectedTabTextColor = getResources().getColor(resId);
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

    public void setTabBackground(int resId) {
        this.tabBackgroundResId = resId;
        updateTabStyles();
    }

    public int getTabBackground() {
        return tabBackgroundResId;
    }

    public void setTabPaddingLeftRight(int paddingPx) {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        this.tabPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, paddingPx, dm);
        updateTabStyles();
    }

    public void setTabMarginsLeftRight(int marginsPx) {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        tabMargins = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, marginsPx, dm);

        defaultTabLayoutParams.setMargins(tabMargins, 0, tabMargins, 0);
        expandedTabLayoutParams.setMargins(tabMargins, 0, tabMargins, 0);
        updateTabStyles();
    }

    public int getTabPaddingLeftRight() {
        return tabPadding;
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

    /**
     * 切换是否有动画
     */
    public void setIsCurrentItemAnimation(boolean isCurrentItemAnimation) {
        this.isCurrentItemAnimation = isCurrentItemAnimation;
    }

    private static class SavedState extends BaseSavedState {

        int currentPosition;

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

    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        invalidate();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public interface OnTabItemClickListener {
        void onClick(int position);
    }
}