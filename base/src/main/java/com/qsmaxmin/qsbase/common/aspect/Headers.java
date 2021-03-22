package com.qsmaxmin.qsbase.common.aspect;

import com.qsmaxmin.qsbase.common.http.HttpRequest;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @CreateBy qsmaxmin
 * @Date 2021/3/22 13:37
 * @Description 自定义Header, key和value以冒号隔开，例如：{header1:value1, header2:value2, header3:value3}
 * 公共请求头请在QsHttpCallback的processParams回调中设置
 * @see com.qsmaxmin.qsbase.common.http.QsHttpCallback#processParams(HttpRequest)
 */
@Documented
@Target(METHOD)
@Retention(RUNTIME)
public @interface Headers {
    String[] value();
}
