package com.qsmaxmin.qsbase.common.viewbind;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.qsmaxmin.qsbase.common.viewbind.annotation.Bind;
import com.qsmaxmin.qsbase.common.viewbind.annotation.OnClick;
import com.qsmaxmin.qsbase.mvp.QsABActivity;
import com.qsmaxmin.qsbase.mvp.QsActivity;
import com.qsmaxmin.qsbase.mvp.QsViewPagerABActivity;
import com.qsmaxmin.qsbase.mvp.QsViewPagerActivity;
import com.qsmaxmin.qsbase.mvp.adapter.QsListAdapterItem;
import com.qsmaxmin.qsbase.mvp.adapter.QsRecycleAdapterItem;
import com.qsmaxmin.qsbase.mvp.adapter.QsTabViewPagerAdapter;
import com.qsmaxmin.qsbase.mvp.adapter.QsViewPagerAdapter;
import com.qsmaxmin.qsbase.mvp.fragment.QsFragment;
import com.qsmaxmin.qsbase.mvp.fragment.QsHeaderViewpagerFragment;
import com.qsmaxmin.qsbase.mvp.fragment.QsListFragment;
import com.qsmaxmin.qsbase.mvp.fragment.QsPullHeaderViewpagerFragment;
import com.qsmaxmin.qsbase.mvp.fragment.QsPullListFragment;
import com.qsmaxmin.qsbase.mvp.fragment.QsPullRecyclerFragment;
import com.qsmaxmin.qsbase.mvp.fragment.QsRecyclerFragment;
import com.qsmaxmin.qsbase.mvp.fragment.QsViewPagerFragment;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

public final class ViewBindImpl implements ViewBind {

    private static final ArrayList<Class<?>> IGNORED = new ArrayList<>();

    static {
        IGNORED.add(Object.class);
        IGNORED.add(Activity.class);
        IGNORED.add(FragmentActivity.class);
        IGNORED.add(AppCompatActivity.class);
        IGNORED.add(Fragment.class);
        IGNORED.add(android.app.Fragment.class);

        IGNORED.add(QsActivity.class);
        IGNORED.add(QsABActivity.class);
        IGNORED.add(QsViewPagerActivity.class);
        IGNORED.add(QsViewPagerABActivity.class);

        IGNORED.add(QsFragment.class);
        IGNORED.add(QsListFragment.class);
        IGNORED.add(QsPullListFragment.class);
        IGNORED.add(QsRecyclerFragment.class);
        IGNORED.add(QsPullRecyclerFragment.class);
        IGNORED.add(QsViewPagerFragment.class);
        IGNORED.add(QsHeaderViewpagerFragment.class);
        IGNORED.add(QsPullHeaderViewpagerFragment.class);

        IGNORED.add(QsViewPagerAdapter.class);
        IGNORED.add(QsTabViewPagerAdapter.class);
        IGNORED.add(QsListAdapterItem.class);
        IGNORED.add(QsRecycleAdapterItem.class);
    }

    @Override public void bind(Object handler, View view) {
        injectObject(handler, handler.getClass(), new ViewFinder(view));
    }

    private static void injectObject(Object handler, Class<?> clazz, ViewFinder finder) {
        if (clazz == null || IGNORED.contains(clazz)) {
            return;
        }
        injectObject(handler, clazz.getSuperclass(), finder);
        Field[] fields = clazz.getDeclaredFields();
        if (fields != null && fields.length > 0) {
            for (Field field : fields) {
                Class<?> fieldType = field.getType();
                if (
                /* 不注入静态字段 */     Modifier.isStatic(field.getModifiers()) ||
                /* 不注入final字段 */    Modifier.isFinal(field.getModifiers()) ||
                /* 不注入基本类型字段 */  fieldType.isPrimitive() ||
                /* 不注入数组类型字段 */  fieldType.isArray()) {
                    continue;
                }
                Bind bind = field.getAnnotation(Bind.class);
                if (bind != null) {
                    try {
                        View view = finder.findViewById(bind.value(), bind.parentId());
                        if (view != null) {
                            field.setAccessible(true);
                            field.set(handler, view);
                        } else {
                            throw new RuntimeException("Invalid @Bind for " + clazz.getSimpleName() + "." + field.getName());
                        }
                    } catch (Throwable ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }

        Method[] methods = clazz.getDeclaredMethods();
        if (methods != null && methods.length > 0) {
            for (Method method : methods) {
                if (Modifier.isStatic(method.getModifiers())) {
                    continue;
                }
                OnClick annotation = method.getAnnotation(OnClick.class);
                if (annotation != null) {
                    try {
                        int[] values = annotation.value();
                        int[] parentIds = annotation.parentId();
                        int parentIdsLen = parentIds.length;
                        for (int i = 0; i < values.length; i++) {
                            int value = values[i];
                            if (value > 0) {
                                ViewInfo info = new ViewInfo();
                                info.value = value;
                                info.parentId = parentIdsLen > i ? parentIds[i] : 0;
                                method.setAccessible(true);
                                EventListenerManager.addEventMethod(finder, info, annotation, handler, method);
                            }
                        }
                    } catch (Throwable ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }
}
