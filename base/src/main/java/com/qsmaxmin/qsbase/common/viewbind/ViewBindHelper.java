package com.qsmaxmin.qsbase.common.viewbind;

import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.View;

import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.utils.QsHelper;
import com.qsmaxmin.qsbase.common.viewbind.annotation.Bind;
import com.qsmaxmin.qsbase.common.viewbind.annotation.BindBundle;
import com.qsmaxmin.qsbase.common.viewbind.annotation.OnClick;
import com.qsmaxmin.qsbase.common.widget.dialog.QsDialogFragment;
import com.qsmaxmin.qsbase.mvp.QsIActivity;
import com.qsmaxmin.qsbase.mvp.QsIView;
import com.qsmaxmin.qsbase.mvp.adapter.QsListAdapterItem;
import com.qsmaxmin.qsbase.mvp.adapter.QsRecycleAdapterItem;
import com.qsmaxmin.qsbase.mvp.fragment.QsIFragment;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.Map;

import dalvik.system.DexFile;

/**
 * @CreateBy qsmaxmin
 * @Date 2018/10/18 9:34
 * @Description 缓存机制提高性能
 * 1，View层控件绑定
 * 2，控件点击事件
 * 3，Bundle值寻找
 */
public class ViewBindHelper {

    private static LruCache<Class<?>, ViewBindData> viewCache = new LruCache<>(200);

    /**
     * 典型的以内存换时间，以后可能会用上
     */
    public static void preInit() {
        if (QsHelper.getInstance().getApplication().isMainProcess()) {
            QsHelper.getInstance().getThreadHelper().getWorkThreadPoll().execute(new Runnable() {
                @Override public void run() {
                    long start = System.nanoTime();
                    try {
                        String packageName = QsHelper.getInstance().getApplication().getPackageName();
                        String packageCodePath = QsHelper.getInstance().getApplication().getPackageCodePath();
                        DexFile df = new DexFile(packageCodePath);
                        Enumeration<String> entries = df.entries();
                        while (entries.hasMoreElements()) {
                            String classPath = entries.nextElement();
                            if (classPath.startsWith(packageName) && !classPath.contains("$") && !classPath.contains("\\.R\\.")) {
                                try {
                                    Class<?> aClass = Class.forName(classPath);
                                    ViewBindData viewBindData = viewCache.get(aClass);
                                    if (viewBindData == null && QsIActivity.class.isAssignableFrom(aClass) || QsIFragment.class.isAssignableFrom(aClass)
                                            || QsListAdapterItem.class.isAssignableFrom(aClass) || QsRecycleAdapterItem.class.isAssignableFrom(aClass)
                                            || QsDialogFragment.class.isAssignableFrom(aClass)) {
                                        viewCache.put(aClass, new ViewBindData(aClass));
                                    }
                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    long end = System.nanoTime();
                    Log.e("ViewBindHelper", "init...... cache size:" + viewCache.size() + ", use time:" + (end - start) / 1000000f + "ms");
                }
            });
        }
    }

    private static ViewBindData getBindData(Class<?> clazz) {
        ViewBindData cacheBindData = viewCache.get(clazz);
        if (cacheBindData == null) {
            cacheBindData = new ViewBindData(clazz);
            viewCache.put(clazz, cacheBindData);
            if (QsHelper.getInstance().getApplication().isLogOpen()) {
                L.i(cacheBindData.targetName, "create new ViewBindData by class, cache size:" + viewCache.size());
            }
        }
        return cacheBindData;
    }

    public static void bindBundle(Object target, Bundle bundle) {
        if (target == null || bundle == null) return;
        Class<?> clazz = target.getClass();
        ViewBindData bindData = getBindData(clazz);
        Map<Field, BindBundle> bundleFieldMap = bindData.bundleFieldMap;
        if (bundleFieldMap != null) {
            for (Field field : bundleFieldMap.keySet()) {
                BindBundle ann = bundleFieldMap.get(field);
                Object value = bundle.get(ann.value());
                if (value != null) {
                    setFieldValue(target, field, value);
                } else {
                    L.e(bindData.targetName, "not found key(" + ann.value() + ")in Bundle !");
                }
            }
        }
    }

    public static void bindView(final Object target, View rootView) {
        if (target == null || rootView == null) return;
        Class<?> clazz = target.getClass();
        final ViewBindData bindData = getBindData(clazz);
        Map<Field, Bind> viewFieldMap = bindData.viewFieldMap;
        if (viewFieldMap != null) {
            for (Field field : viewFieldMap.keySet()) {
                Bind bind = viewFieldMap.get(field);
                View view = rootView.findViewById(bind.value());
                if (view != null) {
                    setFieldValue(target, field, view);
                } else {
                    L.e(bindData.targetName, "Invalid @Bind(" + field.getName() + ")view not found !");
                }
            }
        }

        if (bindData.onViewClickMethod != null) {
            OnClick annotation = bindData.onViewClickMethod.getAnnotation(OnClick.class);
            if (annotation != null) {
                int[] values = annotation.value();
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
                for (int value : values) {
                    final View view = rootView.findViewById(value);
                    if (view != null) {
                        view.setOnClickListener(listener);
                    } else {
                        L.e(bindData.targetName, "Invalid @OnClick(id:" + value + ") view not found !");
                    }
                }
            }
        }
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
