package com.qsmaxmin.qsbase.common.http;

import com.google.gson.Gson;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.utils.StreamCloseUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;


class HttpConverter {

    private static final String TAG = "HttpConverter";

    private final Gson gson;

    HttpConverter() {
        this.gson = new Gson();
    }


    Object jsonFromBody(ResponseBody body, Type type, String methodName) throws IOException {
        if (body == null) return null;
        Charset charset = Charset.forName("UTF-8");
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
                L.i(TAG, "method:" + methodName + " response ok  Json:" + result.toString());
                return gson.fromJson(json, type);
            } finally {
                StreamCloseUtils.close(inputStreamReader, is, bufferedReader);
            }
        }
        return null;
    }

    RequestBody jsonToBody(String mimeType, Object object, Type type) {
        String json = gson.toJson(object, type);
        L.i(TAG, "请求体:mimeType :" + mimeType + ", json : " + json);
        return RequestBody.create(MediaType.parse(mimeType), json);
    }

    RequestBody fileToBody(String mimeType, File file) {
        L.i(TAG, "请求体:mimeType :" + mimeType + ",  file:" + file.getPath());

        return RequestBody.create(MediaType.parse(mimeType), file);
    }

    RequestBody byteToBody(String mimeType, byte[] bytes) {
        L.i(TAG, "请求体:mimeType :" + mimeType + ",  bytes length:" + bytes.length);
        return RequestBody.create(MediaType.parse(mimeType), bytes);
    }
}
