package com.qsmaxmin.qsbase.mvp.presenter;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/6/21 16:14
 * @Description Presenter注入必备
 */
@Documented
@Target(TYPE)
@Retention(RUNTIME)
public @interface Presenter {
    Class value();
}
