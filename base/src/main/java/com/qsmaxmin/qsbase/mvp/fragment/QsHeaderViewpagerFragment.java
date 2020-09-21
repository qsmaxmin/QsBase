package com.qsmaxmin.qsbase.mvp.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.common.widget.headerview.HeaderScrollView;
import com.qsmaxmin.qsbase.common.widget.headerview.ScrollerProvider;
import com.qsmaxmin.qsbase.mvp.QsIHeaderView;
import com.qsmaxmin.qsbase.mvp.presenter.QsPresenter;

import androidx.fragment.app.Fragment;

/**
 * @CreateBy qsmaxmin
 * @Date 2016/11/21 16:06
 * @Description
 */

public abstract class QsHeaderViewpagerFragment<P extends QsPresenter> extends QsViewPagerFragment<P> implements QsIHeaderView {
    private HeaderScrollView headerScrollView;

    @Override public int layoutId() {
        return R.layout.qs_header_viewpager;
    }

    @Override public int getHeaderLayout() {
        return 0;
    }

    @Override protected View initView(LayoutInflater inflater) {
        View view = super.initView(inflater);
        if (view instanceof HeaderScrollView) {
            headerScrollView = (HeaderScrollView) view;
        } else {
            headerScrollView = view.findViewById(R.id.qs_header_scroll_view);
        }
        headerScrollView.registerScrollerProvider(this);
        headerScrollView.setOnScrollListener(this);
        if (getHeaderLayout() != 0) {
            ViewGroup headerContainer = view.findViewById(R.id.qs_header_container);
            inflater.inflate(getHeaderLayout(), headerContainer, true);
        }
        return view;
    }

    @Override public HeaderScrollView getHeaderScrollView() {
        return headerScrollView;
    }

    @Override public void onHeaderScroll(int currentY, int maxY) {
        //custom your logic
    }

    @Override public void smoothScrollToTop(boolean autoRefresh) {
        super.smoothScrollToTop(autoRefresh);
        headerScrollView.smoothScrollToTop();
    }

    @Override public View getScrollableView() {
        Fragment fragment = getCurrentFragment();
        if (fragment instanceof ScrollerProvider) {
            return ((ScrollerProvider) fragment).getScrollableView();
        }
        return null;
    }

}
