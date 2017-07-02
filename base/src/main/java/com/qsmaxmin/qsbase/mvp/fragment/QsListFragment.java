package com.qsmaxmin.qsbase.mvp.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.widget.listview.LoadingFooter;
import com.qsmaxmin.qsbase.common.widget.viewpager.headerpager.base.InnerScroller;
import com.qsmaxmin.qsbase.common.widget.viewpager.headerpager.base.InnerScrollerContainer;
import com.qsmaxmin.qsbase.common.widget.viewpager.headerpager.base.OuterScroller;
import com.qsmaxmin.qsbase.mvp.adapter.QsAdapterItem;
import com.qsmaxmin.qsbase.mvp.presenter.QsPresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sky on 15/2/6. ListFragment 视图
 */
public abstract class QsListFragment<P extends QsPresenter> extends QsFragment<P> implements QsIListFragment, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, AbsListView.OnScrollListener, InnerScrollerContainer {//

    protected List          mList;
    protected ListView      mListView;
    protected BaseAdapter   mListAdapter;
    protected LoadingFooter mLoadingFooter;

    @Override public int layoutId() {
        return R.layout.qs_fragment_listview;
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
        View view = super.initView();
        initListView(view);
        return view;
    }

    /**
     * 初始化ListView
     */
    protected void initListView(View view) {
        if (view instanceof ListView) {
            mListView = (ListView) view;
        } else {
            mListView = (ListView) view.findViewById(android.R.id.list);
        }
        if (mListView == null) throw new RuntimeException("ListView is not exit or its id not 'android.R.id.list' in current layout!!");
        if (getHeaderLayout() != 0) {
            View headerView = LayoutInflater.from(getActivity()).inflate(getHeaderLayout(), null, false);
            mListView.addHeaderView(headerView);
        }
        if (getFooterLayout() != 0) {
            View footerView = LayoutInflater.from(getActivity()).inflate(getFooterLayout(), null, false);
            if (footerView instanceof LoadingFooter) {
                mLoadingFooter = (LoadingFooter) footerView;
            } else {
                mLoadingFooter = (LoadingFooter) footerView.findViewById(R.id.loading_footer);
            }
            mListView.addFooterView(footerView);
        }
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);
        BaseAdapter adapter = onCreateAdapter();
        if (adapter != null) {
            mListAdapter = adapter;
        } else {
            mListAdapter = new MyAdapter();
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


    @Override public void setData(List list) {
        setData(list, true);
    }

    @Override public void setData(List list, boolean bool) {
        mList = list;
        updateAdapter(bool);
    }

    public void addData(List list) {
        if (list != null && list.size() > 0) {
            mList.addAll(list);
            updateAdapter();
        }
    }

    public void delete(int position) {
        mList.remove(position);
        updateAdapter();
    }

    public void deleteAll() {
        mList.clear();
        updateAdapter();
    }

    public final List getData() {
        return mList;
    }

    @Override public void resetAdapter() {
        mListAdapter = new MyAdapter();
        mListView.setAdapter(mListAdapter);
    }

    @Override public void updateAdapter() {
        updateAdapter(true);
    }

    @Override public void updateAdapter(boolean shouldShowEmptyView) {
        if (mListAdapter != null) {
            if (mViewAnimator != null) {
                List list = getData();
                int state = mViewAnimator.getDisplayedChild();
                if (shouldShowEmptyView && (state == 1 || state == 0) && (list == null || list.isEmpty())) {
                    showEmptyView();
                } else if (state != 1 && list != null && !list.isEmpty()) {
                    showContentView();
                }
            }
            mListAdapter.notifyDataSetChanged();
        }
    }


    @Override public BaseAdapter onCreateAdapter() {
        return null;
    }

    @Override public BaseAdapter getAdapter() {
        return mListAdapter;
    }

    private final class MyAdapter extends BaseAdapter {
        MyAdapter() {
            mList = new ArrayList();
        }

        @Override public int getCount() {
            return mList.size();
        }

        @Override public Object getItem(int position) {
            if (position > getCount() - 1) return null;
            return mList.get(position);
        }

        @Override public long getItemId(int position) {
            return position;
        }

        @Override public int getItemViewType(int position) {
            return QsListFragment.this.getItemViewType(position);
        }

        /**
         * 返回类型数量
         **/
        @Override public int getViewTypeCount() {
            return QsListFragment.this.getViewTypeCount();
        }

        /**
         * getView
         **/
        @Override public View getView(int position, View convertView, ViewGroup parent) {
            QsAdapterItem item = null;
            if (convertView == null) {
                int count = getViewTypeCount();
                if (count > 1) {
                    int type = getItemViewType(position);
                    item = getQsAdapterItem(type);
                } else {
                    item = getQsAdapterItem(0);
                }
                convertView = LayoutInflater.from(getActivity()).inflate(item.getItemLayout(), null, false);
                item.init(convertView);
                convertView.setTag(item);
            }
            if (item == null) item = (QsAdapterItem) convertView.getTag();
            if (item != null) item.bindData(getItem(position), position, getCount());
            return convertView;
        }
    }


    @Override public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }



    /*----------------------- 以下是HeaderViewPager支持 ----------------------------*/

    protected OuterScroller mOuterScroller;
    protected int           mIndex;

    @Override public void setMyOuterScroller(OuterScroller outerScroller, int myPosition) {
        mOuterScroller = outerScroller;
        mIndex = myPosition;
        if (mListView instanceof InnerScroller) {
            L.i(initTag(), "注册调度控件：setMyOuterScroller()   position:" + myPosition);
            ((InnerScroller) mListView).register2Outer(mOuterScroller, mIndex);
        }
    }
}
