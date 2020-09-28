package com.qsmaxmin.qsbase.common.exception;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/6/30 13:20
 * @Description for network exception
 */
public class QsException extends Exception {
    private final Object requestTag;

    public QsException(Object requestTag, String message) {
        super(message);
        this.requestTag = requestTag;
    }

    public QsException(Object requestTag, Throwable cause) {
        super(cause);
        this.requestTag = requestTag;
    }

    public Object getRequestTag() {
        return requestTag;
    }
}
