package com.qsmaxmin.qsbase.common.model;

import com.qsmaxmin.annotation.QsNotProguard;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/6/29 14:44
 * @Description base model
 */

public class QsModel implements QsNotProguard {
    public boolean isResponseOk() {
        return true;
    }

    public boolean isLastPage() {
        return false;
    }

    public String getMessage() {
        return null;
    }
}
