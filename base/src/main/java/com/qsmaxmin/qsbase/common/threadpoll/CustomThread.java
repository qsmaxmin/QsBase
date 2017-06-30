package com.qsmaxmin.qsbase.common.threadpoll;

import android.os.Process;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/3/21 20:16
 * @Description
 */

class CustomThread extends Thread {
    CustomThread(Runnable runnable, String name) {
        super(runnable, name);
    }

    @Override public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        super.run();
    }
}
