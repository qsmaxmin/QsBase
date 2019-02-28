package com.qsmaxmin.qsbase.common.aspect;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/6/29 15:12
 * @Description 请求体注解，POST等请求时将被加入到http请求体中，支持String, byte[], File, Object;
 * 1，mimeType默认是JSON类型，所以当上传的格式非JSON时需要修改成对应的mimeType类型
 * 2，其中Object类型默认解析成Json串并加入请求体中
 */
@Documented
@Target(PARAMETER)
@Retention(RUNTIME)
public @interface Body {
    String mimeType() default "application/json; charset=UTF-8";
}