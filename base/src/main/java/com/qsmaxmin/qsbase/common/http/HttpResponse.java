package com.qsmaxmin.qsbase.common.http;

import okhttp3.Response;

/**
 * @CreateBy qsmaxmin
 * @Date 2018/12/19 9:10
 * @Description
 */
public class HttpResponse {
    Response    response;
    HttpBuilder httpBuilder;
    byte[]      decryptionBytes;

    public HttpBuilder getHttpBuilder() {
        return httpBuilder;
    }

    public Response getResponse() {
        return response;
    }

    public void setDecryptionBytes(byte[] decryptionBytes) {
        this.decryptionBytes = decryptionBytes;
    }
}
