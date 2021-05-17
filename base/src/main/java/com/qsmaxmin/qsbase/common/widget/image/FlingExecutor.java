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
            data.currentMatrix.set(tempMatrix);
            data.currentMatrix.postTranslate(currX, currY);
            mapWithOriginalRect(data.currentMatrix, data.currentRect);
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
            if (data.currentRect.left < data.cutRect.left && velocityX > 0) {
                maxX = (int) (data.cutRect.left - data.currentRect.left);
            } else if (data.currentRect.right > data.cutRect.right && velocityX < 0) {
                minX = (int) (data.cutRect.right - data.currentRect.right);
            }

            if (data.currentRect.top < data.cutRect.top && velocityY > 0) {
                maxY = (int) (data.cutRect.top - data.currentRect.top);
            } else if (data.currentRect.bottom > data.cutRect.bottom && velocityY < 0) {
                minY = (int) (data.cutRect.bottom - data.currentRect.bottom);
            }
            if (minX != 0 || maxX != 0 || minY != 0 || maxY != 0) {
                isFling = true;
                scroller.fling(0, 0, velocityX, velocityY, minX, maxX, minY, maxY);
                tempMatrix.set(data.currentMatrix);
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
