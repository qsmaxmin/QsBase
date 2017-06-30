package com.qsmaxmin.qsbase.common.aspect;

import com.qsmaxmin.qsbase.common.log.L;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/6/29 15:19
 * @Description
 */
@Aspect
public class HttpAspect {

    private static final String POINTCUT_GET  = "execution(@com.qsmaxmin.qsbase.common.aspect.GET(java.lang.String) * *(..))";
    private static final String POINTCUT_POST = "execution(@com.qsmaxmin.qsbase.common.aspect.POST(java.lang.String) * *(..))";

    @Pointcut(value = POINTCUT_GET) public void onGetPoint() {
    }

    @Around("onGetPoint()") public Object onGetExecutor(final ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        for (Object object : args) {
            L.i("==========>>>", object.getClass().getSimpleName());
        }
        return null;
    }
}
