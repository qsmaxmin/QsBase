package com.qsmaxmin.qsbase.common.widget.image;

import androidx.annotation.NonNull;

/**
 * @CreateBy qsmaxmin
 * @Date 2021/5/14 13:33
 * @Description
 */
abstract class ExecutorAnimated extends ExecutorBase {
    private float time;
    private int   duration;

    /**
     * @param data 数据实体
     */
    ExecutorAnimated(@NonNull ImageData data) {
        super(data);
    }

    @Override public final void run() {
        if (time < duration) {
            time += 16;
            if (time > duration) time = duration;
            onAnimating(time / duration, false);
            postAnimation(this);
        } else {
            onAnimating(1f, true);
            setAnimating(false);
        }
    }

    protected int getDuration() {
        return 400;
    }

    protected abstract void onAnimating(float progress, boolean ended);

    protected final void startAnimation() {
        startAnimation(getDuration());
    }

    protected final void startAnimation(int duration) {
        this.time = 0;
        this.duration = duration;
        setAnimating(true);
        post(this);
    }
}
