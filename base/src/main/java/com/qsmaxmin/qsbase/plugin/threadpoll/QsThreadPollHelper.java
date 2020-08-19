package com.qsmaxmin.qsbase.plugin.threadpoll;

import android.os.Handler;
import android.os.Looper;

import com.qsmaxmin.qsbase.mvp.model.QsConstants;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/3/21 20:11
 * @Description 线程池管理类，对外提供api
 */

public class QsThreadPollHelper {
    private static QsThreadPollHelper helper;
    private        Handler            handler;
    private        ThreadPoolExecutor workThreadPoll;
    private        ThreadPoolExecutor httpThreadPoll;
    private        ThreadPoolExecutor singleThreadPoll;

    private QsThreadPollHelper() {
        handler = new Handler(Looper.getMainLooper());
    }

    public static QsThreadPollHelper getInstance() {
        if (helper == null) {
            synchronized (QsThreadPollHelper.class) {
                if (helper == null) {
                    helper = new QsThreadPollHelper();
                }
            }
        }
        return helper;
    }

    public static boolean isMainThread() {
        return Thread.currentThread() == Looper.getMainLooper().getThread();
    }

    public static boolean isWorkThread() {
        return QsConstants.NAME_WORK_THREAD.equals(Thread.currentThread().getName());
    }

    public static boolean isHttpThread() {
        return QsConstants.NAME_HTTP_THREAD.equals(Thread.currentThread().getName());
    }

    public static boolean isSingleThread() {
        return QsConstants.NAME_SINGLE_THREAD.equals(Thread.currentThread().getName());
    }

    public static void post(Runnable action) {
        if (isMainThread()) {
            action.run();
        } else {
            getInstance().handler.post(action);
        }
    }

    public static void postDelayed(Runnable action, long delayed) {
        getInstance().handler.postDelayed(action, delayed);
    }

    public static void runOnWorkThread(Runnable action) {
        getWorkThreadPoll().execute(action);
    }

    public static void runOnHttpThread(Runnable action) {
        getHttpThreadPoll().execute(action);
    }

    public static void runOnSingleThread(Runnable action) {
        getSingleThreadPoll().execute(action);
    }

    public static ThreadPoolExecutor getWorkThreadPoll() {
        if (getInstance().workThreadPoll == null) {
            synchronized (QsThreadPollHelper.class) {
                if (getInstance().workThreadPoll == null) getInstance().workThreadPoll = createWorkThreadPool();
            }
        }
        return getInstance().workThreadPoll;
    }

    public static ThreadPoolExecutor getHttpThreadPoll() {
        if (getInstance().httpThreadPoll == null) {
            synchronized (QsThreadPollHelper.class) {
                if (getInstance().httpThreadPoll == null) getInstance().httpThreadPoll = createHttpThreadPool();
            }

        }
        return getInstance().httpThreadPoll;
    }

    public static ThreadPoolExecutor getSingleThreadPoll() {
        if (getInstance().singleThreadPoll == null) {
            synchronized (QsThreadPollHelper.class) {
                if (getInstance().singleThreadPoll == null) getInstance().singleThreadPoll = createSingleThreadPool();
            }
        }
        return getInstance().singleThreadPoll;
    }

    public static void release() {
        if (helper != null) {
            if (helper.workThreadPoll != null) {
                helper.workThreadPoll.shutdown();
                helper.workThreadPoll = null;
            }
            if (helper.httpThreadPoll != null) {
                helper.httpThreadPoll.shutdown();
                helper.httpThreadPoll = null;
            }
            if (helper.singleThreadPoll != null) {
                helper.singleThreadPoll.shutdown();
                helper.singleThreadPoll = null;
            }
            helper.handler = null;
            helper = null;
        }
    }

    private static ThreadPoolExecutor createSingleThreadPool() {
        return new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(), generateThread(QsConstants.NAME_SINGLE_THREAD));
    }

    private static ThreadPoolExecutor createHttpThreadPool() {
        return new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(), generateThread(QsConstants.NAME_HTTP_THREAD));
    }

    private static ThreadPoolExecutor createWorkThreadPool() {
        return new ThreadPoolExecutor(10, 10, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(), generateThread(QsConstants.NAME_WORK_THREAD));
    }

    private static ThreadFactory generateThread(final String name) {
        return new ThreadFactory() {
            @Override public Thread newThread(@NonNull Runnable runnable) {
                Thread t = new Thread(runnable, name);
                if (!t.isDaemon()) t.setDaemon(true);
                if (t.getPriority() != Thread.NORM_PRIORITY) t.setPriority(Thread.NORM_PRIORITY);
                return t;
            }
        };
    }
}
