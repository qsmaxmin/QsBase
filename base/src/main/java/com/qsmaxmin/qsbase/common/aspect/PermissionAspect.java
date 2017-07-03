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

import java.util.ArrayList;
import java.util.Collections;

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
     * 在子线程中请求权限
     */
    private void startRequestPermission(final ProceedingJoinPoint joinPoint, Permission permission) {
        final String value = permission.value();
        String[] values = permission.values();

        ArrayList<String> arrayList = new ArrayList<>();

        if (TextUtils.isEmpty(value) && values.length < 1) {
            return;
        }
        if (!TextUtils.isEmpty(value)) arrayList.add(value);
        if (values.length > 0) Collections.addAll(arrayList, values);
        FragmentActivity activity = QsHelper.getInstance().getScreenHelper().currentActivity();
        if (!arrayList.isEmpty() && activity != null) {
            PermissionBuilder builder = PermissionUtils.getInstance().createBuilder();
            for (String permissionStr : arrayList) {
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
                        }
                    });

            builder.start();
        }
    }
}
