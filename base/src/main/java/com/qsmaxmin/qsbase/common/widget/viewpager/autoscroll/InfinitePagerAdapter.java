package com.qsmaxmin.qsbase.common.widget.viewpager.autoscroll;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.common.utils.ImageHelper;
import com.qsmaxmin.qsbase.common.utils.QsHelper;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

/**
 * @CreateBy qsmaxmin
 * @Date 2017-7-6 12:37:16
 * @Description
 */
public class InfinitePagerAdapter extends PagerAdapter {
    private final List<Object>        urls                 = new ArrayList<>();
    private       boolean             infinitePagesEnabled = true;
    private       OnPageClickListener listener;
    private       int                 holderId;
    private       int                 corners;

    public void setOnPageClickListener(OnPageClickListener listener) {
        this.listener = listener;
    }

    public void setPlaceholder(int holderId) {
        this.holderId = holderId;
    }

    public void setData(List data) {
        synchronized (urls) {
            urls.clear();
            if (data != null && !data.isEmpty()) {
                urls.addAll(data);
            }
            notifyDataSetChanged();
        }
    }

    public List<Object> getData() {
        return new ArrayList<>(urls);
    }

    public void clear() {
        synchronized (urls) {
            urls.clear();
            notifyDataSetChanged();
        }
    }

    public void setRoundedCorners(int corners) {
        this.corners = corners;
    }

    int getRealCount() {
        return urls.size();
    }

    public void enableInfinite(boolean enable) {
        if (infinitePagesEnabled != enable) {
            infinitePagesEnabled = enable;
            notifyDataSetChanged();
        }
    }

    boolean isEnableInfinite() {
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
        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        return imageView;
    }

    @NonNull @Override public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        final int virtualPosition = getVirtualPosition(position);

        final View pageView = getPageView(container.getContext(), virtualPosition, urls.size());
        ImageView imageView = (pageView instanceof ImageView) ? (ImageView) pageView : (ImageView) pageView.findViewById(R.id.qs_banner_image);
        if (imageView == null) throw new IllegalStateException("InfinitePageAdapter getPageView(Context) should return a ImageView or ViewGroup Contains a ID of 'R.id.qs_banner_image' ImageView");

        if (virtualPosition < urls.size()) {
            if (holderId > 0) {
                Object object = urls.get(virtualPosition);
                if (object instanceof String) {
                    imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    imageView.setImageDrawable(container.getResources().getDrawable(holderId));
                    ImageHelper.ImageRequestListener listener = createImageRequestCallback(imageView);
                    QsHelper.getImageHelper().roundedCorners(corners).load(object).into(imageView, listener);
                } else if (object instanceof Bitmap) {
                    imageView.setImageBitmap((Bitmap) object);
                } else {
                    imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    imageView.setImageDrawable(container.getResources().getDrawable(holderId));
                    ImageHelper.ImageRequestListener listener = createImageRequestCallback(imageView);
                    QsHelper.getImageHelper().roundedCorners(corners).load(object).into(imageView, listener);
                }
            } else {
                Object object = urls.get(virtualPosition);
                if (object instanceof String) {
                    QsHelper.getImageHelper().roundedCorners(corners).load((String) object).into(imageView);
                } else if (object instanceof Bitmap) {
                    imageView.setImageBitmap((Bitmap) object);
                } else {
                    QsHelper.getImageHelper().roundedCorners(corners).load(urls.get(virtualPosition)).into(imageView);
                }
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
        container.addView(pageView);
        return pageView;
    }

    @NonNull private ImageHelper.ImageRequestListener createImageRequestCallback(final ImageView imageView) {
        return new ImageHelper.ImageRequestListener() {
            @Override public void onLoadFailed(String message) {
            }

            @Override public void onSuccess(Drawable drawable, Object model) {
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            }
        };
    }

    @Override public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    int getVirtualPosition(int position) {
        return infinitePagesEnabled ? (getRealCount() > 0 ? position % getRealCount() : 0) : position;
    }

    public interface OnPageClickListener {
        void onPageClick(int position);
    }
}
