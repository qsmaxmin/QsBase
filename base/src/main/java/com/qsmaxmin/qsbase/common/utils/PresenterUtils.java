package com.qsmaxmin.qsbase.common.utils;


import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.mvp.QsIView;
import com.qsmaxmin.qsbase.mvp.presenter.Presenter;
import com.qsmaxmin.qsbase.mvp.presenter.QsPresenter;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/6/20 16:40
 * @Description
 */
public final class PresenterUtils {
    /**
     * 创建业务类
     */
    public static <P extends QsPresenter, V extends QsIView> P createPresenter(Class paramClazz, V iView) {
        L.i("PresenterUtils", "createPresenter()");
        P presenterImpl;
        Presenter presenter = (Presenter) paramClazz.getAnnotation(Presenter.class);
        if (presenter == null) {
            throw new IllegalArgumentException(String.valueOf(paramClazz) + "，该类没有注入Presenter！");
        }
        Class clazz;
        try {
            clazz = Class.forName(presenter.value().getName());
            L.i("PresenterUtils", "注入:" + clazz.getName());
            presenterImpl = (P) clazz.newInstance();
            presenterImpl.initPresenter(iView);
            return presenterImpl;
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(String.valueOf(paramClazz) + "，没有找到业务类！");
        } catch (InstantiationException e) {
            throw new IllegalArgumentException(String.valueOf(paramClazz) + "，实例化异常！");
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(String.valueOf(paramClazz) + "，访问权限异常！");
        }
    }
}
