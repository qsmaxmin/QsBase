package com.qsmaxmin.qsbase.common.aspect;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @CreateBy qsmaxmin
 * @Date 2020-03-31  15:23
 * @Description 自定义okHttp的RequestBody对象
 */
@Documented
@Target(PARAMETER)
@Retention(RUNTIME)
public @interface RequestBody {
}
