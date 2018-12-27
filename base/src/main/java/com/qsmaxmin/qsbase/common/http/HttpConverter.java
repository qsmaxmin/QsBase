package com.qsmaxmin.qsbase.common.http;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.qsmaxmin.qsbase.common.log.L;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.RequestBody;


class HttpConverter {

    private static final String TAG = "HttpConverter";

    private final Gson gson;

    HttpConverter() {
        this.gson = new Gson();
    }

    Object jsonToObject(String jsonStr, Type type) {
        return gson.fromJson(jsonStr, type);
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

    String formatJson(String s) {
        if (TextUtils.isEmpty(s)) return null;
        int level = 0;
        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < s.length(); index++) {
            char c = s.charAt(index);
            if (level > 0 && '\n' == builder.charAt(builder.length() - 1)) {
                builder.append(getLevelStr(level));
            }
            switch (c) {
                case '{':
                case '[':
                    builder.append(c).append("\n");
                    level++;
                    break;
                case ',':
                    builder.append(c).append("\n");
                    break;
                case '}':
                case ']':
                    builder.append("\n");
                    level--;
                    builder.append(getLevelStr(level));
                    builder.append(c);
                    break;
                default:
                    builder.append(c);
                    break;
            }
        }
        return builder.toString();
    }

    private String getLevelStr(int level) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < level; i++) {
            builder.append("\t");
        }
        return builder.toString();
    }
}
