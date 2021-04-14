package com.qsmaxmin.qsbase.common.widget.text;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.ReplacementSpan;
import android.util.AttributeSet;

import com.qsmaxmin.qsbase.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

/**
 * @CreateBy qsmaxmin
 * @Date 2021/4/12 9:17
 * @Description 支持自定义文字缩放规则，自定义文字颜色规则，以及设置文字以一个图形开头
 */
public class RelativeTextView extends AppCompatTextView {
    //文字缩放相关属性
    private int[]    orderScaleCounts;
    private float[]  orderScaleSizes;
    private int[]    invertedOrderScaleCounts;
    private float[]  invertedOrderScaleSizes;
    private String   leftOfScaleKey;
    private float    leftOfScaleValue;
    private String   rightOfScaleKey;
    private float    rightOfScaleValue;
    private String[] specifiedScaleTexts;
    private float[]  specifiedScaleValues;
    //文字颜色相关属性
    private int[]    orderColorCounts;
    private int[]    orderColorSizes;
    private int[]    invertedOrderColorCounts;
    private int[]    invertedOrderColorSizes;
    private String   leftOfColorKey;
    private int      leftOfColorValue;
    private String   rightOfColorKey;
    private int      rightOfColorValue;
    private String[] specifiedColorTexts;
    private int[]    specifiedColorValues;

    private Drawable drawableFirst;
    private Drawable drawableEnd;
    private float    drawableFirstMargin;
    private float    drawableEndMargin;
    private int      scaleGravity;

    public RelativeTextView(@NonNull Context context) {
        super(context);
        init(null);
    }

    public RelativeTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public RelativeTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        TypedArray typedArray = getResources().obtainAttributes(attrs, R.styleable.RelativeTextView);

        String orderScale = typedArray.getString(R.styleable.RelativeTextView_rtv_orderScale);
        String invertedOrderScale = typedArray.getString(R.styleable.RelativeTextView_rtv_invertedOrderScale);
        String leftOfTextScale = typedArray.getString(R.styleable.RelativeTextView_rtv_leftOfTextScale);
        String rightOfTextScale = typedArray.getString(R.styleable.RelativeTextView_rtv_rightOfTextScale);
        String specifiedTextScale = typedArray.getString(R.styleable.RelativeTextView_rtv_specifiedTextScale);

        String orderColor = typedArray.getString(R.styleable.RelativeTextView_rtv_orderColor);
        String invertedOrderColor = typedArray.getString(R.styleable.RelativeTextView_rtv_invertedOrderColor);
        String leftOfTextColor = typedArray.getString(R.styleable.RelativeTextView_rtv_leftOfTextColor);
        String rightOfTextColor = typedArray.getString(R.styleable.RelativeTextView_rtv_rightOfTextColor);
        String specifiedTextColor = typedArray.getString(R.styleable.RelativeTextView_rtv_specifiedTextColor);

        scaleGravity = typedArray.getInt(R.styleable.RelativeTextView_rtv_gravity, 0);

        drawableFirst = typedArray.getDrawable(R.styleable.RelativeTextView_rtv_drawableFirst);
        drawableEnd = typedArray.getDrawable(R.styleable.RelativeTextView_rtv_drawableEnd);
        drawableFirstMargin = typedArray.getDimension(R.styleable.RelativeTextView_rtv_drawableFirstMargin, 0);
        drawableEndMargin = typedArray.getDimension(R.styleable.RelativeTextView_rtv_drawableEndMargin, 0);
        typedArray.recycle();

        //-------------------scale--------------------
        if (orderScale != null) {
            String[] split = orderScale.split(",");
            if (split.length % 2 != 0) throw new IllegalStateException("rtv_orderScale：必须标明‘字数’和对应的‘缩放比例’，如‘2,0.8,3,0.9...’");
            orderScaleCounts = new int[split.length / 2];
            orderScaleSizes = new float[split.length / 2];
            try {
                for (int i = 0; i < orderScaleCounts.length; i++) {
                    orderScaleCounts[i] = Integer.parseInt(split[i * 2]);
                    orderScaleSizes[i] = Float.parseFloat(split[i * 2 + 1]);
                }
            } catch (Exception e) {
                throw new IllegalStateException("rtv_orderScale：必须标明‘字数’和对应的‘缩放比例’，如‘2,0.8,3,0.9...’");
            }
        }

        if (invertedOrderScale != null) {
            String[] split = invertedOrderScale.split(",");
            if (split.length % 2 != 0) throw new IllegalStateException("rtv_invertedOrderScale：必须标明‘字数’和对应的‘缩放比例’，如‘2,0.8,3,0.9...’");
            invertedOrderScaleCounts = new int[split.length / 2];
            invertedOrderScaleSizes = new float[split.length / 2];
            try {
                for (int i = 0; i < invertedOrderScaleCounts.length; i++) {
                    invertedOrderScaleCounts[i] = Integer.parseInt(split[i * 2]);
                    invertedOrderScaleSizes[i] = Float.parseFloat(split[i * 2 + 1]);
                }
            } catch (Exception e) {
                throw new IllegalStateException("rtv_invertedOrderScale：必须标明‘字数’和对应的‘缩放比例’，如‘2,0.8,3,0.9...’");
            }
        }

        if (leftOfTextScale != null) {
            String[] split = leftOfTextScale.split(",");
            if (split.length != 2) throw new IllegalStateException("rtv_leftOfTextScale：必须标明指定字符和缩放比例，如‘你,0.8’表示‘你’字左边的字符缩放0.8倍");
            try {
                leftOfScaleKey = split[0];
                leftOfScaleValue = Float.parseFloat(split[1]);
            } catch (Exception e) {
                throw new IllegalStateException("rtv_leftOfTextScale：必须标明指定字符和缩放比例，如‘你,0.8’表示‘你’字左边的字符缩放0.8倍");
            }
        }

        if (rightOfTextScale != null) {
            String[] split = rightOfTextScale.split(",");
            if (split.length != 2) throw new IllegalStateException("rtv_rightOfTextScale：必须标明指定字符和缩放比例，如‘你,0.8’表示‘你’字左边的字符缩放0.8倍");
            try {
                rightOfScaleKey = split[0];
                rightOfScaleValue = Float.parseFloat(split[1]);
            } catch (Exception e) {
                throw new IllegalStateException("rtv_rightOfTextScale：必须标明指定字符和缩放比例，如‘你,0.8’表示‘你’字左边的字符缩放0.8倍");
            }
        }
        if (specifiedTextScale != null) {
            String[] split = specifiedTextScale.split(",");
            if (split.length % 2 != 0) throw new IllegalStateException("rtv_specifiedTextScale：必须标明指定字符和对应的缩放比例，如‘你,0.8,好,0.9’表示‘你’字和‘好’字分别缩放0.8和0.9倍");
            try {
                specifiedScaleTexts = new String[split.length / 2];
                specifiedScaleValues = new float[split.length / 2];
                for (int i = 0; i < specifiedScaleTexts.length; i++) {
                    specifiedScaleTexts[i] = split[i * 2];
                    specifiedScaleValues[i] = Float.parseFloat(split[i * 2 + 1]);
                }
            } catch (Exception e) {
                throw new IllegalStateException("rtv_specifiedTextScale：必须标明指定字符和对应的缩放比例，如‘你,0.8,好,0.9’表示‘你’字和‘好’字分别缩放0.8和0.9倍");
            }
        }

        //-------------------color--------------------
        if (orderColor != null) {
            String[] split = orderColor.split(",");
            if (split.length % 2 != 0) throw new IllegalStateException("rtv_orderColor：必须标明‘字数’和对应的‘颜色’，如‘2,#FF0000,3,#00FF00...’");
            orderColorCounts = new int[split.length / 2];
            orderColorSizes = new int[split.length / 2];
            try {
                for (int i = 0; i < orderColorCounts.length; i++) {
                    orderColorCounts[i] = Integer.parseInt(split[i * 2]);
                    orderColorSizes[i] = Color.parseColor(split[i * 2 + 1]);
                }
            } catch (Exception e) {
                throw new IllegalStateException("rtv_orderColor：必须标明‘字数’和对应的‘颜色’，如‘2,#FF0000,3,#00FF00...’");
            }
        }

        if (invertedOrderColor != null) {
            String[] split = invertedOrderColor.split(",");
            if (split.length % 2 != 0) throw new IllegalStateException("rtv_invertedOrderColor：必须标明‘字数’和对应的‘颜色’，如‘2,#FF0000,3,#00FF00...’");
            invertedOrderColorCounts = new int[split.length / 2];
            invertedOrderColorSizes = new int[split.length / 2];
            try {
                for (int i = 0; i < invertedOrderColorCounts.length; i++) {
                    invertedOrderColorCounts[i] = Integer.parseInt(split[i * 2]);
                    invertedOrderColorSizes[i] = Color.parseColor(split[i * 2 + 1]);
                }
            } catch (Exception e) {
                throw new IllegalStateException("rtv_invertedOrderColor：必须标明‘字数’和对应的‘颜色’，如‘2,#FF0000,3,#00FF00...’");
            }
        }

        if (leftOfTextColor != null) {
            String[] split = leftOfTextColor.split(",");
            if (split.length != 2) throw new IllegalStateException("rtv_leftOfTextColor：必须标明指定字符和颜色，如‘你,#FF0000’表示‘你’字左边的字颜色为#FF0000");
            try {
                leftOfColorKey = split[0];
                leftOfColorValue = Color.parseColor(split[1]);
            } catch (Exception e) {
                throw new IllegalStateException("rtv_leftOfTextColor：必须标明指定字符和颜色，如‘你,0.8’表示‘你’字左边的字颜色为#FF0000");
            }
        }

        if (rightOfTextColor != null) {
            String[] split = rightOfTextColor.split(",");
            if (split.length != 2) throw new IllegalStateException("rtv_rightOfTextColor：必须标明指定字符和颜色，如‘你,#FF0000’表示‘你’字左边的字颜色为#FF0000");
            try {
                rightOfColorKey = split[0];
                rightOfColorValue = Color.parseColor(split[1]);
            } catch (Exception e) {
                throw new IllegalStateException("rtv_rightOfTextColor：必须标明指定字符和颜色，如‘你,#FF0000’表示‘你’字左边的字颜色为#FF0000");
            }
        }
        if (specifiedTextColor != null) {
            String[] split = specifiedTextColor.split(",");
            if (split.length % 2 != 0) throw new IllegalStateException("rtv_specifiedTextColor：必须标明指定字符和对应的颜色，如‘你,#FF0000,好,#0000FF’表示‘你’字和‘好’字颜色分别为#FF0000和#0000FF");
            try {
                specifiedColorTexts = new String[split.length / 2];
                specifiedColorValues = new int[split.length / 2];
                for (int i = 0; i < specifiedColorTexts.length; i++) {
                    specifiedColorTexts[i] = split[i * 2];
                    specifiedColorValues[i] = Color.parseColor(split[i * 2 + 1]);
                }
            } catch (Exception e) {
                throw new IllegalStateException("rtv_specifiedTextColor：必须标明指定字符和对应的颜色，如‘你,#FF0000,好,#0000FF’表示‘你’字和‘好’字颜色分别为#FF0000和#0000FF");
            }
        }

        setText(getText());
    }

    @Override public void setText(CharSequence text, BufferType type) {
        if (text == null || text.length() == 0) {
            super.setText(text, type);
            return;
        }
        SpannableStringBuilder sb = null;
        int textLen = text.length();

        if (drawableFirst != null) {
            sb = new SpannableStringBuilder(text);
            sb.setSpan(new MyReplacementSpan(drawableFirst, drawableFirstMargin), 0, 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        if (drawableEnd != null) {
            if (sb == null) sb = new SpannableStringBuilder(text);
            sb.setSpan(new MyReplacementSpan(drawableEnd, drawableEndMargin), textLen - 1, textLen, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }

        //-----------------scale------------------
        if (leftOfScaleKey != null && leftOfScaleKey.length() > 0 && leftOfScaleValue >= 0) {
            if (sb == null) sb = new SpannableStringBuilder(text);
            int index = text.toString().indexOf(leftOfScaleKey);
            if (index > 0) {
                sb.setSpan(new MyRelativeSizeSpan(leftOfScaleValue, scaleGravity), 0, index, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            }
        }
        if (rightOfScaleKey != null && rightOfScaleKey.length() > 0 && rightOfScaleValue >= 0) {
            if (sb == null) sb = new SpannableStringBuilder(text);
            int index = text.toString().indexOf(rightOfScaleKey);
            if (index + rightOfScaleKey.length() < text.length()) {
                sb.setSpan(new MyRelativeSizeSpan(rightOfScaleValue, scaleGravity), index + 1, textLen, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            }
        }
        if (orderScaleCounts != null && orderScaleCounts.length > 0) {
            if (sb == null) sb = new SpannableStringBuilder(text);
            int start = 0;
            for (int i = 0; i < orderScaleCounts.length; i++) {
                if (start >= textLen) break;
                int cnt = orderScaleCounts[i];
                float scale = orderScaleSizes[i];
                if (cnt <= 0) continue;
                if (scale < 0) scale = 0;
                MyRelativeSizeSpan span = new MyRelativeSizeSpan(scale, scaleGravity);
                sb.setSpan(span, start, Math.min(start + cnt, textLen), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                start += cnt;
            }
        }
        if (invertedOrderScaleCounts != null && invertedOrderScaleCounts.length > 0) {
            if (sb == null) sb = new SpannableStringBuilder(text);
            int end = textLen;
            for (int i = 0; i < invertedOrderScaleCounts.length; i++) {
                if (end < 0) break;
                int cnt = invertedOrderScaleCounts[i];
                float scale = invertedOrderScaleSizes[i];
                if (cnt <= 0) continue;
                if (scale < 0) scale = 0;
                MyRelativeSizeSpan span = new MyRelativeSizeSpan(scale, scaleGravity);
                sb.setSpan(span, Math.max(0, end - cnt), end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                end -= cnt;
            }
        }
        if (specifiedScaleTexts != null && specifiedScaleTexts.length > 0) {
            String textString = text.toString();
            if (sb == null) sb = new SpannableStringBuilder(text);
            for (int i = 0; i < specifiedScaleTexts.length; i++) {
                String str = specifiedScaleTexts[i];
                float scale = specifiedScaleValues[i];
                if (scale < 0) scale = 0;
                for (int j = 0, len = textLen - str.length() + 1; j < len; j++) {
                    if (textString.startsWith(str, j)) {
                        sb.setSpan(new MyRelativeSizeSpan(scale, scaleGravity), j, j + str.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    }
                }
            }
        }

        //----------------color---------------------
        if (leftOfColorKey != null && leftOfColorKey.length() > 0) {
            if (sb == null) sb = new SpannableStringBuilder(text);
            int index = text.toString().indexOf(leftOfColorKey);
            if (index > 0) {
                sb.setSpan(new ForegroundColorSpan(leftOfColorValue), 0, index, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            }
        }
        if (rightOfColorKey != null && rightOfColorKey.length() > 0 && rightOfScaleValue >= 0) {
            if (sb == null) sb = new SpannableStringBuilder(text);
            int index = text.toString().indexOf(rightOfColorKey);
            if (index + rightOfColorKey.length() < text.length()) {
                sb.setSpan(new ForegroundColorSpan(rightOfColorValue), index + 1, textLen, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            }
        }
        if (orderColorCounts != null && orderColorCounts.length > 0) {
            if (sb == null) sb = new SpannableStringBuilder(text);
            int start = 0;
            for (int i = 0; i < orderColorCounts.length; i++) {
                if (start >= textLen) break;
                int cnt = orderColorCounts[i];
                int color = orderColorSizes[i];
                if (cnt <= 0) continue;
                ForegroundColorSpan span = new ForegroundColorSpan(color);
                sb.setSpan(span, start, Math.min(start + cnt, textLen), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                start += cnt;
            }
        }
        if (invertedOrderColorCounts != null && invertedOrderColorCounts.length > 0) {
            if (sb == null) sb = new SpannableStringBuilder(text);
            int end = textLen;
            for (int i = 0; i < invertedOrderColorCounts.length; i++) {
                if (end < 0) break;
                int cnt = invertedOrderColorCounts[i];
                int color = invertedOrderColorSizes[i];
                if (cnt <= 0) continue;
                ForegroundColorSpan span = new ForegroundColorSpan(color);
                sb.setSpan(span, Math.max(end - cnt, 0), end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                end -= cnt;
            }
        }
        if (specifiedColorTexts != null && specifiedColorTexts.length > 0) {
            String textString = text.toString();
            if (sb == null) sb = new SpannableStringBuilder(text);
            for (int i = 0; i < specifiedColorTexts.length; i++) {
                String str = specifiedColorTexts[i];
                int color = specifiedColorValues[i];
                for (int j = 0, len = textLen - str.length() + 1; j < len; j++) {
                    if (textString.startsWith(str, j)) {
                        sb.setSpan(new ForegroundColorSpan(color), j, j + str.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    }
                }
            }
        }
        super.setText(sb == null ? text : sb, type);
    }

    private static class MyReplacementSpan extends ReplacementSpan {
        private final Drawable             drawable;
        private final float                margin;
        private final Rect                 rect;
        private final Paint.FontMetricsInt metricsInt;

        public MyReplacementSpan(Drawable drawable, float margin) {
            this.drawable = drawable;
            this.margin = margin;
            this.rect = new Rect();
            this.metricsInt = new Paint.FontMetricsInt();
        }

        @Override public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fm) {
            if (fm != null) {
                paint.getFontMetricsInt(metricsInt);
                fm.top = metricsInt.top;
                fm.bottom = metricsInt.bottom;
                fm.ascent = metricsInt.ascent;
                fm.descent = metricsInt.descent;
                fm.leading = metricsInt.leading;
            }
            return (int) (drawable.getIntrinsicWidth() + margin + paint.measureText(text, start, end));
        }

        @Override public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
            rect.set(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            rect.offset((int) x, (int) ((bottom + top) / 2f - rect.centerY()));
            drawable.setBounds(rect);
            drawable.draw(canvas);

            if (text instanceof Spanned) {
                Spanned sp = (Spanned) text;
                ForegroundColorSpan[] spans = sp.getSpans(start, end, ForegroundColorSpan.class);
                if (spans != null && spans.length > 0) {
                    int color = spans[spans.length - 1].getForegroundColor();
                    paint.setColor(color);
                }
            }
            canvas.drawText(text, start, end, x + rect.width() + margin, y, paint);
        }
    }

    private static class MyRelativeSizeSpan extends RelativeSizeSpan {
        private final Paint.FontMetricsInt metrics;
        private final int                  gravity;

        public MyRelativeSizeSpan(float proportion, int gravity) {
            super(proportion);
            this.gravity = gravity;
            metrics = new Paint.FontMetricsInt();
        }

        @Override public void updateDrawState(@NonNull TextPaint ds) {
            if (gravity == 0) {
                super.updateDrawState(ds);
                return;
            }
            ds.getFontMetricsInt(metrics);
            int descent0 = metrics.descent;
            int ascent0 = metrics.ascent;

            super.updateDrawState(ds);

            ds.getFontMetricsInt(metrics);
            int descent1 = metrics.descent;
            int ascent1 = metrics.ascent;

            if (gravity == 1) {//top
                ds.baselineShift -= (ascent1 - ascent0);
            } else if (gravity == 2) {//center
                float center0 = (descent0 - ascent0) / 2f + ascent0;
                float center1 = (descent1 - ascent1) / 2f + ascent1;
                ds.baselineShift += (int) (center0 - center1);
            } else if (gravity == 3) {//bottom
                ds.baselineShift += (descent0 - descent1);
            }
        }
    }
}
