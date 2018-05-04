package com.qsmaxmin.qsbase.mvp.model;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/6/21 15:19
 * @Description
 */

public class QsConstants {
    private QsConstants() {
    }

    /**
     * View状态布局
     */
    public static final int VIEW_STATE_LOADING = 0;
    public static final int VIEW_STATE_CONTENT = 1;
    public static final int VIEW_STATE_EMPTY   = 2;
    public static final int VIEW_STATE_ERROR   = 3;

    /**
     * 线程名称
     */
    public static final String NAME_HTTP_THREAD   = "HttpThreadPoll";
    public static final String NAME_WORK_THREAD   = "WorkThreadPoll";
    public static final String NAME_SINGLE_THREAD = "SingleThreadPoll";

    /**
     * {@link com.qsmaxmin.qsbase.common.config.QsProperties}config保存数据到file目录下子文件夹名称
     * {@link com.qsmaxmin.qsbase.common.utils.CacheHelper}cache保存数据到file目录下子文件夹名称
     */
    public static final String PARENT_FILE_DIR_NAME = "QsBase";
}
