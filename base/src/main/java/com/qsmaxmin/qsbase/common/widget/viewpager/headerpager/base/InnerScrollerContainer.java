package com.qsmaxmin.qsbase.common.widget.viewpager.headerpager.base;

/**
 * @CreateBy qsmaxmin
 * @Date 2016/11/22 10:38
 * @Description InnerScroller's Container,which Fragment that contains a InnerScroller must implements.
 */
public interface InnerScrollerContainer {
    void setMyOuterScroller(OuterScroller outerScroller, int myPosition);
}
