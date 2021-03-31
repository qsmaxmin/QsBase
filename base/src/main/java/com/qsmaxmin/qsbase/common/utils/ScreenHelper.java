package com.qsmaxmin.qsbase.common.utils;

import android.app.Activity;

import com.qsmaxmin.qsbase.common.log.L;

import java.util.ArrayList;
import java.util.List;


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
    private final List<Activity>                  activityStack = new ArrayList<>();
    private       ArrayList<OnTaskChangeListener> listeners;

    static ScreenHelper getInstance() {
        return instance;
    }

    /**
     * 获取当前活动的activity
     */
    public Activity currentActivity() {
        Activity[] activities = stackToArray();
        if (activities.length == 0) {
            L.i(TAG, "Activity堆栈 size = 0");
            return null;
        }
        return activities[activities.length - 1];
    }

    /**
     * 入栈
     */
    void pushActivity(Activity activity) {
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
    void popActivity(Activity activity) {
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
        Activity[] array = stackToArray();
        for (int i = array.length - 1; i > -1; i--) {
            Activity activity = array[i];
            if (clazz != activity.getClass()) {
                activity.finish();
            } else if (interrupt) {
                break;
            }
        }
    }

    public void popAllActivity() {
        Activity[] array = stackToArray();
        for (int i = array.length - 1; i > -1; i--) {
            array[i].finish();
        }
    }

    public boolean contains(Class clazz) {
        Activity[] array = stackToArray();
        for (Activity ac : array) {
            if (ac.getClass() == clazz) {
                return true;
            }
        }
        return false;
    }

    public List<Activity> getActivityStack() {
        return activityStack;
    }

    private Activity[] stackToArray() {
        int size = activityStack.size();
        return activityStack.toArray(new Activity[size]);
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
        void onActivityAdd(Activity activity);

        void onActivityRemove(Activity activity);
    }

    private void onActivityAdded(Activity activity) {
        if (listeners != null && listeners.size() != 0) {
            int size = listeners.size();
            OnTaskChangeListener[] listenerArr = listeners.toArray(new OnTaskChangeListener[size]);
            for (OnTaskChangeListener listener : listenerArr) {
                listener.onActivityAdd(activity);
            }
        }
    }

    private void onActivityRemoved(Activity activity) {
        if (listeners != null && listeners.size() != 0) {
            int size = listeners.size();
            OnTaskChangeListener[] listenerArr = listeners.toArray(new OnTaskChangeListener[size]);
            for (OnTaskChangeListener listener : listenerArr) {
                listener.onActivityRemove(activity);
            }
        }
    }
}
