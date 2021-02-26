package com.qsmaxmin.qsbase.mvp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.common.utils.ViewHelper;
import com.qsmaxmin.qsbase.common.widget.headerview.HeaderScrollView;
import com.qsmaxmin.qsbase.common.widget.headerview.ScrollerProvider;
import com.qsmaxmin.qsbase.mvp.presenter.QsPresenter;
import com.qsmaxmin.qsbase.mvvm.MvIHeaderViewPager;

import androidx.fragment.app.Fragment;

/**
 * @CreateBy administrator
 * @Date 2020/9/9 10:24
 * @Description
 */
public abstract class QsHeaderViewPagerActivity<P extends QsPresenter> extends QsViewPagerActivity<P> implements MvIHeaderViewPager {
    private HeaderScrollView headerScrollView;

    @Override public int layoutId() {
        return R.layout.qs_header_viewpager;
    }

    @Override protected View initView(LayoutInflater inflater) {
        View view = super.initView(inflater);
        if (view instanceof HeaderScrollView) {
            headerScrollView = (HeaderScrollView) view;
        } else {
            headerScrollView = view.findViewById(R.id.qs_header_scroll_view);
        }
        if (headerScrollView == null) throw new IllegalStateException("HeaderScrollView is not exist or its id not 'R.id.qs_header_scroll_view' in current layout!!");
        headerScrollView.registerScrollerProvider(this);
        headerScrollView.setOnScrollListener(this);

        ViewGroup headerContainer = view.findViewById(R.id.qs_header_container);
        View headerView = onCreateHeaderView(inflater, headerContainer);
        if (headerView != null) {
            ViewHelper.addToParent(headerView, headerContainer);
        }
        return view;
    }

    @Override public HeaderScrollView getHeaderScrollView() {
        return headerScrollView;
    }

    @Override public void onHeaderScroll(int currentY, int maxY) {
        //custom your logic
    }

    @Override public View getScrollableView() {
        Fragment fragment = getCurrentFragment();
        if (fragment instanceof ScrollerProvider) {
            return ((ScrollerProvider) fragment).getScrollableView();
        }
        return null;
    }

    @Override public void smoothScrollToTop(boolean autoRefresh) {
        super.smoothScrollToTop(autoRefresh);
        headerScrollView.smoothScrollToTop();
    }
}
