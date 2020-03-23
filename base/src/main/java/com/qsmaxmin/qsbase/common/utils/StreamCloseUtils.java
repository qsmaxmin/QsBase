package com.qsmaxmin.qsbase.common.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/6/29 17:43
 * @Description
 */

public class StreamCloseUtils {
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
}
