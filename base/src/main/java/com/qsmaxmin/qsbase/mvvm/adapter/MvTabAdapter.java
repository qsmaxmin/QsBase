package com.qsmaxmin.qsbase.mvvm.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qsmaxmin.qsbase.mvvm.MvIViewPager;
import com.qsmaxmin.qsbase.mvvm.model.MvModelPager;

import java.util.ArrayList;
import java.util.List;

/**
 * @CreateBy qsmaxmin
 * @Date 2020/10/26 15:22
 * @Description tab 适配器
 */
public class MvTabAdapter {
    private final List<MvTabAdapterItem> items;
    private final List<MvModelPager>     modelPagers;

    public MvTabAdapter(MvIViewPager viewLayer, List<MvModelPager> modelPagers, MvTabAdapterItem firstTabItem) {
        this.modelPagers = modelPagers;
        this.items = new ArrayList<>(modelPagers.size());
        items.add(firstTabItem);
        for (int i = 1, size = modelPagers.size(); i < size; i++) {
            MvTabAdapterItem tabAdapterItem = viewLayer.createTabAdapterItemInner(i);
            if (tabAdapterItem == null) {
                throw new IllegalStateException(viewLayer.getClass().getName() + ".createTabAdapterItem() cannot return null!");
            }
            items.add(tabAdapterItem);
        }
    }

    public final View getTabItemView(LayoutInflater inflater, ViewGroup parent, int position) {
        return items.get(position).onCreateTabItemView(inflater, parent);
    }

    public final List<MvModelPager> getModelPagers() {
        return modelPagers;
    }

    public final List<MvTabAdapterItem> getTabAdapterItems() {
        return items;
    }

    public final MvTabAdapterItem getTabAdapterItem(int index) {
        return (index < 0 || index >= items.size()) ? null : items.get(index);
    }

    public final void init(View itemView, int position) {
        MvTabAdapterItem adapterItem = getTabAdapterItem(position);
        if (adapterItem != null) adapterItem.init(itemView, getModelPagers());
    }

    public void onPageSelected(int currentPosition, int oldPosition) {
        if (currentPosition >= 0 && currentPosition < items.size()) {
            items.get(currentPosition).onPageSelectChanged(true);
        }
        if (oldPosition >= 0 && oldPosition < items.size()) {
            items.get(oldPosition).onPageSelectChanged(false);
        }
    }

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        for (MvTabAdapterItem item : items) {
            item.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    public void onPageScrollStateChanged(int state) {
        for (MvTabAdapterItem item : items) {
            item.onPageScrollStateChanged(state);
        }
    }
}
