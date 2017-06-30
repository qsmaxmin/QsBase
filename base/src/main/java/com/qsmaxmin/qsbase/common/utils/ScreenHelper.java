package com.qsmaxmin.qsbase.common.utils;

import android.support.v4.app.FragmentActivity;

import com.qsmaxmin.qsbase.common.log.L;

import java.util.Stack;


/**
 * Created by sky on 15/1/26. fragmentactivity管理器
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
    private static final Stack<FragmentActivity> fragmentActivities = new Stack<>();


    static ScreenHelper getInstance() {
        return instance;
    }

    /**
     * 获取当前活动的activity
     */
    public FragmentActivity currentActivity() {
        if (fragmentActivities.size() == 0) {
            L.i(TAG, "FragmentActivity堆栈 size = 0");
            return null;
        }
        FragmentActivity fragmentActivity = fragmentActivities.peek();
        L.i(TAG, "获取当前FragmentActivity:" + fragmentActivity.getClass().getSimpleName() + "   栈大小：" + fragmentActivities.size());
        return fragmentActivity;
    }

    /**
     * 入栈
     */
    public void pushActivity(FragmentActivity activity) {
        if (activity != null) {
            fragmentActivities.add(activity);
            L.i(TAG, "activity入栈:" + activity.getClass().getSimpleName() + "   当前栈大小：" + fragmentActivities.size());
        } else L.e(TAG, "pushActivity 传入的参数为空!");
    }

    /**
     * 出栈
     */
    public void popActivity(FragmentActivity activity) {
        if (activity != null) {
            fragmentActivities.remove(activity);
            L.i(TAG, "activity出栈:" + activity.getClass().getSimpleName() + "   当前栈大小：" + fragmentActivities.size());
        } else {
            L.e(TAG, "popActivity 传入的参数为空!");
        }
        if (fragmentActivities.size() < 1) {
            QsHelper.getInstance().getImageHelper().clearMemoryCache();
            QsHelper.getInstance().getThreadHelper().shutdown();
        }
    }

    /**
     * 退出堆栈中所有Activity, 当前的Activity除外
     *
     * @param clazz 当前活动窗口
     */
    public void popAllActivityExceptMain(Class clazz) {
        while (true) {
            FragmentActivity activity = currentActivity();
            if (activity != null) {
                if (!activity.getClass().equals(clazz)) {
                    popActivity(activity);
                    activity.finish();
                } else {
                    break;
                }
            }
        }
    }
}
