package com.qsmaxmin.qsbase.common.aspect;

import android.os.Looper;

import com.qsmaxmin.qsbase.common.exception.QsException;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.utils.QsHelper;
import com.qsmaxmin.qsbase.mvp.presenter.QsPresenter;

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
            QsHelper.post(new Runnable() {
                @Override public void run() {
                    L.i("ThreadAspect", joinPoint.toShortString() + " in main thread... ");
                    startOriginalMethod(joinPoint);
                }
            });
        }
        return null;
    }

    @Around(POINTCUT_METHOD_HTTP) public Object onCheckNetHttpExecutor(final ProceedingJoinPoint joinPoint) throws Throwable {
        QsHelper.executeInHttpThread(new Runnable() {
            @Override public void run() {
                L.i("ThreadAspect", joinPoint.toShortString() + " in http thread... ");
                startOriginalMethod(joinPoint);
            }
        });
        return null;
    }

    @Around(POINTCUT_METHOD_WORK) public Object onWorkExecutor(final ProceedingJoinPoint joinPoint) throws Throwable {
        QsHelper.executeInWorkThread(new Runnable() {
            @Override public void run() {
                L.i("ThreadAspect", joinPoint.toShortString() + " in work thread... ");
                startOriginalMethod(joinPoint);
            }
        });
        return null;
    }

    @Around(POINTCUT_METHOD_SINGLE_WORK) public Object onSingleWorkExecutor(final ProceedingJoinPoint joinPoint) throws Throwable {
        QsHelper.executeInSingleThread(new Runnable() {
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
            final Object target = joinPoint.getTarget();
            if (target instanceof QsPresenter) {
                final QsPresenter presenter = (QsPresenter) target;
                if (!presenter.isViewDetach()) {
                    if (L.isEnable()) L.e("ThreadAspect", "received error..." + joinPoint.toShortString() + ", handle to:" + target.getClass().getName());
                    QsHelper.post(new Runnable() {
                        @Override public void run() {
                            presenter.methodError(e0);
                        }
                    });
                } else {
                    if (L.isEnable()) L.e("ThreadAspect", "received error, but view is destroy, not handle error..." + joinPoint.toShortString());
                }
            } else {
                try {
                    if (L.isEnable()) L.e("ThreadAspect", "method received error..." + joinPoint.toShortString() + ", try handle to:" + target.getClass().getName());
                    final Method methodError = target.getClass().getMethod("methodError", QsException.class);
                    QsHelper.post(new Runnable() {
                        @Override public void run() {
                            try {
                                methodError.invoke(target, e0);
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }
                    });
                } catch (NoSuchMethodException e2) {
                    L.e("ThreadAspect", "no method(pub void methodError(QsException e)) find in " + target.getClass().getName() + ", cannot handle error!" +
                            "\nmethod info:" + joinPoint.toShortString() +
                            "\nYou can also start the asynchronous thread request network by yourself(QsHelper.getHttpHelper().create(XXX.class, Object o))," +
                            "\nAnd use try catch to package the network request code and handle the exceptions by yourself.");
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }
}
