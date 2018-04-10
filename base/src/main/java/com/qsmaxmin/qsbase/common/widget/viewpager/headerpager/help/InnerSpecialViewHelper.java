package com.qsmaxmin.qsbase.common.widget.viewpager.headerpager.help;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.common.widget.viewpager.headerpager.base.OuterScroller;


/**
 * @CreateBy qsmaxmin
 * @Date 2016/11/22 10:18
 * @Description Helper of special views in InnerScroller.
 */
public class InnerSpecialViewHelper {
    private Context       mContext;
    private OuterScroller mOuterScroller;
    private View          mInnerEmptyView;

    private View mCustomEmptyView;
    private int mCustomEmptyViewHeight = ViewGroup.LayoutParams.MATCH_PARENT;
    private int     mCustomEmptyViewHeightOffset;
    private boolean mContentAutoCompletionViewAutomaticMinimumHeight;


    public InnerSpecialViewHelper(Context context) {
        mContext = context;
    }

    public View getCustomEmptyView() {
        return mCustomEmptyView;
    }

    public View getInnerEmptyView() {
        if (mCustomEmptyView != null) {
            return mCustomEmptyView;
        }
        return mInnerEmptyView;
    }

    public void setInnerEmptyView(View innerEmptyView) {
        mInnerEmptyView = innerEmptyView;
    }

    public View getInnerEmptyViewSafely() {
        if (mCustomEmptyView != null) {
            return mCustomEmptyView;
        }
        if (mInnerEmptyView != null) {
            return mInnerEmptyView;
        }
        mInnerEmptyView = new View(mContext);
        return mInnerEmptyView;
    }


    public int getInnerEmptyViewHeightSafely() {
        int result;
        switch (mCustomEmptyViewHeight) {
            case ViewGroup.LayoutParams.WRAP_CONTENT:
                result = getInnerEmptyViewSafely().getMeasuredHeight();
                break;
            case ViewGroup.LayoutParams.MATCH_PARENT:
                if (mOuterScroller != null) {
                    result = mOuterScroller.getContentAreaMaxVisibleHeight();
                } else {
                    result = MagicHeaderUtils.getScreenHeight(mContext);
                }
                break;
            default:
                result = mCustomEmptyViewHeight;
        }
        result += mCustomEmptyViewHeightOffset;

        // height result may not be larger than outers ContentAreaMaxVisibleHeight.
        // in case of listview position error after notifyDataSetChanged().
        if (mOuterScroller != null) {
            result = Math.min(result, mOuterScroller.getContentAreaMaxVisibleHeight());
        }
        return result;
    }

    public void setOuterScroller(OuterScroller outerScroller) {
        mOuterScroller = outerScroller;
    }

    /********
     * Inner Empty View Customization
     *************/

    public void setCustomEmptyView(View customEmptyView) {
        mCustomEmptyView = customEmptyView;
        mInnerEmptyView = mCustomEmptyView;
    }

    public void setCustomEmptyViewHeight(int height, int offset) {
        mCustomEmptyViewHeight = height;
        mCustomEmptyViewHeightOffset = offset;
    }


    /*************
     * Content Auto Completion View
     *****************/

    public View mContentAutoCompletionView;

    int mContentAutoCompletionColor = ORIGIN_AUTO_COMPLETION_COLOR;

    public View getContentAutoCompletionView() {
        return mContentAutoCompletionView;
    }

    public View getContentAutoCompletionViewSafely() {
        if (mContentAutoCompletionView == null) {
            generateContentAutoCompletionView();
        }
        return mContentAutoCompletionView;
    }

    public void setContentAutoCompletionView(View contentAutoCompletionView) {
        mContentAutoCompletionView = contentAutoCompletionView;
    }

    private static final int ORIGIN_AUTO_COMPLETION_COLOR = Color.TRANSPARENT;

    public void setContentAutoCompletionColor(int color) {
        mContentAutoCompletionColor = color;
        getContentAutoCompletionViewSafely().setBackgroundColor(mContentAutoCompletionColor);
    }

    public View generateContentAutoCompletionView() {
        mContentAutoCompletionView = new View(mContext);
        mContentAutoCompletionView.setTag(R.id.id_for_auto_completion_content, "");
        if (mContentAutoCompletionColor != ORIGIN_AUTO_COMPLETION_COLOR) {
            mContentAutoCompletionView.setBackgroundColor(mContentAutoCompletionColor);
        }
        return mContentAutoCompletionView;
    }

    private int mContentAutoCompletionViewOffset;

    public void setContentAutoCompletionViewOffset(int offset) {
        mContentAutoCompletionViewOffset = offset;
    }

    public int getContentAutoCompletionViewOffset() {
        return mContentAutoCompletionViewOffset;
    }

    public void setContentAutoCompletionViewAutomaticMinimumHeight(boolean isAutomaticMinimumHeight) {
        mContentAutoCompletionViewAutomaticMinimumHeight = isAutomaticMinimumHeight;
    }

    public boolean isContentAutoCompletionViewAutomaticMinimumHeight() {
        return mContentAutoCompletionViewAutomaticMinimumHeight;
    }
}
