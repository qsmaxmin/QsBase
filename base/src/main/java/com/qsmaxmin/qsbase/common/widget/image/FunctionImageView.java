package com.qsmaxmin.qsbase.common.widget.image;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
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

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

/**
 * @CreateBy qsmaxmin
 * @Date 2021/5/14 13:19
 * @Description 可预览图片和裁切图片的ImageView
 */
public class FunctionImageView extends AppCompatImageView {
    private ImageData data;
    private int       functionMode;

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
        if (attrs != null) {
            TypedArray typedArray = getResources().obtainAttributes(attrs, R.styleable.FunctionImageView);
            functionMode = typedArray.getInt(R.styleable.FunctionImageView_fiv_function, 0);
            typedArray.recycle();
        }
        initData();
        data.setFunction(functionMode);
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
        if (end) data.startRecover();
    }

    /**
     * 重置旋转，缩放，平移到初始状态
     */
    public void reset() {
        data.reset(true);
    }

    public void reset(boolean anim) {
        data.reset(anim);
    }

    /**
     * 设置功能模式
     * 0：预览模式
     * 1：裁切模式
     */
    public void setFunctionMode(int functionMode) {
        this.functionMode = functionMode;
        data.setFunction(functionMode);
    }

    public void setGestureListener(GestureListener listener) {
        data.setGestureListener(listener);
    }

    /**
     * 根据裁剪区域纹理生成一个Bitmap
     *
     * @return 裁切后的Bitmap，如果手指未离开该控件或者该控件变换动画未执行完成，返回null
     */
    @Nullable public Bitmap getBitmap() {
        return data.getBitmap();
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
            setImagePath(null);
        }
    }

    public void setImagePath(String path) {
        Bitmap bitmap = null;
        if (!TextUtils.isEmpty(path)) {
            try {
                FileInputStream fis = new FileInputStream(path);
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
}
