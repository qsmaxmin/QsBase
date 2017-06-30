package com.qsmaxmin.qsbase.common.exception;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/6/30 13:20
 * @Description
 */

public class QsException extends RuntimeException {
    private final QsExceptionType mType;

    public QsException(QsExceptionType type, String message) {
        super(message);
        this.mType = type;
    }

    public QsExceptionType getExceptionType() {
        return mType;
    }
}
