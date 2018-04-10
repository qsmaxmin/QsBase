package com.qsmaxmin.qsbase.common.widget.viewpager.headerpager;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.common.widget.viewpager.headerpager.base.InnerScroller;
import com.qsmaxmin.qsbase.common.widget.viewpager.headerpager.base.OuterScroller;
import com.qsmaxmin.qsbase.common.widget.viewpager.headerpager.help.InnerSpecialViewHelper;
import com.qsmaxmin.qsbase.common.widget.viewpager.headerpager.help.IntegerVariable;
import com.qsmaxmin.qsbase.common.widget.viewpager.headerpager.help.MagicHeaderUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @CreateBy QS
 * @Date 16/11/20  下午7:21
 * @Description ListView in Fragment in ViewPager of MagicHeaderViewPager.
 */
public class InnerListView extends ListView implements InnerScroller, AbsListView.OnScrollListener {

    public static final    String          TAG                            = "InnerListView";
    private                IntegerVariable mEmptyHeaderHeight             = new IntegerVariable(0);
    private                boolean         mGettingScrollY                = false;
    protected              int             mItemPosition                  = ORIGIN_ITEM_POSITION;
    protected static final int             ORIGIN_ITEM_POSITION           = -1;
    protected static final int             ORIGIN_ITEM_MARGIN_TOP_2HEADER = 0;
    protected              int             mItemMarginTop2Header          = ORIGIN_ITEM_MARGIN_TOP_2HEADER;
    private                DataStatus      mDataStatus                    = DataStatus.IDLE;
    protected              int             mLastHeaderVisibleHeight       = 0;
    private                int             mScrollState                   = SCROLL_STATE_IDLE;

    private   LinearLayout                    mHeaderContainerCompat;
    private   InnerScrollListener             mInnerScrollListener;
    private   OnScrollListener                mOnScrollListener;
    private   View                            mReceiveView;
    private   int                             mVisibleHeaderCount;
    private   boolean                         mPreDataSetObserverRegistered;
    private   InflateFirstItemIfNeededAdapter mInnerAdapter;
    private   boolean                         mRendered;
    protected View                            mEmptyHeader;

    public InnerListView(Context context) {
        super(context);
        initView();
    }

    public InnerListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public InnerListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    private void initView() {
        initEmptyHeader();
        checkCompat();
    }

    /**
     * Check compat things
     */
    private void checkCompat() {
        checkHeaderAdditionIfNeeded();
        checkScrollModeCompat();
    }

    private void checkScrollModeCompat() {
        setOverScrollMode(OVER_SCROLL_NEVER);
    }

    private void initEmptyHeader() {
        mEmptyHeader = new FrameLayout(getContext());
        super.addHeaderView(mEmptyHeader, null, false);
    }

    @Override public int getInnerScrollY() {
        return getListViewScrollY();
    }

    @Override public final void onScroll(final AbsListView absListView, final int firstVisibleItem, final int visibleItemCount, final int totalItemCount) {
        if (!mAttached || mOuterScroller == null || mBlockMeasure) {
            return;
        }
        if (mIndex == mOuterScroller.getCurrentInnerScrollerIndex()) {
            triggerOuterScroll();
            recordScrollPosition(firstVisibleItem);
        }
    }

    @Override public final void triggerOuterScroll() {
        if (!mGettingScrollY && mOuterScroller != null) {
            mGettingScrollY = true;
            int scrollY = getInnerScrollY();
            if (scrollY != -1) {
                mOuterScroller.onInnerScroll(mIndex, scrollY);
            }
            mGettingScrollY = false;
        }
    }

    @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (oldw == 0 && oldh == 0 && !mRendered) {
            mRendered = true;
            onRender();
        }
    }

    private void onRender() {
        adjustEmptyHeaderHeight();
    }

    @Override public final void adjustEmptyHeaderHeight() {
        if (mEmptyHeader == null || mOuterScroller == null || mOuterScroller.getHeaderHeight() == 0) {
            return;
        }
        if (mEmptyHeaderHeight.getValue() != mOuterScroller.getHeaderHeight()) {
            post(new PaddingRunnable(mEmptyHeader, 0, mOuterScroller.getHeaderHeight(), 0, 0));
            mEmptyHeaderHeight.setValue(mOuterScroller.getHeaderHeight());
            updateEmptyHeaderHeight();
        }
    }

    private class PaddingRunnable implements Runnable {
        View view;
        int  left;
        int  top;
        int  right;
        int  bottom;

        PaddingRunnable(View view, int left, int top, int right, int bottom) {
            this.view = view;
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }

        @Override public void run() {
            view.setPadding(left, top, right, bottom);
        }
    }

    public final void recordScrollPosition(int firstVisibleItem) {
        mLastHeaderVisibleHeight = mOuterScroller.getHeaderVisibleHeight();
        if (getChildAt(0) != null) {
            int itemMarginTop = getChildAt(0).getTop();

            mItemPosition = firstVisibleItem;
            mItemMarginTop2Header = itemMarginTop - mLastHeaderVisibleHeight;
        }
    }

    public final void performScroll(final int itemMariginTop2Header) {

        if (!mAttached || mOuterScroller == null) {
            return;
        }

        mLastHeaderVisibleHeight = mOuterScroller.getHeaderVisibleHeight();

        if (getChildAt(0) != null) {
            if (mItemPosition < 0) {
                scrollToInnerTop();
            } else {
                setSelectionFromTop(mItemPosition, itemMariginTop2Header + mLastHeaderVisibleHeight);
            }
        }
    }

    @Override public final void syncScroll() {
        if (!mAttached || mOuterScroller == null) {
            return;
        }
        if (mOuterScroller.getHeaderVisibleHeight() != mLastHeaderVisibleHeight) {
            performScroll(mItemMarginTop2Header);
        }

    }

    protected OuterScroller mOuterScroller;
    private int mIndex = -1;

    @Override public OuterScroller getOuterScroller() {
        return mOuterScroller;
    }

    @Override public void register2Outer(OuterScroller outerScroller, int index) {

        if (outerScroller != null && (outerScroller != mOuterScroller || mIndex != index)) {
            mIndex = index;
            mOuterScroller = outerScroller;
            mOuterScroller.registerInnerScroller(index, this);
            getEmptyViewHelper().setOuterScroller(mOuterScroller);
            adjustEmptyHeaderHeight();
            checkEmptyAdapterInitialization();
        }
        if (mInnerScrollListener == null) {
            setOnScrollListener(null);
        }
    }

    public void checkEmptyAdapterInitialization() {
        if (mInnerAdapter != null) {
            return;
        }
        setAdapter(new BaseAdapter() {
            @Override public int getCount() {
                return 0;
            }

            @Override public Object getItem(int position) {
                return null;
            }

            @Override public long getItemId(int position) {
                return position;
            }

            @Override public View getView(int position, View convertView, ViewGroup parent) {
                return null;
            }
        });
    }

    public View getReceiveView() {
        return mReceiveView == null ? this : mReceiveView;
    }

    public void setReceiveView(View receiveView) {
        this.mReceiveView = receiveView;
    }

    @Override public void addHeaderView(View v, Object data, boolean isSelectable) {
        if (needCompatHeaderAddition() && mHeaderContainerCompat != null && mHeaderContainerCompat != v) {
            mHeaderContainerCompat.addView(v);
        } else {
            super.addHeaderView(v, data, isSelectable);
            mVisibleHeaderCount++;
        }
    }

    public boolean removeHeaderView(View view) {
        boolean success = super.removeHeaderView(view);
        if (success) {
            mVisibleHeaderCount--;
        }
        return success;
    }

    private int getInvisibleHeaderCount() {
        return getHeaderViewsCount() - mVisibleHeaderCount;
    }

    public void onRefresh(boolean isRefreshing) {
        if (mOuterScroller != null) {
            mOuterScroller.updateRefreshState(isRefreshing);
        }
    }

    @Override public final void onScrollStateChanged(AbsListView view, int scrollState) {
        this.mScrollState = scrollState;
        if (scrollState == SCROLL_STATE_IDLE) {

            if (mOuterScroller != null && mIndex == mOuterScroller.getCurrentInnerScrollerIndex()) {
                // bug fixed on MX3(API 17): trigger Magic Header not so well cuz onScroll() callback frequency is too low on that device.
                // 所以这儿再来trigger一下吧，防火防盗防魅族~XD
                triggerOuterScroll();
                recordScrollPosition(getFirstVisiblePosition());

                mOuterScroller.onInnerScrollerStop();
            }
        }
    }

    @Override public void setOnScrollListener(OnScrollListener l) {
        mInnerScrollListener = new InnerScrollListener(l);
        super.setOnScrollListener(mInnerScrollListener);
    }

    private class InnerScrollListener implements OnScrollListener {

        InnerScrollListener(OnScrollListener onScrollListener) {
            mOnScrollListener = onScrollListener;
        }

        @Override public void onScrollStateChanged(AbsListView absListView, int scrollState) {
            InnerListView.this.onScrollStateChanged(absListView, scrollState);
            if (mOnScrollListener != null && mOnScrollListener != InnerListView.this) {
                mOnScrollListener.onScrollStateChanged(absListView, scrollState);
            }
        }

        @Override public void onScroll(AbsListView absListView, int i, int i1, int i2) {
            InnerListView.this.onScroll(absListView, i, i1, i2);
            if (mOnScrollListener != null && mOnScrollListener != InnerListView.this) {
                mOnScrollListener.onScroll(absListView, i, i1, i2);
            }
        }
    }

    private class InflateFirstItemIfNeededAdapter extends BaseAdapter {
        ListAdapter mAdapter;

        InflateFirstItemIfNeededAdapter(ListAdapter adapter) {
            if (adapter == null) {
                throw new NullPointerException();
            }
            mAdapter = adapter;
        }

        public void setAdapter(ListAdapter adapter) {
            if (adapter == null) {
                throw new NullPointerException();
            }
            this.mAdapter = adapter;
        }

        public boolean hasStableIds() {
            return mAdapter.hasStableIds();
        }

        public void registerDataSetObserver(DataSetObserver observer) {
            mAdapter.registerDataSetObserver(observer);
        }

        public void unregisterDataSetObserver(DataSetObserver observer) {
            mAdapter.unregisterDataSetObserver(observer);
        }

        @Override public void notifyDataSetChanged() {
            if (mAdapter instanceof BaseAdapter) {
                ((BaseAdapter) mAdapter).notifyDataSetChanged();
            }
        }

        public void notifyDataSetInvalidated() {
            if (mAdapter instanceof BaseAdapter) {
                ((BaseAdapter) mAdapter).notifyDataSetInvalidated();
            }
        }

        public boolean areAllItemsEnabled() {
            return mAdapter.areAllItemsEnabled();
        }

        public boolean isEnabled(int position) {
            return position >= getCount() || mAdapter.isEnabled(position);
        }

        public int getItemViewType(int position) {
            innerTempCount = mAdapter.getCount();

            // empty content +0
            if (isEmptyContent(position, innerTempCount)) {
                return mAdapter.getItemViewType(position);
            }

            // auto completion +1
            if (isAutoCompletion(position, innerTempCount)) {
                return mAdapter.getItemViewType(position) + 1;
            }

            // plain item +2
            return mAdapter.getItemViewType(position) + 2;
        }

        public int getViewTypeCount() {
            return mAdapter.getViewTypeCount() + 2;
        }

        public ListAdapter getAdapter() {
            return mAdapter;
        }

        @Override public int getCount() {
            innerTempCount = mAdapter.getCount();
            if (mOuterScroller == null) {
                return innerTempCount;
            }

            if (innerTempCount == 0) {
                return 2; // empty content + auto completion
            }

            return innerTempCount + 1;
        }

        @Override public Object getItem(int position) {
            if (position >= getCount()) {
                return null;
            }
            return mAdapter.getItem(position);
        }

        @Override public long getItemId(int position) {
            if (position >= getCount()) {
                return -1L;
            }
            return mAdapter.getItemId(position);
        }

        int empty_first_position = -127;

        private int innerTempCount;

        @Override public View getView(final int position, View convertView, ViewGroup parent) {

            innerTempCount = mAdapter.getCount();
            // Empty Content
            if (isEmptyContent(position, innerTempCount)) {
                View viewEmptyFirst = getInnerEmptyViewSafely();
                viewEmptyFirst.setTag(R.id.id_for_empty_content, "");
                LayoutParams lp = new LayoutParams(MagicHeaderUtils.getScreenWidth(getContext()), getCustomEmptyViewHeight());
                viewEmptyFirst.setLayoutParams(lp);
                if (empty_first_position < -126) {
                    empty_first_position = position;
                }
                return viewEmptyFirst;
            }

            if (isAutoCompletion(position, innerTempCount)) {
                if (mDataStatus != DataStatus.CHANGING) {
                    convertView = configureAutoEmptyCompletionView(getGapHeight(position));
                } else {
                    mDataStatus = DataStatus.IDLE;
                    convertView = configureAutoEmptyCompletionView(mOuterScroller.getContentAreaMaxVisibleHeight());
                    post(new Runnable() {
                        @Override public void run() {
                            reMeasureHeights();
                            configureAutoEmptyCompletionView(getGapHeight(position));
                        }
                    });
                }
                return convertView;
            }

            return mAdapter.getView(position, convertView, parent);
        }

        private boolean isEmptyContent(int position, int innerCount) {
            return innerCount == 0 && position == innerCount;
        }

        private boolean isAutoCompletion(int position, int innerCount) {
            return innerCount == 0 && position == innerCount + 1 || position == innerCount;
        }
    }

    private boolean mBlockMeasure;

    private void setBlockMeasure(boolean blockMeasure) {
        this.mBlockMeasure = blockMeasure;
    }

    public boolean isBlockMeasure() {
        return mBlockMeasure;
    }

    @Override public void setAdapter(ListAdapter adapter) {
        setBlockMeasure(true);
        setVisibility(INVISIBLE);
        mDataStatus = DataStatus.CHANGING;
        if (mInnerAdapter != null) unRegisterPreDataSetObserver(mInnerAdapter.getAdapter());
        mInnerAdapter = new InflateFirstItemIfNeededAdapter(adapter);
        super.setAdapter(mInnerAdapter);
        registerPreDataSetObserver(adapter);
        onSetAdapterSuccess();
    }

    private void onSetAdapterSuccess() {
        Runnable runnable = new Runnable() {
            @Override public void run() {
                scrollToInnerTop();

                if (isBlockMeasure()) {
                    setVisibility(VISIBLE);
                    if (mAttached) {
                        setBlockMeasure(false);
                    }
                }
            }
        };
        safelyPost(runnable);
    }

    private void unRegisterPreDataSetObserver(ListAdapter adapter) {
        if (adapter != null && mPreDataSetObserverRegistered) {
            adapter.unregisterDataSetObserver(mPreDataSetObserver);
            mPreDataSetObserverRegistered = false;
        }
    }

    private void registerPreDataSetObserver(ListAdapter newAdapter) {
        if (newAdapter != null && !mPreDataSetObserverRegistered) {
            mPreDataSetObserverRegistered = true;
            newAdapter.registerDataSetObserver(mPreDataSetObserver);
        }
    }

    private enum DataStatus {
        IDLE,
        CHANGING
    }


    DataSetObserver mPreDataSetObserver = new DataSetObserver() {
        @Override public void onChanged() {
            mDataStatus = DataStatus.CHANGING;
            super.onChanged();
        }

        @Override public void onInvalidated() {
            mDataStatus = DataStatus.CHANGING;
            super.onInvalidated();
        }
    };

    @Override public ListAdapter getAdapter() {
        return super.getAdapter();
    }

    public InflateFirstItemIfNeededAdapter getInnerAdapter() {
        return mInnerAdapter;
    }

    boolean mAttached    = false;
    boolean mHasDetached = false;

    @Override protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        mAttached = true;

        if (mHasDetached) {
            onReAttached();
        }
    }

    private void reMeasureHeights() {
        old_FirstVisiblePosition = old_LastVisiblePosition = 0;
        getInnerScrollY();
    }

    private void onReAttached() {
        Runnable runnable = new Runnable() {
            @Override public void run() {
                performScroll(mItemMarginTop2Header);
                if (isBlockMeasure()) {
                    setVisibility(VISIBLE);
                    if (mAttached) {
                        setBlockMeasure(false);
                    }
                }
            }
        };
        safelyPost(runnable);
    }

    private void safelyPost(Runnable runnable) {
        if (mAttached || !mHasDetached) {
            post(runnable);
        } else {
            runnable.run();
        }
    }

    @Override protected void onDetachedFromWindow() {
        mHasDetached = true;
        mAttached = false;
        super.onDetachedFromWindow();
    }

    @Override public void scrollToTop() {
        setSelection(0);
    }

    @Override public void scrollToInnerTop() {
        if (mOuterScroller != null) {
            final int invisibleHeaderCount = getInvisibleHeaderCount();
            setSelectionFromTop(invisibleHeaderCount, mOuterScroller.getHeaderVisibleHeight());
        } else {
            setSelection(0);
        }
    }

    @Override public boolean isScrolling() {
        return mScrollState != SCROLL_STATE_IDLE;
    }

    @Override public void draw(Canvas canvas) {
        final int restoreCount = canvas.save();
        if (mOuterScroller != null) {
            canvas.clipRect(0, mOuterScroller.getHeaderVisibleHeight(), getWidth(), getHeight());
        }
        super.draw(canvas);
        canvas.restoreToCount(restoreCount);
    }

    private void checkHeaderAdditionIfNeeded() {
        if (needCompatHeaderAddition()) {
            if (mHeaderContainerCompat == null) {
                mHeaderContainerCompat = new LinearLayout(getContext());
                mHeaderContainerCompat.setOrientation(LinearLayout.VERTICAL);
                if (mHeaderContainerCompat.getParent() == null) {
                    addHeaderView(mHeaderContainerCompat, null, true);
                }
            }
        }
    }

    private static boolean needCompatHeaderAddition() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2;
    }

    private InnerSpecialViewHelper mEmptyViewHelper;

    private InnerSpecialViewHelper getEmptyViewHelper() {
        if (mEmptyViewHelper == null) {
            mEmptyViewHelper = new InnerSpecialViewHelper(getContext());
        }
        return mEmptyViewHelper;
    }

    @Override public void setCustomEmptyView(View emptyView) {
        getEmptyViewHelper().setCustomEmptyView(emptyView);
    }

    @Override public void setCustomEmptyViewHeight(int height, int offset) {
        getEmptyViewHelper().setCustomEmptyViewHeight(height, offset);
    }

    public int getCustomEmptyViewHeight() {
        return getEmptyViewHelper().getInnerEmptyViewHeightSafely();
    }

    public View getCustomEmptyView() {
        return getEmptyViewHelper().getCustomEmptyView();
    }

    private View getInnerEmptyViewSafely() {
        return getEmptyViewHelper().getInnerEmptyViewSafely();
    }

    @Override public void setContentAutoCompletionColor(int color) {
        getEmptyViewHelper().setContentAutoCompletionColor(color);
    }

    @Override public View get() {
        return this;
    }

    private View getAutoCompletionView() {
        return getEmptyViewHelper().getContentAutoCompletionView();
    }

    private View getAutoCompletionViewSafely() {
        return getEmptyViewHelper().getContentAutoCompletionViewSafely();
    }

    public void setContentAutoCompletionViewOffset(int offset) {
        getEmptyViewHelper().setContentAutoCompletionViewOffset(offset);
    }

    public void setContentAutoCompletionViewAutomaticMinimumHeight(boolean isAutomatic) {
        getEmptyViewHelper().setContentAutoCompletionViewAutomaticMinimumHeight(isAutomatic);
    }

    private View configureAutoEmptyCompletionView(int height) {
        View autoEmptyCompletion = getAutoCompletionViewSafely();
        LayoutParams lp = (LayoutParams) autoEmptyCompletion.getLayoutParams();
        if (lp == null) {
            lp = new LayoutParams(MagicHeaderUtils.getScreenWidth(getContext()), height);
            autoEmptyCompletion.setLayoutParams(lp);
        } else {
            if (lp.height != height) {
                lp.height = height;
                autoEmptyCompletion.requestLayout();
            }
        }
        return autoEmptyCompletion;
    }

    private int getGapHeight(int position) {
        List<IntegerVariable> heights = getHeightsSafely();
        int heightSum = getItemHeightSum(position, heights);
        int itemAreaHeight = mOuterScroller.getContentAreaMaxVisibleHeight();
        if (getEmptyViewHelper().isContentAutoCompletionViewAutomaticMinimumHeight()) {
            int emptyViewHeight = getCustomEmptyViewHeight();
            return Math.min((itemAreaHeight - heightSum), emptyViewHeight);
        }
        return Math.max(0, itemAreaHeight - heightSum - mEmptyViewHelper.getContentAutoCompletionViewOffset());
    }

    ArrayList<IntegerVariable> heights;

    private static final int INVALID_RESULT = -1;
    // Their value will be update before use every time, so no need to clear them.
    private int old_FirstVisiblePosition;
    private int old_LastVisiblePosition;

    /**
     * Get ListView pixel scrollY
     * （Logic ScrollY, can be thousands, even tens of thousands ）
     */
    private int getListViewScrollY() {

        if (getChildCount() == 0) {
            return INVALID_RESULT;
        }
        int result = 0;
        // use a local variable to accelerate
        ArrayList<IntegerVariable> heightsLocal = getHeightsSafely();
        int firstVisiblePosition = getFirstVisiblePosition();
        int lastVisiblePosition = getLastVisiblePosition();
        // measure if the first or last position changed
        if (firstVisiblePosition != old_FirstVisiblePosition || lastVisiblePosition != old_LastVisiblePosition) {
            // update value
            old_FirstVisiblePosition = firstVisiblePosition;
            old_LastVisiblePosition = lastVisiblePosition;

            if (Math.max(heightsLocal.size() - 1, getInvisibleHeaderCount() - 1) < firstVisiblePosition) {
                Log.w(TAG, "Warning：heights.size() -1=" + (heights.size() - 1) + ", firstVisiblePosition=" + firstVisiblePosition + ", Some items may not be measured.");
            }
            MagicHeaderUtils.ensureCapacityWithEmptyObject(heightsLocal, lastVisiblePosition + 1, IntegerVariable.class);

            int tempMeasureHeight;
            IntegerVariable tempMeaseredHeight = null;
            for (int i = Math.max(firstVisiblePosition, getInvisibleHeaderCount()); i <= lastVisiblePosition; i++) {

                tempMeasureHeight = getChildAt(i - firstVisiblePosition).getMeasuredHeight();
                if (i >= 0 && i < heightsLocal.size()) {
                    tempMeaseredHeight = heightsLocal.get(i);
                }
                if (tempMeaseredHeight != null && tempMeasureHeight != tempMeaseredHeight.getValue()) {
                    tempMeaseredHeight.setValue(tempMeasureHeight);
                }
            }
            // clear invalid value
            IntegerVariable tempIntegerVariable;
            for (int i = lastVisiblePosition + 1; i < heightsLocal.size(); i++) {
                tempIntegerVariable = heightsLocal.get(i);
                if (tempIntegerVariable.getValue() != 0) {
                    tempIntegerVariable.setValue(0);
                }
            }
        }

        for (int i = 0; i < firstVisiblePosition; i++) {
            result += heightsLocal.get(i).getValue();
        }
        final int top = getChildAt(0).getTop();
        result -= top;
        return result;
    }

    /**
     * (as its name)
     */
    private ArrayList<IntegerVariable> ensureEmptyHeaderHeight() {
        if (heights == null) {
            heights = new ArrayList<>();
        }
        MagicHeaderUtils.ensureCapacityWithEmptyObject(heights, 1, IntegerVariable.class);
        heights.set(0, mEmptyHeaderHeight);
        return heights;
    }

    private ArrayList<IntegerVariable> getHeightsSafely() {
        if (heights == null) {
            ensureEmptyHeaderHeight();
        }
        return heights;
    }

    private void updateEmptyHeaderHeight() {
        ensureEmptyHeaderHeight();
        if (mItemPosition > ORIGIN_ITEM_POSITION) {
            mItemPosition = ORIGIN_ITEM_POSITION;
            mItemMarginTop2Header = ORIGIN_ITEM_MARGIN_TOP_2HEADER;
            performScroll(mItemMarginTop2Header);
        }
    }

    /**
     * Get heights of items excluding empty header, used for scrolling.
     */
    private int getItemHeightSum(int LastPosition, List<IntegerVariable> heights) {
        int heightSum = 0;
        int start = getInvisibleHeaderCount();
        int index = getHeaderViewsCount() + LastPosition;
        int end = Math.min(index + 1, heights.size());
        for (int i = start; i < end; i++) {
            if (i == index) {
                continue;
            }
            heightSum += heights.get(i).getValue();
        }
        return heightSum;
    }
}