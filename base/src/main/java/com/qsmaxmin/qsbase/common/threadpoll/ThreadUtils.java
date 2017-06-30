package com.qsmaxmin.qsbase.common.threadpoll;

import android.support.annotation.NonNull;

import java.util.concurrent.ThreadFactory;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/6/20 17:50
 * @Description
 */

class ThreadUtils {

    /**
     * 默认线程创建方式
     *
     * @param name   名称
     * @param daemon true 表示守护线程 false 用户线程
     *               说明：守护线程:主线程挂掉也跟着挂掉. 用户线程:主线程挂掉不会跟着挂掉
     */
    static ThreadFactory generateThread(final String name, final boolean daemon) {
        return new ThreadFactory() {
            @Override public Thread newThread(@NonNull Runnable runnable) {
                CustomThread result = new CustomThread(runnable, name);
                result.setDaemon(daemon);
                return result;
            }
        };
    }
}
