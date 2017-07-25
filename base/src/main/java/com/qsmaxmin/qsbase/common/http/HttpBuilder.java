package com.qsmaxmin.qsbase.common.http;

import android.text.TextUtils;

import com.qsmaxmin.qsbase.common.exception.QsException;
import com.qsmaxmin.qsbase.common.exception.QsExceptionType;

import okhttp3.Headers;

/**
 * @CreateBy qsmaxmin
 * @Date 17/7/26  上午12:20
 * @Description
 */

public class HttpBuilder {

    private String terminal;

    private Headers.Builder headerBuilder = new Headers.Builder();

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
            throw new QsException(QsExceptionType.UNEXPECTED, "terminal is empty...");
        }
        return this;
    }

    String getTerminal() {
        return terminal;
    }
}
