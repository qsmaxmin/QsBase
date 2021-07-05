package com.qsmaxmin.qsbase.common.widget.recyclerview;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.qsmaxmin.qsbase.common.log.L;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/**
 * @CreateBy qsmaxmin
 * @Date 2016/11/24 11:19
 * @Description
 */
public class HeaderFooterRecyclerAdapter extends RecyclerView.Adapter {
    private static final int HEADER_VIEW_TYPE = 520131400;
    private static final int FOOTER_VIEW_TYPE = 520141300;

    private final RecyclerView.AdapterDataObserver dataObserver;
    private       RecyclerView.Adapter             innerAdapter;
    private       View                             headerView;
    private       View                             footerView;

    HeaderFooterRecyclerAdapter() {
        dataObserver = new RecyclerView.AdapterDataObserver() {
            @Override public void onChanged() {
                notifyDataSetChanged();
            }

            @Override public void onItemRangeChanged(int positionStart, int itemCount) {
                notifyItemRangeChanged(positionStart + getHeaderViewSize(), itemCount);
            }

            @Override public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
                notifyItemRangeChanged(positionStart + getHeaderViewSize(), itemCount, payload);
            }

            @Override public void onItemRangeInserted(int positionStart, int itemCount) {
                notifyItemRangeInserted(positionStart + getHeaderViewSize(), itemCount);
            }

            @Override public void onItemRangeRemoved(int positionStart, int itemCount) {
                notifyItemRangeRemoved(positionStart + getHeaderViewSize(), itemCount);
            }
        };
    }

    void bindAdapter(@Nullable RecyclerView.Adapter adapter) {
        if (innerAdapter != null) {
            innerAdapter.unregisterAdapterDataObserver(dataObserver);
        }
        if (adapter != null) {
            adapter.registerAdapterDataObserver(dataObserver);
        }
        innerAdapter = adapter;
    }

    @Override public int getItemViewType(int position) {
        int headerSize = getHeaderViewSize();
        if (position < headerSize) {
            return HEADER_VIEW_TYPE;
        } else {
            int innerItemCount = innerAdapter.getItemCount();
            int innerPosition = position - headerSize;
            if (innerPosition < innerItemCount) {
                return innerAdapter.getItemViewType(innerPosition);
            } else {
                return FOOTER_VIEW_TYPE;
            }
        }
    }

    @Override public long getItemId(int position) {
        return position;
    }

    @NonNull @Override public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case HEADER_VIEW_TYPE: {
                ViewParent p = headerView.getParent();
                if (L.isEnable()) {
                    L.i("HeaderFooterRecyclerAdapter", "onCreateViewHolder....HEADER_VIEW_TYPE, parent:" + p);
                }
                if (p != null) parent.removeView(headerView);
                return new HeaderFooterViewHolder(headerView);
            }
            case FOOTER_VIEW_TYPE: {
                ViewParent p = footerView.getParent();
                if (L.isEnable()) {
                    L.i("HeaderFooterRecyclerAdapter", "onCreateViewHolder....FOOTER_VIEW_TYPE, parent:" + p);
                }
                if (p != null) parent.removeView(footerView);
                return new HeaderFooterViewHolder(footerView);
            }
            default:
                return innerAdapter.onCreateViewHolder(parent, viewType);
        }
    }

    @SuppressWarnings("unchecked")
    @Override public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int headerSize = getHeaderViewSize();
        int realPosition = position - headerSize;
        if (position >= headerSize && realPosition < innerAdapter.getItemCount()) {
            innerAdapter.onBindViewHolder(holder, realPosition);
        }
    }

    @Override public int getItemCount() {
        return innerAdapter.getItemCount() + getHeaderViewSize() + getFooterViewSize();
    }

    @Override public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        if (holder instanceof HeaderFooterViewHolder) {
            ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
            if (layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) layoutParams;
                params.setFullSpan(true);
            }
        }
    }

    @Override public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = (GridLayoutManager) manager;
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override public int getSpanSize(int position) {
                    int itemViewType = getItemViewType(position);
                    if (itemViewType == HEADER_VIEW_TYPE || itemViewType == FOOTER_VIEW_TYPE) {
                        return gridManager.getSpanCount();
                    } else {
                        return 1;
                    }
                }
            });
        }
    }

    void setHeaderView(View view) {
        headerView = view;
        update();
    }

    void setFooterView(View view) {
        footerView = view;
        update();
    }

    boolean removeHeaderView() {
        boolean removed = getHeaderViewSize() > 0;
        if (removed) {
            headerView = null;
            update();
        }
        return removed;
    }

    boolean removeFooterView() {
        boolean removed = getFooterViewSize() > 0;
        if (removed) {
            footerView = null;
            update();
        }
        return removed;
    }

    private void update() {
        if (innerAdapter != null) {
            innerAdapter.notifyDataSetChanged();
        }
    }

    int getHeaderViewSize() {
        return headerView == null ? 0 : 1;
    }

    int getFooterViewSize() {
        return footerView == null ? 0 : 1;
    }

    View getHeaderView() {
        return headerView;
    }

    View getFooterView() {
        return footerView;
    }

    private static class HeaderFooterViewHolder extends RecyclerView.ViewHolder {
        public HeaderFooterViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
