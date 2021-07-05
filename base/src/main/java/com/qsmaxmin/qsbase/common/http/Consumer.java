package com.qsmaxmin.qsbase.common.http;

/**
 * @CreateBy qsmaxmin
 * @Date 2021/7/5 15:45
 * @Description
 */
public interface Consumer<D> {
    void accept(D d) throws Exception;
}
