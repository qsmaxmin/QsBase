package com.qsmaxmin.qsbase.common.widget.image;

import android.graphics.Matrix;

import androidx.annotation.NonNull;

/**
 * @CreateBy qsmaxmin
 * @Date 2021/5/17 18:15
 * @Description 图片绘制转换器
 * 包含图片原始坐标，控件坐标，初始位置坐标，以及图片当前坐标
 */
class TransformMatrix {
    private final Matrix             matrix;
    private final Coordinate         originalCoordinate;
    private final Coordinate         initCoordinate;
    private final Coordinate         viewCoordinate;
    private final CoordinateVariable currentCoordinate;
    private       float              lastAngle;

    TransformMatrix() {
        this.matrix = new Matrix();
        this.originalCoordinate = new Coordinate();
        this.initCoordinate = new Coordinate();
        this.viewCoordinate = new Coordinate();
        this.currentCoordinate = new CoordinateVariable();
    }

    private TransformMatrix(Coordinate originalCoordinate, Coordinate initCoordinate, Coordinate viewCoordinate) {
        this.originalCoordinate = originalCoordinate;
        this.initCoordinate = initCoordinate;
        this.viewCoordinate = viewCoordinate;
        this.matrix = new Matrix();
        this.currentCoordinate = new CoordinateVariable();
    }

    @NonNull @Override public String toString() {
        return "TransformMatrix{" +
                "\nlastAngle=" + lastAngle +
                "\ninitCoordinate=" + initCoordinate +
                "\ncurrentCoordinate=" + currentCoordinate +
                '}';
    }

    void init(int viewWidth, int viewHeight, int bitmapWidth, int bitmapHeight, float left, float top, float right, float bottom) {
        originalCoordinate.setValuesByEdge(0, 0, bitmapWidth, bitmapHeight);
        viewCoordinate.setValuesByEdge(0, 0, viewWidth, viewHeight);
        initCoordinate.setValuesByEdge(left, top, right, bottom);
    }

    boolean hasInit() {
        return viewCoordinate.getWidth() > 0
                && viewCoordinate.getHeight() > 0
                && originalCoordinate.getWidth() > 0
                && originalCoordinate.getHeight() > 0;
    }

    private void updateCoordinate() {
        matrix.mapPoints(currentCoordinate.getValues(), originalCoordinate.getValues());
    }

    void setAngle(float angle) {
        if (angle != lastAngle) {
            postRotate(angle - lastAngle);
            lastAngle = angle;
            updateCoordinate();
        }
    }

    void postRotate(float degrees) {
        postRotate(degrees, viewCoordinate.getCenterX(), viewCoordinate.getCenterY());
    }

    void postRotate(float degrees, float centerX, float centerY) {
        matrix.postRotate(degrees, centerX, centerY);
        updateCoordinate();
    }

    void postTranslate(float dx, float dy) {
        if (dx != 0 || dy != 0) {
            matrix.postTranslate(dx, dy);
            updateCoordinate();
        }
    }

    void postScale(float sx, float sy) {
        postScale(sx, sy, currentCoordinate.getCenterX(), currentCoordinate.getCenterY());
    }

    void postScale(float sx, float sy, float px, float py) {
        if (sx != 1 || sy != 1) {
            matrix.postScale(sx, sy, px, py);
            updateCoordinate();
        }
    }

    void setValues(float[] value) {
        currentCoordinate.setValues(value);
    }

    void reset() {
        lastAngle = 0;
        currentCoordinate.setValues(initCoordinate.getValues());
        updateMatrix();
    }

    float[] getValues() {
        return currentCoordinate.getValues();
    }

    void copyValues(float[] values) {
        currentCoordinate.copyValues(values);
    }

    Matrix updateMatrix() {
        matrix.setPolyToPoly(originalCoordinate.getValues(), 0, currentCoordinate.getValues(), 0, currentCoordinate.pointCount());
        return matrix;
    }

    TransformMatrix copy() {
        TransformMatrix m = new TransformMatrix(originalCoordinate, initCoordinate, viewCoordinate);
        m.currentCoordinate.setValues(currentCoordinate.getValues());
        m.matrix.set(matrix);
        return m;
    }

    Coordinate getInitCoordinate() {
        return initCoordinate;
    }

    Coordinate getViewCoordinate() {
        return viewCoordinate;
    }

    CoordinateVariable getCurrentCoordinate() {
        return currentCoordinate;
    }

    Coordinate getOriginalCoordinate() {
        return originalCoordinate;
    }

    float getScale() {
        return currentCoordinate.getWidth() / initCoordinate.getWidth();
    }

    float calculateTouchProgress() {
        float ratio = (currentCoordinate.getTop() - initCoordinate.getTop()) / initCoordinate.getHeight();
        if (ratio < 0f) ratio = 0f;
        else if (ratio > 1f) ratio = 1f;
        return ratio;
    }

    boolean hasBeenReset() {
        return currentCoordinate.matched(initCoordinate);
    }

    boolean canFling() {
        return currentCoordinate.contains(viewCoordinate);
    }

    boolean contains(float x, float y) {
        return currentCoordinate.contains(x, y);
    }

    boolean canTouchScaleDown() {
        return currentCoordinate.getTop() > initCoordinate.getTop();
    }
}
