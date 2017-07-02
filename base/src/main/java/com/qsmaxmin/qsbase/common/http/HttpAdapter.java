package com.qsmaxmin.qsbase.common.http;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.qsmaxmin.qsbase.common.aspect.Body;
import com.qsmaxmin.qsbase.common.aspect.GET;
import com.qsmaxmin.qsbase.common.aspect.POST;
import com.qsmaxmin.qsbase.common.aspect.Query;
import com.qsmaxmin.qsbase.common.exception.QsException;
import com.qsmaxmin.qsbase.common.exception.QsExceptionType;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.proxy.HttpHandler;
import com.qsmaxmin.qsbase.common.utils.QsHelper;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Headers;
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

    private String          terminal;
    private GsonConverter   converter;
    private OkHttpClient    client;
    private Headers.Builder headerBuilder;


    public HttpAdapter() {
        initDefaults();
        QsHelper.getInstance().getApplication().initHttpAdapter(this);
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

    public HttpAdapter setTerminal(String terminal) {
        if (!TextUtils.isEmpty(terminal)) {
            if (terminal.endsWith("/")) {
                terminal = terminal.substring(0, terminal.length() - 1);
            }
            this.terminal = terminal;
        } else {
            throw new RuntimeException("terminal is null...");
        }
        return this;
    }

    public HttpAdapter addHeader(String key, String value) {
        if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
            if (headerBuilder == null) {
                synchronized (this) {
                    if (headerBuilder == null) {
                        headerBuilder = new Headers.Builder();
                        headerBuilder.add("Content-Type", "application/json");
                    }
                }
            }
            headerBuilder.add(key, value);
        }
        return this;
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
        POST post = method.getAnnotation(POST.class);
        if (post != null) {
            String path = post.value();
            return executePost(method, args, path);
        } else {
            GET get = method.getAnnotation(GET.class);
            if (get != null) {
                String path = get.value();
                return executeGet(method, args, path);
            } else {
                throw new QsException(QsExceptionType.UNEXPECTED, "create(interface) the interface must has an annotation:@POST or @GET");
            }
        }
    }

    private Object executeGet(Method method, Object[] args, String path) {
        StringBuilder url = getUrl(method, path);
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
        requestBuilder.headers(headerBuilder.build());
        Request request = requestBuilder.tag(url.toString()).url(url.toString()).method("GET", null).build();
        try {
            Call call = client.newCall(request);
            Response response = call.execute();
            return createResult(method, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Object executePost(Method method, Object[] args, String path) {
        StringBuilder url = getUrl(method, path);
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
        requestBuilder.headers(headerBuilder.build());
        Request request = requestBuilder.tag(url.toString()).url(url.toString()).method("POST", requestBody).build();
        try {
            Call call = client.newCall(request);
            Response response = call.execute();
            return createResult(method, response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private Object createResult(Method method, Response response) {
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
            return converter.fromBody(body, returnType);
        } catch (IOException e) {
            e.printStackTrace();
            throw new QsException(QsExceptionType.UNEXPECTED, "parse response body to json object fail");
        }
    }

    @Nullable private StringBuilder getUrl(Method method, String path) {
        if (method == null) {
            throw new QsException(QsExceptionType.UNEXPECTED, "method is null...");
        }
        if (TextUtils.isEmpty(path)) {
            throw new QsException(QsExceptionType.UNEXPECTED, "path is null...");
        }
        if (!path.startsWith("/")) {
            throw new QsException(QsExceptionType.UNEXPECTED, "path=" + path + "  (path is not start with '/')");
        }
        String apiUrl = this.terminal;
        StringBuilder url = new StringBuilder(apiUrl);
        url.append(path);
        L.i(TAG, "请求路径:" + url.toString());
        return url;
    }
}
