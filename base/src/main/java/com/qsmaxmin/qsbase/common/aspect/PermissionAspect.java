package com.qsmaxmin.qsbase.common.aspect;

import android.support.v4.app.FragmentActivity;

import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.utils.QsHelper;
import com.qsmaxmin.qsbase.common.utils.permission.PermissionBuilder;
import com.qsmaxmin.qsbase.common.utils.permission.PermissionCallbackListener;
import com.qsmaxmin.qsbase.common.utils.permission.PermissionUtils;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/7/3 10:11
 * @Description
 */
@Aspect
public class PermissionAspect {

    private static final String POINTCUT_METHOD_DEFAULT = "execution(@com.qsmaxmin.qsbase.common.aspect.Permission * *(..)) && @annotation(permission)";


    @Around(POINTCUT_METHOD_DEFAULT) public Object onPermissionExecute(final ProceedingJoinPoint joinPoint, final Permission permission) throws Throwable {
        startRequestPermission(joinPoint, permission);
        return null;
    }

    /**
     * 申请权限
     */
    private void startRequestPermission(final ProceedingJoinPoint joinPoint, final Permission permission) {
        if (permission == null || PermissionUtils.isPermissionGranted(permission.value())) {
            if (L.isEnable()) L.i("PermissionAspect", "permission is all granted.......");
            proceed(joinPoint);
            return;
        }

        String[] values = permission.value();
        FragmentActivity activity = QsHelper.getScreenHelper().currentActivity();
        if (activity != null) {
            PermissionBuilder builder = new PermissionBuilder();
            for (String permissionStr : values) {
                builder.addWantPermission(permissionStr);
            }
            builder.setActivity(activity)//
                    .setForceGoOn(permission.forceGoOn())
                    .setShowCustomDialog(permission.showCustomDialog())//
                    .setListener(new PermissionCallbackListener() {
                        @Override public void onPermissionCallback() {
                            proceed(joinPoint);
                        }
                    });
            PermissionUtils.startRequestPermission(builder);
        }
    }

    private void proceed(ProceedingJoinPoint joinPoint) {
        if (L.isEnable()) L.i("PermissionAspect", "proceed.......");
        try {
            joinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
