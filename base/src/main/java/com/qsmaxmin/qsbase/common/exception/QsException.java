package com.qsmaxmin.qsbase.common.exception;

import com.qsmaxmin.qsbase.common.http.NetworkErrorCallback;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/6/30 13:20
 * @Description for network exception
 */
public class QsException extends RuntimeException {
    private final Object               requestTag;
    private final NetworkErrorCallback errorCallback;

    public QsException(Object requestTag, NetworkErrorCallback errorCallback, String message) {
        super(message);
        this.requestTag = requestTag;
        this.errorCallback = errorCallback;
    }

    public QsException(Object requestTag, NetworkErrorCallback errorCallback, Throwable cause) {
        super(cause);
        this.requestTag = requestTag;
        this.errorCallback = errorCallback;
    }

    public Object getRequestTag() {
        return requestTag;
    }

    public NetworkErrorCallback getErrorCallback() {
        return errorCallback;
    }
}
