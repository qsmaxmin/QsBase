package com.qsmaxmin.qsbase.mvvm;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.common.widget.headerview.HeaderScrollView;
import com.qsmaxmin.qsbase.common.widget.headerview.ScrollerProvider;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * @CreateBy administrator
 * @Date 2020/9/9 10:24
 * @Description
 */
public abstract class MvHeaderViewPagerActivity extends MvViewPagerActivity implements MvIHeaderView {
    private HeaderScrollView headerScrollView;

    @Override public View onCreateContentView(@NonNull LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(R.layout.qs_header_viewpager, parent, false);
    }

    @Override protected View initView(LayoutInflater inflater) {
        View view = super.initView(inflater);
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
        if (headerView.getParent() == null) {
            headerContainer.addView(headerView);
        }
        return view;
    }

    @Override public final HeaderScrollView getHeaderScrollView() {
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
