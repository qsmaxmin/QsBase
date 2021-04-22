package com.qsmaxmin.qsbase.common.model;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/6/29 14:44
 * @Description model层超类的实现类
 */

public class QsModel implements QsIModel {
    public long sentRequestAtMillis;
    public long receivedResponseAtMillis;

    @Override public boolean isResponseOk() {
        return true;
    }

    @Override public boolean isLastPage() {
        return false;
    }

    @Override public String getMessage() {
        return null;
    }

    @Override public long getRequestTimeLoss() {
        return receivedResponseAtMillis - sentRequestAtMillis;
    }
}
