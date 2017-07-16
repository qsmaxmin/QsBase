package com.qsmaxmin.qsbase.common.aspect;

import android.support.v4.app.FragmentActivity;

import com.qsmaxmin.qsbase.common.utils.QsHelper;
import com.qsmaxmin.qsbase.common.utils.permission.PermissionBuilder;
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
        if (permission == null) return;
        String[] values = permission.value();
        if (values.length < 1) {
            return;
        }

        FragmentActivity activity = QsHelper.getInstance().getScreenHelper().currentActivity();
        if (activity != null) {
            PermissionBuilder builder = PermissionUtils.getInstance().createBuilder();
            for (String permissionStr : values) {
                builder.addWantPermission(permissionStr);
            }
            builder.setActivity(activity)//
                    .setShowCustomDialog(true)//
                    .setListener(new PermissionBuilder.PermissionListener() {
                        @Override public void onPermissionCallback(int requestCode, boolean isGrantedAll) {
                            if (isGrantedAll) {
                                try {
                                    joinPoint.proceed();
                                } catch (Throwable throwable) {
                                    throwable.printStackTrace();
                                }
                            }
                            if (permission.needCallback()) {
                                Object[] args = joinPoint.getArgs();
                                if (args != null) {
                                    for (Object object : args) {
                                        if (object instanceof PermissionBuilder.PermissionListener) {
                                            ((PermissionBuilder.PermissionListener) object).onPermissionCallback(requestCode, isGrantedAll);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    });

            builder.start();
        }
    }
}
