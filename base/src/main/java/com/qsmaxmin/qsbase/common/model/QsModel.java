package com.qsmaxmin.qsbase.common.model;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/6/29 14:44
 * @Description 网络请求基类
 */

public class QsModel {
    public QsModel() {
    }

    /**
     * http请求是否成功，由子类实现
     */
    public boolean isResponseOk() {
        return true;
    }

    /**
     * 列表分页是否是最后一页，由子类实现
     */
    public boolean isLastPage() {
        return false;
    }

    /**
     * 获取网络请求信息，由子类实现
     */
    public String getMessage() {
        return null;
    }
}
