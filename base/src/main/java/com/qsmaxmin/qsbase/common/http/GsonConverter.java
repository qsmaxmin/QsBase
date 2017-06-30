package com.qsmaxmin.qsbase.common.http;

import com.google.gson.Gson;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.utils.StreamCloseUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;


class GsonConverter {

    private static final String TAG = "GsonConverter";

    private final Gson      gson;
    private final Charset   charset;
    private final MediaType mediaType;

    GsonConverter() {
        this(new Gson());
    }

    private GsonConverter(Gson gson) {
        this(gson, Charset.forName("UTF-8"));
    }

    private GsonConverter(Gson gson, Charset charset) {
        if (gson == null) throw new NullPointerException("gson == null");
        if (charset == null) throw new NullPointerException("charset == null");
        this.gson = gson;
        this.charset = charset;
        this.mediaType = MediaType.parse("application/json; charset=" + charset.name());
    }

    Object fromBody(ResponseBody body, Type type) throws IOException {
        if (body == null) return null;
        Charset charset = this.charset;
        MediaType mediaType = body.contentType();
        if (mediaType != null) {
            charset = mediaType.charset(charset);
        }
        InputStream is = body.byteStream();
        if (is != null && charset != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(is, charset);
            BufferedReader bufferedReader = null;
            try {
                bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    result.append(line).append("\n");
                }
                String json = result.toString();
                L.i(TAG, result.toString());
                return gson.fromJson(json, type);
            } finally {
                StreamCloseUtils.close(inputStreamReader, is, bufferedReader);
            }
        }
        return null;
    }

    RequestBody toBody(Object object, Type type) {
        String json = gson.toJson(object, type);
        L.i(TAG, "请求体:mediaType :" + mediaType + ", json : " + json);
        return RequestBody.create(mediaType, json);
    }
}
