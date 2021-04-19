package com.qsmaxmin.qsbase.common.http;

import android.net.Uri;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.qsmaxmin.qsbase.common.aspect.Body;
import com.qsmaxmin.qsbase.common.aspect.DELETE;
import com.qsmaxmin.qsbase.common.aspect.FieldMap;
import com.qsmaxmin.qsbase.common.aspect.FormBody;
import com.qsmaxmin.qsbase.common.aspect.FormParam;
import com.qsmaxmin.qsbase.common.aspect.GET;
import com.qsmaxmin.qsbase.common.aspect.HEAD;
import com.qsmaxmin.qsbase.common.aspect.Header;
import com.qsmaxmin.qsbase.common.aspect.PATCH;
import com.qsmaxmin.qsbase.common.aspect.POST;
import com.qsmaxmin.qsbase.common.aspect.PUT;
import com.qsmaxmin.qsbase.common.aspect.Path;
import com.qsmaxmin.qsbase.common.aspect.Query;
import com.qsmaxmin.qsbase.common.aspect.RequestStyle;
import com.qsmaxmin.qsbase.common.aspect.TERMINAL;
import com.qsmaxmin.qsbase.common.log.L;

import org.json.JSONObject;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * @CreateBy qsmaxmin
 * @Date 2020/7/17 11:52
 * @Description
 */
public final class HttpRequest {
    private final Method                  method;
    private final Object[]                args;
    private final Object                  requestTag;
    private final String                  methodName;
    private final Class<?>                returnType;
    private final Gson                    gson;
    private       String                  terminal;
    private       int                     requestStyle;
    private       String                  path;
    private       String                  requestType;
    private       HashMap<String, Object> filedMap;
    private       HashMap<String, String> queryMap;
    private       String                  requestBodyMimeType;
    private       Object                  requestBody;
    private       Headers.Builder         headerBuilder;

    HttpRequest(Method method, Object[] args, Object requestTag, Gson gson) throws Exception {
        this.method = method;
        this.args = args;
        this.requestTag = requestTag;
        this.methodName = method.getName();
        this.returnType = method.getReturnType();
        this.gson = gson;

        processMethodAnnotation();
        processParamsAnnotation();
    }

    @NonNull public Request createRequest() throws Exception {
        if (TextUtils.isEmpty(terminal)) {
            throw new Exception("error... method:" + methodName + ", 未设置主机地址，可单独给接口添加@TERMINAL注解，也可以通过拦截器添加公共主机地址");
        }

        StringBuilder url = new StringBuilder(terminal);
        url.append(path);

        if (queryMap != null && !queryMap.isEmpty()) {
            appendQueryParams(url);
        }

        Request.Builder builder = new Request.Builder();
        if (headerBuilder != null) builder.headers(headerBuilder.build());
        if (requestTag != null) builder.tag(requestTag);
        return builder.url(url.toString()).method(requestType, createRequestBody()).build();
    }

    private RequestBody createRequestBody() {
        if (shouldProcessBody()) {
            if (filedMap != null) {
                return createRequestBodyByFieldMap(filedMap);
            } else if (requestBody != null) {
                return createRequestBodyByObject(requestBodyMimeType, requestBody);
            }
        }
        return null;
    }

    private void appendQueryParams(StringBuilder urlBuilder) {
        int i = 0;
        Uri uri = Uri.parse(urlBuilder.toString());
        String uriQuery = uri.getQuery();
        boolean shouldAdd = TextUtils.isEmpty(uriQuery) && urlBuilder.charAt(urlBuilder.length() - 1) != '?';
        for (String key : queryMap.keySet()) {
            String value = queryMap.get(key);
            urlBuilder.append((i == 0 && shouldAdd) ? '?' : '&').append(key).append('=').append(value);
            i++;
        }
    }

    private void processParamsAnnotation() throws Exception {
        List<Object> pathReplaceList = null;

        if (args != null && args.length > 0) {
            Annotation[][] paramsAnnTotal = method.getParameterAnnotations();
            if (paramsAnnTotal.length != args.length) {
                throw new Exception("params error method:" + methodName + " params have to have one annotation, such as @Query @Path");
            }
            Annotation[] paramsAnn = new Annotation[paramsAnnTotal.length];
            for (int i = 0; i < paramsAnnTotal.length; i++) {
                Annotation[] annotations = paramsAnnTotal[i];
                if (annotations.length != 1) {
                    throw new Exception("params error method:" + methodName + " params have to have one annotation, but there is more than one !");
                } else {
                    paramsAnn[i] = annotations[0];
                }
            }

            for (int i = 0; i < paramsAnn.length; i++) {
                Annotation annotation = paramsAnn[i];
                if (annotation instanceof com.qsmaxmin.qsbase.common.aspect.Field || annotation instanceof FormParam) {
                    Object arg = args[i];
                    if (arg != null) {
                        if (annotation instanceof FormParam) {
                            getFiledMap().put(((FormParam) annotation).value(), arg);
                        } else {
                            getFiledMap().put(((com.qsmaxmin.qsbase.common.aspect.Field) annotation).value(), arg);
                        }
                    }

                } else if (annotation instanceof Query) {
                    Object arg = args[i];
                    String key = ((Query) annotation).value();
                    getQueryMap().put(key, arg == null ? "" : String.valueOf(arg));

                } else if (annotation instanceof FieldMap) {
                    if (!(args[i] instanceof Map)) {
                        throw new Exception("param error....@FieldMap only support Map field");
                    }
                    Map map = (Map) args[i];
                    for (Object k : map.keySet()) {
                        getFiledMap().put(String.valueOf(k), map.get(k));
                    }

                } else if (annotation instanceof FormBody) {
                    if (args[i] != null) {
                        parseFormBody(getFiledMap(), args[i]);
                    }

                } else if (annotation instanceof Header) {
                    if (args[i] != null) {
                        String headerKey = ((Header) annotation).value();
                        getHeader().add(headerKey, String.valueOf(args[i]));
                    }

                } else if (annotation instanceof Body) {
                    requestBody = args[i];
                    requestBodyMimeType = ((Body) annotation).mimeType();
                    if (TextUtils.isEmpty(requestBodyMimeType)) {
                        throw new Exception("param error...  method:" + methodName + "  the annotation @Body must have mimeType value");
                    }

                } else if (annotation instanceof Path) {
                    if (pathReplaceList == null) pathReplaceList = new ArrayList<>();
                    pathReplaceList.add(args[i]);
                }
            }
        }

        if (pathReplaceList != null) {
            path = String.format(path, pathReplaceList.toArray());
        }
    }

    /**
     * 处理Method注解
     */
    private void processMethodAnnotation() throws Exception {
        Annotation[] methodAnn = method.getAnnotations();
        if (methodAnn.length == 0) {
            throw new Exception("Annotation error... the method:" + methodName + " must have one annotation at least!! @GET @POST or @PUT...");
        }
        String[] headerArrays = null;
        Annotation pathAnnotation = null;
        for (Annotation annotation : methodAnn) {
            if (annotation instanceof TERMINAL) {
                terminal = ((TERMINAL) annotation).value();
            } else if (annotation instanceof RequestStyle) {
                requestStyle = ((RequestStyle) annotation).value();
            } else if (annotation instanceof com.qsmaxmin.qsbase.common.aspect.Headers) {
                headerArrays = ((com.qsmaxmin.qsbase.common.aspect.Headers) annotation).value();
            } else {
                pathAnnotation = annotation;
            }
        }
        if (pathAnnotation == null) {
            throw new Exception("Annotation error... the method:" + methodName + " must has an annotation,such as:@PUT @POST or @GET...");
        }
        if (headerArrays != null && headerArrays.length > 0) {
            for (String header : headerArrays) {
                getHeader().add(header);
            }
        }

        if (pathAnnotation instanceof POST) {
            path = ((POST) pathAnnotation).value();
            requestType = "POST";
        } else if (pathAnnotation instanceof GET) {
            path = ((GET) pathAnnotation).value();
            requestType = "GET";
        } else if (pathAnnotation instanceof PUT) {
            path = ((PUT) pathAnnotation).value();
            requestType = "PUT";
        } else if (pathAnnotation instanceof DELETE) {
            path = ((DELETE) pathAnnotation).value();
            requestType = "DELETE";
        } else if (pathAnnotation instanceof HEAD) {
            path = ((HEAD) pathAnnotation).value();
            requestType = "HEAD";
        } else if (pathAnnotation instanceof PATCH) {
            path = ((PATCH) pathAnnotation).value();
            requestType = "PATCH";
        } else {
            throw new Exception("Annotation error... the method:" + methodName + " must has an annotation, such as:@PUT @POST or @GET...");
        }

        if (TextUtils.isEmpty(path)) {
            throw new Exception("url path error, method:" + methodName + ", path is null...");
        }
    }

    private boolean shouldProcessBody() {
        return !"GET".equals(requestType) && !"HEAD".equals(requestType);
    }

    @NonNull public final Method getMethod() {
        return method;
    }

    @NonNull public final Class<?> getReturnType() {
        return returnType;
    }

    public final Object[] getArgs() {
        return args;
    }

    public final Object getRequestTag() {
        return requestTag;
    }

    public final void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public final String getTerminal() {
        return terminal;
    }

    public final int getRequestStyle() {
        return requestStyle;
    }

    @NonNull public final String getPath() {
        return path;
    }

    @NonNull public final String getMethodName() {
        return methodName;
    }

    @NonNull public final String getRequestType() {
        return requestType;
    }

    @NonNull public final HashMap<String, String> getQueryMap() {
        if (queryMap == null) queryMap = new HashMap<>();
        return queryMap;
    }

    public final void setQueryMap(HashMap<String, String> queryMap) {
        this.queryMap = queryMap;
    }

    /**
     * @see #addQuery(String, String)
     * @deprecated
     */
    public final void addQueryParam(String key, String value) {
        addQuery(key, value);
    }

    public final void addQuery(String key, String value) {
        if (!TextUtils.isEmpty(key)) {
            HashMap<String, String> map = this.queryMap;
            map.put(key, value == null ? "" : value);
        }
    }

    /**
     * @see #getFiledMap()
     * @deprecated
     */
    @NonNull public final HashMap<String, Object> getFormMap() {
        return getFiledMap();
    }

    /**
     * @see #setFiledMap(HashMap)
     * @deprecated
     */
    public final void setFormMap(HashMap<String, Object> formMap) {
        setFiledMap(formMap);
    }

    /**
     * @see #addFiled(String, Object)
     * @deprecated
     */
    public final void addFormParam(String key, Object value) {
        addFiled(key, value);
    }

    public final void setFiledMap(HashMap<String, Object> formMap) {
        this.filedMap = formMap;
    }

    @NonNull public final HashMap<String, Object> getFiledMap() {
        if (filedMap == null) filedMap = new HashMap<>();
        return filedMap;
    }

    public final void addFiled(String key, Object value) {
        if (!TextUtils.isEmpty(key) && value != null) {
            getFiledMap().put(key, value);
        }
    }

    public final Object getField(String key) {
        return getFiledMap().get(key);
    }

    public final Object getRequestBody() {
        return requestBody;
    }

    public final void setRequestBody(Object requestBody) {
        this.requestBody = requestBody;
    }

    public final void addHeader(@NonNull String key, @NonNull String value) {
        if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
            Headers.Builder header = getHeader();
            header.add(key, value);
        }
    }

    @NonNull public final Headers.Builder getHeader() {
        if (headerBuilder == null) headerBuilder = new Headers.Builder();
        return headerBuilder;
    }

    private void parseFormBody(@NonNull HashMap<String, Object> formMap, @NonNull Object formBody) throws Exception {
        if (formBody instanceof Map) {
            L.i("QsRequestParams", "methodName:" + methodName + ", @FormBody type of Map....");
            Map dataMap = (Map) formBody;
            for (Object key : dataMap.keySet()) {
                String keyStr = String.valueOf(key);
                String valueStr = String.valueOf(dataMap.get(key));
                if (!TextUtils.isEmpty(keyStr) && !TextUtils.isEmpty(valueStr)) formMap.put(keyStr, valueStr);
            }
        } else if (formBody instanceof String) {
            L.i("QsRequestParams", "methodName:" + methodName + ", FormBody type of String....");
            JSONObject jsonObject = new JSONObject((String) formBody);
            while (jsonObject.keys().hasNext()) {
                String key = jsonObject.keys().next();
                Object value = jsonObject.get(key);
                if (key != null && value != null) {
                    formMap.put(key, String.valueOf(value));
                }
            }
        } else {
            L.i("QsRequestParams", "methodName:" + methodName + ", FormBody type of Object....");
            Field[] fieldArr = formBody.getClass().getFields();
            if (fieldArr.length > 0) {
                for (Field field : fieldArr) {
                    Object value = field.get(formBody);
                    if (value != null) {
                        formMap.put(field.getName(), String.valueOf(value));
                    }
                }
            }
        }
    }

    private RequestBody createRequestBodyByObject(String mimeType, @NonNull Object data) {
        if (data instanceof String) {
            return RequestBody.create(MediaType.parse(mimeType), (String) data);
        } else if (data instanceof File) {
            return RequestBody.create(MediaType.parse(mimeType), (File) data);
        } else if (data instanceof byte[]) {
            return RequestBody.create(MediaType.parse(mimeType), (byte[]) data);
        } else {
            String json = gson.toJson(data, data.getClass());
            return RequestBody.create(MediaType.parse(mimeType), json);
        }
    }

    private RequestBody createRequestBodyByFieldMap(@NonNull HashMap<String, Object> formMap) {
        boolean isFormBody = true;
        for (String key : formMap.keySet()) {
            Object obj = formMap.get(key);
            if (obj instanceof File || obj instanceof byte[]) {
                isFormBody = false;
            }
        }
        if (isFormBody) {
            okhttp3.FormBody.Builder builder = new okhttp3.FormBody.Builder();
            for (String key : formMap.keySet()) {
                if (TextUtils.isEmpty(key)) continue;
                Object value = formMap.get(key);
                if (value != null) {
                    builder.add(key, valueToString(value));
                }
            }
            return builder.build();
        } else {
            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            for (String key : formMap.keySet()) {
                if (TextUtils.isEmpty(key)) continue;
                Object value = formMap.get(key);
                if (value instanceof File) {
                    RequestBody rb = RequestBody.create(MediaType.parse("file/*"), (File) value);
                    builder.addFormDataPart(key, String.valueOf(System.nanoTime()), rb);
                } else if (value instanceof byte[]) {
                    RequestBody rb = RequestBody.create(MediaType.parse("file/*"), (byte[]) value);
                    builder.addFormDataPart(key, String.valueOf(System.nanoTime()), rb);
                } else if (value != null) {
                    builder.addFormDataPart(key, valueToString(value));
                }
            }
            return builder.build();
        }
    }

    public static String valueToString(@NonNull Object value) {
        if (value.getClass().isArray()) {
            Object[] a = (Object[]) value;
            int iMax = a.length - 1;
            if (iMax == -1) return "[]";
            StringBuilder b = new StringBuilder();
            b.append('[');
            for (int i = 0; ; i++) {
                b.append(a[i]);
                if (i == iMax) {
                    return b.append(']').toString();
                }
                b.append(",");
            }
        } else {
            return String.valueOf(value);
        }
    }
}
