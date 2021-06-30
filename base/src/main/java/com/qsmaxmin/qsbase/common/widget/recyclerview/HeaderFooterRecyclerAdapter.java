package com.qsmaxmin.qsbase.common.widget.recyclerview;

import android.view.View;
import android.view.ViewGroup;

import com.qsmaxmin.qsbase.common.log.L;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/**
 * @CreateBy qsmaxmin
 * @Date 2016/11/24 11:19
 * @Description
 */
public class HeaderFooterRecyclerAdapter extends RecyclerView.Adapter {
    private final static int                              HEADER_VIEW_TYPE = 1 << 31;
    private final static int                              FOOTER_VIEW_TYPE = 1 << 30;
    private final        List<View>                       headerView;
    private final        List<View>                       footerView;
    private final        RecyclerView.Adapter             innerAdapter;
    private final        RecyclerView.AdapterDataObserver dataObserver;

    public HeaderFooterRecyclerAdapter(RecyclerView.Adapter innerAdapter, List<View> headerViews, List<View> footerViews) {
        this.innerAdapter = innerAdapter;
        this.headerView = headerViews;
        this.footerView = footerViews;
        this.dataObserver = new RecyclerView.AdapterDataObserver() {
            @Override public void onChanged() {
                notifyDataSetChanged();
            }

            @Override public void onItemRangeChanged(int positionStart, int itemCount) {
                notifyItemRangeChanged(positionStart + headerView.size(), itemCount);
            }

            @Override public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
                notifyItemRangeChanged(positionStart + headerView.size(), itemCount, payload);
            }

            @Override public void onItemRangeInserted(int positionStart, int itemCount) {
                notifyItemRangeInserted(positionStart + headerView.size(), itemCount);
            }

            @Override public void onItemRangeRemoved(int positionStart, int itemCount) {
                notifyItemRangeRemoved(positionStart + headerView.size(), itemCount);
            }

            @Override public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                notifyItemMoved(fromPosition, toPosition);
            }
        };
        innerAdapter.registerAdapterDataObserver(dataObserver);
    }

    @Override public int getItemViewType(int position) {
        int headerSize = headerView.size();
        if (position < headerSize) {
            return HEADER_VIEW_TYPE;
        } else {
            int realPosition = position - headerSize;
            if (realPosition < innerAdapter.getItemCount()) {
                return innerAdapter.getItemViewType(realPosition);
            } else {
                return FOOTER_VIEW_TYPE;
            }
        }
    }

    @NonNull @Override public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case HEADER_VIEW_TYPE: {
                View view = headerView.get(0);
                if (L.isEnable()) {
                    L.i("HeaderFooterRecyclerAdapter", "onCreateViewHolder....HEADER_VIEW_TYPE, parent:" + view.getParent());
                }
                if (view.getParent() != null) {
                    parent.removeView(view);
                }
                return new HeaderFooterViewHolder(view);
            }
            case FOOTER_VIEW_TYPE: {
                View view = footerView.get(0);
                if (L.isEnable()) {
                    L.i("HeaderFooterRecyclerAdapter", "onCreateViewHolder....FOOTER_VIEW_TYPE, parent:" + view.getParent());
                }
                if (view.getParent() != null) {
                    parent.removeView(view);
                }
                return new HeaderFooterViewHolder(view);
            }
            default:
                return innerAdapter.onCreateViewHolder(parent, viewType);
        }
    }

    @SuppressWarnings("unchecked")
    @Override public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int headerSize = headerView.size();
        int itemCount = innerAdapter.getItemCount();
        int realPosition = position - headerSize;
        if (position >= headerSize && realPosition < itemCount) {
            innerAdapter.onBindViewHolder(holder, realPosition);
        }
    }

    @Override public int getItemCount() {
        return innerAdapter.getItemCount() + headerView.size() + footerView.size();
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

    public void release() {
        if (innerAdapter != null) {
            innerAdapter.unregisterAdapterDataObserver(dataObserver);
        }
    }

    private static class HeaderFooterViewHolder extends RecyclerView.ViewHolder {
        public HeaderFooterViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
