package com.qsmaxmin.qsbase.common.aspect;

import android.os.Looper;

import com.qsmaxmin.qsbase.common.exception.QsException;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.utils.QsHelper;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.lang.reflect.Method;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/6/28 14:09
 * @Description
 */
@Aspect
public class ThreadAspect {

    private static final String POINTCUT_METHOD_MAIN        = "execution(@com.qsmaxmin.qsbase.common.aspect.ThreadPoint(com.qsmaxmin.qsbase.common.aspect.ThreadType.MAIN) * *(..))";
    private static final String POINTCUT_METHOD_HTTP        = "execution(@com.qsmaxmin.qsbase.common.aspect.ThreadPoint(com.qsmaxmin.qsbase.common.aspect.ThreadType.HTTP) * *(..))";
    private static final String POINTCUT_METHOD_WORK        = "execution(@com.qsmaxmin.qsbase.common.aspect.ThreadPoint(com.qsmaxmin.qsbase.common.aspect.ThreadType.WORK) * *(..))";
    private static final String POINTCUT_METHOD_SINGLE_WORK = "execution(@com.qsmaxmin.qsbase.common.aspect.ThreadPoint(com.qsmaxmin.qsbase.common.aspect.ThreadType.SINGLE_WORK) * *(..))";


    @Around(POINTCUT_METHOD_MAIN) public Object onMainExecutor(final ProceedingJoinPoint joinPoint) throws Throwable {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            return joinPoint.proceed();
        } else {
            QsHelper.getInstance().getThreadHelper().getMainThread().execute(new Runnable() {
                @Override public void run() {
                    L.i("ThreadAspect", joinPoint.toShortString() + " in main thread... ");
                    startOriginalMethod(joinPoint);
                }
            });
        }
        return null;
    }

    @Around(POINTCUT_METHOD_HTTP) public Object onCheckNetHttpExecutor(final ProceedingJoinPoint joinPoint) throws Throwable {
        QsHelper.getInstance().getThreadHelper().getHttpThreadPoll().execute(new Runnable() {
            @Override public void run() {
                L.i("ThreadAspect", joinPoint.toShortString() + " in http thread... ");
                startOriginalMethod(joinPoint);
            }
        });
        return null;
    }

    @Around(POINTCUT_METHOD_WORK) public Object onWorkExecutor(final ProceedingJoinPoint joinPoint) throws Throwable {
        QsHelper.getInstance().getThreadHelper().getWorkThreadPoll().execute(new Runnable() {
            @Override public void run() {
                L.i("ThreadAspect", joinPoint.toShortString() + " in work thread... ");
                startOriginalMethod(joinPoint);
            }
        });
        return null;
    }

    @Around(POINTCUT_METHOD_SINGLE_WORK) public Object onSingleWorkExecutor(final ProceedingJoinPoint joinPoint) throws Throwable {
        QsHelper.getInstance().getThreadHelper().getSingleThreadPoll().execute(new Runnable() {
            @Override public void run() {
                L.i("ThreadAspect", joinPoint.toShortString() + " in single work thread... ");
                startOriginalMethod(joinPoint);
            }
        });
        return null;
    }

    /**
     * 执行原始方法，将异常映射到{@link com.qsmaxmin.qsbase.mvp.presenter.QsPresenter#methodError(QsException)}
     */
    private void startOriginalMethod(ProceedingJoinPoint joinPoint) {
        try {
            joinPoint.proceed();
        } catch (final QsException e0) {
            try {
                final Object target = joinPoint.getTarget();
                final Method methodError = target.getClass().getMethod("methodError", QsException.class);
                if (methodError != null) QsHelper.getInstance().getThreadHelper().getMainThread().execute(new Runnable() {
                    @Override public void run() {
                        try {
                            methodError.invoke(target, e0);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                });
            } catch (NoSuchMethodException e2) {
                e2.printStackTrace();
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
