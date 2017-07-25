package com.qsmaxmin.qsbase.common.http;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.qsmaxmin.qsbase.common.aspect.Body;
import com.qsmaxmin.qsbase.common.aspect.GET;
import com.qsmaxmin.qsbase.common.aspect.POST;
import com.qsmaxmin.qsbase.common.aspect.PUT;
import com.qsmaxmin.qsbase.common.aspect.Query;
import com.qsmaxmin.qsbase.common.exception.QsException;
import com.qsmaxmin.qsbase.common.exception.QsExceptionType;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.model.QsModel;
import com.qsmaxmin.qsbase.common.proxy.HttpHandler;
import com.qsmaxmin.qsbase.common.utils.QsHelper;
import com.qsmaxmin.qsbase.mvp.model.QsConstants;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/6/29 14:53
 * @Description
 */

public class HttpAdapter {
    private static final String TAG     = "HttpAdapter";
    private final static int    timeOut = 30;
    private GsonConverter converter;
    private OkHttpClient  client;


    public HttpAdapter() {
        initDefaults();
    }

    public OkHttpClient getHttpClient() {
        if (client == null) {
            initDefaults();
        }
        return client;
    }

    /**
     * 获取默认值
     */
    private void initDefaults() {
        if (converter == null) {
            converter = new GsonConverter();
        }
        if (client == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(timeOut, TimeUnit.SECONDS);
            builder.readTimeout(timeOut, TimeUnit.SECONDS);
            builder.writeTimeout(timeOut, TimeUnit.SECONDS);
            client = builder.build();
        }
    }

    private HttpBuilder getHttpBuilder() {
        HttpBuilder httpBuilder = new HttpBuilder();
        QsHelper.getInstance().getApplication().initHttpAdapter(httpBuilder);
        return httpBuilder;
    }


    /**
     * 创建代理
     */
    public <T> T create(Class<T> clazz) {
        validateIsInterface(clazz);
        validateIsExtendInterface(clazz);
        HttpHandler handler = new HttpHandler(this);
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, handler);
    }


    /**
     * 判断是否是一个接口
     */
    private static <T> void validateIsInterface(Class<T> service) {
        if (service == null || !service.isInterface()) {
            throw new QsException(QsExceptionType.UNEXPECTED, String.valueOf(service) + "，该类不是接口！");
        }
    }

    /**
     * 判断是否继承其他接口
     */
    private static <T> void validateIsExtendInterface(Class<T> service) {
        if (service.getInterfaces().length > 0) {
            throw new QsException(QsExceptionType.UNEXPECTED, "接口不能继承其它接口");
        }
    }

    public Object startRequest(Method method, Object[] args) {
        Annotation[] annotations = method.getAnnotations();
        if (annotations.length != 1) {
            throw new QsException(QsExceptionType.UNEXPECTED, "the method:" + method.getName() + " must have one annotation!! @GET @POST or @PUT");
        }
        Annotation annotation = annotations[0];
        if (annotation instanceof POST) {
            String path = ((POST) annotation).value();
            return executeWithBody(method, args, path, "POST");
        } else if (annotation instanceof GET) {
            String path = ((GET) annotation).value();
            return executeGet(method, args, path);
        } else if (annotation instanceof PUT) {
            String path = ((PUT) annotation).value();
            return executeWithBody(method, args, path, "PUT");
        } else {
            throw new QsException(QsExceptionType.UNEXPECTED, "create(interface) the interface must has an annotation:@PUT @POST or @GET");
        }
    }


    private Object executeGet(Method method, Object[] args, String path) {
        HttpBuilder httpBuilder = getHttpBuilder();
        StringBuilder url = getUrl(httpBuilder.getTerminal(), path);
        if (TextUtils.isEmpty(url)) throw new QsException(QsExceptionType.UNEXPECTED, "request url is null...");
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        Map<String, Object> params = null;
        for (int i = 0; i < parameterAnnotations.length; i++) {
            Annotation[] annotations = parameterAnnotations[i];
            for (Annotation annotation : annotations) {
                if (annotation instanceof Query && args != null && i < args.length) {
                    if (params == null) params = new HashMap<>();
                    Object arg = args[i];
                    String key = ((Query) annotation).value();
                    params.put(key, arg);
                }
            }
        }
        if (params != null) {
            for (String key : params.keySet()) {
                Object object = params.get(key);
                url.append("&").append(key).append("=").append(object);
            }
        }

        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.headers(httpBuilder.getHeaderBuilder().build());
        Request request = requestBuilder.tag(url.toString()).url(url.toString()).method("GET", null).build();
        try {
            Call call = client.newCall(request);
            Response response = call.execute();
            return createResult(method, response, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Object executeWithBody(Method method, Object[] args, String path, String type) {
        HttpBuilder httpBuilder = getHttpBuilder();
        StringBuilder url = getUrl(httpBuilder.getTerminal(), path);
        if (TextUtils.isEmpty(url)) {
            throw new QsException(QsExceptionType.UNEXPECTED, "request url is null...");
        }
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();//参数可以有多个注解
        Object body = null;
        HashMap<String, Object> params = null;
        for (int i = 0; i < parameterAnnotations.length; i++) {
            Annotation[] annotations = parameterAnnotations[i];
            for (Annotation annotation : annotations) {
                if (args != null && i < args.length) {
                    if (annotation instanceof Body) {
                        body = args[i];
                        break;
                    } else if (annotation instanceof Query) {
                        if (params == null) params = new HashMap<>();
                        Object arg = args[i];
                        String key = ((Query) annotation).value();
                        params.put(key, arg);
                    }
                }
            }
        }
        RequestBody requestBody = null;
        if (body != null) {
            requestBody = converter.toBody(body, body.getClass());
        }
        if (params != null) {
            for (String key : params.keySet()) {
                Object object = params.get(key);
                url.append("&").append(key).append("=").append(object);
            }
        }
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.headers(httpBuilder.getHeaderBuilder().build());
        Request request = requestBuilder.tag(url.toString()).url(url.toString()).method(type, requestBody).build();
        try {
            Call call = client.newCall(request);
            Response response = call.execute();
            return createResult(method, response, args);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private Object createResult(Method method, Response response, Object[] args) {
        Class<?> returnType = method.getReturnType();
        if (returnType == void.class) return null;
        int responseCode = response.code();
        if (responseCode < 200 || responseCode >= 300) {
            throw new QsException(QsExceptionType.HTTP_ERROR, "http response code = " + responseCode);
        }
        if (returnType.equals(Response.class)) {
            return response;
        }
        ResponseBody body = response.body();
        if (body == null) throw new QsException(QsExceptionType.HTTP_ERROR, "http response body is null!!");
        try {
            Object result = converter.fromBody(body, returnType);
            if (result instanceof QsModel && ((QsModel) result).status == 401) {
                synchronized (QsConstants.HTTP_THREAD_LOCKER) {
                    try {
                        QsConstants.HTTP_THREAD_LOCKER.wait(30000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    startRequest(method, args);
                }
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            throw new QsException(QsExceptionType.UNEXPECTED, "parse response body to json object fail");
        }
    }

    @Nullable private StringBuilder getUrl(String terminal, String path) {
        if (TextUtils.isEmpty(path)) {
            throw new QsException(QsExceptionType.UNEXPECTED, "path is null...");
        }
        if (!path.startsWith("/")) {
            throw new QsException(QsExceptionType.UNEXPECTED, "path=" + path + "  (path is not start with '/')");
        }
        StringBuilder url = new StringBuilder(terminal);
        url.append(path);
        L.i(TAG, "请求路径:" + url.toString());
        return url;
    }
}
