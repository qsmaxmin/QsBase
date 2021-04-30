package com.qsmaxmin.qsbase.mvvm;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.utils.QsHelper;
import com.qsmaxmin.qsbase.mvvm.adapter.MvListAdapter;
import com.qsmaxmin.qsbase.mvvm.adapter.MvListAdapterItem;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

/**
 * @CreateBy qsmaxmin
 * @Date 2020/12/9 11:51
 * @Description
 */
public abstract class MvListActivity<D> extends MvActivity implements MvIListView<D> {
    protected final List<D>     mList = new ArrayList<>();
    private         ListView    mListView;
    private         BaseAdapter mListAdapter;
    private         View        headerView;
    private         View        footerView;

    @Override public int layoutId() {
        return R.layout.qs_listview;
    }

    @Override public int getHeaderLayout() {
        return 0;
    }

    @Override public int getFooterLayout() {
        return 0;
    }

    @Override public View onCreateListHeaderView(@NonNull LayoutInflater inflater) {
        return getHeaderLayout() == 0 ? null : inflater.inflate(getHeaderLayout(), null);
    }

    @Override public View onCreateListFooterView(@NonNull LayoutInflater inflater) {
        return getFooterLayout() == 0 ? null : inflater.inflate(getFooterLayout(), null);
    }

    @Override public final View getHeaderView() {
        return headerView;
    }

    @Override public final View getFooterView() {
        return footerView;
    }

    @Override public final ListView getListView() {
        return mListView;
    }

    @Override public int getViewTypeCount() {
        return 1;
    }

    @Override public int getItemViewType(int position) {
        return 0;
    }

    @NonNull @Override protected View initView(@NonNull LayoutInflater inflater) {
        View rootView = super.initView(inflater);
        mListView = rootView.findViewById(android.R.id.list);
        if (mListView == null) throw new RuntimeException("ListView is not exit or its id not 'android.R.id.list' in current layout!!");

        headerView = onCreateListHeaderView(inflater);
        if (headerView != null) {
            mListView.addHeaderView(headerView);
        }

        footerView = onCreateListFooterView(inflater);
        if (footerView != null) {
            mListView.addFooterView(footerView);
        }
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);
        mListAdapter = new MvListAdapter<>(this, mList);
        mListView.setAdapter(mListAdapter);
        mListView.setOnScrollListener(this);
        return rootView;
    }

    @NonNull @Override public MvListAdapterItem<D> getListAdapterItemInner(int type) {
        MvListAdapterItem<D> adapterItem = getListAdapterItem(type);
        adapterItem.setViewLayer(this);
        return adapterItem;
    }

    @Override public void onReceiveAdapterItemEvent(int eventType, D data, int position) {
        if (L.isEnable()) L.i(initTag(), "onReceiveAdapterItemEvent......eventType:" + eventType + ", position:" + position);
    }

    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (L.isEnable()) L.i(initTag(), "onItemClick... position:" + position);
    }

    @Override public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }


    @Override public final void setData(List<D> list) {
        setData(list, true);
    }

    @Override public void setData(final List<D> list, final boolean showEmptyView) {
        if (QsHelper.isMainThread()) {
            if (list != mList) {
                mList.clear();
                if (list != null && !list.isEmpty()) mList.addAll(list);
            }
            updateAdapter(showEmptyView);
        } else {
            post(new Runnable() {
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

    @Override public final void addData(final D d) {
        if (d != null) {
            if (QsHelper.isMainThread()) {
                mList.add(d);
                updateAdapter(true);
            } else {
                post(new Runnable() {
                    @Override public void run() {
                        mList.add(d);
                        updateAdapter(true);
                    }
                });
            }
        }
    }

    @Override public final void addData(final int position, final D d) {
        if (d != null) {
            if (QsHelper.isMainThread()) {
                mList.add(position, d);
                updateAdapter(true);
            } else {
                post(new Runnable() {
                    @Override public void run() {
                        mList.add(position, d);
                        updateAdapter(true);
                    }
                });
            }
        }
    }

    @Override public final void addData(List<D> list) {
        addData(list, mList.size());
    }

    @Override public void addData(final List<D> list, int position) {
        if (list != null && !list.isEmpty() && position >= 0) {
            if (QsHelper.isMainThread()) {
                position = Math.min(position, mList.size());
                mList.addAll(position, list);
                updateAdapter(true);
            } else {
                final int finalPosition = Math.min(position, mList.size());
                post(new Runnable() {
                    @Override public void run() {
                        mList.addAll(finalPosition, list);
                        updateAdapter(true);
                    }
                });
            }
        }
    }

    @Override public final void delete(final int position) {
        if (position >= 0 && position < mList.size()) {
            if (QsHelper.isMainThread()) {
                mList.remove(position);
                updateAdapter(true);
            } else {
                post(new Runnable() {
                    @Override public void run() {
                        mList.remove(position);
                        updateAdapter(true);
                    }
                });
            }
        }
    }

    @Override public final void delete(final D d) {
        if (d != null) {
            if (QsHelper.isMainThread()) {
                boolean success;
                success = mList.remove(d);
                if (success) updateAdapter(true);
            } else {
                post(new Runnable() {
                    @Override public void run() {
                        boolean success;
                        success = mList.remove(d);
                        if (success) updateAdapter(true);
                    }
                });
            }
        }
    }

    @Override public final void deleteAll() {
        if (!mList.isEmpty()) {
            if (QsHelper.isMainThread()) {
                mList.clear();
                updateAdapter(true);
            } else {
                post(new Runnable() {
                    @Override public void run() {
                        mList.clear();
                        updateAdapter(true);
                    }
                });
            }
        }
    }

    @NonNull @Override public final List<D> getData() {
        return mList;
    }

    @NonNull @Override public final List<D> copyData() {
        ArrayList<D> list = new ArrayList<>();
        if (!mList.isEmpty()) list.addAll(mList);
        return list;
    }

    @Override public D getData(int position) {
        if (position >= 0 && position < mList.size()) {
            return mList.get(position);
        }
        return null;
    }

    /**
     * 该方法必须在主线程中执行
     */
    @Override public void updateAdapter(final boolean showEmptyView) {
        if (mListAdapter != null) {
            mListAdapter.notifyDataSetChanged();
            if (mList.isEmpty() && showEmptyView) {
                showEmptyView();
            } else {
                showContentView();
            }
        }
    }

    @Override public final BaseAdapter getAdapter() {
        return mListAdapter;
    }


    @Override public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }

    @Override public boolean canListScrollDown() {
        return getListView() != null && getListView().canScrollVertically(-1);
    }

    @Override public boolean canListScrollUp() {
        return getListView() != null && getListView().canScrollVertically(1);
    }

    @Override public void smoothScrollToTop(boolean autoRefresh) {
        final ListView listView = getListView();
        if (listView == null) return;
        final int firstVisiblePosition = listView.getFirstVisiblePosition();
        if (firstVisiblePosition > 3) listView.setSelection(3);
        listView.post(new Runnable() {
            @Override public void run() {
                listView.smoothScrollToPositionFromTop(0, 0, 500);
            }
        });
        listView.postDelayed(new Runnable() {
            @Override public void run() {
                listView.setSelection(0);
            }
        }, 600);
    }

    @Override public void onAdapterGetView(int position, int totalCount) {
        //for custom logic
    }
}
