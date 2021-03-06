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
import com.qsmaxmin.qsbase.mvvm.MvIActivity;
import com.qsmaxmin.qsbase.mvvm.MvIView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

/**
 * @CreateBy qsmaxmin
 * @Date 2020/12/8 14:57
 * @Description
 */
public class ViewHelper {
    private static long lastClickTime;

    public static boolean isFastClick(long interval) {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < interval) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    public static void initStatusAndNavigationBar(MvIActivity layer) {
        if (layer.isTransparentStatusBar() && layer.isFullScreenFix()) {
            fullScreenFix(layer.getActivity());
        }
        if (layer.isHideStatusNavigationBar()) {
            hideStatusNavigationBar(layer.getActivity());
        }
        if (layer.isBlackIconStatusBar() || layer.isTransparentStatusBar() || layer.isTransparentNavigationBar()) {
            initStatusBar(layer);
        }
    }

    private static void fullScreenFix(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            try {
                WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
                lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
                activity.getWindow().setAttributes(lp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void hideStatusNavigationBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            int flag = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                flag |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            }
            activity.getWindow().getDecorView().setSystemUiVisibility(flag);
        }
    }

    private static void initStatusBar(MvIActivity layer) {
        boolean transparentStatus = layer.isTransparentStatusBar();
        boolean transparentNavigation = layer.isTransparentNavigationBar();
        boolean blackIconInStatus = layer.isBlackIconStatusBar();
        if (transparentStatus || transparentNavigation) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = layer.getActivity().getWindow();
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

            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Window window = layer.getActivity().getWindow();
                WindowManager.LayoutParams winParams = window.getAttributes();
                final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
                winParams.flags |= bits;
                window.setAttributes(winParams);
            }
        } else if (blackIconInStatus && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = layer.getActivity().getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    public static void initViewAnimator(ViewAnimator animator, MvIView iView) {
        Animation inAnim = iView.viewStateInAnimation();
        if (inAnim != null) {
            animator.setInAnimation(inAnim);
        } else if (iView.viewStateInAnimationId() != 0) {
            animator.setInAnimation(iView.getContext(), iView.viewStateInAnimationId());
        }

        Animation outAnim = iView.viewStateOutAnimation();
        if (outAnim != null) {
            animator.setOutAnimation(outAnim);
        } else if (iView.viewStateOutAnimationId() != 0) {
            animator.setOutAnimation(iView.getContext(), iView.viewStateOutAnimationId());
        }
        animator.setAnimateFirstView(iView.viewStateAnimateFirstView());
    }

    public static void intent2Activity(Activity activity, Class<?> clazz, Bundle bundle, int requestCode, ActivityOptionsCompat optionsCompat, int inAnimId, int outAnimId) {
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

    public static void intent2Activity(Fragment fragment, Class<?> clazz, Bundle bundle, int requestCode, ActivityOptionsCompat optionsCompat, int inAnimId, int outAnimId) {
        if (fragment != null && clazz != null) {
            Context context = fragment.getContext();
            FragmentActivity activity = fragment.getActivity();
            if (context == null || activity == null) return;
            Intent intent = new Intent();
            intent.setClass(context, clazz);
            if (bundle != null) intent.putExtras(bundle);
            if (optionsCompat == null) {
                if (requestCode > 0) {
                    fragment.startActivityForResult(intent, requestCode);
                } else {
                    fragment.startActivity(intent);
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
    public static <T> T tryGetTargetView(Class<T> targetViewClass, View view) {
        if (targetViewClass.isAssignableFrom(view.getClass())) {
            return (T) view;
        } else if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0, size = viewGroup.getChildCount(); i < size; i++) {
                View childAt = viewGroup.getChildAt(i);
                T targetView = tryGetTargetView(targetViewClass, childAt);
                if (targetView != null) {
                    return targetView;
                }
            }
        }
        return null;
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

    public static int findViewIndexByState(ViewGroup parent, int state) {
        if (parent != null) {
            int childCount = parent.getChildCount();
            for (int index = 0; index < childCount; index++) {
                Object tag = parent.getChildAt(index).getTag(R.id.qs_view_state_key);
                if (tag != null && (int) tag == state) {
                    return index;
                }
            }
        }
        return -1;
    }

    /**
     * 添加前检查view是否已经被添加到父控件
     *
     * @return 返回被添加的view
     */
    public static View addToParent(@NonNull View view, @NonNull ViewGroup parent, int tag) {
        if (view != parent) {
            view.setTag(R.id.qs_view_state_key, tag);
            if (view.getParent() == null) {
                parent.addView(view);
            }
            return view;
        } else {
            View current = parent.getChildAt(parent.getChildCount() - 1);
            current.setTag(R.id.qs_view_state_key, tag);
            return current;
        }
    }

    public static View addToParent(@NonNull View view, @NonNull ViewGroup parent) {
        if (view != parent) {
            if (view.getParent() == null) {
                parent.addView(view);
            }
            return view;
        } else {
            return parent.getChildAt(parent.getChildCount() - 1);
        }
    }
}
