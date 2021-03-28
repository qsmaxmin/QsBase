package com.qsmaxmin.qsbase.common.http;

import androidx.annotation.NonNull;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @CreateBy qsmaxmin
 * @Date 2021/3/28  12:14 PM
 * @Description
 */
public class Chain {
    private final HttpRequest  httpRequest;
    private final OkHttpClient client;

    Chain(@NonNull HttpRequest httpRequest, @NonNull OkHttpClient client) {
        this.httpRequest = httpRequest;
        this.client = client;
    }

    @NonNull public final HttpRequest getRequest() {
        return httpRequest;
    }

    @NonNull public final Response process() throws Exception {
        return process(httpRequest.createRequest());
    }

    @NonNull public final Response process(Request request) throws Exception {
        return client.newCall(request).execute();
    }
}
