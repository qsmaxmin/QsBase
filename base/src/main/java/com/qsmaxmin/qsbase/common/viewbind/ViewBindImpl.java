package com.qsmaxmin.qsbase.common.viewbind;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.viewbind.annotation.Bind;
import com.qsmaxmin.qsbase.common.viewbind.annotation.OnClick;
import com.qsmaxmin.qsbase.common.widget.dialog.QsDialogFragment;
import com.qsmaxmin.qsbase.mvp.QsABActivity;
import com.qsmaxmin.qsbase.mvp.QsActivity;
import com.qsmaxmin.qsbase.mvp.QsIView;
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
    private static final String              TAG     = "ViewBindImpl";
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
        IGNORED.add(QsDialogFragment.class);
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

    private void injectObject(final Object handler, Class<?> clazz, ViewFinder finder) {
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
                        View view = finder.findViewById(bind.value());
                        if (view != null) {
                            field.setAccessible(true);
                            field.set(handler, view);
                        } else {
                            L.i("ViewBindImpl", "Invalid @Bind for " + clazz.getSimpleName() + "." + field.getName());
                        }
                    } catch (Throwable ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }

        Method targetMethod = null;
        try {
            targetMethod = clazz.getDeclaredMethod("onViewClick", View.class);
        } catch (NoSuchMethodException e) {
            L.i(TAG, "never override method:onViewClick(View view)");
        }
        if (targetMethod != null) {
            OnClick annotation = targetMethod.getAnnotation(OnClick.class);
            if (annotation != null) {
                int[] values = annotation.value();
                for (int value : values) {
                    final View view = finder.findViewById(value);
                    if (view != null) {
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override public void onClick(View v) {
                                if (handler instanceof QsIView) {
                                    ((QsIView) handler).onViewClick(view);
                                } else if (handler instanceof QsListAdapterItem) {
                                    ((QsListAdapterItem) handler).onViewClick(view);
                                } else if (handler instanceof QsRecycleAdapterItem) {
                                    ((QsRecycleAdapterItem) handler).onViewClick(view);
                                } else if (handler instanceof QsDialogFragment) {
                                    ((QsDialogFragment) handler).onViewClick(view);
                                } else {
                                    L.e("", "Invalid @OnClick target, support only Activity Fragment QsListAdapterItem and QsRecycleAdapterItem, not support:" + handler.getClass().getSimpleName());
                                }
                            }
                        });
                    } else {
                        L.e(TAG, "@OnClick....view is null, id:" + value);
                    }
                }
            }
        }
    }
}
