package com.qsmaxmin.qsbase.common.utils;


import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.mvp.QsIView;
import com.qsmaxmin.qsbase.mvp.presenter.Presenter;
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

        Presenter presenterAnn = viewClass.getAnnotation(Presenter.class);
        if (presenterAnn != null) {
            Class presenterClass = presenterAnn.value();
            try {
                presenterImpl = (P) presenterClass.newInstance();
                presenterImpl.initPresenter(iView);
                return presenterImpl;
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            String viewName = QsHelper.getInstance().getApplication().isLogOpen() ? iView.getClass().getSimpleName() : "QsIView";
            L.i("PresenterUtils", viewName + "未添加@Presenter注解，创建Presenter将使用泛型第一个参数");
        }

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
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        String viewName = QsHelper.getInstance().getApplication().isLogOpen() ? iView.getClass().getSimpleName() : "QsIView";
        L.e("PresenterUtils", viewName + "未添加泛型Presenter类，将使用默认的QsPresenter");
        presenterImpl = (P) new QsPresenter<>();
        presenterImpl.initPresenter(iView);
        return presenterImpl;
    }
}
