package com.qsmaxmin.qsbase.common.viewbind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 事件注解.
 * 被注解的方法必须具备以下形式:
 * 1. private 修饰
 * 2. 返回值类型没有要求
 * 3. 参数签名和type的接口要求的参数签名一致.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OnClick {
    /**
     * 控件的id集合, id小于1时不执行ui事件绑定.
     */
    int[] value();
}
