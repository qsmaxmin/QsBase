package com.qsmaxmin.qsbase.mvp;

import com.qsmaxmin.qsbase.common.widget.headerview.HeaderScrollListener;
import com.qsmaxmin.qsbase.common.widget.headerview.HeaderScrollView;
import com.qsmaxmin.qsbase.common.widget.headerview.ScrollerProvider;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/7/4 11:08
 * @Description header view
 */

public interface QsIHeaderView extends ScrollerProvider, HeaderScrollListener {
    int getHeaderLayout();

    HeaderScrollView getHeaderScrollView();
}
