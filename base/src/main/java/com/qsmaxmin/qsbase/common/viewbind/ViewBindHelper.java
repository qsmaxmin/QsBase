package com.qsmaxmin.qsbase.common.viewbind;

import android.os.Bundle;
import android.util.LruCache;
import android.view.View;

import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.widget.dialog.QsDialogFragment;
import com.qsmaxmin.qsbase.mvp.QsIView;
import com.qsmaxmin.qsbase.mvp.adapter.QsListAdapterItem;
import com.qsmaxmin.qsbase.mvp.adapter.QsRecycleAdapterItem;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * @CreateBy qsmaxmin
 * @Date 2018/10/18 9:34
 * @Description 缓存机制提高性能
 * 1，View层控件绑定
 * 2，控件点击事件
 * 3，Bundle值寻找
 */
public class ViewBindHelper {
    private static final boolean                          FAST_MODE = true;
    private static       LruCache<Class<?>, ViewBindData> viewCache = new LruCache<>(200);

    public static void bindBundle(Object target, Bundle bundle) {
        if (target == null || bundle == null) return;
        if (FAST_MODE) {
            AnnotationHelper.bindBundle(target, bundle);
        } else {
            bindBundleInner(target, bundle);
        }
    }

    public static void bindView(final Object target, View rootView) {
        if (target == null || rootView == null) return;
        if (FAST_MODE) {
            AnnotationHelper.bindView(target, rootView);
        } else {
            bindViewInner(target, rootView);
        }
    }

    private static void bindBundleInner(Object target, Bundle bundle) {
        Class<?> clazz = target.getClass();
        ViewBindData bindData = getBindData(clazz);
        Map<Field, String> bundleFieldMap = bindData.bundleFieldMap;
        if (bundleFieldMap != null) {
            for (Field field : bundleFieldMap.keySet()) {
                String bundleKey = bundleFieldMap.get(field);
                Object value = bundle.get(bundleKey);
                if (value != null) {
                    setFieldValue(target, field, value);
                } else {
                    L.e(bindData.targetName, "not found key(" + bundleKey + ")in Bundle !");
                }
            }
        }
    }

    private static void bindViewInner(final Object target, View rootView) {
        Class<?> clazz = target.getClass();
        final ViewBindData bindData = getBindData(clazz);
        Map<Field, Integer> viewFieldMap = bindData.viewFieldMap;
        if (viewFieldMap != null) {
            for (Field field : viewFieldMap.keySet()) {
                Integer viewId = viewFieldMap.get(field);
                if (viewId != null) {
                    View view = rootView.findViewById(viewId);
                    if (view != null) {
                        setFieldValue(target, field, view);
                    } else {
                        L.e(bindData.targetName, "Invalid @Bind(" + field.getName() + ")view not found !");
                    }
                }
            }
        }

        if (bindData.onViewClickMethod != null && bindData.clickIds != null) {
            View.OnClickListener listener = new View.OnClickListener() {
                @Override public void onClick(View v) {
                    if (target instanceof QsIView) {
                        ((QsIView) target).onViewClick(v);
                    } else if (target instanceof QsListAdapterItem) {
                        ((QsListAdapterItem) target).onViewClick(v);
                    } else if (target instanceof QsRecycleAdapterItem) {
                        ((QsRecycleAdapterItem) target).onViewClick(v);
                    } else if (target instanceof QsDialogFragment) {
                        ((QsDialogFragment) target).onViewClick(v);
                    } else {
                        try {
                            bindData.onViewClickMethod.invoke(target, v);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            for (int value : bindData.clickIds) {
                final View view = rootView.findViewById(value);
                if (view != null) {
                    view.setOnClickListener(listener);
                } else {
                    L.e(bindData.targetName, "Invalid @OnClick(id:" + value + ") view not found !");
                }
            }
        }
    }

    private static ViewBindData getBindData(Class<?> clazz) {
        ViewBindData cacheBindData = viewCache.get(clazz);
        if (cacheBindData == null) {
            cacheBindData = new ViewBindData(clazz);
            viewCache.put(clazz, cacheBindData);
            if (L.isEnable()) {
                L.i(cacheBindData.targetName, "create new ViewBindData by class, cache size:" + viewCache.size());
            }
        }
        return cacheBindData;
    }

    private static void setFieldValue(Object target, Field field, Object value) {
        try {
            field.set(target, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
