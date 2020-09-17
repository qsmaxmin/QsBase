package com.qsmaxmin.qsbase.common.exception;

import com.qsmaxmin.qsbase.common.http.NetworkErrorReceiver;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/6/30 13:20
 * @Description for network exception
 */
public class QsException extends RuntimeException {
    private final Object               requestTag;
    private final NetworkErrorReceiver errorReceiver;

    public QsException(Object requestTag, NetworkErrorReceiver errorReceiver, String message) {
        super(message);
        this.requestTag = requestTag;
        this.errorReceiver = errorReceiver;
    }

    public QsException(Object requestTag, NetworkErrorReceiver errorReceiver, Throwable cause) {
        super(cause);
        this.requestTag = requestTag;
        this.errorReceiver = errorReceiver;
    }

    public Object getRequestTag() {
        return requestTag;
    }

    public NetworkErrorReceiver getErrorReceiver() {
        return errorReceiver;
    }
}
