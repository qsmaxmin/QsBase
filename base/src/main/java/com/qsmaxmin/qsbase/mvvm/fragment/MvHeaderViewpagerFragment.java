package com.qsmaxmin.qsbase.mvvm.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.common.widget.headerview.HeaderScrollView;
import com.qsmaxmin.qsbase.common.widget.headerview.ScrollerProvider;
import com.qsmaxmin.qsbase.mvvm.MvIHeaderViewPager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * @CreateBy qsmaxmin
 * @Date 2016/11/21 16:06
 * @Description
 */

public abstract class MvHeaderViewpagerFragment extends MvViewPagerFragment implements MvIHeaderViewPager {
    private HeaderScrollView headerScrollView;

    @Override public int layoutId() {
        return R.layout.qs_header_viewpager;
    }

    @Override protected View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        View view = super.initView(inflater, container);
        if (view instanceof HeaderScrollView) {
            headerScrollView = (HeaderScrollView) view;
        } else {
            headerScrollView = view.findViewById(R.id.qs_header_scroll_view);
        }
        if (headerScrollView == null) {
            throw new IllegalStateException("HeaderScrollView is not exist or its id not 'R.id.qs_header_scroll_view' in current layout!!");
        }
        headerScrollView.registerScrollerProvider(this);
        headerScrollView.setOnScrollListener(this);
        ViewGroup headerContainer = view.findViewById(R.id.qs_header_container);
        View headerView = onCreateHeaderView(inflater, headerContainer);
        if (headerView != null && headerView.getParent() == null) {
            headerContainer.addView(headerView);
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
