package com.qsmaxmin.qsbase.common.model;

import com.qsmaxmin.annotation.QsNotProguard;

/**
 * @CreateBy qsmaxmin
 * @Date 2021/1/22 16:01
 * @Description model层超类
 */
public interface QsIModel extends QsNotProguard {

    boolean isResponseOk();

    boolean isLastPage();

    String getMessage();

    long getNetworkTimeLoss();
}
