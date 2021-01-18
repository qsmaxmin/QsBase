package com.qsmaxmin.qsbase.common.http;

import com.qsmaxmin.qsbase.common.exception.QsException;

import androidx.annotation.NonNull;

/**
 * @CreateBy qsmaxmin
 * @Date 2020/9/16 18:09
 * @Description for network request
 * @see HttpHelper#create(Class, Object, NetworkErrorReceiver)
 */
public interface NetworkErrorReceiver {
    void methodError(@NonNull QsException e);
}
