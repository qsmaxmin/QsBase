package com.qsmaxmin.qsbase.common.utils;


import com.qsmaxmin.qsbase.mvp.QsIView;
import com.qsmaxmin.qsbase.mvp.presenter.QsPresenter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/6/20 16:40
 * @Description
 */
public final class PresenterUtils {
    /**
     * 创建业务类
     */
    public static <P extends QsPresenter, V extends QsIView> P createPresenter(V iView) {
        Class<? extends QsIView> viewClass = iView.getClass();
        P presenterImpl;
        Type genericSuperclass = viewClass.getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType) {
            Type[] typeArguments = ((ParameterizedType) genericSuperclass).getActualTypeArguments();
            if (typeArguments != null && typeArguments.length > 0) {
                Class typeArgument = (Class) typeArguments[0];
                try {
                    presenterImpl = (P) typeArgument.newInstance();
                    presenterImpl.initPresenter(iView);
                    return presenterImpl;
                } catch (InstantiationException e) {
                    throw new IllegalArgumentException(String.valueOf(viewClass) + "，实例化异常！");
                } catch (IllegalAccessException e) {
                    throw new IllegalArgumentException(String.valueOf(viewClass) + "，访问权限异常！");
                }
            }
        }
        presenterImpl = (P) new QsPresenter<>();
        presenterImpl.initPresenter(iView);
        return presenterImpl;
    }
}
