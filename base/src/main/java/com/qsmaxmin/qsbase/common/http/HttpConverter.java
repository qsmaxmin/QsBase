package com.qsmaxmin.qsbase.common.http;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.qsmaxmin.qsbase.common.log.L;

import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

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

    /**
     * 将json格式化输出
     */
    String formatJson(String sourceStr) {
        if (TextUtils.isEmpty(sourceStr)) return null;
        String str = unicodeToCn(sourceStr);
        int level = 0;
        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < str.length(); index++) {
            char c = str.charAt(index);
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

    /**
     * 字符串中，所有以 \\u 开头的UNICODE字符串，全部替换成汉字
     */
    private String unicodeToCn(final String str) {
        String singlePattern = "[0-9|a-f|A-F]";
        String pattern = singlePattern + singlePattern + singlePattern + singlePattern;

        StringBuilder sb = new StringBuilder();
        int length = str.length();
        for (int i = 0; i < length; ) {
            String tmpStr = str.substring(i, (i < length - 6) ? (i + 6) : length);
            if (isStartWithUnicode(pattern, tmpStr)) { // 分支1
                sb.append(unicodeToCnSingle(tmpStr));
                i += 6;
            } else {
                sb.append(str, i, i + 1);
                i++;
            }
        }
        return sb.toString();
    }

    /**
     * 字符串是否以Unicode字符开头。约定Unicode字符以\\u开头。
     */
    private boolean isStartWithUnicode(String pattern, String str) {
        if (TextUtils.isEmpty(str) || !str.startsWith("\\u") || str.length() < 6) {
            return false;
        }
        String content = str.substring(2, 6);
        return Pattern.matches(pattern, content);
    }

    /**
     * 把'\\u'开头的单字转成汉字，如 \\u6B65 ->　步
     */
    private String unicodeToCnSingle(final String str) {
        int code = Integer.decode("0x" + str.substring(2, 6));
        return String.valueOf((char) code);
    }
}
