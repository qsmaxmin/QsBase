package com.qsmaxmin.qsbase.common.http;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.qsmaxmin.qsbase.common.aspect.Body;
import com.qsmaxmin.qsbase.common.aspect.DELETE;
import com.qsmaxmin.qsbase.common.aspect.FormBody;
import com.qsmaxmin.qsbase.common.aspect.FormParam;
import com.qsmaxmin.qsbase.common.aspect.GET;
import com.qsmaxmin.qsbase.common.aspect.HEAD;
import com.qsmaxmin.qsbase.common.aspect.PATCH;
import com.qsmaxmin.qsbase.common.aspect.POST;
import com.qsmaxmin.qsbase.common.aspect.PUT;
import com.qsmaxmin.qsbase.common.aspect.Path;
import com.qsmaxmin.qsbase.common.aspect.Query;
import com.qsmaxmin.qsbase.common.aspect.TERMINAL;
import com.qsmaxmin.qsbase.common.exception.QsException;
import com.qsmaxmin.qsbase.common.exception.QsExceptionType;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.proxy.HttpHandler;
import com.qsmaxmin.qsbase.common.utils.QsHelper;
import com.qsmaxmin.qsbase.common.utils.StreamCloseUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Dispatcher;
import okhttp3.MediaType;
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
    private static final String        TAG          = "HttpAdapter";
    private static final String        PATH_REPLACE = "\\{\\w*\\}";
    private final static int           timeOut      = 10;
    private              OkHttpClient  client;
    private              HttpConverter converter;


    public HttpAdapter() {
        initDefaults();
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

    /**
     * 获取默认值
     */
    private void initDefaults() {
        if (client == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(timeOut, TimeUnit.SECONDS);
            builder.readTimeout(timeOut, TimeUnit.SECONDS);
            builder.writeTimeout(timeOut, TimeUnit.SECONDS);
            builder.retryOnConnectionFailure(true);
            client = builder.build();
        }
        if (converter == null) {
            converter = new HttpConverter();
        }
    }

    private HttpBuilder getHttpBuilder(Object requestTag, String terminal, String path, Object[] args, String requestType, Object body, HashMap<String, String> formBody, HashMap<String, String> paramsMap) throws Exception {
        HttpBuilder httpBuilder = new HttpBuilder(requestTag, terminal, path, args, requestType, body, formBody, paramsMap);
        QsHelper.getInstance().getApplication().initHttpAdapter(httpBuilder);
        return httpBuilder;
    }

    /**
     * 创建代理
     */
    public <T> T create(Class<T> clazz, Object requestTag) {
        validateIsInterface(clazz, requestTag);
        validateIsExtendInterface(clazz, requestTag);
        HttpHandler handler = new HttpHandler(this, requestTag);
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, handler);
    }

    /**
     * 判断是否是一个接口
     */
    private static <T> void validateIsInterface(Class<T> service, Object requestTag) {
        if (service == null || !service.isInterface()) {
            throw new QsException(QsExceptionType.UNEXPECTED, requestTag, String.valueOf(service) + " is not interface！");
        }
    }

    /**
     * 判断是否继承其他接口
     */
    private static <T> void validateIsExtendInterface(Class<T> service, Object requestTag) {
        if (service.getInterfaces().length > 0) {
            throw new QsException(QsExceptionType.UNEXPECTED, requestTag, String.valueOf(service) + " can not extend interface!!");
        }
    }

    public Object startRequest(Method method, Object[] args, Object requestTag) {
        Annotation[] annotations = method.getAnnotations();
        if (annotations == null || annotations.length < 1) {
            throw new QsException(QsExceptionType.UNEXPECTED, requestTag, "Annotation error... the method:" + method.getName() + " must have one annotation at least!! @GET @POST or @PUT");
        }
        Annotation pathAnnotation = null;
        String terminal = null;
        for (Annotation annotation : annotations) {
            if (annotation instanceof TERMINAL) {
                terminal = ((TERMINAL) annotation).value();
            } else {
                pathAnnotation = annotation;
            }
        }
        if (pathAnnotation == null) {
            throw new QsException(QsExceptionType.UNEXPECTED, requestTag, "Annotation error... the method:" + method.getName() + " create(Object.class) the method must has an annotation,such as:@PUT @POST or @GET...");
        }
        if (pathAnnotation instanceof POST) {
            String path = ((POST) pathAnnotation).value();
            return executeWithOkHttp(terminal, method, args, path, requestTag, "POST");

        } else if (pathAnnotation instanceof GET) {
            String path = ((GET) pathAnnotation).value();
            return executeWithOkHttp(terminal, method, args, path, requestTag, "GET");

        } else if (pathAnnotation instanceof PUT) {
            String path = ((PUT) pathAnnotation).value();
            return executeWithOkHttp(terminal, method, args, path, requestTag, "PUT");

        } else if (pathAnnotation instanceof DELETE) {
            String path = ((DELETE) pathAnnotation).value();
            return executeWithOkHttp(terminal, method, args, path, requestTag, "DELETE");

        } else if (pathAnnotation instanceof HEAD) {
            String path = ((HEAD) pathAnnotation).value();
            return executeWithOkHttp(terminal, method, args, path, requestTag, "HEAD");

        } else if (pathAnnotation instanceof PATCH) {
            String path = ((PATCH) pathAnnotation).value();
            return executeWithOkHttp(terminal, method, args, path, requestTag, "PATCH");

        } else {
            throw new QsException(QsExceptionType.UNEXPECTED, requestTag, "Annotation error... the method:" + method.getName() + "create(Object.class) the method must has an annotation, such as:@PUT @POST or @GET...");
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
                        L.i(TAG, "cancel queued request success... requestTag=" + requestTag + "  url=" + request.url());
                        call.cancel();
                    }
                }

                List<Call> runningCalls = dispatcher.runningCalls();
                for (Call call : runningCalls) {
                    Request request = call.request();
                    if (requestTag.equals(request.tag())) {
                        L.i(TAG, "cancel running request ... requestTag=" + requestTag + "  url=" + call.request().url());
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

    private Object executeWithOkHttp(String terminal, Method method, Object[] args, String path, Object requestTag, String requestType) {
        Annotation[][] annotations = method.getParameterAnnotations();//参数可以有多个注解，但这里是不允许的

        checkParamsAnnotation(annotations, args, method.getName(), requestTag);

        RequestBody requestBody = null;
        Object body = null;
        HashMap<String, String> formMap = null;
        HashMap<String, String> paramsMap = null;
        String mimeType = null;

        for (int i = 0; i < annotations.length; i++) {
            Annotation[] annotationArr = annotations[i];
            Annotation annotation = annotationArr[0];
            if (annotation instanceof Body) {
                body = args[i];
                mimeType = ((Body) annotation).mimeType();
                if (TextUtils.isEmpty(mimeType)) throw new QsException(QsExceptionType.UNEXPECTED, requestTag, "request body exception...  method:" + method.getName() + "  the annotation @Body not have mimeType value");
                break;
            } else if (annotation instanceof Query) {
                Object arg = args[i];
                if (paramsMap == null) paramsMap = new HashMap<>();
                String key = ((Query) annotation).value();
                paramsMap.put(key, arg == null ? "" : String.valueOf(arg));
            } else if (annotation instanceof FormBody) {
                Object formBody = args[i];
                if (formBody != null) {
                    try {
                        HashMap<String, String> map = converter.parseFormBody(method.getName(), formBody);
                        if (formMap == null) {
                            formMap = new HashMap<>(map);
                        } else {
                            formMap.putAll(map);
                        }
                    } catch (Exception e) {
                        throw new QsException(QsExceptionType.UNEXPECTED, requestTag, "method:" + method.getName() + " message:" + e.getMessage());
                    }
                }
            } else if (annotation instanceof FormParam) {
                Object arg = args[i];
                if (arg != null && !TextUtils.isEmpty(String.valueOf(arg))) {
                    String key = ((FormParam) annotation).value();
                    if (formMap == null) formMap = new HashMap<>();
                    formMap.put(key, String.valueOf(arg));
                }
            }
        }

        HttpBuilder httpBuilder;
        try {
            httpBuilder = getHttpBuilder(requestTag, terminal, path, args, requestType, body, formMap, paramsMap);
        } catch (Exception e) {
            throw new QsException(QsExceptionType.UNEXPECTED, "http 公共处理逻辑出错", "error:" + e.getMessage());
        }
        StringBuilder url = getUrl(httpBuilder.getTerminal(), path, method, args, requestTag);
        if (TextUtils.isEmpty(url)) throw new QsException(QsExceptionType.UNEXPECTED, requestTag, "url error... method:" + method.getName() + "  request url is null...");
        paramsMap = httpBuilder.getUrlParameters();
        formMap = httpBuilder.getFormBody();
        body = httpBuilder.getBody();

        if ((!"GET".equals(requestType)) && (!"HEAD".equals(requestType))) {
            if (body != null) {
                if (body instanceof String) {
                    requestBody = converter.stringToBody(method.getName(), mimeType, (String) body);
                } else if (body instanceof File) {
                    requestBody = converter.fileToBody(method.getName(), mimeType, (File) body);
                } else if (body instanceof byte[]) {
                    requestBody = converter.byteToBody(method.getName(), mimeType, (byte[]) body);
                } else {
                    requestBody = converter.jsonToBody(method.getName(), mimeType, body, body.getClass());
                }
            } else if (formMap != null) {
                okhttp3.FormBody.Builder builder = new okhttp3.FormBody.Builder();
                for (String key : formMap.keySet()) {
                    String valueStr = formMap.get(key);
                    if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(valueStr)) builder.add(key, valueStr);
                }
                requestBody = builder.build();
            }
        }

        if (paramsMap != null && !paramsMap.isEmpty()) {
            int i = 0;
            Uri uri = Uri.parse(url.toString());
            String uriQuery = uri.getQuery();
            for (String key : paramsMap.keySet()) {
                String value = paramsMap.get(key);
                url.append((i == 0 && TextUtils.isEmpty(uriQuery) && url.charAt(url.length() - 1) != '?') ? "?" : "&").append(key).append("=").append(value);
                i++;
            }
        }
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.headers(httpBuilder.getHeaderBuilder().build());
        if (requestTag != null) requestBuilder.tag(requestTag);
        L.i(TAG, "method:" + method.getName() + "  http request url:" + url.toString());
        Request request = requestBuilder.url(url.toString()).method(requestType, requestBody).build();
        try {
            if (QsHelper.getInstance().isNetworkAvailable()) {
                Call call = client.newCall(request);
                Response response = call.execute();
                return createResult(method, response, requestTag, httpBuilder);
            } else {
                throw new QsException(QsExceptionType.NETWORK_ERROR, requestTag, "network error...  method:" + method.getName() + " message:network disable");
            }
        } catch (IOException e) {
            throw new QsException(QsExceptionType.HTTP_ERROR, requestTag, "IOException...  method:" + method.getName() + " message:" + e.getMessage());
        } catch (Throwable e) {
            throw new QsException(QsExceptionType.UNEXPECTED, requestTag, "method:" + method.getName() + ", error:" + e.getMessage());
        }
    }

    private Object createResult(Method method, Response response, Object requestTag, HttpBuilder httpBuilder) {
        if (response == null) return null;
        int responseCode = response.code();
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.response = response;
        httpResponse.httpBuilder = httpBuilder;

        if (responseCode >= 200 && responseCode < 300) {
            Class<?> returnType = method.getReturnType();
            if (returnType == void.class) {
                QsHelper.getInstance().getApplication().onCommonHttpResponse(httpResponse);
                response.close();
                return null;
            } else if (returnType.equals(Response.class)) {
                QsHelper.getInstance().getApplication().onCommonHttpResponse(httpResponse);
                return response;
            } else {
                ResponseBody body = response.body();
                if (body == null) {
                    throw new QsException(QsExceptionType.HTTP_ERROR, requestTag, "http response error... method:" + method.getName() + "  response body is null!!");
                }
                QsHelper.getInstance().getApplication().onCommonHttpResponse(httpResponse);
                String jsonStr = httpResponse.getJsonString();
                response.close();
                if (QsHelper.getInstance().getApplication().isLogOpen()) {
                    L.i(TAG, "methodName:" + method.getName() + "  响应体 Json:" + converter.formatJson(jsonStr));
                }
                if (!TextUtils.isEmpty(jsonStr)) {
                    return converter.jsonToObject(jsonStr, returnType);
                }
            }
        } else {
            QsHelper.getInstance().getApplication().onCommonHttpResponse(httpResponse);
            response.close();
            throw new QsException(QsExceptionType.HTTP_ERROR, requestTag, "http error... method:" + method.getName() + "  http response code = " + responseCode);
        }
        return null;
    }

    private String getJsonFromBody(ResponseBody body, Object requestTag) {
        Charset charset = getCharset(body);
        InputStream is = body.byteStream();
        if (is != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(is, charset);
            BufferedReader bufferedReader = null;
            try {
                bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    result.append(line).append("\n");
                }
                return result.toString();
            } catch (IOException e) {
                throw new QsException(QsExceptionType.UNEXPECTED, requestTag, e.getMessage());
            } catch (Exception e) {
                throw new QsException(QsExceptionType.UNEXPECTED, requestTag, e.getMessage());
            } finally {
                StreamCloseUtils.close(inputStreamReader, is, bufferedReader);
            }
        }
        return null;
    }

    @NonNull private Charset getCharset(ResponseBody body) {
        Charset charset = Charset.forName("UTF-8");
        MediaType mediaType = body.contentType();
        if (mediaType != null) {
            Charset c = mediaType.charset(charset);
            if (c != null) charset = c;
        }
        return charset;
    }

    @NonNull private StringBuilder getUrl(String terminal, String path, Method method, Object[] args, Object requestTag) {
        if (TextUtils.isEmpty(terminal)) {
            throw new QsException(QsExceptionType.UNEXPECTED, requestTag, "url terminal error... method:" + method.getName() + "  terminal is null...");
        }
        if (TextUtils.isEmpty(path)) {
            throw new QsException(QsExceptionType.UNEXPECTED, requestTag, "url path error... method:" + method.getName() + "  path is null...");
        }
        if (!path.startsWith("/")) {
            throw new QsException(QsExceptionType.UNEXPECTED, requestTag, "url path error... method:" + method.getName() + "  path=" + path + "  (path is not start with '/')");
        }
        Annotation[][] annotations = method.getParameterAnnotations();
        for (int i = 0; i < annotations.length; i++) {
            Annotation ann = annotations[i][0];
            if (ann instanceof Path) {
                StringBuilder stringBuilder = new StringBuilder();
                String[] split = path.split(PATH_REPLACE);
                Object arg = args[i];
                if (!(arg instanceof String[])) {
                    throw new QsException(QsExceptionType.UNEXPECTED, requestTag, "params error method:" + method.getName() + "  @Path annotation only fix String[] arg !");
                }
                String[] param = (String[]) arg;
                if (split.length - param.length > 1) {
                    throw new QsException(QsExceptionType.UNEXPECTED, requestTag, "params error method:" + method.getName() + "  the path with '{xx}' is more than @Path annotation arg length!");
                }
                for (int index = 0; index < split.length; index++) {
                    if (index < param.length) {
                        stringBuilder.append(split[index]).append(param[index]);
                    } else {
                        stringBuilder.append(split[index]);
                    }
                }
                path = stringBuilder.toString();
            }
        }
        StringBuilder url = new StringBuilder(terminal);
        url.append(path);
        return url;
    }


    /**
     * 检查参数的注解
     * 每个参数有且仅有一个注解
     */
    private void checkParamsAnnotation(Annotation[][] annotations, Object[] args, String methodName, Object requestTag) {
        if (annotations != null && args != null && annotations.length > 0 && args.length > 0) {
            if (annotations.length != args.length) throw new QsException(QsExceptionType.UNEXPECTED, requestTag, "params error method:" + methodName + "  params have to have one annotation, such as @Query @Path");
            for (Annotation[] annotationArr : annotations) {
                if (annotationArr.length != 1) throw new QsException(QsExceptionType.UNEXPECTED, requestTag, "params error method:" + methodName + "  params have to have one annotation, but there is more than one !");
            }
        }
    }
}
