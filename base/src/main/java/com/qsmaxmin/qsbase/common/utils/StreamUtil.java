package com.qsmaxmin.qsbase.common.utils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/6/29 17:43
 * @Description
 */

public class StreamUtil {
    /**
     * @see #close(Closeable) recommend
     * @deprecated unsafe, not recommend
     */
    public static void close(Closeable... closeable) {
        if (closeable != null) {
            for (Closeable able : closeable) {
                close(able);
            }
        }
    }

    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void copyStream(InputStream is, OutputStream os) throws IOException {
        copyStream(is, os, new byte[1024]);
    }

    public static void copyStream(InputStream is, OutputStream os, byte[] buff) throws IOException {
        int len;
        while ((len = is.read(buff)) != -1) {
            os.write(buff, 0, len);
        }
    }
}
