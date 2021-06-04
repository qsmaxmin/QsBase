package com.qsmaxmin.qsbase.mvvm.adapter;

import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.utils.QsHelper;
import com.qsmaxmin.qsbase.mvvm.MvIListView;

import java.util.ArrayList;
import java.util.List;

/**
 * @CreateBy qsmaxmin
 * @Date 2020/12/9 11:57
 * @Description
 */
public class MvListAdapter<D> extends BaseAdapter {
    private final MvIListView<D>                    listLayer;
    private final List<D>                           mList;
    private final int                               preloadSize;
    private       boolean                           hasInitPreload;
    private       SparseArray<MvListAdapterItem<D>> preloadCache;

    public MvListAdapter(MvIListView<D> listLayer) {
        this.listLayer = listLayer;
        this.mList = new ArrayList<>();
        this.preloadSize = listLayer.getAdapterItemPreloadSize();
    }

    @Override public int getCount() {
        return mList.size();
    }

    @Override public D getItem(int position) {
        if (position > getCount() - 1) return null;
        return mList.get(position);
    }

    @Override public long getItemId(int position) {
        return position;
    }

    @Override public int getItemViewType(int position) {
        return listLayer.getItemViewType(position);
    }

    @Override public int getViewTypeCount() {
        return listLayer.getViewTypeCount();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override public View getView(int position, View convertView, ViewGroup parent) {
        listLayer.onAdapterGetView(position, getCount());
        MvListAdapterItem<D> item = null;
        if (convertView == null) {
            if (preloadCache != null) {
                item = getPreloadItem(position);
            }
            if (item != null) {
                convertView = item.getPreloadedView();
            } else {
                item = createListAdapterItem(position);
                convertView = item.onCreateItemView(LayoutInflater.from(parent.getContext()), parent);
            }
            item.init(convertView);
            convertView.setTag(item);
        } else {
            item = (MvListAdapterItem) convertView.getTag();
        }

        if (item != null) {
            item.bindDataInner(getItem(position), position, getCount());
        }
        return convertView;
    }

    private MvListAdapterItem<D> getPreloadItem(int position) {
        if (preloadCache != null) {
            MvListAdapterItem<D> item = preloadCache.get(position);
            preloadCache.remove(position);
            Log.e("MvListAdapter", "getView from preload, position:" + position + ", cacheSize:" + preloadCache.size());
            if (preloadCache.size() == 0 || position >= preloadSize) {
                preloadCache = null;
            }
            return item;
        }
        return null;
    }

    private MvListAdapterItem<D> createListAdapterItem(int position) {
        MvListAdapterItem<D> item;
        int count = getViewTypeCount();
        if (count > 1) {
            int type = getItemViewType(position);
            item = listLayer.getListAdapterItemInner(type);
        } else {
            item = listLayer.getListAdapterItemInner(0);
        }
        return item;
    }

    public final void onScrollStateChanged(AbsListView view, int scrollState) {
        int childCount = view.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = view.getChildAt(i);
            Object tag = childView.getTag();
            if (tag instanceof MvListAdapterItem) {
                MvListAdapterItem item = (MvListAdapterItem) tag;
                item.onScrollStateChangedInner(scrollState);
            }
        }
    }

    public final void setData(final List<D> list, final boolean showEmptyView) {
        if (QsHelper.isMainThread()) {
            if (list != mList) {
                mList.clear();
                if (list != null && !list.isEmpty()) mList.addAll(list);
            }
            updateAdapter(showEmptyView);
            if (!hasInitPreload && preloadSize > 0 && L.isEnable()) {
                hasInitPreload = true;
                preloadCache = null;
                L.e("MvListAdapter", "setData(xxx)方法必须在异步线程中执行才能开启 [首次加载异步预初始化] 功能’");
            }
        } else {
            if (!hasInitPreload && preloadSize > 0 && list != null && list.size() > 0) {
                hasInitPreload = true;
                long st = 0;
                if (L.isEnable()) st = System.currentTimeMillis();
                if (list != mList) {
                    mList.clear();
                    mList.addAll(list);
                }
                int realPreloadSize = Math.min(preloadSize, list.size());
                preloadCache = new SparseArray<>(realPreloadSize);
                LayoutInflater inflater = LayoutInflater.from(listLayer.getContext());
                for (int i = 0; i < realPreloadSize; i++) {
                    MvListAdapterItem<D> item = createListAdapterItem(i);
                    item.preCreateItemView(inflater, listLayer.getListView());
                    preloadCache.put(i, item);
                }
                if (L.isEnable()) {
                    long useTime = System.currentTimeMillis() - st;
                    L.i("MvListAdapter", "setData...首次加载异步预初始化适配器项" + realPreloadSize + "个, 耗时：" + (useTime) + "ms");
                }
                listLayer.post(new Runnable() {
                    @Override public void run() {
                        updateAdapter(showEmptyView);
                    }
                });
            } else {
                preloadCache = null;
                listLayer.post(new Runnable() {
                    @Override public void run() {
                        if (list != mList) {
                            mList.clear();
                            if (list != null && !list.isEmpty()) mList.addAll(list);
                        }
                        updateAdapter(showEmptyView);
                    }
                });
            }
        }
    }

    public final void addData(final D d) {
        if (d != null) {
            if (QsHelper.isMainThread()) {
                mList.add(d);
                updateAdapter(true);
            } else {
                listLayer.post(new Runnable() {
                    @Override public void run() {
                        mList.add(d);
                        updateAdapter(true);
                    }
                });
            }
        }
    }

    public final void addData(final int position, final D d) {
        if (d != null) {
            if (QsHelper.isMainThread()) {
                mList.add(position, d);
                updateAdapter(true);
            } else {
                listLayer.post(new Runnable() {
                    @Override public void run() {
                        mList.add(position, d);
                        updateAdapter(true);
                    }
                });
            }
        }
    }

    public final void addData(final List<D> list) {
        addData(list, mList.size());
    }

    public void addData(final List<D> list, int position) {
        if (list != null && !list.isEmpty() && position >= 0) {
            if (QsHelper.isMainThread()) {
                position = Math.min(position, mList.size());
                mList.addAll(position, list);
                updateAdapter(true);
            } else {
                final int finalPosition = Math.min(position, mList.size());
                listLayer.post(new Runnable() {
                    @Override public void run() {
                        mList.addAll(finalPosition, list);
                        updateAdapter(true);
                    }
                });
            }
        }
    }

    public final void delete(final int position) {
        if (position >= 0 && position < mList.size()) {
            if (QsHelper.isMainThread()) {
                mList.remove(position);
                updateAdapter(true);
            } else {
                listLayer.post(new Runnable() {
                    @Override public void run() {
                        mList.remove(position);
                        updateAdapter(true);
                    }
                });
            }
        }
    }

    public final void delete(final D d) {
        if (d != null) {
            if (QsHelper.isMainThread()) {
                boolean success;
                success = mList.remove(d);
                if (success) updateAdapter(true);
            } else {
                listLayer.post(new Runnable() {
                    @Override public void run() {
                        boolean success;
                        success = mList.remove(d);
                        if (success) updateAdapter(true);
                    }
                });
            }
        }
    }

    public final void deleteAll() {
        if (!mList.isEmpty()) {
            if (QsHelper.isMainThread()) {
                mList.clear();
                updateAdapter(true);
            } else {
                listLayer.post(new Runnable() {
                    @Override public void run() {
                        mList.clear();
                        updateAdapter(true);
                    }
                });
            }
        }
    }

    public List<D> getData() {
        return mList;
    }

    public final List<D> copyData() {
        ArrayList<D> list = new ArrayList<>();
        if (!mList.isEmpty()) list.addAll(mList);
        return list;
    }

    public D getData(int position) {
        if (position >= 0 && position < mList.size()) {
            return mList.get(position);
        }
        return null;
    }

    public void updateAdapter(final boolean showEmptyView) {
        notifyDataSetChanged();
        if (mList.isEmpty() && showEmptyView) {
            listLayer.showEmptyView();
        } else {
            listLayer.showContentView();
        }
    }
}
