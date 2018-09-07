package com.qsmaxmin.qsbase.common.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/6/20 16:40
 * @Description 线程切换必备
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface ThreadPoint {
    ThreadType value();
}
