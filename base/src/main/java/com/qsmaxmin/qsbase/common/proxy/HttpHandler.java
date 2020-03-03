package com.qsmaxmin.qsbase.common.proxy;

import com.qsmaxmin.qsbase.common.http.HttpAdapter;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;


/**
 * 动态代理 - 网络层
 */
public class HttpHandler implements InvocationHandler {
    private final HttpAdapter adapter;
    private final Object      tag;

    public HttpHandler(HttpAdapter adapter, Object tag) {
        this.adapter = adapter;
        this.tag = tag;
    }

    @Override public Object invoke(Object proxy, Method method, final Object[] args) throws Throwable {
        return adapter.startRequest(method, args, tag);
    }
}
