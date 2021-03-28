package com.qsmaxmin.qsbase.common.http;

import com.google.gson.Gson;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.proxy.HttpHandler;
import com.qsmaxmin.qsbase.common.utils.QsHelper;
import com.qsmaxmin.qsbase.common.utils.StreamCloseUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
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
    private static       HttpHelper         helper;
    private static final String             TAG     = "HttpAdapter";
    private final static int                timeOut = 10;
    private              OkHttpClient       client;
    private              Gson               gson;
    private              JsonPrintFormatter printFormatter;
    private              HttpInterceptor    httpInterceptor;

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
            helper.httpInterceptor = null;
            helper.printFormatter = null;
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
        httpInterceptor = QsHelper.getAppInterface().registerGlobalHttpInterceptor();
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
        } else {
            if (clazz == null) {
                throw new IllegalStateException("class is null...");
            } else {
                throw new IllegalStateException("class:" + clazz.getName() + ", is not Interface...");
            }
        }
    }

    public Object startRequest(Method method, Object[] args, Object requestTag) throws Exception {
        HttpRequest httpRequest = new HttpRequest(method, args, requestTag, gson);
        Chain chain = new Chain(httpRequest, getHttpClient());

        Response response;
        if (httpInterceptor != null) {
            response = httpInterceptor.onIntercept(chain);
        } else {
            response = chain.process();
        }
        return createResult(httpRequest, response);
    }

    private Object createResult(HttpRequest request, Response response) throws Exception {
        try {
            if (response == null) {
                throw new Exception("http error... method:" + request.getMethodName() + ", http response is null !!");
            }
            if (!response.isSuccessful()) {
                throw new Exception("http error... method:" + request.getMethodName() + ", http response code = " + response.code());
            }

            Class<?> returnType = request.getReturnType();
            if (returnType == Response.class) {
                return response;
            } else if (returnType == void.class) {
                return null;
            } else {
                ResponseBody body = response.body();
                if (body == null) return null;
                return returnType == byte[].class ? body.bytes() : gson.fromJson(body.charStream(), returnType);
            }
        } finally {
            StreamCloseUtils.close(response);
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

    public void cancelRequest(Collection<Object> list) {
        if (client != null && list != null) {
            synchronized (client.dispatcher()) {
                Dispatcher dispatcher = client.dispatcher();
                List<Call> queuedCalls = dispatcher.queuedCalls();
                for (Call call : queuedCalls) {
                    Request request = call.request();
                    if (list.contains(request.tag())) {
                        if (L.isEnable()) {
                            L.i(TAG, "cancel queued request success... requestTag=" + list + "  url=" + request.url());
                        }
                        call.cancel();
                    }
                }
                List<Call> runningCalls = dispatcher.runningCalls();
                for (Call call : runningCalls) {
                    Request request = call.request();
                    if (list.contains(request.tag())) {
                        if (L.isEnable()) {
                            L.i(TAG, "cancel running request ... requestTag=" + list + "  url=" + call.request().url());
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

    public String formatJson(String sourceStr) {
        if (printFormatter == null) printFormatter = new JsonPrintFormatter();
        return printFormatter.formatJson(sourceStr);
    }
}
