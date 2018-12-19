package com.qsmaxmin.qsbase.common.http;

import okhttp3.Response;

/**
 * @CreateBy qsmaxmin
 * @Date 2018/12/19 9:10
 * @Description
 */
public class HttpResponse {
     Response    response;
     String      jsonStr;
     HttpBuilder httpBuilder;

    public HttpBuilder getHttpBuilder() {
        return httpBuilder;
    }

    public Response getResponse() {
        return response;
    }

    public String getJsonStr() {
        return jsonStr;
    }

    public void setJsonStr(String jsonStr) {
        this.jsonStr = jsonStr;
    }
}
