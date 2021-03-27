package com.qsmaxmin.qsbase.common.http;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @CreateBy qsmaxmin
 * @Date 2019/3/5 17:13
 * @Description http common callback
 */
public interface QsHttpCallback {
    /**
     * step 1, processParams
     *
     * @throws Exception do not catch
     */
    void processParams(@NonNull HttpRequest request) throws Exception;

    /**
     * step 2, onHttpResponse
     *
     * @throws Exception do not catch
     */
    void onHttpResponse(@NonNull HttpRequest request, @Nullable HttpResponse response) throws Exception;

    /**
     * step 3, onHttpComplete
     *
     * @throws Exception do not catch
     */
    void onHttpComplete(@NonNull HttpRequest request, @Nullable Object result) throws Exception;
}
