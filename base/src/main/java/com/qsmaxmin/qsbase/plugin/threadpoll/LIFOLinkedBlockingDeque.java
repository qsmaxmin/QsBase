package com.qsmaxmin.qsbase.plugin.threadpoll;

import java.util.concurrent.LinkedBlockingDeque;

/**
 * @CreateBy qsmaxmin
 * @Date 2021/3/1 14:02
 * @Description 一个后进先出的队列
 */
public class LIFOLinkedBlockingDeque<T> extends LinkedBlockingDeque<T> {

    @Override public boolean offer(T e) {
        return super.offerFirst(e);
    }

    @Override public T remove() {
        return super.removeFirst();
    }
}
