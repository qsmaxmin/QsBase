package com.qsmaxmin.qsbase.common.http;

import com.google.gson.Gson;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.utils.QsHelper;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;


public class HttpHandler implements InvocationHandler {
    private NetworkErrorReceiver errorReceiver;

    HttpHandler() {
    }

    HttpHandler(NetworkErrorReceiver errorReceiver) {
        this.errorReceiver = errorReceiver;
    }

    /**
     * 如果返回值类型是HttpCall，则创建HttpCall对象并返回
     * 否则直接检查网络并网络开始请求
     */
    @Override public Object invoke(Object proxy, Method method, final Object[] args) throws Throwable {
        Class<?> returnType = method.getReturnType();
        if (returnType == HttpCall.class) {
            Type responseType = getCallResponseType(method.getGenericReturnType());
            Gson gson = HttpHelper.getInstance().getJson();
            HttpRequest httpRequest = new HttpRequest(method, args, gson, responseType);
            return new HttpCall<>(httpRequest);

        } else {
            try {
                if (!QsHelper.isNetworkAvailable()) {
                    throw new Exception("network disable");
                }
                HttpHelper helper = HttpHelper.getInstance();
                Gson gson = helper.getJson();
                HttpRequest httpRequest = new HttpRequest(method, args, gson, method.getGenericReturnType());
                return helper.startRequest(httpRequest, null);
            } catch (Throwable t) {
                if (errorReceiver != null) errorReceiver.methodError(t);
                if (L.isEnable()) L.e("HttpHelper", t);
                return null;
            }
        }
    }

    private Type getCallResponseType(Type returnType) throws Exception {
        if (returnType instanceof ParameterizedType) {
            return getParameterUpperBound((ParameterizedType) returnType);
        }
        return null;
    }

    private Type getParameterUpperBound(ParameterizedType type) throws Exception {
        Type[] types = type.getActualTypeArguments();
        if (types.length > 0) {
            Type paramType = types[0];
            if (paramType instanceof WildcardType) {
                return ((WildcardType) paramType).getUpperBounds()[0];
            }
            return paramType;
        } else {
            throw new Exception("HttpCall泛型参数不可为空");
        }
    }
}
