package com.qsmaxmin.qsbase.common.widget.viewpager.autoscroll;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.qsmaxmin.qsbase.common.utils.QsHelper;

import java.util.ArrayList;
import java.util.List;

public class InfiniteStatePagerAdapter extends PagerAdapter {

    private List<String> urls = new ArrayList<>();
    private FragmentManager     manager;
    public  PageIndicator       indicator;
    private int                 placeholder;
    private OnPageClickListener listener;

    public void setOnPageClickListener(OnPageClickListener listener) {
        this.listener = listener;
    }

    public void setPlaceholder(int placeholder) {
        this.placeholder = placeholder;
    }

    public InfiniteStatePagerAdapter(PageIndicator indicator, FragmentManager fm) {
        manager = fm;
        this.indicator = indicator;
    }

    public FragmentManager getManager() {
        return manager;
    }

    @Override public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public void addData(String url) {
        urls.add(url);
    }

    public void setData(List<String> data) {
        this.urls = data;
    }


    public void addData(List<String> fragments) {
        urls.addAll(fragments);
    }

    public void clearData() {
        urls.clear();
    }

    @Override public int getCount() {
        return urls.size();
    }

    @Override public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override public Object instantiateItem(ViewGroup container, final int position) {
        FrameLayout frameLayout = new FrameLayout(container.getContext());
        final ImageView view = getImageView(container.getContext());
        final ImageView holderImage = new ImageView(container.getContext());
        view.setScaleType(ImageView.ScaleType.FIT_XY);
        holderImage.setScaleType(ImageView.ScaleType.CENTER);
        view.setId(position);
        frameLayout.addView(holderImage);
        frameLayout.addView(view);
        if (placeholder > 0) {
            holderImage.setImageDrawable(container.getContext().getResources().getDrawable(placeholder));
            QsHelper.getInstance().getImageHelper().createRequest(container.getContext()).load(urls.get(position)).listener(new RequestListener<Drawable>() {
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
        } else {
            QsHelper.getInstance().getImageHelper().createRequest(container.getContext()).load(urls.get(position)).into(view);
        }
        if (listener != null) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    if (listener != null) {
                        listener.onPageClick(position);
                    }
                }
            });
        }

        container.addView(frameLayout);
        return frameLayout;
    }

    protected ImageView getImageView(Context context) {
        return new ImageView(context);
    }

    public interface OnPageClickListener {
        void onPageClick(int position);
    }
}
