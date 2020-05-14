package com.qsmaxmin.qsbase.common.ann;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @CreateBy administrator
 * @Date 2020/5/14 12:47
 * @Description 有了该注解，就能使APT生效
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface QsAnn {
    /**
     * 当前项目是否是library
     */
    boolean isLibrary() default false;
}
