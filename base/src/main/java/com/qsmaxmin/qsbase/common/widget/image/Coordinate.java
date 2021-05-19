package com.qsmaxmin.qsbase.common.widget.image;

import android.graphics.RectF;

import java.util.Arrays;

import androidx.annotation.NonNull;

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

    float getCenterX() {
        return getCenterX(mValues);
    }

    float getCenterY() {
        return getCenterY(mValues);
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
        return getWidth(mValues);
    }

    float getHeight() {
        return getHeight(mValues);
    }

    static float getCenterX(float[] coordinate) {
        return ((coordinate[0] + coordinate[2]) * .5f + (coordinate[4] + coordinate[6]) * .5f) * .5f;
    }

    static float getCenterY(float[] coordinate) {
        return ((coordinate[1] + coordinate[7]) * .5f + (coordinate[3] + coordinate[5]) * .5f) * .5f;
    }

    static float getHeight(float[] coordinate) {
        float dx = coordinate[6] - coordinate[0];
        float dy = coordinate[7] - coordinate[1];
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    static float getWidth(float[] coordinate) {
        float dx = coordinate[2] - coordinate[0];
        float dy = coordinate[3] - coordinate[1];
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    static float[] getCoordinate(RectF rectF) {
        float dx = rectF.width() / 2f;
        float dy = rectF.height() / 2f;
        float cx = rectF.centerX();
        float cy = rectF.centerY();
        float[] pos = new float[8];
        pos[0] = pos[6] = cx - dx;
        pos[1] = pos[3] = cy - dy;
        pos[2] = pos[4] = cx + dx;
        pos[5] = pos[7] = cy + dy;
        return pos;
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

    @NonNull @Override public String toString() {
        return "values:" + Arrays.toString(mValues) + ", outRect:[" + getLeft() + ", " + getTop() + ", " + getRight() + ", " + getBottom() + "]";
    }
}
