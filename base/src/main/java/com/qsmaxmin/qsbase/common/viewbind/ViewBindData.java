package com.qsmaxmin.qsbase.common.viewbind;

import android.view.View;

import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.utils.QsHelper;
import com.qsmaxmin.qsbase.common.viewbind.annotation.Bind;
import com.qsmaxmin.qsbase.common.viewbind.annotation.BindBundle;
import com.qsmaxmin.qsbase.common.viewbind.annotation.OnClick;

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
public final class ViewBindData {
    String              targetName = "ViewBindData";
    Map<Field, Integer> viewFieldMap;
    Map<Field, String>  bundleFieldMap;
    Method              onViewClickMethod;
    int[]               clickIds;


    /**
     * 貌似field的getAnnotations方法比较耗时，所以只调用一次
     */
    ViewBindData(Class<?> target) {
        if (L.isEnable()) {
            targetName = target.getSimpleName();
        }

        Field[] fields = target.getDeclaredFields();
        if (fields != null && fields.length > 0) {
            out:
            for (Field field : fields) {
                Annotation[] annotations = field.getAnnotations();
                if (annotations == null || annotations.length <= 0) continue;
                for (Annotation ann : annotations) {
                    if (ann instanceof Bind) {
                        if (viewFieldMap == null) viewFieldMap = new HashMap<>();
                        field.setAccessible(true);
                        viewFieldMap.put(field, ((Bind) ann).value());
                        continue out;
                    } else if (ann instanceof BindBundle) {
                        if (bundleFieldMap == null) bundleFieldMap = new HashMap<>();
                        field.setAccessible(true);
                        bundleFieldMap.put(field, ((BindBundle) ann).value());
                        continue out;
                    }
                }
            }
        }

        try {
            Method method = target.getDeclaredMethod("onViewClick", View.class);
            if (method != null) {
                OnClick onClick = method.getAnnotation(OnClick.class);
                if (onClick != null && onClick.value().length > 0) {
                    clickIds = onClick.value();
                    method.setAccessible(true);
                    this.onViewClickMethod = method;
                }
            }
        } catch (NoSuchMethodException e) {
            L.i(targetName, "never override method:onViewClick(View view), When you need to add click events, you must rewrite it.");
        }
    }
}
