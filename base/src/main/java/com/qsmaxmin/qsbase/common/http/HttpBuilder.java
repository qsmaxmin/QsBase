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

    private final Object                  requestTag;
    private final String                  path;
    private final Object[]                args;
    private final String                  requestType;
    private       String                  terminal;
    private       HashMap<String, String> urlParameters;

    private Headers.Builder headerBuilder = new Headers.Builder();


    HttpBuilder(Object requestTag, String path, Object[] args, String requestType) {
        this.requestTag = requestTag;
        this.path = path;
        this.args = args;
        this.requestType = requestType;
    }

    public HttpBuilder addHeader(String key, String value) {
        if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
            headerBuilder.add(key, value);
        }
        return this;
    }

    Headers.Builder getHeaderBuilder() {
        return headerBuilder;
    }

    public HttpBuilder setTerminal(String terminal) {
        if (!TextUtils.isEmpty(terminal)) {
            if (terminal.endsWith("/")) {
                terminal = terminal.substring(0, terminal.length() - 1);
            }
            this.terminal = terminal;
        } else {
            throw new QsException(QsExceptionType.UNEXPECTED, requestTag, "terminal is empty...");
        }
        return this;
    }

    String getTerminal() {
        return terminal;
    }

    public HttpBuilder addUrlParameters(String key, String value) {
        if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
            if (urlParameters == null) {
                synchronized (HttpBuilder.class) {
                    if (urlParameters == null) urlParameters = new HashMap<>();
                }
            }
            urlParameters.put(key, value);
        }
        return this;
    }

    HashMap<String, String> getUrlParameters() {
        return urlParameters;
    }

    public String getPath() {
        return path;
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

}
