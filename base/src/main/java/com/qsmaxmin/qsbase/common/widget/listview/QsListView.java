package com.qsmaxmin.qsbase.common.widget.listview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * @CreateBy qsmaxmin
 * @Date 2019/1/25 11:24
 * @Description
 */
public class QsListView extends ListView {
    private OnScrollChangedListener mListener;

    public QsListView(Context context) {
        super(context);
    }

    public QsListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public QsListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override protected void onScrollChanged(int l, int t, int oldL, int oldT) {
        super.onScrollChanged(l, t, oldL, oldT);
        if (mListener != null) {
            mListener.onScrollChanged(l, t, oldL, oldT);
        }
    }

    public void setOnScrollChangeListener(OnScrollChangedListener listener) {
        this.mListener = listener;
    }

}
