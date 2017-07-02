package com.qsmaxmin.qsbase.common.model;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/5/8 15:01
 * @Description 模型适配，对泛型model进行封装，添加自定义属性以便于扩展而不污染原model
 * 推荐使用：{@link com.qsmaxmin.qsbase.common.utils.ModelTransformHelper}获取和转换
 */

public class QsModelListAdapter<T> {
    public T model;

    public int     viewType;//item类型，用于复杂listView
    public boolean isChecked;//是否选中，本地字体列表管理页面使用
    public boolean enableEditMode;//是否激活编辑模式，带checkBox

    /**
     * 适配器比较唯一性就是model的唯一性
     * 最终比较的就是这两个model的fontId
     * 适用于List的元素包含判断等操作
     */
    @Override public boolean equals(Object o) {
        return o != null && o instanceof QsModelListAdapter && model != null && model.equals(((QsModelListAdapter) o).model);
    }
}
