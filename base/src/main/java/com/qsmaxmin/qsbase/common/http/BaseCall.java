package com.qsmaxmin.qsbase.common.http;

import com.qsmaxmin.annotation.thread.ThreadType;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.plugin.threadpoll.QsThreadPollHelper;

import java.util.concurrent.ThreadPoolExecutor;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

/**
 * @CreateBy qsmaxmin
 * @Date 2021/7/5 16:25
 * @Description
 */
public abstract class BaseCall<D> {
    private boolean                                  isDestroy;
    private ThreadType                               observerThreadType;
    private ThreadType                               subscribeThreadType;
    private Consumer<D>                              consumer;
    private Function<? super Throwable, ? extends D> errorReturnFunction;
    private D                                        errorReturnItem;
    private Lifecycle                                lifecycle;
    private LifecycleEventObserver                   lifecycleObserver;

    public void subscribe() {
        subscribe(null);
    }

    public void subscribe(Consumer<D> c) {
        this.consumer = c;
        if (subscribeThreadType == null) {
            subscribeInner();
        } else if (subscribeThreadType == ThreadType.MAIN) {
            if (QsThreadPollHelper.isMainThread()) {
                subscribeInner();
            } else {
                QsThreadPollHelper.post(new Runnable() {
                    @Override public void run() {
                        subscribeInner();
                    }
                });
            }
        } else {
            getExecutor(subscribeThreadType).execute(new Runnable() {
                @Override public void run() {
                    subscribeInner();
                }
            });
        }
    }

    private void subscribeInner() {
        try {
            if (isDestroy) return;
            D data = execute();
            if (isDestroy) return;
            observerInner(data);
        } catch (Exception e) {
            if (isDestroy) return;
            if (errorReturnItem != null) {
                observerInner(errorReturnItem);
            } else if (errorReturnFunction != null) {
                D d = null;
                try {
                    d = errorReturnFunction.apply(e);
                } catch (Exception e1) {
                    L.e("HttpCall", e1);
                } finally {
                    observerInner(d);
                }
            } else {
                observerInner(null);
            }
            if (L.isEnable()) L.e("HttpCall", e);
        }
    }

    private void observerInner(final D d) {
        if (isDestroy) return;
        if (observerThreadType == null) {
            consumeSafely(consumer, d);

        } else if (observerThreadType == ThreadType.MAIN) {
            if (QsThreadPollHelper.isMainThread()) {
                consumeSafely(consumer, d);

            } else {
                QsThreadPollHelper.post(new Runnable() {
                    @Override public void run() {
                        consumeSafely(consumer, d);
                    }
                });
            }
        } else {
            getExecutor(observerThreadType).execute(new Runnable() {
                @Override public void run() {
                    consumeSafely(consumer, d);
                }
            });
        }
    }

    private void consumeSafely(Consumer<D> consumer, D d) {
        try {
            if (isDestroy) return;
            if (consumer != null) {
                consumer.accept(d);
            }
        } catch (Exception e) {
            if (L.isEnable()) L.e("HttpCall", e);
        } finally {
            removeLifecycle();
        }
    }

    private void removeLifecycle() {
        if (lifecycle != null && lifecycleObserver != null) {
            lifecycle.removeObserver(lifecycleObserver);
        }
    }

    private ThreadPoolExecutor getExecutor(ThreadType type) {
        switch (type) {
            case HTTP:
            default:
                return QsThreadPollHelper.getHttpThreadPoll();
            case WORK:
                return QsThreadPollHelper.getWorkThreadPoll();
            case SINGLE_WORK:
                return QsThreadPollHelper.getSingleThreadPoll();
        }
    }

    @NonNull public BaseCall<D> as(LifecycleOwner owner) {
        if (owner == null) return this;
        return as(owner.getLifecycle());
    }

    @NonNull public BaseCall<D> as(Lifecycle lifecycle) {
        if (lifecycle != null) {
            this.lifecycle = lifecycle;
            this.lifecycleObserver = new LifecycleEventObserver() {
                @Override public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                    if (event == Lifecycle.Event.ON_DESTROY) {
                        isDestroy = true;
                    }
                }
            };
            lifecycle.addObserver(lifecycleObserver);
        }
        return this;
    }

    /**
     * 当请求发生错误时，返回什么数据实体给Consumer
     *
     * @see Consumer#accept(Object)
     */
    @NonNull public BaseCall<D> onErrorReturn(Function<? super Throwable, ? extends D> function) {
        this.errorReturnFunction = function;
        return this;
    }

    /**
     * 当请求发生错误时，返回什么数据实体给Consumer
     *
     * @see Consumer#accept(Object)
     */
    @NonNull public BaseCall<D> onErrorReturnItem(D d) {
        this.errorReturnItem = d;
        return this;
    }

    @NonNull public BaseCall<D> observeOnMainThread() {
        return observeOn(ThreadType.MAIN);
    }

    @NonNull public BaseCall<D> observeOnHttpThread() {
        return observeOn(ThreadType.HTTP);
    }

    @NonNull public BaseCall<D> observeOnWorkThread() {
        return observeOn(ThreadType.WORK);
    }

    @NonNull public BaseCall<D> observeOnSingleThread() {
        return observeOn(ThreadType.SINGLE_WORK);
    }

    /**
     * 指定 ‘接口回调逻辑’ 被执行的线程
     * 若不指定则在原线程执行
     *
     * @see Consumer#accept(Object)
     */
    @NonNull public BaseCall<D> observeOn(ThreadType threadType) {
        this.observerThreadType = threadType;
        return this;
    }

    @NonNull public BaseCall<D> subscribeOnMainThread() {
        return subscribeOn(ThreadType.MAIN);
    }

    @NonNull public BaseCall<D> subscribeOnHttpThread() {
        return subscribeOn(ThreadType.HTTP);
    }

    @NonNull public BaseCall<D> subscribeOnWorkThread() {
        return subscribeOn(ThreadType.WORK);
    }

    @NonNull public BaseCall<D> subscribeOnSingleThread() {
        return subscribeOn(ThreadType.SINGLE_WORK);
    }

    /**
     * 指定 ‘接口请求逻辑’ 被执行的线程
     * 若不指定则在原线程执行
     */
    @NonNull public BaseCall<D> subscribeOn(ThreadType threadType) {
        this.subscribeThreadType = threadType;
        return this;
    }

    protected abstract D execute() throws Exception;
}
