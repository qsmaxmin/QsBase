package com.qsmaxmin.qsbase.common.widget.ptr.header;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.qsmaxmin.qsbase.common.widget.ptr.PtrFrameLayout;
import com.qsmaxmin.qsbase.common.widget.ptr.PtrUIHandler;
import com.qsmaxmin.qsbase.common.widget.ptr.indicator.PtrIndicator;


/**
 * @CreateBy qsmaxmin
 * @Date 16/8/19  上午11:18
 * @Description
 */
public class BeautyCircleRefreshHeader extends RelativeLayout implements PtrUIHandler {

    private int                  headerHeight;
    private ImageView            ivRefresh;
    private BeautyCircleDrawable circleLogoDrawable;

    public BeautyCircleRefreshHeader(Context context) {
        this(context, null);
        initView();
    }

    public BeautyCircleRefreshHeader(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        initView();
    }

    public BeautyCircleRefreshHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        setGravity(Gravity.CENTER);
        addView(getHeaderView());
    }

    private View getHeaderView() {
        float density = getContext().getResources().getDisplayMetrics().density;
        headerHeight = (int) (density * 50);
        if (ivRefresh == null) {
            ivRefresh = new ImageView(getContext());
            ivRefresh.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            circleLogoDrawable = new BeautyCircleDrawable((int) (2 * density));
            ivRefresh.setBackgroundDrawable(circleLogoDrawable);
            LayoutParams imageLayout = new LayoutParams((int) (density * 35), (int) (density * 35));
            imageLayout.addRule(CENTER_IN_PARENT, TRUE);
            ivRefresh.setLayoutParams(imageLayout);
        }
        RelativeLayout headerLayout = new RelativeLayout(getContext());
        LayoutParams layoutParams = new LayoutParams(headerHeight, headerHeight);
        headerLayout.setLayoutParams(layoutParams);
        headerLayout.addView(ivRefresh);
        return headerLayout;
    }

    /**
     * 准备刷新
     */
    @Override public void onUIRefreshPrepare(PtrFrameLayout ptrFrameLayout) {
        if (circleLogoDrawable != null) {
            circleLogoDrawable.onPrepare();
        }
    }

    /**
     * 开始刷新
     */
    @Override public void onUIRefreshBegin(PtrFrameLayout ptrFrameLayout) {
        if (circleLogoDrawable != null) {
            circleLogoDrawable.onBegin();
        }
    }

    /**
     * 刷新完成
     */
    @Override public void onUIRefreshComplete(PtrFrameLayout ptrFrameLayout) {
        if (circleLogoDrawable != null) {
            circleLogoDrawable.onRefreshComplete();
        }
    }

    /**
     * 刷新重置
     */
    @Override public void onUIReset(PtrFrameLayout ptrFrameLayout) {
        if (circleLogoDrawable != null) {
            circleLogoDrawable.onReset();
        }
    }

    /**
     * 当下拉高度高于设定临界点时改变刷新头状态
     */
    @Override public void onUIPositionChange(PtrFrameLayout ptrFrameLayout, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {
        float percent = Math.min(ptrIndicator.getRatioOfHeaderToHeightRefresh(), ptrIndicator.getCurrentPercent());
        if (checkCanAnimation()) {
            circleLogoDrawable.setPercent(percent);
            circleLogoDrawable.setIsReachCriticalPoint(percent == ptrIndicator.getRatioOfHeaderToHeightRefresh());
            ivRefresh.setTranslationY((1f - percent) * headerHeight / 2);
        }
    }

    private boolean checkCanAnimation() {
        return circleLogoDrawable != null && ivRefresh != null;
    }
}
