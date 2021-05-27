package com.qsmaxmin.qsbase.common.http;

import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.plugin.threadpoll.QsThreadPollHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @CreateBy qsmaxmin
 * @Date 2021/5/26 9:44
 * @Description
 */
public class HttpCall<D> {
    private final HttpRequest request;

    HttpCall(HttpRequest request) {
        this.request = request;
    }

    public void enqueue(@NonNull HttpCallback<D> callback) {
        enqueue(null, callback);
    }

    public void enqueue(final Object requestTag, @NonNull final HttpCallback<D> callback) {
        QsThreadPollHelper.runOnHttpThread(new Runnable() {
            @Override public void run() {
                try {
                    final D resp = execute(requestTag);
                    if (callback != null) {
                        QsThreadPollHelper.post(new Runnable() {
                            @Override public void run() {
                                callback.onSuccess(resp);
                            }
                        });
                    }
                } catch (final Throwable t) {
                    if (callback != null) {
                        QsThreadPollHelper.post(new Runnable() {
                            @Override public void run() {
                                callback.onFailed(t);
                            }
                        });
                    }
                    if (L.isEnable()) L.e("HttpCall", t);
                }
            }
        });
    }

    @Nullable public D executeSafely() {
        return executeSafely(null);
    }

    @Nullable public D executeSafely(Object requestTag) {
        try {
            return execute(requestTag);
        } catch (Throwable t) {
            if (L.isEnable()) L.e("HttpCall", t);
            return null;
        }
    }

    public D execute() throws Exception {
        return execute(null);
    }

    @SuppressWarnings("unchecked")
    public D execute(Object requestTag) throws Exception {
        request.setRequestTag(requestTag);
        return (D) HttpHelper.getInstance().startRequest(request);
    }
}
