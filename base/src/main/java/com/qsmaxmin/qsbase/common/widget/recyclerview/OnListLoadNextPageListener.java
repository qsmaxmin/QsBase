package com.qsmaxmin.qsbase.common.widget.recyclerview;

import android.view.View;

/**
 * RecyclerView/ListView/GridView 滑动加载下一页时的回调接口
 */
public interface OnListLoadNextPageListener {

    /**
     * 开始加载下一页
     * @param view 当前RecyclerView/ListView/GridView
     */
    void onLoadNextPage(View view);
}
