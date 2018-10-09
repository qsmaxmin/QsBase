package com.qsmaxmin.qsbase.common.viewbind;

import android.os.Bundle;
import android.view.View;

import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.utils.QsHelper;
import com.qsmaxmin.qsbase.common.viewbind.annotation.Bind;
import com.qsmaxmin.qsbase.common.viewbind.annotation.BindBundle;
import com.qsmaxmin.qsbase.common.viewbind.annotation.OnClick;
import com.qsmaxmin.qsbase.common.widget.dialog.QsDialogFragment;
import com.qsmaxmin.qsbase.mvp.QsIView;
import com.qsmaxmin.qsbase.mvp.adapter.QsListAdapterItem;
import com.qsmaxmin.qsbase.mvp.adapter.QsRecycleAdapterItem;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/6/21 16:40
 * @Description View层控件绑定，控件点击事件，Bundle值寻找
 */
public final class ViewBindHelper {
    private Object                 mTarget;
    private Map<Field, Bind>       viewFieldMap;
    private Map<Field, BindBundle> bundleFieldMap;

    /**
     * 貌似field的getAnnotations方法比较耗时，所以只调用一次
     */
    public ViewBindHelper(Object target) {
        this.mTarget = target;
        Field[] fields = target.getClass().getDeclaredFields();
        if (fields != null && fields.length > 0) {
            out:
            for (Field field : fields) {
                Annotation[] annotations = field.getAnnotations();
                if (annotations == null || annotations.length <= 0) continue;
                for (Annotation ann : annotations) {
                    if (ann instanceof Bind) {
                        if (viewFieldMap == null) viewFieldMap = new HashMap<>();
                        viewFieldMap.put(field, (Bind) ann);
                        continue out;
                    } else if (ann instanceof BindBundle) {
                        if (bundleFieldMap == null) bundleFieldMap = new HashMap<>();
                        bundleFieldMap.put(field, (BindBundle) ann);
                        continue out;
                    }
                }
            }
        }
    }

    public void bindBundle(Bundle bundle) {
        if (bundleFieldMap == null || bundle == null) return;
        for (Field field : bundleFieldMap.keySet()) {
            BindBundle ann = bundleFieldMap.get(field);
            Object value = bundle.get(ann.value());
            if (value != null) {
                setFieldValue(field, value);
            } else {
                L.e(getTag(), "not found key:" + ann.value() + " in Bundle, target:" + mTarget.getClass().getSimpleName());
            }
        }
    }

    public void bindView(View rootView) {
        if (rootView == null) return;
        if (viewFieldMap != null) {
            for (Field field : viewFieldMap.keySet()) {
                Bind bind = viewFieldMap.get(field);
                View view = rootView.findViewById(bind.value());
                if (view != null) {
                    setFieldValue(field, view);
                } else {
                    L.e(getTag(), "Invalid @Bind for " + field.getName() + ", view not found !");
                }
            }
        }
        final Object target = mTarget;
        Method targetMethod = null;
        try {
            targetMethod = target.getClass().getDeclaredMethod("onViewClick", View.class);
        } catch (NoSuchMethodException e) {
            L.i(getTag(), "never override method:onViewClick(View view)");
        }
        if (targetMethod == null) return;
        OnClick annotation = targetMethod.getAnnotation(OnClick.class);
        if (annotation == null) return;
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
                    L.e(getTag(), "Invalid @OnClick target, support only Activity Fragment QsListAdapterItem and QsRecycleAdapterItem, not support!");
                }
            }
        };
        for (int value : values) {
            final View view = rootView.findViewById(value);
            if (view != null) {
                view.setOnClickListener(listener);
            } else {
                L.e(getTag(), "Invalid @OnClick for id:" + value + ", view not found ! ");
            }
        }
    }

    public void release() {
        mTarget = null;
        if (viewFieldMap != null) {
            viewFieldMap.clear();
            viewFieldMap = null;
        }
        if (bundleFieldMap != null) {
            bundleFieldMap.clear();
            bundleFieldMap = null;
        }
    }

    private String getTag() {
        return (mTarget == null || QsHelper.getInstance().getApplication().isLogOpen()) ? "ViewBindHelper" : mTarget.getClass().getName();
    }

    private void setFieldValue(Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(mTarget, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
