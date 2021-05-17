package com.qsmaxmin.qsbase.common.widget.image;

import android.graphics.Matrix;
import android.widget.Scroller;

import androidx.annotation.NonNull;

/**
 * @CreateBy qsmaxmin
 * @Date 2021/5/14 14:04
 * @Description
 */
public class FlingExecutor extends BaseExecutor {
    private final Scroller scroller;
    private final Matrix   tempMatrix;
    private       boolean  isFling;

    /**
     * @param data 数据实体
     */
    FlingExecutor(@NonNull ImageData data) {
        super(data);
        scroller = new Scroller(getContext());
        tempMatrix = new Matrix();
    }

    @Override public void run() {
        if (!scroller.isFinished() && scroller.computeScrollOffset()) {
            int currX = scroller.getCurrX();
            int currY = scroller.getCurrY();
            data.getCurrentMatrix().set(tempMatrix);
            data.getCurrentMatrix().postTranslate(currX, currY);
            mapCurrentRect();
            invalidate();
            postAnimation(this);
        } else {
            isFling = false;
            data.startRecover();
        }
    }

    void fling(int velocityX, int velocityY) {
        scroller.forceFinished(true);
        int minX = 0, maxX = 0, minY = 0, maxY = 0;
        if (data.getCurrentRect().left < data.getCutRect().left && velocityX > 0) {
            maxX = (int) (data.getCutRect().left - data.getCurrentRect().left);
        } else if (data.getCurrentRect().right > data.getCutRect().right && velocityX < 0) {
            minX = (int) (data.getCutRect().right - data.getCurrentRect().right);
        }

        if (data.getCurrentRect().top < data.getCutRect().top && velocityY > 0) {
            maxY = (int) (data.getCutRect().top - data.getCurrentRect().top);
        } else if (data.getCurrentRect().bottom > data.getCutRect().bottom && velocityY < 0) {
            minY = (int) (data.getCutRect().bottom - data.getCurrentRect().bottom);
        }
        if (minX != 0 || maxX != 0 || minY != 0 || maxY != 0) {
            isFling = true;
            scroller.fling(0, 0, velocityX, velocityY, minX, maxX, minY, maxY);
            tempMatrix.set(data.getCurrentMatrix());
            postAnimation(this);
        } else {
            data.startRecover();
        }
    }

    void stopFling() {
        scroller.forceFinished(true);
    }

    boolean isFling() {
        return isFling;
    }
}
