package com.qsmaxmin.qsbase.common.widget.headerview;

import android.view.View;

/**
 * @CreateBy qsmaxmin
 * @Date 2020/9/8 14:28
 * @Description register scroller provider for HeaderScrollView
 * @see HeaderScrollView
 */
public interface ScrollerProvider {
    View getScrollableView();
}
