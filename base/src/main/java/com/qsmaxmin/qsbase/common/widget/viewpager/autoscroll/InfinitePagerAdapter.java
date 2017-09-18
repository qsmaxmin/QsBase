package com.qsmaxmin.qsbase.common.widget.viewpager.autoscroll;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.utils.QsHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * create by skyJC
 */
public class InfinitePagerAdapter extends PagerAdapter {

    private final        List<String> urls                   = new ArrayList<>();
    private static final float        PAGE_WIDTH_SINGLE_ITEM = 1.0f;
    private              boolean      infinitePagesEnabled   = true;

    private OnPageClickListener listener;
    private int                 placeholder;

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

    public void removeData() {
        synchronized (urls) {
            urls.clear();
        }
    }

    public int getRealCount() {
        return urls.size();
    }

    public void enableInfinite(boolean enable) {
        infinitePagesEnabled = enable;
    }

    @Override public int getCount() {
        if (infinitePagesEnabled) {
            return Integer.MAX_VALUE;
        } else {
            return urls.size();
        }
    }

    protected ImageView getImageView(Context context) {
        return new ImageView(context);
    }


    @Override public float getPageWidth(int position) {
        return PAGE_WIDTH_SINGLE_ITEM;
    }

    @Override public Object instantiateItem(ViewGroup container, final int position) {
        final int virtualPosition = getVirtualPosition(position);
        L.i("InfinitePagerAdapter", "instantiateItem... position:" + virtualPosition + " totalCount:" + getCount());
        FrameLayout frameLayout = new FrameLayout(container.getContext());
        final ImageView view = getImageView(container.getContext());
        final ImageView holderImage = new ImageView(container.getContext());
        view.setScaleType(ImageView.ScaleType.FIT_XY);
        holderImage.setScaleType(ImageView.ScaleType.CENTER);
        view.setId(virtualPosition);
        frameLayout.addView(holderImage);
        frameLayout.addView(view);
        if (placeholder > 0) {
            holderImage.setImageDrawable(container.getContext().getResources().getDrawable(placeholder));
            if (virtualPosition < urls.size()) {
                QsHelper.getInstance().getImageHelper().createRequest(container.getContext()).load(urls.get(virtualPosition)).listener(new RequestListener<Drawable>() {
                    @Override public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        view.setVisibility(View.GONE);
                        holderImage.setVisibility(View.VISIBLE);
                        return false;
                    }

                    @Override public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        view.setVisibility(View.VISIBLE);
                        holderImage.setVisibility(View.GONE);
                        return false;
                    }
                }).into(view);
            }
        } else {
            if (virtualPosition < urls.size()) QsHelper.getInstance().getImageHelper().createRequest(container.getContext()).load(urls.get(virtualPosition)).into(view);
        }
        if (listener != null) {
            view.setOnClickListener(new View.OnClickListener() {
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

    int getVirtualPosition(int position) {
        return infinitePagesEnabled ? position % getRealCount() : position;
    }

    public interface OnPageClickListener {
        void onPageClick(int position);
    }
}
