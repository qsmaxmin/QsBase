package com.qsmaxmin.qsbase.common.http;

import android.text.TextUtils;

import com.qsmaxmin.qsbase.common.exception.QsException;
import com.qsmaxmin.qsbase.common.exception.QsExceptionType;

import java.util.HashMap;

import okhttp3.Headers;

/**
 * @CreateBy qsmaxmin
 * @Date 17/7/26  上午12:20
 * @Description
 */

public class HttpBuilder {
    private final String                  methodName;
    private final Object                  requestTag;
    private final int                     requestStyle;
    private final String                  path;
    private final Object[]                args;
    private final String                  requestType;
    private       String                  terminal;
    private       Object                  body;
    private       HashMap<String, Object> formBody;
    private       HashMap<String, String> paramsMap;

    private Headers.Builder headerBuilder = new Headers.Builder();

    HttpBuilder(String methodName, Object requestTag, int requestStyle, String terminal, String path, Object[] args, String requestType, Object body, HashMap<String, Object> formBody, HashMap<String, String> paramsMap) {
        this.methodName = methodName;
        this.requestTag = requestTag;
        this.requestStyle = requestStyle;
        this.terminal = terminal;
        this.path = path;
        this.args = args;
        this.requestType = requestType;
        this.body = body;
        this.formBody = formBody;
        this.paramsMap = paramsMap;
    }

    /**
     * 获取http接口定义的名称
     * isLogOpen=true时返回http接口名，否则返回空字符串
     */
    public String getMethodName() {
        return methodName;
    }

    public HttpBuilder addHeader(String key, String value) {
        if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
            headerBuilder.add(key, value);
        }
        return this;
    }

    public Headers.Builder getHeaderBuilder() {
        return headerBuilder;
    }

    public HttpBuilder setTerminal(String terminal) {
        if (!TextUtils.isEmpty(terminal)) {
            if (terminal.endsWith("/")) {
                terminal = terminal.substring(0, terminal.length() - 1);
            }
            this.terminal = terminal;
        } else {
            throw new QsException(QsExceptionType.UNEXPECTED, requestStyle, "terminal is empty...");
        }
        return this;
    }

    public String getTerminal() {
        return terminal;
    }

    public HttpBuilder addUrlParameters(String key, String value) {
        if (!TextUtils.isEmpty(key)) {
            if (paramsMap == null) paramsMap = new HashMap<>();
            paramsMap.put(key, value == null ? "" : value);
        }
        return this;
    }

    public HttpBuilder setUrlParameters(HashMap<String, String> parameters) {
        paramsMap = parameters;
        return this;
    }

    public HashMap<String, String> getUrlParameters() {
        return paramsMap;
    }

    public String getPath() {
        return path;
    }

    public int getRequestStyle() {
        return requestStyle;
    }

    public Object getRequestTag() {
        return requestTag;
    }

    public Object[] getArgs() {
        return args;
    }

    public String getRequestType() {
        return requestType;
    }

    public Object getBody() {
        return body;
    }

    public HashMap<String, Object> getFormBody() {
        return formBody;
    }

    public HttpBuilder setBody(Object body) {
        this.body = body;
        return this;
    }

    public HttpBuilder setFormBody(HashMap<String, Object> formBody) {
        this.formBody = formBody;
        return this;
    }
}
