package com.qsmaxmin.qsbase.common.http;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.model.QsModel;
import com.qsmaxmin.qsbase.common.utils.QsHelper;
import com.qsmaxmin.qsbase.common.utils.StreamUtil;

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSource;

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


    /**
     * 创建http接口代理
     *
     * @param clazz interface
     */
    public <T> T create(Class<T> clazz) {
        return createHttp(clazz);
    }

    /**
     * @deprecated
     */
    public <T> T create(Class<T> clazz, NetworkErrorReceiver receiver) {
        if (clazz != null && clazz.isInterface()) {
            HttpHandler handler = new HttpHandler(receiver);
            return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, handler);
        } else {
            if (clazz == null) {
                throw new IllegalStateException("class is null...");
            } else {
                throw new IllegalStateException("class:" + clazz.getName() + ", is not Interface...");
            }
        }
    }

    public static <T> T createHttp(Class<T> clazz) {
        if (clazz != null && clazz.isInterface()) {
            HttpHandler handler = new HttpHandler(null);
            return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, handler);
        } else {
            if (clazz == null) {
                throw new IllegalStateException("class is null...");
            } else {
                throw new IllegalStateException("class:" + clazz.getName() + ", is not Interface...");
            }
        }
    }

    Object startRequest(HttpRequest httpRequest) throws Exception {
        Chain chain = new Chain(httpRequest, getHttpClient());
        Response response;
        if (httpInterceptor != null) {
            response = httpInterceptor.onIntercept(chain);
        } else {
            response = chain.process();
        }
        return createResult(httpRequest, response);
    }

    Object createResult(HttpRequest request, Response response) throws Exception {
        try {
            if (response == null) {
                throw new Exception("http error... method:" + request.getMethodName() + ", http response is null !!");
            }
            if (!response.isSuccessful()) {
                throw new Exception("http error... method:" + request.getMethodName() + ", http response code = " + response.code());
            }
            Type returnType = request.getReturnType();
            if (returnType == Response.class) {
                return response;
            } else if (returnType == void.class) {
                return null;
            } else {
                ResponseBody body = response.body();
                if (body == null) return null;

                if (returnType == byte[].class) {
                    return body.bytes();
                } else if (returnType == String.class) {
                    return body.string();
                } else if (returnType == InputStream.class) {
                    return body.byteStream();
                } else if (returnType == Reader.class) {
                    return body.charStream();
                } else if (returnType == BufferedSource.class) {
                    return body.source();
                } else {
                    TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(returnType));
                    JsonReader jsonReader = gson.newJsonReader(body.charStream());
                    Object result = adapter.read(jsonReader);
                    if (jsonReader.peek() != JsonToken.END_DOCUMENT) {
                        throw new Exception("JSON document was not fully consumed.");
                    }
                    if (result instanceof QsModel) {
                        ((QsModel) result).sentRequestAtMillis = response.sentRequestAtMillis();
                        ((QsModel) result).receivedResponseAtMillis = response.receivedResponseAtMillis();
                    }
                    return result;
                }
            }
        } finally {
            StreamUtil.close(response);
        }
    }

    public void cancelRequest(Object requestTag) {
        if (client != null && requestTag != null) {
            try {
                Dispatcher dispatcher = client.dispatcher();
                List<Call> queuedCalls = dispatcher.queuedCalls();
                cancelCallByTag(requestTag, queuedCalls);

                List<Call> runningCalls = dispatcher.runningCalls();
                cancelCallByTag(requestTag, runningCalls);
            } catch (Exception ignored) {
            }
        }
    }

    private void cancelCallByTag(Object requestTag, List<Call> list) {
        for (Call call : list) {
            Request request = call.request();
            if (requestTag.equals(request.tag())) {
                if (L.isEnable()) L.i(TAG, "cancel call by requestTag=" + requestTag + "  url=" + request.url());
                call.cancel();
            }
        }
    }

    public void cancelAllRequest() {
        if (client != null) {
            client.dispatcher().cancelAll();
        }
    }

    public static String formatJson(String sourceStr) {
        if (getInstance().printFormatter == null) getInstance().printFormatter = new JsonPrintFormatter();
        return getInstance().printFormatter.formatJson(sourceStr);
    }

    Gson getJson() {
        return gson;
    }

    HttpInterceptor getHttpInterceptor() {
        return httpInterceptor;
    }
}
