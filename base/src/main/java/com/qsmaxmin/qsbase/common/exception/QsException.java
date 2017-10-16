package com.qsmaxmin.qsbase.common.exception;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/6/30 13:20
 * @Description
 */

public class QsException extends RuntimeException {
    private final QsExceptionType mType;
    private final Object          requestTag;

    public QsException(QsExceptionType type, Object requestTag, String message) {
        super(message);
        this.mType = type;
        this.requestTag = requestTag;
    }

    public QsExceptionType getExceptionType() {
        return mType;
    }

    public Object getRequestTag() {
        return requestTag;
    }
}
