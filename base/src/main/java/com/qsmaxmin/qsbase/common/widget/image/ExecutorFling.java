package com.qsmaxmin.qsbase.common.widget.image;

import android.widget.Scroller;

/**
 * @CreateBy qsmaxmin
 * @Date 2021/5/18 14:13
 * @Description
 */
class ExecutorFling extends ExecutorBase {
    private final Scroller scroller;
    private final float[]  beginValues;

    ExecutorFling(ImageData data) {
        super(data);
        scroller = new Scroller(getContext());
        beginValues = new float[8];
    }

    @Override public void run() {
        if (!scroller.isFinished() && scroller.computeScrollOffset()) {
            int currX = scroller.getCurrX();
            int currY = scroller.getCurrY();
            float[] current = data.getMatrix().getValues();
            float dx = beginValues[0] + currX - current[0];
            float dy = beginValues[1] + currY - current[1];
            data.getMatrix().postTranslate(dx, dy);
            invalidate();
            postAnimation(this);
        } else {
            onExecuteComplete();
            data.startRecover();
        }
    }

    void fling(int velocityX, int velocityY) {
        scroller.forceFinished(true);
        int minX = 0, maxX = 0, minY = 0, maxY = 0;
        CoordinateVariable current = data.getMatrix().getCurrentCoordinate();
        Coordinate out = data.getMatrix().getViewCoordinate();

        float outLeft = out.getLeft();
        float outTop = out.getTop();
        float outRight = out.getRight();
        float outBottom = out.getBottom();

        float currentLeft = current.getLeft();
        float currentTop = current.getTop();
        float currentRight = current.getRight();
        float currentBottom = current.getBottom();

        if (currentLeft < outLeft && velocityX > 0) {
            maxX = (int) (outLeft - currentLeft);
        } else if (currentRight > outRight && velocityX < 0) {
            minX = (int) (outRight - currentRight);
        }

        if (currentTop < outTop && velocityY > 0) {
            maxY = (int) (outTop - currentTop);
        } else if (currentBottom > outBottom && velocityY < 0) {
            minY = (int) (outBottom - currentBottom);
        }
        if (minX != 0 || maxX != 0 || minY != 0 || maxY != 0) {
            data.getMatrix().copyValues(beginValues);
            scroller.fling(0, 0, velocityX, velocityY, minX, maxX, minY, maxY);
            post(this);
        } else {
            data.startRecover();
        }
    }

    void stopFling() {
        scroller.forceFinished(true);
    }
}
