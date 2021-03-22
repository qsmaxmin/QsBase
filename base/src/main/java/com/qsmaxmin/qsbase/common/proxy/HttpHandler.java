package com.qsmaxmin.qsbase.common.proxy;

import com.qsmaxmin.qsbase.common.exception.QsException;
import com.qsmaxmin.qsbase.common.http.HttpHelper;
import com.qsmaxmin.qsbase.common.http.NetworkErrorReceiver;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.utils.QsHelper;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;


public class HttpHandler implements InvocationHandler {
    private final HttpHelper           adapter;
    private final Object               requestTag;
    private final NetworkErrorReceiver errorReceiver;

    public HttpHandler(HttpHelper adapter, Object requestTag, NetworkErrorReceiver errorReceiver) {
        this.adapter = adapter;
        this.requestTag = requestTag;
        this.errorReceiver = errorReceiver;
    }

    @Override public Object invoke(Object proxy, Method method, final Object[] args) {
        try {
            if (!QsHelper.isNetworkAvailable()) {
                if (errorReceiver != null) errorReceiver.methodError(new QsException(requestTag, "network disable"));
                return null;
            }
            return adapter.startRequest(method, args, requestTag);
        } catch (Throwable e) {
            if (errorReceiver != null) errorReceiver.methodError(new QsException(requestTag, e));
            if (L.isEnable()) L.e("HttpHelper", e.getMessage(), e);
            return null;
        }
    }
}
