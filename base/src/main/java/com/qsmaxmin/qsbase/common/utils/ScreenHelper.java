package com.qsmaxmin.qsbase.common.utils;

import android.support.v4.app.FragmentActivity;

import com.qsmaxmin.qsbase.common.log.L;

import java.util.ArrayList;
import java.util.Stack;


/**
 * @CreateBy qsmaxmin
 * @Date 2017/6/20 16:49
 * @Description 屏幕管理帮助类
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
                L.i(TAG, "activity入栈:" + activity.getClass().getSimpleName() + "，当前栈大小：" + fragmentActivities.size());
            }
        } else {
            L.e(TAG, "pushActivity 传入的参数为空!");
        }
    }

    /**
     * 将元素置顶
     */
    public void bringActivityToTop(FragmentActivity activity) {
        if (activity == null) return;
        if (currentActivity() == activity) return;
        int index = fragmentActivities.indexOf(activity);
        if (index >= 0) {
            FragmentActivity remove = fragmentActivities.remove(index);
            if (remove != null) fragmentActivities.add(remove);
            if (L.isEnable()) {
                L.i(TAG, "activity(" + activity.getClass().getSimpleName() + ")获取到焦点移到栈顶，当前栈大小：" + fragmentActivities.size());
            }
        }
    }

    /**
     * 出栈
     */
    public void popActivity(FragmentActivity activity) {
        if (activity != null) {
            activity.finish();
            fragmentActivities.remove(activity);
            onActivityRemoved(activity);
            if (L.isEnable()) {
                L.i(TAG, "activity出栈:" + activity.getClass().getSimpleName() + "，当前栈大小：" + fragmentActivities.size());
            }
        } else {
            L.e(TAG, "popActivity 传入的参数为空!");
        }
        if (fragmentActivities.size() == 0) {
            QsHelper.release();
            if (listeners != null) listeners.clear();
            L.i(TAG, "pop all Activity, app shutdown...");
        }
    }

    /**
     * 从栈顶向下pop Activity, 直到传入Activity停止
     * 若入参不为空而该栈中没有该Activity，则pop时留栈底一个Activity
     */
    public void popAllActivityExceptMain(Class clazz) {
        while (true) {
            if (clazz != null && fragmentActivities.size() <= 1) break;
            FragmentActivity activity = currentActivity();
            if (activity == null) break;
            if (activity.getClass().equals(clazz)) break;
            popActivity(activity);
        }
    }

    public void popAllActivity() {
        while (fragmentActivities.size() > 0) {
            FragmentActivity fragmentActivity = fragmentActivities.get(0);
            popActivity(fragmentActivity);
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
