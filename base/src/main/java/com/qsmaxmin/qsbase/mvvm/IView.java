package com.qsmaxmin.qsbase.mvvm;

import android.content.Context;
import android.view.View;

import androidx.annotation.StringRes;
import androidx.fragment.app.FragmentActivity;

/**
 * @CreateBy qsmaxmin
 * @Date 2020/12/22 14:52
 * @Description view层超类
 */
public interface IView {
    void onViewClicked(View view);

    void onViewClicked(View view, long interval);

    Context getContext();

    FragmentActivity getActivity();

    void loading();

    void loading(String message);

    void loading(boolean cancelAble);

    void loading(@StringRes int resId);

    void loading(@StringRes int resId, boolean cancelAble);

    void loading(String message, boolean cancelAble);

    void loadingClose();

    void activityFinish();

    void activityFinish(int enterAnim, int exitAnim);

    void activityFinish(boolean finishAfterTransition);
}
