package com.qsmaxmin.qsbase.common.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/7/3 10:07
 * @Description 权限申请必备
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface Permission {
    String value();
}
