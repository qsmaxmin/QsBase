package com.qsmaxmin.qsbase.common.threadpoll;

import android.os.Looper;
import android.support.annotation.NonNull;

import com.qsmaxmin.qsbase.mvp.model.QsConstants;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/3/21 20:11
 * @Description 线程池管理类，对外提供api
 */

public class QsThreadPollHelper {
    private ThreadPoolExecutor workThreadPoll;
    private ThreadPoolExecutor httpThreadPoll;
    private ThreadPoolExecutor singleThreadPoll;

    public boolean isMainThread() {
        return Thread.currentThread() == Looper.getMainLooper().getThread();
    }

    public boolean isWorkThread() {
        return QsConstants.NAME_WORK_THREAD.equals(Thread.currentThread().getName());
    }

    public boolean isHttpThread() {
        return QsConstants.NAME_HTTP_THREAD.equals(Thread.currentThread().getName());
    }

    public boolean isSingleThread() {
        return QsConstants.NAME_SINGLE_THREAD.equals(Thread.currentThread().getName());
    }

    public MainExecutor getMainThread() {
        return MainExecutor.getInstance();
    }

    public ThreadPoolExecutor getWorkThreadPoll() {
        if (workThreadPoll == null) {
            synchronized (this) {
                if (workThreadPoll == null) workThreadPoll = createWorkThreadPool();
            }
        }
        return workThreadPoll;
    }

    public ThreadPoolExecutor getHttpThreadPoll() {
        if (httpThreadPoll == null) {
            synchronized (this) {
                if (httpThreadPoll == null) httpThreadPoll = createHttpThreadPool();
            }

        }
        return httpThreadPoll;
    }

    public ThreadPoolExecutor getSingleThreadPoll() {
        if (singleThreadPoll == null) {
            synchronized (this) {
                if (singleThreadPoll == null) singleThreadPoll = createSingleThreadPool();
            }
        }
        return singleThreadPoll;
    }

    public void release() {
        if (workThreadPoll != null) {
            workThreadPoll.shutdown();
            workThreadPoll = null;
        }
        if (httpThreadPoll != null) {
            httpThreadPoll.shutdown();
            httpThreadPoll = null;
        }
        if (singleThreadPoll != null) {
            singleThreadPoll.shutdown();
            singleThreadPoll = null;
        }
    }

    /**
     * 池中只有一个线程工作，阻塞队列无界，它能保证按照任务提交的顺序来执行任务
     */
    private ThreadPoolExecutor createSingleThreadPool() {
        return new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(), generateThread(QsConstants.NAME_SINGLE_THREAD));
    }

    /**
     * 使用SynchronousQueue作为阻塞队列，队列无界，线程的空闲时限为60秒。
     * 这种类型的线程池非常适用IO密集的服务，因为IO请求具有密集、数量巨大、不持续、服务器端CPU等待IO响应时间长的特点。
     * 服务器端为了能提高CPU的使用率就应该为每个IO请求都创建一个线程，以免CPU因为等待IO响应而空闲。
     */
    private ThreadPoolExecutor createHttpThreadPool() {
        return new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(), generateThread(QsConstants.NAME_HTTP_THREAD));
    }

    /**
     * 需指定核心线程数，核心线程数和最大线程数相同。
     * 使用LinkedBlockingQueue 作为阻塞队列，队列无界，线程空闲时间0秒。
     * 这种类型的线程池可以适用CPU密集的工作，在这种工作中CPU忙于计算而很少空闲
     */
    private ThreadPoolExecutor createWorkThreadPool() {
        return new ThreadPoolExecutor(10, 10, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(), generateThread(QsConstants.NAME_WORK_THREAD));
    }

    private ThreadFactory generateThread(final String name) {
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
