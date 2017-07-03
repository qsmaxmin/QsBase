package com.qsmaxmin.qsbase.common.aspect;

import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.utils.QsHelper;
import com.qsmaxmin.qsbase.common.utils.permission.PermissionBuilder;
import com.qsmaxmin.qsbase.common.utils.permission.PermissionUtils;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.util.concurrent.CountDownLatch;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/7/3 10:11
 * @Description
 */
@Aspect
public class PermissionAspect {

    private static final String POINTCUT_METHOD_DEFAULT = "execution(@com.qsmaxmin.qsbase.common.aspect.Permission * *(..)) && @annotation(permission)";


    @Around(POINTCUT_METHOD_DEFAULT) public Object onPermissionExecute(final ProceedingJoinPoint joinPoint, Permission annotation) throws Throwable {
        String value = annotation.value();
        FragmentActivity activity = QsHelper.getInstance().getScreenHelper().currentActivity();
        L.i("==========>>>>>", value);
        final CountDownLatch latch = new CountDownLatch(1);
        final Object[] result = new Object[1];
        if (!TextUtils.isEmpty(value) && activity != null) {//
            PermissionUtils.getInstance().createBuilder()//
                    .addWantPermission(value)//
                    .setActivity(activity)//
                    .setShowCustomDialog(true)//
                    .setListener(new PermissionBuilder.PermissionListener() {
                        @Override public void onPermissionCallback(int requestCode, boolean isGrantedAll) {
                            if (isGrantedAll) {
                                try {
                                    result[0] = joinPoint.proceed();
                                } catch (Throwable throwable) {
                                    throwable.printStackTrace();
                                } finally {
                                    latch.countDown();
                                }
                            }
                        }
                    }).start();
            joinPoint.proceed();
        }
        latch.await();
        return result[0];
    }
}
