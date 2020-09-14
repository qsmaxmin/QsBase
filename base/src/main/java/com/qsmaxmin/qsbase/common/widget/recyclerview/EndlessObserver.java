package com.qsmaxmin.qsbase.common.widget.recyclerview;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/**
 * @CreateBy administrator
 * @Date 2020/9/14 9:50
 * @Description
 */
public abstract class EndlessObserver {

    public abstract void onLoadNextPage();

    public void onScrollStateChanged(RecyclerView view, int newState) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            RecyclerView.LayoutManager manager = view.getLayoutManager();
            if (manager instanceof LinearLayoutManager) {
                if (isTrigger(view, (LinearLayoutManager) manager)) {
                    onLoadNextPage();
                }
            } else if (manager instanceof StaggeredGridLayoutManager) {
                if (isTrigger(view, (StaggeredGridLayoutManager) manager)) {
                    onLoadNextPage();
                }
            }
        }
    }

    private boolean isTrigger(RecyclerView view, LinearLayoutManager manager) {
        int orientation = manager.getOrientation();
        return (orientation == RecyclerView.VERTICAL && !view.canScrollVertically(1))
                || (orientation == RecyclerView.HORIZONTAL && !view.canScrollHorizontally(1));
    }

    private boolean isTrigger(RecyclerView view, StaggeredGridLayoutManager manager) {
        int orientation = manager.getOrientation();
        return (orientation == RecyclerView.VERTICAL && !view.canScrollVertically(1))
                || (orientation == RecyclerView.HORIZONTAL && !view.canScrollHorizontally(1));
    }
}
