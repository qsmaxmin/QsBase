package com.qsmaxmin.qsbase.common.widget.listview;

/**
 * @CreateBy qsmaxmin
 * @Date 2021/6/7 13:24
 * @Description
 */
public enum LoadingState {
    Normal(0),
    Loading(1),
    TheEnd(2),
    NetWorkError(3);

    final int index;

    LoadingState(int index) {
        this.index = index;
    }
}
