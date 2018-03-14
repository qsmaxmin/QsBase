package com.qsmaxmin.qsbase.common.aspect;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/6/29 15:12
 * @Description 表单参数注解，支持Map、String（格式：key1=10&key2=1.22&key3=hollow）和Object类型，Object类型会反射获取字段名和值
 */
@Documented
@Target(PARAMETER)
@Retention(RUNTIME)
public @interface FormBody {
}