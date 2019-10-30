package com.qsmaxmin.qsbase.common.viewbind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 点击事件注解.
 * 被注解的方法必须具备以下形式:
 * 1. private 修饰
 * 2. 返回值类型没有要求
 * 3. 参数签名和type的接口要求的参数签名一致.
 * <p>
 * Fragment或Activity初始化时，支持将layoutId()和loadingLayoutId()指向的布局中的控件设置点击事件
 * 由于懒加载机制的原因emptyLayoutId()和errorLayoutId()指向的资源不是立刻加载进来，所以该注解不能绑定这两个布局控件
 * 此时可通过重写onCreateEmptyView()或onCreateErrorView()以达到动态设置的目的
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface OnClick {
    /**
     * 控件的id集合, id小于1时不执行ui事件绑定.
     */
    int[] value();
}
