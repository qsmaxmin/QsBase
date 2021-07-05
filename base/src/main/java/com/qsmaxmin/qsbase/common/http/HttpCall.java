package com.qsmaxmin.qsbase.common.http;

import com.qsmaxmin.annotation.thread.ThreadType;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.plugin.threadpoll.QsThreadPollHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

/**
 * @CreateBy qsmaxmin
 * @Date 2021/5/26 9:44
 * @Description
 */
public class HttpCall<D> extends BaseCall<D> {
    private final HttpRequest request;

    HttpCall(HttpRequest request) {
        this.request = request;
    }

    public final void enqueue(@NonNull final HttpCallback<D> callback) {
        subscribeOn(ThreadType.HTTP)//在Http线程执行接口请求
                .observeOn(ThreadType.MAIN)//在Main线程回调结果
                .onErrorReturn(new Function<Throwable, D>() {//请求发生错误时返回null，并在Main线程回调结果
                    @Override public D apply(@NonNull final Throwable t) {
                        postFailed(callback, t);
                        return null;
                    }
                })
                .subscribe(new Consumer<D>() {//提交接口请求
                    @Override public void accept(D d) {
                        if (callback != null && d != null) {
                            callback.onSuccess(d);
                        }
                    }
                });
    }

    private void postFailed(final HttpCallback<D> callback, final Throwable t) {
        if (callback != null) {
            QsThreadPollHelper.post(new Runnable() {
                @Override public void run() {
                    callback.onFailed(t);
                }
            });
        }
    }

    @Nullable final public D executeSafely() {
        try {
            return execute();
        } catch (Throwable t) {
            if (L.isEnable()) L.e("HttpCall", t);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public final D execute() throws Exception {
        return (D) HttpHelper.getInstance().startRequest(request);
    }

    /**
     * 绑定生命周期，销毁时不再往下执行
     */
    @NonNull @Override public HttpCall<D> as(LifecycleOwner owner) {
        super.as(owner);
        return this;
    }

    /**
     * 绑定生命周期，销毁时不再往下执行
     */
    @NonNull @Override public HttpCall<D> as(Lifecycle lifecycle) {
        super.as(lifecycle);
        return this;
    }
}
