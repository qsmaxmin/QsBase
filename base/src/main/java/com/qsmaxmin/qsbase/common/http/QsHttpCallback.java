package com.qsmaxmin.qsbase.common.http;

/**
 * @CreateBy qsmaxmin
 * @Date 2019/3/5 17:13
 * @Description http 公共回调，用于http请求公共逻辑处理
 */
public interface QsHttpCallback {
    /**
     * 可以在该回调里处理http请求前的参数
     *
     * @param builder http请求参数封装
     *                {@link HttpBuilder#setTerminal(String)}设置主机地址
     *                {@link HttpBuilder#getPath()}获取路径
     *                {@link HttpBuilder#getHeaderBuilder()} ()}获取header
     *                {@link HttpBuilder#getTerminal()} ()}获取主机地址
     *                {@link HttpBuilder#getUrlParameters()} ()}获取url参数
     *                {@link HttpBuilder#getRequestTag()} ()} ()}获取请求tag
     *                {@link HttpBuilder#getBody()} ()} ()}获取请求体对象
     *                {@link HttpBuilder#getFormBody()} ()} ()}获取请求表单对象
     *                ......
     * @throws Exception 有异常别抓，直接throw掉
     */
    void initHttpAdapter(HttpBuilder builder) throws Exception;

    /**
     * http response 回调后立刻执行该函数，可以处理一些譬如加解密的逻辑
     *
     * @param response http 响应体封装
     * @throws Exception 有异常别抓，直接throw掉
     */
    void onHttpResponse(HttpResponse response) throws Exception;

    /**
     * 响应体映射成实体后的回调，根据响应体类型可以处理一些公共逻辑
     *
     * @param result 响应体映射成的实体
     */
    void onResult(Object result) throws Exception;
}
