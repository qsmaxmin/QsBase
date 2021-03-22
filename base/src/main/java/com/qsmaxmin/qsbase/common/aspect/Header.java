package com.qsmaxmin.qsbase.common.aspect;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @CreateBy qsmaxmin
 * @Date 2021/3/22 18:08
 * @Description
 */
@Documented
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface Header {
    String value();
}
