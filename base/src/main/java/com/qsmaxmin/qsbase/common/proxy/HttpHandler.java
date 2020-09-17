package com.qsmaxmin.qsbase.common.proxy;

import com.qsmaxmin.qsbase.common.http.HttpHelper;
import com.qsmaxmin.qsbase.common.http.NetworkErrorReceiver;

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

    @Override public Object invoke(Object proxy, Method method, final Object[] args) throws Throwable {
        return adapter.startRequest(method, args, requestTag, errorReceiver);
    }
}
