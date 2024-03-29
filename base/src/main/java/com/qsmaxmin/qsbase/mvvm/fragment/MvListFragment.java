package com.qsmaxmin.qsbase.mvvm.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.mvvm.MvIListView;
import com.qsmaxmin.qsbase.mvvm.adapter.MvListAdapter;
import com.qsmaxmin.qsbase.mvvm.adapter.MvListAdapterItem;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @CreateBy qsmaxmin
 * @Date 17/7/2  下午4:29
 * @Description
 */
public abstract class MvListFragment<D> extends MvFragment implements MvIListView<D> {
    private final MvListAdapter<D>             mListAdapter = new MvListAdapter<>(this);
    private       ListView                     mListView;
    private       View                         headerView;
    private       View                         footerView;
    private       AbsListView.OnScrollListener scrollListener;

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

    @Override protected View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        View rootView = super.initView(inflater, container);
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

        mListView.setAdapter(mListAdapter);
        mListView.setOnScrollListener(this);
        return rootView;
    }

    @NonNull @Override public final MvListAdapterItem<D> getListAdapterItemInner(int type) {
        MvListAdapterItem<D> adapterItem = getListAdapterItem(type);
        adapterItem.setViewLayer(this);
        return adapterItem;
    }

    @Override public void onReceiveAdapterItemEvent(int eventType, D data, int position) {
        L.i(initTag(), "onReceiveAdapterItemEvent......eventType:" + eventType + ", position:" + position);
    }

    /**
     * @see #setData(List)
     * @see #getViewTypeCount() 必须为1
     * @see MvListAdapterItem
     * listView首次显示前，预加载多少个适配器项
     */
    @Override public int getAdapterItemPreloadSize() {
        return 0;
    }

    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        L.i(initTag(), "onItemClick()... position : " + position);
    }

    @Override public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }


    @Override public final void setData(List<D> list) {
        setData(list, true);
    }

    @Override public void setData(List<D> list, boolean showEmptyView) {
        mListAdapter.setData(list, showEmptyView);
    }

    @Override public final void addData(D d) {
        mListAdapter.addData(d);
    }

    @Override public final void addData(int position, final D d) {
        mListAdapter.addData(position, d);
    }

    @Override public final void addData(List<D> list) {
        addData(list, getData().size());
    }

    @Override public void addData(List<D> list, int position) {
        mListAdapter.addData(list, position);
    }

    @Override public final void delete(final int position) {
        mListAdapter.delete(position);
    }

    @Override public final void delete(final D d) {
        mListAdapter.delete(d);
    }

    @Override public final void deleteAll() {
        mListAdapter.deleteAll();
    }

    @NonNull @Override public final List<D> getData() {
        return mListAdapter.getData();
    }

    @NonNull @Override public final List<D> copyData() {
        return mListAdapter.copyData();
    }

    @Override public D getData(int position) {
        return mListAdapter.getData(position);
    }

    /**
     * 该方法必须在主线程中执行
     */
    @Override public void updateAdapter(final boolean showEmptyView) {
        mListAdapter.updateAdapter(showEmptyView);
    }

    @Override public final BaseAdapter getAdapter() {
        return mListAdapter;
    }

    @Override public void onScrollStateChanged(AbsListView view, int scrollState) {
        mListAdapter.onScrollStateChanged(view, scrollState);
        if (scrollListener != null) scrollListener.onScrollStateChanged(view, scrollState);
    }

    @Override public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (scrollListener != null) scrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
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

    @Override public View getScrollableView() {
        return getListView();
    }

    @Override public void setOnScrollListener(AbsListView.OnScrollListener listener) {
        this.scrollListener = listener;
    }
}
