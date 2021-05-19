package com.qsmaxmin.qsbase.common.widget.image;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.common.log.L;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Retention;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * @CreateBy qsmaxmin
 * @Date 2021/5/14 13:19
 * @Description 可预览图片和裁切图片的ImageView
 */
public class FunctionImageView extends AppCompatImageView {
    public static final int       FUNCTION_PREVIEW = 0;
    public static final int       FUNCTION_CROP    = 1;
    private             ImageData data;

    public FunctionImageView(Context context) {
        super(context);
        init(null);
    }

    public FunctionImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public FunctionImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        int functionMode = FUNCTION_PREVIEW;
        boolean enableTouchScaleDown = true;
        if (attrs != null) {
            TypedArray typedArray = getResources().obtainAttributes(attrs, R.styleable.FunctionImageView);
            functionMode = typedArray.getInt(R.styleable.FunctionImageView_fiv_function, FUNCTION_PREVIEW);
            enableTouchScaleDown = typedArray.getBoolean(R.styleable.FunctionImageView_fiv_enableTouchScaleDown, true);
            typedArray.recycle();
        }
        initData();
        data.setFunction(functionMode);
        data.setEnableTouchScaleDown(enableTouchScaleDown);
    }

    private void initData() {
        if (data == null) {
            data = new ImageData(this);
        }
    }

    /**
     * 设置旋转角度
     *
     * @param end 如果为true则旋转完图片后检测图片大小及边界，若大小或边界不符合要求则缩放或移动图片
     */
    public void setAngle(float angle, boolean end) {
        data.setAngle(angle);
        if (end) data.startRecover("setAngle");
    }

    /**
     * 重置旋转，缩放，平移到初始状态
     */
    public void reset() {
        data.startReset(true);
    }

    public void reset(boolean anim) {
        data.startReset(anim);
    }

    /**
     * 设置功能模式，必须在加载图片前设置该参数
     * 0：预览模式
     * 1：裁切模式
     */
    public void setFunction(@Function int functionMode) {
        data.setFunction(functionMode);
    }

    public int getFunction() {
        return data.getFunction();
    }

    /**
     * 预览模式时，当手指向下滑动到一定位置时，是否进行缩放显示
     * 必须在加载图片前设置该参数
     */
    public void setEnableTouchScaleDown(boolean enable) {
        data.setEnableTouchScaleDown(enable);
    }

    /**
     * 手势触发的回调
     */
    public void setGestureListener(GestureListener listener) {
        data.setGestureListener(listener);
    }

    /**
     * 非手势触发的轮廓变换时的回调，transformTo和setInitTransformFrom方法会触发该回调
     *
     * @see #transformTo(RectF, boolean)
     * @see #setInitTransformFrom(RectF)
     */
    public void setTransformListener(OnTransformListener listener) {
        data.setTransformListener(listener);
    }

    /**
     * 将当前图片的外轮廓变成指定矩形的外轮廓
     *
     * @param rectF 指定外轮廓
     * @param anim  是否开启补间动画
     */
    public void transformTo(RectF rectF, boolean anim) {
        data.transformTo(rectF, anim);
    }

    /**
     * 将当前图片的外轮廓变成指定矩形的外轮廓
     *
     * @param coordinate 指定外轮廓4点角坐标
     * @param anim       是否开启补间动画
     */
    public void transformTo(float[] coordinate, boolean anim) {
        if (coordinate != null && coordinate.length == 8) {
            data.transformTo(coordinate, anim);
        }
    }

    /**
     * 图片第一次显示出来时，图片轮廓变换的开始位置
     *
     * @param rectF 变换开始位置的图片外轮廓
     */
    public void setInitTransformFrom(RectF rectF) {
        data.setInitTransformFrom(rectF, 400);
    }

    public void setInitTransformFrom(RectF rectF, int duration) {
        data.setInitTransformFrom(rectF, duration);
    }

    /**
     * 图片第一次显示出来时，图片轮廓变换的开始位置
     *
     * @param coordinate 变换开始位置的图片外轮廓4点角坐标
     */
    public void setInitTransformFrom(float[] coordinate) {
        if (coordinate != null && coordinate.length == 8) {
            data.setInitTransformFrom(coordinate, 400);
        }
    }

    public void setInitTransformFrom(float[] coordinate, int duration) {
        if (coordinate != null && coordinate.length == 8) {
            data.setInitTransformFrom(coordinate, duration);
        }
    }

    @Nullable public Bitmap getBitmap() {
        return data.getBitmap();
    }

    /**
     * 根据裁剪区域纹理生成一个Bitmap
     *
     * @return 裁切后的Bitmap，bitmap形状与控件一致
     */
    @Nullable public Bitmap getCropBitmap() {
        return data.getCropBitmap();
    }

    @Override public void setImageResource(int resId) {
        Bitmap bitmap = null;
        if (resId != 0) {
            bitmap = BitmapFactory.decodeResource(getResources(), resId);
        }
        setImageBitmapInner(bitmap);
    }

    @Override public void setImageDrawable(@Nullable Drawable drawable) {
        Bitmap bitmap = null;
        if (drawable instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
        }
        setImageBitmapInner(bitmap);
    }

    @Override public void setImageBitmap(Bitmap bitmap) {
        setImageBitmapInner(bitmap);
    }

    @Override public void setImageURI(@Nullable Uri uri) {
        if (uri != null) {
            try {
                InputStream is = getContext().getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                setImageBitmapInner(bitmap);
            } catch (Exception e) {
                setImageBitmapInner(null);
                if (L.isEnable()) L.e("BaseImageView", e);
            }
        } else {
            setImageBitmapInner(null);
        }
    }

    public void setImagePath(String filePath) {
        Bitmap bitmap = null;
        if (!TextUtils.isEmpty(filePath)) {
            try {
                FileInputStream fis = new FileInputStream(filePath);
                bitmap = BitmapFactory.decodeStream(fis);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        setImageBitmapInner(bitmap);
    }

    private void setImageBitmapInner(Bitmap bitmap) {
        initData();
        data.setBitmap(bitmap);
        postInvalidate();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override public boolean onTouchEvent(MotionEvent event) {
        return data.onTouchEvent(event);
    }

    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        data.setViewSize(width, height);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override protected void onDraw(Canvas canvas) {
        data.draw(canvas);
    }

    @Retention(CLASS)
    @IntDef({FUNCTION_PREVIEW, FUNCTION_CROP})
    private @interface Function {
    }
}
