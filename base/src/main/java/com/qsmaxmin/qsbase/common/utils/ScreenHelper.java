package com.qsmaxmin.qsbase.common.utils;

import com.qsmaxmin.qsbase.common.log.L;

import java.util.ArrayList;
import java.util.Stack;

import androidx.fragment.app.FragmentActivity;


/**
 * @CreateBy qsmaxmin
 * @Date 2017/6/20 16:49
 * @Description screen helper
 */
public final class ScreenHelper {
    private static final String TAG = "ScreenHelper";

    private ScreenHelper() {
    }

    /**
     * ScreenManager 单例模式
     */
    private static final ScreenHelper instance = new ScreenHelper();

    /**
     * FragmentActivity堆栈 单例模式
     */
    private final Stack<FragmentActivity>         activityStack = new Stack<>();
    private       ArrayList<OnTaskChangeListener> listeners;

    static ScreenHelper getInstance() {
        return instance;
    }

    /**
     * 获取当前活动的activity
     */
    public FragmentActivity currentActivity() {
        if (activityStack.size() == 0) {
            L.i(TAG, "Activity堆栈 size = 0");
            return null;
        }
        return activityStack.peek();
    }

    /**
     * 入栈
     */
    public void pushActivity(FragmentActivity activity) {
        if (activity != null) {
            activityStack.add(activity);
            onActivityAdded(activity);
            if (L.isEnable()) {
                L.i(TAG, "pushActivity:" + activity + "，task size:" + activityStack.size());
            }
        } else {
            L.e(TAG, "pushActivity param is empty!");
        }
    }

    /**
     * 出栈
     */
    public void popActivity(FragmentActivity activity) {
        if (activity != null && activityStack.remove(activity)) {
            onActivityRemoved(activity);
            if (L.isEnable()) {
                L.i(TAG, "popActivity:" + activity + "，task size:" + activityStack.size());
            }
        }
    }

    public void popAllActivityExceptMain(Class clazz) {
        popAllActivityExceptMain(clazz, true);
    }

    /**
     * 依次关闭栈里的activity
     *
     * @param clazz     指定的activity不关闭
     * @param interrupt 便利到指定activity时是否中断操作, true中断, false不中断
     */
    public void popAllActivityExceptMain(Class clazz, boolean interrupt) {
        FragmentActivity[] array = this.activityStack.toArray(new FragmentActivity[activityStack.size()]);
        for (int i = array.length - 1; i > -1; i--) {
            FragmentActivity activity = array[i];
            if (clazz != activity.getClass()) {
                activity.finish();
            } else if (interrupt) {
                break;
            }
        }
    }

    public void popAllActivity() {
        FragmentActivity[] array = this.activityStack.toArray(new FragmentActivity[activityStack.size()]);
        for (int i = array.length - 1; i > -1; i--) {
            array[i].finish();
        }
    }

    public boolean contains(Class clazz) {
        FragmentActivity[] array = this.activityStack.toArray(new FragmentActivity[activityStack.size()]);
        for (FragmentActivity ac : array) {
            if (ac.getClass() == clazz) {
                return true;
            }
        }
        return false;
    }

    public Stack<FragmentActivity> getActivityStack() {
        return activityStack;
    }

    public void addOnTaskChangedListener(OnTaskChangeListener listener) {
        if (listener == null) return;
        if (listeners == null) listeners = new ArrayList<>(3);
        if (!listeners.contains(listener)) listeners.add(listener);
    }

    public void removeOnTaskChangedListener(OnTaskChangeListener listener) {
        if (listener == null) return;
        if (listeners != null) {
            listeners.remove(listener);
        }
    }

    public interface OnTaskChangeListener {
        void onActivityAdd(FragmentActivity activity);

        void onActivityRemove(FragmentActivity activity);
    }

    private void onActivityAdded(FragmentActivity activity) {
        if (listeners != null && listeners.size() != 0) {
            OnTaskChangeListener[] listenerArr = listeners.toArray(new OnTaskChangeListener[listeners.size()]);
            for (OnTaskChangeListener listener : listenerArr) {
                listener.onActivityAdd(activity);
            }
        }
    }

    private void onActivityRemoved(FragmentActivity activity) {
        if (listeners != null && listeners.size() != 0) {
            OnTaskChangeListener[] listenerArr = listeners.toArray(new OnTaskChangeListener[listeners.size()]);
            for (OnTaskChangeListener listener : listenerArr) {
                listener.onActivityRemove(activity);
            }
        }
    }
}
