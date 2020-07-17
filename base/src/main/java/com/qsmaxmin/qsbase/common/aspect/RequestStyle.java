package com.qsmaxmin.qsbase.common.aspect;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @CreateBy qsmaxmin
 * @Date 2020-03-02  18:28
 * @Description http接口自定义style
 * 可设置多个style属性
 */
@Documented
@Target(METHOD)
@Retention(RUNTIME)
public @interface RequestStyle {
    int value();
}
