package com.qsmaxmin.qsbase.common.widget.viewpager.autoscroll;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.utils.ImageHelper;
import com.qsmaxmin.qsbase.common.utils.QsHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * create by skyJC
 */
public class InfinitePagerAdapter extends PagerAdapter {

    private final        List<Object> urls                   = new ArrayList<>();
    private static final float        PAGE_WIDTH_SINGLE_ITEM = 1.0f;
    private              boolean      infinitePagesEnabled   = true;

    private OnPageClickListener listener;
    private int                 placeholder;
    private int                 corners;

    public void setOnPageClickListener(OnPageClickListener listener) {
        this.listener = listener;
    }

    public void setPlaceholder(int placeholder) {
        this.placeholder = placeholder;
    }

    public void addData(String url) {
        synchronized (urls) {
            if (!TextUtils.isEmpty(url)) {
                urls.add(url);
            }
        }
    }

    public void addData(List<String> data) {
        synchronized (urls) {
            if (data != null && !data.isEmpty()) {
                urls.addAll(data);
            }
        }
    }

    public void setData(List<String> data) {
        synchronized (urls) {
            urls.clear();
            if (data != null && !data.isEmpty()) {
                urls.addAll(data);
            }
        }
    }

    public List<Object> getData() {
        ArrayList<Object> result = new ArrayList<>();
        result.addAll(urls);
        return result;
    }

    public void removeData() {
        synchronized (urls) {
            urls.clear();
        }
    }

    public void setRoundedCorners(int corners) {
        this.corners = corners;
    }

    public int getRealCount() {
        return urls.size();
    }

    public void enableInfinite(boolean enable) {
        infinitePagesEnabled = enable;
    }

    public boolean isEnableInfinite() {
        return infinitePagesEnabled;
    }

    @Override public int getCount() {
        if (infinitePagesEnabled) {
            return Integer.MAX_VALUE;
        } else {
            return urls.size();
        }
    }

    protected View getPageView(Context context, int currentIndex, int totalPage) {
        ImageView imageView = new ImageView(context);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(layoutParams);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        return imageView;
    }

    protected ImageView getPlaceholderView(Context context) {
        ImageView imageView = new ImageView(context);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(layoutParams);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        return imageView;
    }

    @Override public float getPageWidth(int position) {
        return PAGE_WIDTH_SINGLE_ITEM;
    }

    @NonNull @Override public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        final int virtualPosition = getVirtualPosition(position);
        L.i("InfinitePagerAdapter", "instantiateItem... position:" + virtualPosition + " totalCount:" + urls.size());
        FrameLayout frameLayout = new FrameLayout(container.getContext());
        if (virtualPosition < urls.size()) return frameLayout;
        final View pageView = getPageView(container.getContext(), virtualPosition, urls.size());
        ImageView imageView;
        if (pageView instanceof ImageView) {
            imageView = (ImageView) pageView;
        } else {
            imageView = pageView.findViewById(R.id.qs_banner_image);
        }
        if (imageView == null) throw new IllegalStateException("InfinitePageAdapter getPageView(Context) should return a ImageView or ViewGroup Contains a ID of 'R.id.qs_banner_image' ImageView");
        if (placeholder > 0) {
            final ImageView holderImage = getPlaceholderView(container.getContext());
            holderImage.setImageDrawable(container.getContext().getResources().getDrawable(placeholder));
            frameLayout.addView(holderImage);
            frameLayout.addView(pageView);
            Object object = urls.get(virtualPosition);

            ImageHelper.ImageRequestListener listener = new ImageHelper.ImageRequestListener() {
                @Override public void onLoadFailed(String message) {
                    pageView.setVisibility(View.GONE);
                    holderImage.setVisibility(View.VISIBLE);
                }

                @Override public void onSuccess(Drawable drawable, Object model) {
                    pageView.setVisibility(View.VISIBLE);
                    holderImage.setVisibility(View.GONE);
                }
            };

            if (object instanceof String) {
                QsHelper.getInstance().getImageHelper().createRequest()
                        .roundedCorners(corners)
                        .load((String) object)
                        .into(imageView, listener);
            } else if (object instanceof Bitmap) {
                imageView.setImageBitmap((Bitmap) object);
                pageView.setVisibility(View.VISIBLE);
                holderImage.setVisibility(View.GONE);
            } else {
                QsHelper.getInstance().getImageHelper().createRequest()
                        .roundedCorners(corners)
                        .load(object)
                        .into(imageView, listener);
            }

        } else {
            frameLayout.addView(pageView);
            Object object = urls.get(virtualPosition);
            if (object instanceof String) {
                QsHelper.getInstance().getImageHelper().createRequest().roundedCorners(corners).load((String) object).into(imageView);
            } else if (object instanceof Bitmap) {
                imageView.setImageBitmap((Bitmap) object);
            } else {
                QsHelper.getInstance().getImageHelper().createRequest().roundedCorners(corners).load(object).into(imageView);
            }
        }

        if (listener != null) {
            pageView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    if (listener != null) {
                        listener.onPageClick(virtualPosition);
                    }
                }
            });
        }
        container.addView(frameLayout);
        return frameLayout;
    }

    @Override public void destroyItem(ViewGroup container, int position, Object object) {
        int virtualPosition = getVirtualPosition(position);
        L.i("destroyItem", "" + virtualPosition + ":" + position);
        container.removeView((View) object);
    }

    @Override public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    int getVirtualPosition(int position) {
        return infinitePagesEnabled ? position % getRealCount() : position;
    }

    public interface OnPageClickListener {
        void onPageClick(int position);
    }
}
