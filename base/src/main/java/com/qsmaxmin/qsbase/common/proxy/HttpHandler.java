package com.qsmaxmin.qsbase.common.proxy;

import com.qsmaxmin.qsbase.common.http.HttpAdapter;
import com.qsmaxmin.qsbase.common.log.L;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;


/**
 * Created by sky on 15/2/24. 动态代理 - 网络层
 */
public class HttpHandler implements InvocationHandler {
    private static final String TAG = "HttpHandler";

    private final HttpAdapter adapter;

    public HttpHandler(HttpAdapter adapter) {
        this.adapter = adapter;
    }

    @Override public Object invoke(Object proxy, Method method, final Object[] args) throws Throwable {
        if (method.getDeclaringClass() == Object.class) {
            L.i(TAG, "直接执行: " + method.getName());
            return method.invoke(this, args);
        }
        L.i(TAG, "网络请求代理方法：" + method.getName());
        return adapter.startRequest(method, args);
    }
}
