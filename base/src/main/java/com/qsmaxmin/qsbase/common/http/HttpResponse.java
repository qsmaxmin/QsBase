package com.qsmaxmin.qsbase.common.http;

import android.support.annotation.NonNull;

import com.qsmaxmin.qsbase.common.exception.QsException;
import com.qsmaxmin.qsbase.common.exception.QsExceptionType;
import com.qsmaxmin.qsbase.common.utils.StreamCloseUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * @CreateBy qsmaxmin
 * @Date 2018/12/19 9:10
 * @Description
 */
public class HttpResponse {
    Response    response;
    HttpBuilder httpBuilder;
    private DecryptionProvider decryptionProvider;

    public HttpBuilder getHttpBuilder() {
        return httpBuilder;
    }

    public Response getResponse() {
        return response;
    }

    /**
     * 向响应体提供解密对象
     */
    public void registerDecriptionProvider(DecryptionProvider provider) {
        this.decryptionProvider = provider;
    }

    String getJsonString() throws Exception {
        if (decryptionProvider != null) {
            ResponseBody body = response.body();
            if (body != null) {
                byte[] decryptionBytes = decryptionProvider.decryption(body.bytes());
                return new String(decryptionBytes, getCharset(body));
            } else {
                return null;
            }
        } else {
            return getJsonFromBody(response.body(), httpBuilder.getRequestTag());
        }
    }

    private String getJsonFromBody(ResponseBody body, Object requestTag) {
        Charset charset = getCharset(body);
        InputStream is = body.byteStream();
        if (is != null) {
            InputStreamReader isr = new InputStreamReader(is, charset);
            BufferedReader br = null;
            try {
                br = new BufferedReader(isr);
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    result.append(line).append("\n");
                }
                return result.toString();
            } catch (IOException e) {
                throw new QsException(QsExceptionType.UNEXPECTED, requestTag, e.getMessage());
            } catch (Exception e) {
                throw new QsException(QsExceptionType.UNEXPECTED, requestTag, e.getMessage());
            } finally {
                StreamCloseUtils.close(isr);
                StreamCloseUtils.close(is);
                StreamCloseUtils.close(br);
            }
        }
        return null;
    }

    @NonNull private Charset getCharset(ResponseBody body) {
        Charset charset = Charset.forName("UTF-8");
        MediaType mediaType = body.contentType();
        if (mediaType != null) {
            Charset c = mediaType.charset(charset);
            if (c != null) charset = c;
        }
        return charset;
    }

    /**
     * 解密提供者
     */
    public interface DecryptionProvider {
        byte[] decryption(byte[] secretBytes);
    }
}
