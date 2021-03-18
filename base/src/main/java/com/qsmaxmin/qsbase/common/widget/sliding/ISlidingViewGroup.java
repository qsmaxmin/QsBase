package com.qsmaxmin.qsbase.common.widget.sliding;

/**
 * @CreateBy qsmaxmin
 * @Date 2021/3/18 10:18
 * @Description
 */
public interface ISlidingViewGroup {
    void setCanSliding(boolean canSliding);

    boolean isCanSliding();

    void setSlidingListener(SlidingListener listener);
}
