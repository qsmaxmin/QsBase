package com.qsmaxmin.qsbase.mvp;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.common.aspect.ThreadPoint;
import com.qsmaxmin.qsbase.common.aspect.ThreadType;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.mvp.adapter.QsListAdapter;
import com.qsmaxmin.qsbase.mvp.fragment.QsIListView;
import com.qsmaxmin.qsbase.mvp.presenter.QsPresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * @CreateBy administrator
 * @Date 2020/4/9 15:06
 * @Description list activity with actionbar
 */
public abstract class QsListActivity<P extends QsPresenter, D> extends QsActivity<P> implements QsIListView<D> {
    protected final List<D>     mList = new ArrayList<>();
    private         ListView    mListView;
    private         BaseAdapter mListAdapter;
    private         View        headerView;
    private         View        footerView;

    @Override public int layoutId() {
        return R.layout.qs_activity_listview;
    }

    @Override public int getHeaderLayout() {
        return 0;
    }

    @Override public int getFooterLayout() {
        return 0;
    }

    @Override public ListView getListView() {
        return mListView;
    }

    @Override public int getViewTypeCount() {
        return 1;
    }

    @Override public int getItemViewType(int position) {
        return 0;
    }

    @Override protected View initView() {
        View rootView = super.initView();
        LayoutInflater inflater = getLayoutInflater();
        initListView(inflater, rootView);
        return rootView;
    }

    /**
     * 初始化ListView
     */
    protected void initListView(LayoutInflater inflater, View view) {
        if (view instanceof ListView) {
            mListView = (ListView) view;
        } else {
            mListView = view.findViewById(android.R.id.list);
        }
        if (mListView == null) throw new RuntimeException("ListView is not exit or its id not 'android.R.id.list' in current layout!!");
        if (getHeaderLayout() != 0) {
            headerView = inflater.inflate(getHeaderLayout(), null);
            mListView.addHeaderView(headerView);
        }
        if (getFooterLayout() != 0) {
            footerView = inflater.inflate(getFooterLayout(), null);
            mListView.addFooterView(footerView);
        }
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);
        BaseAdapter adapter = onCreateAdapter();
        if (adapter != null) {
            mListAdapter = adapter;
        } else {
            mListAdapter = new QsListAdapter<>(this, mList);
        }
        mListView.setAdapter(mListAdapter);
        mListView.setOnScrollListener(this);
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

    @ThreadPoint(ThreadType.MAIN) @Override public void setData(List<D> list, boolean showEmptyView) {
        mList.clear();
        if (list != null && !list.isEmpty()) mList.addAll(list);
        updateAdapter(showEmptyView);
    }

    @ThreadPoint(ThreadType.MAIN) @Override public void addData(D d) {
        if (d != null) {
            mList.add(d);
            updateAdapter(true);
        }
    }

    @ThreadPoint(ThreadType.MAIN) @Override public void addData(List<D> list) {
        if (list != null && !list.isEmpty()) {
            mList.addAll(list);
            updateAdapter(true);
        }
    }

    @ThreadPoint(ThreadType.MAIN) @Override public void delete(int position) {
        if (position >= 0 && position < mList.size()) {
            mList.remove(position);
            updateAdapter(true);
        }
    }

    @ThreadPoint(ThreadType.MAIN) @Override public void delete(D d) {
        if (d != null) {
            boolean success;
            success = mList.remove(d);
            if (success) updateAdapter(true);
        }
    }

    @ThreadPoint(ThreadType.MAIN) @Override public void deleteAll() {
        if (!mList.isEmpty()) {
            mList.clear();
            updateAdapter(true);
        }
    }

    @Override public final List<D> getData() {
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
            } else if (showContentViewWhenDataLoadingComplete()) {
                showContentView();
            }
        }
    }

    @Override public BaseAdapter onCreateAdapter() {
        return null;
    }

    @Override public BaseAdapter getAdapter() {
        return mListAdapter;
    }

    public View getHeaderView() {
        return headerView;
    }

    public View getFooterView() {
        return footerView;
    }

    @Override public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }

    /**
     * listView是否滑动到顶部
     */
    @Override public boolean canListScrollDown() {
        return getListView().canScrollVertically(-1);
    }

    @Override public boolean showContentViewWhenDataLoadingComplete() {
        return true;
    }

    /**
     * listView是否滑动到底部
     */
    @Override public boolean canListScrollUp() {
        return getListView().canScrollVertically(1);
    }

    @Override public void smoothScrollToTop(boolean autoRefresh) {
        final int firstVisiblePosition = getListView().getFirstVisiblePosition();
        if (firstVisiblePosition > 3) getListView().setSelection(3);
        getListView().post(new Runnable() {
            @Override public void run() {
                getListView().smoothScrollToPositionFromTop(0, 0, 500);
            }
        });
        getListView().postDelayed(new Runnable() {
            @Override public void run() {
                getListView().setSelection(0);
            }
        }, 600);
    }

    @Override public void onAdapterGetView(int position, int totalCount) {
        //for custom logic
    }
}
