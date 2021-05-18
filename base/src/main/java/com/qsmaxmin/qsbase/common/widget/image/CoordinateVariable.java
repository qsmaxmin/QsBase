package com.qsmaxmin.qsbase.common.widget.image;

/**
 * @CreateBy qsmaxmin
 * @Date 2021/5/18 9:34
 * @Description
 */
class CoordinateVariable extends Coordinate {
    void setValues(float[] values) {
        System.arraycopy(values, 0, mValues, 0, mValues.length);
    }
}
