package com.qsmaxmin.qsbase.common.utils;

import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class KeyboardHelper {

    /***
     * 隐藏键盘
     */
    public static void hideSoftInput(Activity activity) {
        if (activity == null) return;
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /***
     * 显示键盘
     */
    public static void showSoftInput(EditText et) {
        if (et == null) return;
        et.requestFocus();
        InputMethodManager imm = (InputMethodManager) et.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(et, InputMethodManager.RESULT_UNCHANGED_SHOWN);
        }
    }

    /***
     * 延迟300毫秒-显示键盘 说明：延迟会解决 有时弹不出键盘的问题
     */
    public static void showSoftInputDelay(final EditText et) {
        if (et == null) return;
        et.postDelayed(new Runnable() {
            @Override public void run() {
                showSoftInput(et);
            }
        }, 300);
    }

    /**
     * 判断键盘是否显示 如果是显示就隐藏
     * 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
     */
    public static boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText) && event != null) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left + v.getWidth();
            return !(event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom);
        }
        return true;
    }
}