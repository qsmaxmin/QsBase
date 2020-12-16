package com.qsmaxmin.qsbase.common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ViewAnimator;

import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.mvp.QsIView;
import com.qsmaxmin.qsbase.mvvm.MvIView;

import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;

/**
 * @CreateBy administrator
 * @Date 2020/12/8 14:57
 * @Description
 */
public class ViewHelper {
    public static void initStatusBar(Activity activity, boolean transparentStatus, boolean transparentNavigation, boolean blackIconInStatus) {
        if (transparentStatus || transparentNavigation) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = activity.getWindow();
                if (transparentNavigation) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                    window.setNavigationBarColor(Color.TRANSPARENT);
                }

                if (transparentStatus) {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(Color.TRANSPARENT);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && blackIconInStatus) {
                        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                    } else {
                        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                    }
                }

            } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                Window window = activity.getWindow();
                WindowManager.LayoutParams winParams = window.getAttributes();
                final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
                winParams.flags |= bits;
                window.setAttributes(winParams);
            }
        } else {
            if (blackIconInStatus && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Window window = activity.getWindow();
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
    }

    public static void initViewAnimator(ViewAnimator animator, MvIView iView) {
        initViewAnimator(iView.getContext(), animator, iView.viewStateInAnimation(), iView.viewStateOutAnimation(),
                iView.viewStateInAnimationId(), iView.viewStateOutAnimationId(), iView.viewStateAnimateFirstView());
    }

    public static void initViewAnimator(ViewAnimator animator, QsIView iView) {
        initViewAnimator(iView.getContext(), animator, iView.viewStateInAnimation(), iView.viewStateOutAnimation(),
                iView.viewStateInAnimationId(), iView.viewStateOutAnimationId(), iView.viewStateAnimateFirstView());
    }

    private static void initViewAnimator(Context context, ViewAnimator animator, Animation inAnim, Animation outAnim, int inAnimId, int outAnimId, boolean animateFirst) {
        if (inAnim != null) {
            animator.setInAnimation(inAnim);
        } else if (inAnimId != 0) {
            animator.setInAnimation(context, inAnimId);
        }
        if (outAnim != null) {
            animator.setOutAnimation(outAnim);
        } else if (outAnimId != 0) {
            animator.setOutAnimation(context, outAnimId);
        }
        animator.setAnimateFirstView(animateFirst);
    }


    public static void intent2Activity(Activity activity, Class clazz, Bundle bundle, int requestCode, ActivityOptionsCompat optionsCompat, int inAnimId, int outAnimId) {
        if (clazz != null) {
            Intent intent = new Intent();
            intent.setClass(activity, clazz);
            if (bundle != null) intent.putExtras(bundle);
            if (optionsCompat == null) {
                if (requestCode > 0) {
                    activity.startActivityForResult(intent, requestCode);
                } else {
                    activity.startActivity(intent);
                }
                if (inAnimId != 0 || outAnimId != 0) activity.overridePendingTransition(inAnimId, outAnimId);
            } else {
                if (requestCode > 0) {
                    ActivityCompat.startActivityForResult(activity, intent, requestCode, optionsCompat.toBundle());
                } else {
                    ActivityCompat.startActivity(activity, intent, optionsCompat.toBundle());
                }
            }
        }
    }


    @SuppressWarnings("unchecked")
    public static <T> T tryGetTargetView(Class<T> targetViewClass, View parentView) {
        T targetView = null;
        if (parentView.getClass() == targetViewClass) {
            targetView = (T) parentView;
        } else if (parentView instanceof ViewGroup) {
            int childCount = ((ViewGroup) parentView).getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = ((ViewGroup) parentView).getChildAt(i);
                targetView = tryGetTargetView(targetViewClass, childAt);
                if (targetView != null) break;
            }
        }
        return targetView;
    }


    public static void setDefaultViewClickListener(View view, final MvIView iView) {
        if (view != null && iView != null) {
            View backView = view.findViewById(R.id.qs_back_in_default_view);
            if (backView != null) {
                if (iView.isShowBackButtonInDefaultView()) {
                    backView.setVisibility(View.VISIBLE);
                    backView.setOnClickListener(new View.OnClickListener() {
                        @Override public void onClick(View v) {
                            iView.onBackPressed();
                        }
                    });
                } else {
                    backView.setVisibility(View.GONE);
                }
            }
            View reloadView = view.findViewById(R.id.qs_reload_in_default_view);
            if (reloadView != null) reloadView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    iView.showLoadingView();
                    iView.initData(null);
                }
            });
        }
    }

    public static void setDefaultViewClickListener(View view, final QsIView iView) {
        if (view != null && iView != null) {
            View backView = view.findViewById(R.id.qs_back_in_default_view);
            if (backView != null) {
                if (iView.isShowBackButtonInDefaultView()) {
                    backView.setVisibility(View.VISIBLE);
                    backView.setOnClickListener(new View.OnClickListener() {
                        @Override public void onClick(View v) {
                            iView.onBackPressed();
                        }
                    });
                } else {
                    backView.setVisibility(View.GONE);
                }
            }
            View reloadView = view.findViewById(R.id.qs_reload_in_default_view);
            if (reloadView != null) reloadView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    iView.showLoadingView();
                    iView.initData(null);
                }
            });
        }
    }
}