package com.qsmaxmin.qsbase.common.http;

import com.qsmaxmin.annotation.thread.ThreadType;
import com.qsmaxmin.qsbase.common.log.L;

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

    /**
     * 如果View层销毁，则收不到回调事件
     *
     * @see #as(Lifecycle)
     */
    public final void enqueue(final HttpCallback<D> callback) {
        subscribeOn(ThreadType.HTTP)//在Http线程执行接口请求
                .observeOn(ThreadType.MAIN)//在Main线程回调结果
                .doOnError(new Consumer<Throwable>() {
                    @Override public void accept(Throwable throwable) {
                        if (callback != null) callback.onFailed(throwable);
                    }
                })
                .subscribe(new Consumer<D>() {//提交接口请求
                    @Override public void accept(D d) {
                        if (callback != null) {
                            callback.onSuccess(d);
                        }
                    }
                });
    }

    /**
     * 如果View层销毁，则返回null
     *
     * @see #as(Lifecycle)
     */
    @Nullable final public D executeSafely() {
        try {
            return onExecute();
        } catch (Throwable t) {
            if (L.isEnable()) L.e("HttpCall", t);
        }
        return null;
    }

    /**
     * 如果View层销毁，则返回null
     *
     * @see #as(Lifecycle)
     */
    public final D execute() throws Exception {
        return onExecute();
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

    public HttpCall<D> requestTag(Object requestTag) {
        request.setRequestTag(requestTag);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override protected final D onExecute() throws Exception {
        return (D) HttpHelper.getInstance().startRequest(request, this);
    }
}
