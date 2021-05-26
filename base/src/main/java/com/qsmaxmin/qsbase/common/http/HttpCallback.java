package com.qsmaxmin.qsbase.common.http;

import androidx.annotation.Nullable;

/**
 * @CreateBy qsmaxmin
 * @Date 2021/5/26 11:13
 * @Description
 */
public interface HttpCallback<D> {
    void onSuccess(@Nullable D resp);

    void onFailed(Throwable t);
}
