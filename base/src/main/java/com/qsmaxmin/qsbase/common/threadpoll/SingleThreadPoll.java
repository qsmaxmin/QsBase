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

public class SingleThreadPoll extends ThreadPoolExecutor {

    SingleThreadPoll() {
        super(0, 1, 60, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), ThreadUtils.generateThread(QsConstants.NAME_SINGLE_THREAD, true));
    }
}
