package com.qsmaxmin.qsbase.common.aspect;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/8/11 18:23
 * @Description 参数占位替代
 */
@Documented
@Target(PARAMETER)
@Retention(RUNTIME)
public @interface Path {
}
