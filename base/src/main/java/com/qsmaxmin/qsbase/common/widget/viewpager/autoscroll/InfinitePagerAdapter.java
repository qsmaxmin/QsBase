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
 * @CreateBy qsmaxmin
 * @Date 2017-7-6 12:37:16
 * @Description 自动轮播适配器
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

    public void addData(List data) {
        synchronized (urls) {
            if (data != null && !data.isEmpty()) {
                urls.addAll(data);
            }
        }
    }

    public void setData(List data) {
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
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        return imageView;
    }

    @Override public float getPageWidth(int position) {
        return PAGE_WIDTH_SINGLE_ITEM;
    }

    @NonNull @Override public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        final int virtualPosition = getVirtualPosition(position);
        FrameLayout frameLayout = new FrameLayout(container.getContext());
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
            if (virtualPosition < urls.size()) {
                Object object = urls.get(virtualPosition);
                if (object instanceof String) {
                    L.i("InfinitePagerAdapter", "instantiateItem... type:String(holder), position:" + virtualPosition + ", totalCount:" + urls.size());
                    QsHelper.getInstance().getImageHelper().createRequest()
                            .roundedCorners(corners)
                            .load(object)
                            .into(imageView, new ImageHelper.ImageRequestListener() {
                                @Override public void onLoadFailed(String message) {
                                    pageView.setVisibility(View.GONE);
                                    holderImage.setVisibility(View.VISIBLE);
                                }

                                @Override public void onSuccess(Drawable drawable, Object model) {
                                    pageView.setVisibility(View.VISIBLE);
                                    holderImage.setVisibility(View.GONE);
                                }
                            });
                } else if (object instanceof Bitmap) {
                    L.i("InfinitePagerAdapter", "instantiateItem... type:Bitmap(holder), position:" + virtualPosition + ", totalCount:" + urls.size());
                    pageView.setVisibility(View.VISIBLE);
                    holderImage.setVisibility(View.GONE);
                    imageView.setImageBitmap((Bitmap) object);
                } else {
                    L.i("InfinitePagerAdapter", "instantiateItem... type:Object(holder), position:" + virtualPosition + ", totalCount:" + urls.size());
                    QsHelper.getInstance().getImageHelper().createRequest()
                            .roundedCorners(corners)
                            .load(object)
                            .into(imageView, new ImageHelper.ImageRequestListener() {
                                @Override public void onLoadFailed(String message) {
                                    pageView.setVisibility(View.GONE);
                                    holderImage.setVisibility(View.VISIBLE);
                                }

                                @Override public void onSuccess(Drawable drawable, Object model) {
                                    pageView.setVisibility(View.VISIBLE);
                                    holderImage.setVisibility(View.GONE);
                                }
                            });
                }
            }
        } else {
            frameLayout.addView(pageView);
            if (virtualPosition < urls.size()) {
                Object object = urls.get(virtualPosition);
                if (object instanceof String) {
                    L.i("InfinitePagerAdapter", "instantiateItem... type:String(no holder), position:" + virtualPosition + ", totalCount:" + urls.size());
                    QsHelper.getInstance().getImageHelper().createRequest().roundedCorners(corners).load((String) object).into(imageView);
                } else if (object instanceof Bitmap) {
                    L.i("InfinitePagerAdapter", "instantiateItem... type:Bitmap(no holder), position:" + virtualPosition + ", totalCount:" + urls.size());
                    imageView.setImageBitmap((Bitmap) object);
                } else {
                    L.i("InfinitePagerAdapter", "instantiateItem... type:Object(no holder), position:" + virtualPosition + ", totalCount:" + urls.size());
                    QsHelper.getInstance().getImageHelper().createRequest().roundedCorners(corners).load(urls.get(virtualPosition)).into(imageView);
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
        container.addView(frameLayout);
        return frameLayout;
    }

    @Override public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        int virtualPosition = getVirtualPosition(position);
        L.i("destroyItem", virtualPosition + ":" + position);
        container.removeView((View) object);
    }

    @Override public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
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
