package com.qsmaxmin.qsbase.common.http;

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
    void processParams(HttpRequest request) throws Exception;

    /**
     * step 2, onHttpResponse
     *
     * @throws Exception do not catch
     */
    void onHttpResponse(HttpRequest request, HttpResponse response) throws Exception;

    /**
     * step 3, onHttpComplete
     *
     * @throws Exception do not catch
     */
    void onHttpComplete(HttpRequest builder, Object result) throws Exception;
}
