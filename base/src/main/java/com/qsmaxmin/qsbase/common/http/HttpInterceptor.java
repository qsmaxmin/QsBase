package com.qsmaxmin.qsbase.common.http;

import androidx.annotation.NonNull;
import okhttp3.Response;

/**
 * @CreateBy qsmaxmin
 * @Date 2021/3/28  12:20 PM
 * @Description http请求拦截器
 */
public interface HttpInterceptor {

    @NonNull Response onIntercept(@NonNull Chain chain) throws Exception;
}
