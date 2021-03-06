package com.qsmaxmin.qsbase.common.aspect;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/6/29 15:12
 * @Description form param, same as{@link Field}
 * @deprecated
 */
@Documented
@Target(PARAMETER)
@Retention(RUNTIME)
public @interface FormParam {
    String value();
}