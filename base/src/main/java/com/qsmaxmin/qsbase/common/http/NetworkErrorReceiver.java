package com.qsmaxmin.qsbase.common.http;

import androidx.annotation.NonNull;

/**
 * @CreateBy qsmaxmin
 * @Date 2020/9/16 18:09
 * @Description for network request
 */
public interface NetworkErrorReceiver {
    void methodError(@NonNull Throwable t);
}
