package com.qsmaxmin.qsbase.common.proxy;

import com.qsmaxmin.qsbase.common.http.HttpAdapter;
import com.qsmaxmin.qsbase.common.log.L;

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
        L.i("HttpHandler", "网络请求代理方法：" + method.getName() + "  requestTag:" + tag);
        return adapter.startRequest(method, args, tag);
    }
}
