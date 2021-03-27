package com.qsmaxmin.qsbase.common.http;

import com.google.gson.Gson;
import com.qsmaxmin.qsbase.common.log.L;

import androidx.annotation.NonNull;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * @CreateBy qsmaxmin
 * @Date 2018/12/19 9:10
 * @Description
 */
public class HttpResponse {
    @NonNull private final Response           response;
    @NonNull private final Gson               gson;
    @NonNull private final HttpRequest        request;
    private                DecryptionProvider decryptionProvider;

    public HttpResponse(@NonNull Response response, @NonNull HttpRequest request, @NonNull Gson gson) {
        this.response = response;
        this.request = request;
        this.gson = gson;
    }

    public Response getResponse() {
        return response;
    }

    public void registerDecryptionProvider(DecryptionProvider provider) {
        this.decryptionProvider = provider;
    }

    Object getResponseObject() throws Exception {
        Class<?> returnType = request.getReturnType();
        if (returnType == Response.class) {
            if (L.isEnable()) {
                L.i("HttpAdapter", "method:" + request.getMethodName() + ", url:" + request.getUrl() + "\nbody:returnType is Response, so can not print response body!!!");
            }
            return response;
        }

        ResponseBody body = response.body();
        if (body == null) {
            if (L.isEnable()) {
                L.i("HttpAdapter", "method:" + request.getMethodName() + ", url:" + request.getUrl() + "\nbody:null");
            }
            return null;
        }

        if (returnType == void.class) {
            if (L.isEnable()) {
                L.i("HttpAdapter", "method:" + request.getMethodName() + ", url:" + request.getUrl() + "\nbody:" + format(body.bytes()));
            }
            return null;
        }

        if (L.isEnable()) {
            byte[] bytes = body.bytes();
            if (decryptionProvider != null) bytes = decryptionProvider.decryption(bytes);
            L.i("HttpAdapter", "method:" + request.getMethodName() + ", url:" + request.getUrl() + "\nbody:" + format(bytes));
            return returnType == byte[].class ? bytes : gson.fromJson(new String(bytes), returnType);

        } else {
            if (decryptionProvider != null) {
                byte[] bytes = decryptionProvider.decryption(body.bytes());
                return returnType == byte[].class ? bytes : gson.fromJson(new String(bytes), returnType);
            } else {
                return returnType == byte[].class ? body.bytes() : gson.fromJson(body.charStream(), returnType);
            }
        }
    }

    /**
     * decrypt provider
     */
    public interface DecryptionProvider {
        byte[] decryption(byte[] secretBytes);
    }

    private String format(byte[] bytes) {
        return bytes == null || bytes.length == 0 ? null : format(new String(bytes));
    }

    private String format(String text) {
        return HttpHelper.getInstance().getConverter().formatJson(text);
    }
}
