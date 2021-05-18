package com.qsmaxmin.qsbase.common.widget.image;

import java.util.Arrays;

/**
 * @CreateBy qsmaxmin
 * @Date 2021/5/17 16:17
 * @Description
 */
class Coordinate {
    protected final float[] mValues = new float[8];

    void setValuesByEdge(float left, float top, float right, float bottom) {
        mValues[0] = mValues[6] = left;
        mValues[1] = mValues[3] = top;
        mValues[2] = mValues[4] = right;
        mValues[5] = mValues[7] = bottom;
    }

    float[] getValues() {
        return mValues;
    }

    void copyValues(float[] values) {
        System.arraycopy(mValues, 0, values, 0, values.length);
    }

    int pointCount() {
        return 4;
    }

    float getX(int pointIndex) {
        return mValues[pointIndex * 2];
    }

    float getY(int pointIndex) {
        return mValues[pointIndex * 2 + 1];
    }

    float getCenterX() {
        return ((mValues[0] + mValues[2]) * .5f + (mValues[4] + mValues[6]) * .5f) * .5f;
    }

    float getCenterY() {
        return ((mValues[1] + mValues[7]) * .5f + (mValues[3] + mValues[5]) * .5f) * .5f;
    }

    float getLeft() {
        float v0 = Math.min(mValues[0], mValues[2]);
        float v1 = Math.min(mValues[4], mValues[6]);
        return Math.min(v0, v1);
    }

    float getRight() {
        float v0 = Math.max(mValues[0], mValues[2]);
        float v1 = Math.max(mValues[4], mValues[6]);
        return Math.max(v0, v1);
    }

    float getTop() {
        float v0 = Math.min(mValues[1], mValues[3]);
        float v1 = Math.min(mValues[5], mValues[7]);
        return Math.min(v0, v1);
    }

    float getBottom() {
        float v0 = Math.max(mValues[1], mValues[3]);
        float v1 = Math.max(mValues[5], mValues[7]);
        return Math.max(v0, v1);
    }

    float getWidth() {
        float dx = mValues[2] - mValues[0];
        float dy = mValues[3] - mValues[1];
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    float getHeight() {
        float dx = mValues[6] - mValues[0];
        float dy = mValues[7] - mValues[1];
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    float getRectWidth() {
        return getRight() - getLeft();
    }

    float getRectHeight() {
        return getBottom() - getTop();
    }

    boolean contains(Coordinate coordinate) {
        return getLeft() <= coordinate.getLeft()
                && getTop() <= coordinate.getTop()
                && getRight() >= coordinate.getRight()
                && getBottom() >= coordinate.getBottom();
    }

    boolean contains(float x, float y) {
        return getLeft() <= x
                && getTop() <= y
                && getRight() >= x
                && getBottom() >= y;
    }

    boolean matched(Coordinate coordinate) {
        float[] v = coordinate.getValues();
        for (int i = 0; i < v.length; i++) {
            if (v[i] != mValues[i]) return false;
        }
        return true;
    }

    @Override public String toString() {
        return "values:" + Arrays.toString(mValues) + ", outRect:[" + getLeft() + ", " + getTop() + ", " + getRight() + ", " + getBottom() + "]";
    }
}
