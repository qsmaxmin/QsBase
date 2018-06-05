package com.qsmaxmin.qsbase.common.http;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.qsmaxmin.qsbase.common.exception.QsException;
import com.qsmaxmin.qsbase.common.exception.QsExceptionType;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.utils.StreamCloseUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;


class HttpConverter {

    private static final String TAG = "HttpConverter";

    private final Gson gson;

    HttpConverter() {
        this.gson = new Gson();
    }


    Object jsonFromBody(ResponseBody body, Type type, String methodName, Object requestTag) throws IOException {
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
                L.i(TAG, "methodName:" + methodName + "  响应体 Json:" + result.toString());
                return gson.fromJson(json, type);
            } catch (JsonSyntaxException e1) {
                throw new QsException(QsExceptionType.UNEXPECTED, requestTag, "数据解析错误");
            } catch (JsonIOException e2) {
                throw new QsException(QsExceptionType.UNEXPECTED, requestTag, "Json IO 错误");
            } finally {
                StreamCloseUtils.close(inputStreamReader, is, bufferedReader);
            }
        }
        return null;
    }

    RequestBody stringToBody(String methodName, String mimeType, String body) {
        L.i(TAG, "methodName:" + methodName + "  请求体 mimeType:" + mimeType + ", String:" + body);
        return RequestBody.create(MediaType.parse(mimeType), body);
    }

    RequestBody jsonToBody(String methodName, String mimeType, Object object, Type type) {
        String json = gson.toJson(object, type);
        L.i(TAG, "methodName:" + methodName + "  请求体 mimeType:" + mimeType + ", Json : " + json);
        return RequestBody.create(MediaType.parse(mimeType), json);
    }

    RequestBody fileToBody(String methodName, String mimeType, File file) {
        L.i(TAG, "methodName:" + methodName + "  请求体 mimeType:" + mimeType + ", File:" + file.getPath());
        return RequestBody.create(MediaType.parse(mimeType), file);
    }

    RequestBody byteToBody(String methodName, String mimeType, byte[] bytes) {
        L.i(TAG, "methodName:" + methodName + "  请求体 mimeType:" + mimeType + ", bytes length:" + bytes.length);
        return RequestBody.create(MediaType.parse(mimeType), bytes);
    }

    RequestBody stringToFormBody(String methodName, Object formBody) {
        L.i(TAG, "methodName:" + methodName + "  提交表单:" + formBody.getClass().getSimpleName());
        FormBody.Builder builder = new FormBody.Builder();
        if (formBody instanceof Map) {
            Map dataMap = (Map) formBody;
            for (Object key : dataMap.keySet()) {
                String keyStr = String.valueOf(key);
                String valueStr = String.valueOf(dataMap.get(key));
                if (!TextUtils.isEmpty(keyStr) && !TextUtils.isEmpty(valueStr)) builder.add(keyStr, valueStr);
            }
        } else if (formBody instanceof String) {
            String formStr = (String) formBody;
            String[] paramArr = formStr.split("&");
            for (String param : paramArr) {
                if (!TextUtils.isEmpty(param)) {
                    String[] keyValue = param.split("=");
                    if (keyValue.length == 2 && !TextUtils.isEmpty(keyValue[0]) && !TextUtils.isEmpty(keyValue[1])) {
                        builder.add(keyValue[0], keyValue[1]);
                    }
                }
            }
        } else {
            Field[] fieldArr = formBody.getClass().getFields();
            if (fieldArr != null && fieldArr.length > 0) {
                try {
                    for (Field field : fieldArr) {
                        Object value = field.get(formBody);
                        if (value != null) {
                            builder.add(field.getName(), String.valueOf(value));
                        }
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return builder.build();
    }
}
