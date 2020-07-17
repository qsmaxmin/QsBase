package com.qsmaxmin.qsbase.common.http;

import android.net.Uri;
import android.text.TextUtils;

import com.google.gson.Gson;
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
import com.qsmaxmin.qsbase.common.aspect.RequestStyle;
import com.qsmaxmin.qsbase.common.aspect.TERMINAL;
import com.qsmaxmin.qsbase.common.exception.QsException;
import com.qsmaxmin.qsbase.common.exception.QsExceptionType;
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
public class HttpRequest {
    private final Method   method;
    private final Object[] args;
    private final Object   requestTag;
    private final String   methodName;
    private final Class<?> returnType;
    private final Gson     gson;
    private       String   terminal;
    private       int      requestStyle;
    private       String   path;
    private       String   requestType;

    private HashMap<String, Object> formMap;
    private HashMap<String, String> queryMap;
    private Object                  requestBody;
    private String                  url;
    private RequestBody             httpRequestBody;
    private Headers.Builder         headerBuilder;

    public HttpRequest(Method method, Object[] args, Object requestTag, Gson gson, QsHttpCallback callback) throws Exception {
        this.method = method;
        this.args = args;
        this.requestTag = requestTag;
        this.methodName = method.getName();
        this.returnType = method.getReturnType();
        this.gson = gson;

        Annotation[] methodAnn = method.getAnnotations();
        if (methodAnn.length == 0) {
            throwException("Annotation error... the method:" + methodName + " must have one annotation at least!! @GET @POST or @PUT...");
        }

        Annotation pathAnnotation = null;
        for (Annotation annotation : methodAnn) {
            if (annotation instanceof TERMINAL) {
                terminal = ((TERMINAL) annotation).value();
            } else if (annotation instanceof RequestStyle) {
                requestStyle = ((RequestStyle) annotation).value();
            } else {
                pathAnnotation = annotation;
            }
        }

        if (pathAnnotation == null) {
            throwException("Annotation error... the method:" + methodName + " must has an annotation,such as:@PUT @POST or @GET...");
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
            throwException("Annotation error... the method:" + methodName + " must has an annotation, such as:@PUT @POST or @GET...");
            return;
        }

        if (args != null && args.length > 0) {
            Annotation[][] paramsAnnTotal = method.getParameterAnnotations();
            if (paramsAnnTotal.length != args.length) {
                throwException("params error method:" + methodName + " params have to have one annotation, such as @Query @Path");
                return;
            }
            Annotation[] paramsAnn = new Annotation[paramsAnnTotal.length];
            for (int i = 0; i < paramsAnnTotal.length; i++) {
                Annotation[] annotations = paramsAnnTotal[i];
                if (annotations.length != 1) {
                    throwException("params error method:" + methodName + " params have to have one annotation, but there is more than one !");
                    return;
                } else {
                    paramsAnn[i] = annotations[0];
                }
            }

            List<Object> pathReplaceList = null;
            String mimeType = "application/json; charset=UTF-8";

            for (int i = 0; i < paramsAnn.length; i++) {
                Annotation annotation = paramsAnn[i];
                if (annotation instanceof Body) {
                    requestBody = args[i];
                    mimeType = ((Body) annotation).mimeType();
                    if (TextUtils.isEmpty(mimeType)) {
                        throwException("request body exception...  method:" + methodName + "  the annotation @Body not have mimeType value");
                        return;
                    }
                    break;
                } else if (annotation instanceof Query) {
                    Object arg = args[i];
                    String key = ((Query) annotation).value();
                    if (queryMap == null) queryMap = new HashMap<>();
                    queryMap.put(key, arg == null ? "" : String.valueOf(arg));
                } else if (annotation instanceof FormBody) {
                    Object formBody = args[i];
                    if (formBody != null) {
                        if (formMap == null) formMap = new HashMap<>();
                        parseFormBody(formMap, formBody);
                    }
                } else if (annotation instanceof FormParam) {
                    Object arg = args[i];
                    if (arg != null) {
                        if (formMap == null) formMap = new HashMap<>();
                        String key = ((FormParam) annotation).value();
                        formMap.put(key, arg);
                    }
                } else if (annotation instanceof Path) {
                    if (pathReplaceList == null) pathReplaceList = new ArrayList<>();
                    pathReplaceList.add(args[i]);
                }
            }

            if (callback != null) callback.processParams(this);

            if (TextUtils.isEmpty(terminal)) {
                throwException("url terminal error... method:" + methodName + ", terminal is null...");
                return;
            }
            if (TextUtils.isEmpty(path)) {
                throwException("url path error... method:" + methodName + ", path is null...");
                return;
            }

            if (pathReplaceList != null) {
                path = String.format(path, pathReplaceList.toArray());
            }
            StringBuilder urlBuilder = new StringBuilder(terminal);
            urlBuilder.append(path);

            if (queryMap != null && !queryMap.isEmpty()) {
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
            url = urlBuilder.toString();
            if (L.isEnable()) {
                L.i("QsRequestParams", "start request....method:" + method.getName() + ", url:" + url);
            }

            if (shouldProcessBody()) {
                if (requestBody != null) {
                    httpRequestBody = createRequestBodyByObject(mimeType, requestBody);
                } else if (formMap != null) {
                    httpRequestBody = createRequestBodyByForm(formMap);
                }
            }
        }
    }

    private boolean shouldProcessBody() {
        return !"GET".equals(requestType) && !"HEAD".equals(requestType);
    }

    @NonNull public Method getMethod() {
        return method;
    }

    @NonNull public Class<?> getReturnType() {
        return returnType;
    }

    public Object[] getArgs() {
        return args;
    }

    public Object getRequestTag() {
        return requestTag;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public String getTerminal() {
        return terminal;
    }

    public int getRequestStyle() {
        return requestStyle;
    }

    @NonNull public String getPath() {
        return path;
    }

    @NonNull public String getMethodName() {
        return methodName;
    }

    @NonNull public String getRequestType() {
        return requestType;
    }

    @NonNull public HashMap<String, String> getQueryMap() {
        if (queryMap == null) queryMap = new HashMap<>();
        return queryMap;
    }

    public void setQueryMap(HashMap<String, String> queryMap) {
        this.queryMap = queryMap;
    }

    public void addQueryParam(String key, String value) {
        if (!TextUtils.isEmpty(key)) {
            HashMap<String, String> map = this.queryMap;
            map.put(key, value == null ? "" : value);
        }
    }

    @NonNull public HashMap<String, Object> getFormMap() {
        if (formMap == null) formMap = new HashMap<>();
        return formMap;
    }

    public void setFormMap(HashMap<String, Object> formMap) {
        this.formMap = formMap;
    }

    public void addFormParam(String key, Object value) {
        if (!TextUtils.isEmpty(key)) {
            HashMap<String, Object> map = this.formMap;
            map.put(key, value == null ? "" : value);
        }
    }

    public Object getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(Object requestBody) {
        this.requestBody = requestBody;
    }

    public void addHeader(String key, String value) {
        if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
            if (headerBuilder == null) headerBuilder = new Headers.Builder();
            headerBuilder.add(key, value);
        }
    }

    @NonNull public Headers.Builder getHeader() {
        if (headerBuilder == null) headerBuilder = new Headers.Builder();
        return headerBuilder;
    }

    String getUrl() {
        return url;
    }

    private void parseFormBody(@NonNull HashMap<String, Object> formMap, @NonNull Object formBody) throws Exception {
        if (formBody instanceof Map) {
            L.i("QsRequestParams", "methodName:" + methodName + ", FormBody类型为Map，将key和value映射到表单");
            Map dataMap = (Map) formBody;
            for (Object key : dataMap.keySet()) {
                String keyStr = String.valueOf(key);
                String valueStr = String.valueOf(dataMap.get(key));
                if (!TextUtils.isEmpty(keyStr) && !TextUtils.isEmpty(valueStr)) formMap.put(keyStr, valueStr);
            }
        } else if (formBody instanceof String) {
            L.i("QsRequestParams", "methodName:" + methodName + ", FormBody类型为String，尝试解析成Json格式（非Json格式不支持）");
            JSONObject jsonObject = new JSONObject((String) formBody);
            while (jsonObject.keys().hasNext()) {
                String key = jsonObject.keys().next();
                Object value = jsonObject.get(key);
                if (key != null && value != null) {
                    formMap.put(key, String.valueOf(value));
                }
            }
        } else {
            L.i("QsRequestParams", "methodName:" + methodName + ", FormBody类型为Object，尝试通过反射获取表单数据");
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

    private RequestBody createRequestBodyByObject(String mimeType, Object data) {
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

    private RequestBody createRequestBodyByForm(HashMap<String, Object> formMap) {
        boolean isArrString = true;
        for (String key : formMap.keySet()) {
            Object obj = formMap.get(key);
            if (obj instanceof File || obj instanceof byte[]) {
                isArrString = false;
            }
        }
        if (isArrString) {
            okhttp3.FormBody.Builder builder = new okhttp3.FormBody.Builder();
            for (String key : formMap.keySet()) {
                String valueStr = String.valueOf(formMap.get(key));
                if (!TextUtils.isEmpty(key) && valueStr.length() > 0) {
                    builder.add(key, valueStr);
                }
            }
            return builder.build();
        } else {
            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            for (String key : formMap.keySet()) {
                Object value = formMap.get(key);
                if (value instanceof File) {
                    RequestBody rb = RequestBody.create(MediaType.parse("file/*"), (File) value);
                    builder.addFormDataPart(key, String.valueOf(System.nanoTime()), rb);
                } else if (value instanceof byte[]) {
                    RequestBody rb = RequestBody.create(MediaType.parse("file/*"), (byte[]) value);
                    builder.addFormDataPart(key, String.valueOf(System.nanoTime()), rb);
                } else {
                    if (!TextUtils.isEmpty(key)) {
                        builder.addFormDataPart(key, String.valueOf(value));
                    }
                }
            }
            return builder.build();
        }
    }

    private void throwException(String message) throws QsException {
        throw new QsException(QsExceptionType.UNEXPECTED, requestTag, message);
    }

    public Request createRequest() {
        Request.Builder builder = new Request.Builder();
        if (headerBuilder != null) builder.headers(headerBuilder.build());
        if (requestTag != null) builder.tag(requestTag);
        return builder.url(url).method(requestType, httpRequestBody).build();
    }

}
