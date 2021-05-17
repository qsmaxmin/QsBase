package com.qsmaxmin.qsbase.common.widget.image;

import android.view.ScaleGestureDetector;

/**
 * @CreateBy qsmaxmin
 * @Date 2021/5/17 10:47
 * @Description
 */
class OnScaleGestureListenerImpl implements ScaleGestureDetector.OnScaleGestureListener {
    private final ImageData data;

    OnScaleGestureListenerImpl(ImageData data) {
        this.data = data;
    }

    @Override public boolean onScale(ScaleGestureDetector detector) {
        float scaleFactor = detector.getScaleFactor();
        data.getCurrentMatrix().postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
        data.mapCurrentRect();
        data.invalidate();
        return true;
    }

    @Override public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override public void onScaleEnd(ScaleGestureDetector detector) {
        data.startRecover();
    }
}
