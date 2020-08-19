package com.qsmaxmin.qsbase.common.proxy;

import com.qsmaxmin.qsbase.common.http.HttpHelper;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;


public class HttpHandler implements InvocationHandler {
    private final HttpHelper adapter;
    private final Object     tag;

    public HttpHandler(HttpHelper adapter, Object tag) {
        this.adapter = adapter;
        this.tag = tag;
    }

    @Override public Object invoke(Object proxy, Method method, final Object[] args) throws Throwable {
        return adapter.startRequest(method, args, tag);
    }
}
