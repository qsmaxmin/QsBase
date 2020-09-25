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
    private final Stack<FragmentActivity>         fragmentActivities = new Stack<>();
    private       ArrayList<OnTaskChangeListener> listeners;

    static ScreenHelper getInstance() {
        return instance;
    }

    /**
     * 获取当前活动的activity
     */
    public FragmentActivity currentActivity() {
        if (fragmentActivities.size() == 0) {
            L.i(TAG, "Activity堆栈 size = 0");
            return null;
        }
        return fragmentActivities.peek();
    }

    /**
     * 入栈
     */
    public void pushActivity(FragmentActivity activity) {
        if (activity != null) {
            fragmentActivities.add(activity);
            onActivityAdded(activity);
            if (L.isEnable()) {
                L.i(TAG, "activity in:" + activity + "，task size:" + fragmentActivities.size());
            }
        } else {
            L.e(TAG, "pushActivity param is empty!");
        }
    }

    /**
     * 出栈
     */
    public void popActivity(FragmentActivity activity) {
        if (activity != null && fragmentActivities.remove(activity)) {
            onActivityRemoved(activity);
            if (L.isEnable()) {
                L.i(TAG, "activity out:" + activity + "，task size:" + fragmentActivities.size());
            }

            if (fragmentActivities.size() == 0) {
                L.i(TAG, "pop all Activity......");
                if (listeners != null) listeners.clear();
            }
        }
    }

    public void popAllActivityExceptMain(Class clazz) {
        while (true) {
            if (clazz != null && fragmentActivities.size() <= 1) break;
            FragmentActivity activity = currentActivity();
            if (activity == null || activity.getClass().equals(clazz)) break;
            popActivity(activity);
            activity.finish();
        }
    }

    public void popAllActivity() {
        while (fragmentActivities.size() > 0) {
            FragmentActivity activity = fragmentActivities.get(0);
            activity.finish();
        }
    }

    public Stack<FragmentActivity> getActivityStack() {
        return fragmentActivities;
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
            OnTaskChangeListener[] listenerArr = listeners.toArray(new OnTaskChangeListener[0]);
            for (OnTaskChangeListener listener : listenerArr) {
                listener.onActivityAdd(activity);
            }
        }
    }

    private void onActivityRemoved(FragmentActivity activity) {
        if (listeners != null && listeners.size() != 0) {
            OnTaskChangeListener[] listenerArr = listeners.toArray(new OnTaskChangeListener[0]);
            for (OnTaskChangeListener listener : listenerArr) {
                listener.onActivityRemove(activity);
            }
        }
    }
}
