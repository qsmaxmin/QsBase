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
    private boolean          shouldShow;
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
                    dismissDialog();
                }
            }
        });
    }

    public void initView(QsProgressDialog dialog) {
        this.progressDialog = dialog;
        if (progressDialog != null) {
            LayoutInflater factory = LayoutInflater.from(getContext());
            View view = progressDialog.onCreateContentView(factory, this);
            ViewHelper.addToParent(view, this);
        }
    }

    public void setMessage(final CharSequence message) {
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
        if (progressDialog != null) {
            progressDialog.setCancelAble(cancelAble);
        }
    }

    public void show(final Activity activity) {
        if (progressDialog != null) {
            show(activity, progressDialog.getDelayedShowingTime());
        }
    }

    public void show(final Activity activity, long delayed) {
        if (progressDialog == null || activity == null) {
            return;
        }
        this.shouldShow = true;
        this.activity = activity;
        if (delayed > 0) {
            QsThreadPollHelper.postDelayed(new Runnable() {
                @Override public void run() {
                    showDialog();
                }
            }, delayed);
        } else if (QsThreadPollHelper.isMainThread()) {
            showDialog();
        } else {
            QsThreadPollHelper.post(new Runnable() {
                @Override public void run() {
                    showDialog();
                }
            });
        }
    }

    public void hide() {
        if (!shouldShow || progressDialog == null || activity == null) {
            return;
        }
        shouldShow = false;
        if (QsThreadPollHelper.isMainThread()) {
            dismissDialog();
        } else {
            post(new Runnable() {
                @Override public void run() {
                    dismissDialog();
                }
            });
        }
    }

    private void showDialog() {
        if (shouldShow && activity != null && progressDialog != null) {
            progressDialog.show(activity, this);
        }
    }

    private void dismissDialog() {
        if (progressDialog != null && activity != null) {
            progressDialog.hide(activity, this);
        }
    }
}
