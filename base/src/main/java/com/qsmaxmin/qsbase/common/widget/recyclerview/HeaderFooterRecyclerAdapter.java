package com.qsmaxmin.qsbase.common.widget.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * @CreateBy qsmaxmin
 * @Date 2016/11/24 11:19
 * @Description
 */

public class HeaderFooterRecyclerAdapter extends RecyclerView.Adapter {

    private List<View>           headerView;
    private List<View>           footerView;
    private RecyclerView.Adapter tagAdapter;

    //定义FooterView类型 和 HeaderView类型
    private final static int HEADER_VIEW_TYPE = Integer.MAX_VALUE / 123;
    private final static int FOOTER_VIEW_TYPE = Integer.MAX_VALUE / 321;


    public HeaderFooterRecyclerAdapter(RecyclerView.Adapter tagAdapter, List<View> headerViews, List<View> footerViews) {
        this.tagAdapter = tagAdapter;
        this.headerView = headerViews;
        this.footerView = footerViews;
    }

    @Override public int getItemViewType(int position) {
        int headerNum = headerView.size();
        if (position < headerNum) {
            return HEADER_VIEW_TYPE;
        } else if (position >= headerNum) {
            int itemCount = tagAdapter.getItemCount();
            int realPosition = position - headerNum;
            if (realPosition < itemCount) {
                return tagAdapter.getItemViewType(realPosition);
            }
        }
        return FOOTER_VIEW_TYPE;
    }

    @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case HEADER_VIEW_TYPE:
                return new HeaderOrFooterView(headerView.get(0));
            case FOOTER_VIEW_TYPE:
                return new HeaderOrFooterView(footerView.get(0));
            default:
                return tagAdapter.onCreateViewHolder(parent, viewType);
        }
    }

    @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int headerNum = headerView.size();
        if (position < headerNum) {
            onHeaderFooterBindViewHolder(holder);
        } else if (position >= headerNum) {
            int itemCount = tagAdapter.getItemCount();
            int realPosition = position - headerNum;
            if (realPosition < itemCount) {
                tagAdapter.onBindViewHolder(holder, realPosition);
            }
            return;
        }
        onHeaderFooterBindViewHolder(holder);
    }

    @Override public int getItemCount() {
        return tagAdapter.getItemCount() + headerView.size() + footerView.size();
    }

    @Override public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        if (holder instanceof HeaderOrFooterView) {
            ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
            if (layoutParams != null && layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) layoutParams;
                params.setFullSpan(true);
            }
        }
    }

    public void registerDataSetObserver(RecyclerView.AdapterDataObserver mDataObserver) {
        if (tagAdapter != null) {
            tagAdapter.registerAdapterDataObserver(mDataObserver);
        }
    }

    public void unregisterDataSetObserver(RecyclerView.AdapterDataObserver mDataObserver) {
        if (tagAdapter != null) {
            tagAdapter.unregisterAdapterDataObserver(mDataObserver);
        }
    }

    private void onHeaderFooterBindViewHolder(RecyclerView.ViewHolder holder) {
        if (holder instanceof HeaderOrFooterView) {
            HeaderOrFooterView headerHolder = (HeaderOrFooterView) holder;
            headerHolder.bindData();
        }
    }

    public interface OnRecyclerViewAdapterBindViewHolder {
        void onAdapterBindViewHolder();
    }

    /**
     * headerView 和 footerView  ViewHolder的持有类
     */
    private class HeaderOrFooterView extends RecyclerView.ViewHolder {
        View view;

        HeaderOrFooterView(View itemView) {
            super(itemView);
            itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            this.view = itemView;
        }

        void bindData() {
            if (view != null && view instanceof OnRecyclerViewAdapterBindViewHolder) {
                OnRecyclerViewAdapterBindViewHolder view = (OnRecyclerViewAdapterBindViewHolder) this.view;
                view.onAdapterBindViewHolder();
            }
        }
    }
}
