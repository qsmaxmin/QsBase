package com.qsmaxmin.qsbase.common.threadpoll;

import com.qsmaxmin.qsbase.mvp.model.QsConstants;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/3/21 20:14
 * @Description
 */

public class WorkThreadPoll extends ThreadPoolExecutor {

    WorkThreadPoll(int threadCount) {
        super(threadCount, threadCount, 20, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), ThreadUtils.generateThread(QsConstants.NAME_WORK_THREAD, true));
    }
}
