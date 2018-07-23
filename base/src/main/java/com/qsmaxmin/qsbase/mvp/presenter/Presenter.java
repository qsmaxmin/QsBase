package com.qsmaxmin.qsbase.mvp.presenter;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @CreateBy qsmaxmin
 * @Date 2018/7/23 10:18
 * @Description
 */
@Documented
@Target(TYPE)
@Retention(RUNTIME)
public @interface Presenter {
    Class value();
}
