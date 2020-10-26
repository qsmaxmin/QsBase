package com.qsmaxmin.qsbase.mvp.adapter;

import android.view.View;

import com.qsmaxmin.qsbase.mvp.QsIViewPager;
import com.qsmaxmin.qsbase.mvp.model.QsModelPager;
import com.qsmaxmin.qsbase.mvp.presenter.QsPresenter;

import java.util.ArrayList;

/**
 * @CreateBy qsmaxmin
 * @Date 2020/10/26 15:22
 * @Description tab 适配器
 */
public class QsTabAdapter {
    private final ArrayList<QsTabAdapterItem> items;
    private final QsModelPager[]              modelPagers;

    public <P extends QsPresenter> QsTabAdapter(QsIViewPager viewLayer, QsModelPager[] modelPagers) {
        this.modelPagers = modelPagers;
        this.items = new ArrayList<>(modelPagers.length);
        for (int i = 0; i < modelPagers.length; i++) {
            QsTabAdapterItem tabAdapterItem = viewLayer.createTabAdapterItem(i);
            if (tabAdapterItem == null) {
                throw new IllegalStateException(viewLayer.getClass().getName() + ".isCustomTabView() return true, but createTabAdapterItem return null!");
            }
            items.add(tabAdapterItem);
        }
    }

    public int tabItemLayoutId(int position) {
        return items.get(position).tabItemLayoutId();
    }

    public QsModelPager[] getModelPagers() {
        return modelPagers;
    }

    public final void init(View itemView, int position) {
        QsTabAdapterItem adapterItem = items.get(position);
        adapterItem.init(itemView, getModelPagers());
    }

    public void onPageSelected(View currentView, View oldView, int currentPosition, int oldPosition) {
        if (currentPosition >= 0 && currentPosition < items.size()) {
            items.get(currentPosition).onPageSelectChanged(true);
        }
        if (oldPosition >= 0 && oldPosition < items.size()) {
            items.get(oldPosition).onPageSelectChanged(false);
        }
    }

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        for (QsTabAdapterItem item : items) {
            item.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    public void onPageScrollStateChanged(int state) {
        for (QsTabAdapterItem item : items) {
            item.onPageScrollStateChanged(state);
        }
    }
}
