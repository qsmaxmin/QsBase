package com.qsmaxmin.qsbase.common.proxy;

import com.qsmaxmin.qsbase.common.http.HttpHelper;
import com.qsmaxmin.qsbase.common.http.NetworkErrorCallback;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;


public class HttpHandler implements InvocationHandler {
    private final HttpHelper           adapter;
    private final Object               requestTag;
    private final NetworkErrorCallback errorCallback;

    public HttpHandler(HttpHelper adapter, Object requestTag, NetworkErrorCallback errorCallback) {
        this.adapter = adapter;
        this.requestTag = requestTag;
        this.errorCallback = errorCallback;
    }

    @Override public Object invoke(Object proxy, Method method, final Object[] args) throws Throwable {
        return adapter.startRequest(method, args, requestTag, errorCallback);
    }
}
