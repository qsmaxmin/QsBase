package com.qsmaxmin.qsbase.common.http;

import android.text.TextUtils;

import java.util.regex.Pattern;


public class JsonPrintFormatter {
    private final String pattern;

    public JsonPrintFormatter() {
        String singlePattern = "[0-9|a-f|A-F]";
        pattern = singlePattern + singlePattern + singlePattern + singlePattern;
    }

    public String formatJson(String sourceStr) {
        if (TextUtils.isEmpty(sourceStr)) return sourceStr;
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

    private String unicodeToCn(final String str) {
        StringBuilder sb = new StringBuilder();
        int length = str.length();
        for (int i = 0; i < length; ) {
            String tmpStr = str.substring(i, (i < length - 6) ? (i + 6) : length);
            if (isStartWithUnicode(tmpStr)) { // 分支1
                sb.append(unicodeToCnSingle(tmpStr));
                i += 6;
            } else {
                sb.append(str, i, i + 1);
                i++;
            }
        }
        return sb.toString();
    }

    private boolean isStartWithUnicode(String str) {
        if (TextUtils.isEmpty(str) || !str.startsWith("\\u") || str.length() < 6) {
            return false;
        }
        String content = str.substring(2, 6);
        return Pattern.matches(pattern, content);
    }

    private String unicodeToCnSingle(final String str) {
        int code = Integer.decode("0x" + str.substring(2, 6));
        return new String(new int[]{code}, 0, 1);
    }
}
