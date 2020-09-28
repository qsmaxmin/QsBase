package com.qsmaxmin.qsbase.common.http;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.qsmaxmin.qsbase.common.exception.QsException;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.proxy.HttpHandler;
import com.qsmaxmin.qsbase.common.utils.QsHelper;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/6/29 14:53
 * @Description
 */
@SuppressWarnings("unchecked")
public class HttpHelper {
    private static       HttpHelper     helper;
    private static final String         TAG     = "HttpAdapter";
    private final static int            timeOut = 10;
    private              OkHttpClient   client;
    private              Gson           gson;
    private              QsHttpCallback callback;
    private              HttpConverter  converter;

    public static HttpHelper getInstance() {
        if (helper == null) {
            synchronized (HttpHelper.class) {
                if (helper == null) helper = new HttpHelper();
            }
        }
        return helper;
    }

    private HttpHelper() {
        initDefaults();
    }

    public static void release() {
        if (helper != null) {
            helper.client = null;
            helper.callback = null;
            helper.converter = null;
            helper.gson = null;
            helper = null;
        }
    }

    public OkHttpClient getHttpClient() {
        if (client == null) {
            initDefaults();
        }
        return client;
    }

    public void setHttpClient(OkHttpClient client) {
        this.client = client;
    }

    private void initDefaults() {
        if (client == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(timeOut, TimeUnit.SECONDS);
            builder.readTimeout(timeOut, TimeUnit.SECONDS);
            builder.writeTimeout(timeOut, TimeUnit.SECONDS);
            builder.retryOnConnectionFailure(true);
            client = builder.build();
        }
        if (gson == null) {
            gson = new Gson();
        }
        callback = QsHelper.getAppInterface().registerGlobalHttpListener();
    }

    public <T> T create(Class<T> clazz) {
        return create(clazz, System.nanoTime(), null);
    }

    public <T> T create(Class<T> clazz, Object requestTag) {
        return create(clazz, requestTag, null);
    }

    public <T> T create(Class<T> clazz, NetworkErrorReceiver receiver) {
        return create(clazz, System.nanoTime(), receiver);
    }

    /**
     * 创建http接口代理
     *
     * @param clazz      interface
     * @param requestTag 标签，用来取消http请求
     * @param receiver   用于接收异常
     * @see #cancelRequest(Object)
     */
    public <T> T create(Class<T> clazz, Object requestTag, NetworkErrorReceiver receiver) {
        if (clazz != null && clazz.isInterface()) {
            HttpHandler handler = new HttpHandler(this, requestTag, receiver);
            return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, handler);
        }
        return null;
    }

    public Object startRequest(Method method, Object[] args, Object requestTag, NetworkErrorReceiver errorReceiver) {
        try {
            if (!QsHelper.isNetworkAvailable()) {
                errorReceiver.methodError(new QsException(requestTag, "network disable"));
                return null;
            }
            HttpRequest httpRequest = new HttpRequest(method, args, requestTag, gson);
            Request request = httpRequest.createRequest(callback);
            Call call = client.newCall(request);
            return createResult(httpRequest, call.execute());
        } catch (Exception e) {
            errorReceiver.methodError(new QsException(requestTag, e));
            if (L.isEnable()) e.printStackTrace();
            return null;
        }
    }

    public void cancelRequest(Object requestTag) {
        if (client != null && requestTag != null) {
            synchronized (client.dispatcher()) {
                Dispatcher dispatcher = client.dispatcher();
                List<Call> queuedCalls = dispatcher.queuedCalls();
                for (Call call : queuedCalls) {
                    Request request = call.request();
                    if (requestTag.equals(request.tag())) {
                        if (L.isEnable()) {
                            L.i(TAG, "cancel queued request success... requestTag=" + requestTag + "  url=" + request.url());
                        }
                        call.cancel();
                    }
                }

                List<Call> runningCalls = dispatcher.runningCalls();
                for (Call call : runningCalls) {
                    Request request = call.request();
                    if (requestTag.equals(request.tag())) {
                        if (L.isEnable()) {
                            L.i(TAG, "cancel running request ... requestTag=" + requestTag + "  url=" + call.request().url());
                        }
                        call.cancel();
                    }
                }
            }
        }
    }

    public void cancelAllRequest() {
        if (client != null) {
            client.dispatcher().cancelAll();
        }
    }

    private Object createResult(HttpRequest request, Response response) throws Exception {
        if (response == null) return null;
        int responseCode = response.code();
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.response = response;

        if (response.isSuccessful()) {
            Class<?> returnType = request.getReturnType();
            if (returnType == void.class) {
                if (callback != null) {
                    callback.onHttpResponse(request, httpResponse);
                    callback.onHttpComplete(request, null);
                }
                response.close();
                return null;

            } else if (returnType == byte[].class) {
                if (callback != null) callback.onHttpResponse(request, httpResponse);
                ResponseBody body = response.body();
                byte[] result = null;
                if (body != null) {
                    result = body.bytes();
                }
                response.close();
                if (callback != null) callback.onHttpComplete(request, result);
                return result;

            } else if (returnType.equals(Response.class)) {
                if (callback != null) {
                    callback.onHttpResponse(request, httpResponse);
                    callback.onHttpComplete(request, response);
                }
                return response;

            } else {
                ResponseBody body = response.body();
                if (body == null) {
                    throw new Exception("http response error... method:" + request.getMethodName() + ", response body is null!!");
                }
                if (callback != null) callback.onHttpResponse(request, httpResponse);
                String jsonStr = httpResponse.getJsonString();
                response.close();
                if (L.isEnable()) {
                    L.i(TAG, "on http response... method:" + request.getMethodName() + ", url:" + request.getUrl() + "\nresponse body Json:\n" + getConverter().formatJson(jsonStr));
                }
                if (!TextUtils.isEmpty(jsonStr)) {
                    Object result = gson.fromJson(jsonStr, returnType);
                    if (callback != null) callback.onHttpComplete(request, result);
                    return result;
                } else {
                    if (callback != null) callback.onHttpComplete(request, null);
                    return null;
                }
            }
        } else {
            if (callback != null) callback.onHttpResponse(request, httpResponse);
            response.close();
            throw new Exception("http error... method:" + request.getMethodName() + ", http response code = " + responseCode);
        }
    }

    private HttpConverter getConverter() {
        if (converter == null) converter = new HttpConverter();
        return converter;
    }

}
