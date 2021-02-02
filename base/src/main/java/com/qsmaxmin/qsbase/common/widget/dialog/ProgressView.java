package com.qsmaxmin.qsbase.common.widget.dialog;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.qsmaxmin.qsbase.plugin.threadpoll.QsThreadPollHelper;

import androidx.annotation.Nullable;

/**
 * @CreateBy qsmaxmin
 * @Date 17/8/3  上午12:35
 * @Description
 */
public class ProgressView extends FrameLayout {
    private TextView         tv_message;
    private boolean          cancelable;
    private boolean          shouldShowing;
    private QsProgressDialog loadingDialog;

    public ProgressView(Context context) {
        super(context);
        init();
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        cancelable = true;
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setOnClickListener(new OnClickListener() {
            @Override public void onClick(View v) {
                if (cancelable) {
                    setVisibility(GONE);
                }
            }
        });
        setVisibility(GONE);
    }

    public void initView(QsProgressDialog loadingDialog) {
        if (loadingDialog != null) {
            this.loadingDialog = loadingDialog;
            LayoutInflater factory = LayoutInflater.from(getContext());
            View view = loadingDialog.onCreateContentView(factory, this);
            if (view != this && view.getParent() == null) {
                addView(view);
            }
        }
    }

    public void setMessage(final String message) {
        if (loadingDialog == null) return;
        post(new Runnable() {
            @Override public void run() {
                loadingDialog.onSetMessage(message);
            }
        });
    }

    public void setCancelable(boolean cancelAble) {
        this.cancelable = cancelAble;
    }

    public void show(final Activity activity) {
        show(activity, 0);
    }

    public void show(final Activity activity, long delayed) {
        if (loadingDialog == null) return;
        shouldShowing = true;
        postDelayed(new Runnable() {
            @Override public void run() {
                if (shouldShowing) {
                    setVisibility(VISIBLE);
                    addToDecorView(activity);
                }
            }
        }, delayed);
    }

    public void hide(final Activity activity) {
        if (loadingDialog == null) return;
        shouldShowing = false;
        if (QsThreadPollHelper.isMainThread()) {
            setVisibility(GONE);
            removeFromDecorView(activity);
        } else {
            post(new Runnable() {
                @Override public void run() {
                    setVisibility(GONE);
                    removeFromDecorView(activity);
                }
            });
        }
    }

    private void addToDecorView(Activity activity) {
        if (getParent() == null) {
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            decorView.addView(this);
            loadingDialog.onShowing();
        }
    }

    private void removeFromDecorView(Activity activity) {
        if (getParent() != null) {
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            decorView.removeView(this);
            loadingDialog.onHidden();
        }
    }
}
