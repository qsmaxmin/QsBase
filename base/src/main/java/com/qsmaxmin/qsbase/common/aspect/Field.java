package com.qsmaxmin.qsbase.common.aspect;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @CreateBy qsmaxmin
 * @Date 2021/3/22 13:12
 * @Description form param, same as{@link FormParam}
 */
@Documented
@Target(PARAMETER)
@Retention(RUNTIME)
public @interface Field {
    String value();
}


