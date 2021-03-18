package com.qsmaxmin.qsbase.common.widget.sliding;

/**
 * @CreateBy qsmaxmin
 * @Date 2021/3/16 13:51
 * @Description
 */
public interface SlidingListener {
    void onOpen();

    void onClose();

    void onSliding(float ratio);
}
