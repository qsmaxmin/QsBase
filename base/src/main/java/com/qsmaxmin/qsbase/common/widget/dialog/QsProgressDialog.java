package com.qsmaxmin.qsbase.common.widget.dialog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @CreateBy qsmaxmin
 * @Date 17/8/3  上午12:35
 * @Description
 */
public abstract class QsProgressDialog {

    public abstract View onCreateContentView(LayoutInflater inflater, ViewGroup parent);

    public abstract void onSetMessage(CharSequence text);

    public void onShowing() {
        //custom your logic
    }

    public void onHidden() {
        //custom your logic
    }

    /**
     * 当调用show方法时View已经被添加到Activity
     * 延迟一定ms后才让View显示出来
     * 适合的场景，如调用show方法后立即调用hidden，此时loading窗一闪而过，而延迟300ms就不会出现这种情况
     */
    public long getDelayedShowingTime() {
        return 300;
    }
}
