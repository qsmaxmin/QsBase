package com.qsmaxmin.qsbase.mvp;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/6/21 15:00
 * @Description
 */

public interface QsIActivity extends QsIView {

    int emptyLayoutId();

    int loadingLayoutId();

    int errorLayoutId();

    boolean isTransparentStatusBar();
}
