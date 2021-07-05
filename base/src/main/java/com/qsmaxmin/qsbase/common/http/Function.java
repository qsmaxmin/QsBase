package com.qsmaxmin.qsbase.common.http;

import androidx.annotation.NonNull;

/**
 * @CreateBy qsmaxmin
 * @Date 2021/7/5 15:37
 * @Description
 */
public interface Function<T, D> {
    D apply(@NonNull T t) throws Exception;
}
