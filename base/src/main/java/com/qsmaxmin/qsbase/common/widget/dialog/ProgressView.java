package com.qsmaxmin.qsbase.common.widget.dialog;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.qsmaxmin.qsbase.common.utils.ViewHelper;
import com.qsmaxmin.qsbase.plugin.threadpoll.QsThreadPollHelper;

import androidx.annotation.Nullable;

/**
 * @CreateBy qsmaxmin
 * @Date 17/8/3  上午12:35
 * @Description
 */
public class ProgressView extends FrameLayout {
    private boolean          cancelable;
    private boolean          shouldShowing;
    private QsProgressDialog progressDialog;
    private Activity         activity;

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
                    removeFromDecorView();
                }
            }
        });
    }

    public void initView(QsProgressDialog progressDialog) {
        if (progressDialog != null) {
            this.progressDialog = progressDialog;
            LayoutInflater factory = LayoutInflater.from(getContext());
            View view = progressDialog.onCreateContentView(factory, this);
            ViewHelper.addToParent(view, this);
        }
    }

    public void setMessage(final String message) {
        if (progressDialog == null) return;
        if (QsThreadPollHelper.isMainThread()) {
            progressDialog.onSetMessage(message);
        } else {
            QsThreadPollHelper.post(new Runnable() {
                @Override public void run() {
                    progressDialog.onSetMessage(message);
                }
            });
        }
    }

    public void setCancelable(boolean cancelAble) {
        this.cancelable = cancelAble;
    }

    public void show(final Activity activity) {
        show(activity, progressDialog.getDelayedShowingTime());
    }

    public void show(final Activity activity, long delayed) {
        if (progressDialog == null || activity == null || activity.isFinishing()) {
            return;
        }
        this.shouldShowing = true;
        this.activity = activity;
        if (delayed > 0) {
            QsThreadPollHelper.postDelayed(new Runnable() {
                @Override public void run() {
                    if (shouldShowing) {
                        addToDecorView();
                    }
                }
            }, delayed);
        } else if (QsThreadPollHelper.isMainThread()) {
            if (shouldShowing) {
                addToDecorView();
            }
        } else {
            QsThreadPollHelper.post(new Runnable() {
                @Override public void run() {
                    if (shouldShowing) {
                        addToDecorView();
                    }
                }
            });
        }
    }

    public void hide() {
        if (!shouldShowing || progressDialog == null || activity == null || activity.isFinishing()) {
            return;
        }
        shouldShowing = false;
        if (QsThreadPollHelper.isMainThread()) {
            removeFromDecorView();
        } else {
            post(new Runnable() {
                @Override public void run() {
                    removeFromDecorView();
                }
            });
        }
    }

    private void addToDecorView() {
        if (getParent() == null && activity != null && !activity.isFinishing()) {
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            decorView.addView(this);
            if (progressDialog != null) progressDialog.onShowing();
        }
    }

    private void removeFromDecorView() {
        if (getParent() != null && activity != null && !activity.isFinishing()) {
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            decorView.removeView(this);
            if (progressDialog != null) progressDialog.onHidden();
        }
    }
}
