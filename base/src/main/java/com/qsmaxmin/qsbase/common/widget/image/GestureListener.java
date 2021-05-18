package com.qsmaxmin.qsbase.common.widget.image;

/**
 * @CreateBy qsmaxmin
 * @Date 2021/5/14 13:56
 * @Description
 */
public interface GestureListener {
    void onSingleTap();

    /**
     * 预览模式时，当手指向下滑动到一定位置时缩放显示
     * 此时回调缩放进度
     */
    void onTouchScaleChanged(float progress);
}
