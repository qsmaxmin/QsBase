package com.qsmaxmin.qsbase.common.utils.permission;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.SparseArray;

import com.qsmaxmin.qsbase.R;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.utils.QsHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/3/7 12:36
 * @Description
 */

public class PermissionHelper {
    private static final String                         TAG = "PermissionHelper";
    private              int                            requestCode;
    private              SparseArray<PermissionBuilder> maps;

    public PermissionHelper() {
        maps = new SparseArray<>();
    }

    public void release() {
        if (maps != null) {
            maps.clear();
            maps = null;
        }
    }

    public boolean isPermissionGranted(String... permissionArr) {
        if (permissionArr == null) return true;
        for (String permission : permissionArr) {
            if (!(ContextCompat.checkSelfPermission(QsHelper.getApplication(), permission) == PackageManager.PERMISSION_GRANTED)) return false;
        }
        return true;
    }

    public void startRequestPermission(PermissionBuilder builder) {
        if (builder == null) return;
        if (builder.getActivity() == null) {
            L.e(TAG, "activity can not be null, please setActivity()");
            return;
        }
        if (builder.getWantPermissionArr().size() == 0) {
            L.e(TAG, "you has not addWantPermission(String)");
            return;
        }
        L.i(TAG, "startRequestPermission:" + builder.toString());
        ArrayList<String> unGrantedPermission = getUnGrantedPermissionArr(builder.getWantPermissionArr());
        if (unGrantedPermission.size() > 0) {
            requestCode++;
            L.i(TAG, "start request permission  requestCode=" + requestCode + "   wantPermission=" + unGrantedPermission.toString());
            builder.setRequestCode(requestCode);
            maps.put(requestCode, builder);
            String[] permissionArr = new String[unGrantedPermission.size()];
            ActivityCompat.requestPermissions(builder.getActivity(), unGrantedPermission.toArray(permissionArr), requestCode);
        } else {
            L.i(TAG, "all permission is granted....");
            if (builder.getListener() != null) {
                builder.getListener().onPermissionCallback();
            }
        }
    }

    private ArrayList<String> getUnGrantedPermissionArr(List<String> list) {
        ArrayList<String> arrayList = new ArrayList<>();
        for (String permission : list) {
            if (!isPermissionGranted(permission)) {//用户未授权
                arrayList.add(permission);
            }
        }
        return arrayList;
    }


    /*------------------------------------- 以下是申请权限回调的数据解析 ---------------------------------------*/
    public void parsePermissionResultData(int requestCode, String[] permissions, int[] grantResults, Activity activity) {
        if (maps == null) return;
        PermissionBuilder builder = maps.get(requestCode);
        maps.remove(requestCode);
        if (builder == null) return;
        boolean grantedAll = true;
        ArrayList<String> unGrantedArr = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {//用户不同意，向用户展示该权限作用
                grantedAll = false;
                if (i < permissions.length) {
                    L.i(TAG, "user un granted permission:" + permissions[i]);
                    unGrantedArr.add(permissions[i]);
                }
            }
        }
        if (grantedAll) {
            L.i(TAG, "user granted all permission....");
            if (builder.getListener() != null) builder.getListener().onPermissionCallback();

        } else {
            if (builder.isForceGoOn() && builder.getListener() != null) {
                builder.getListener().onPermissionCallback();
            }

            if (builder.isShowCustomDialog()) {
                boolean shouldShowDialog = false;
                ArrayList<String> shouldShowDialogArr = new ArrayList<>();
                for (String unGrantedStr : unGrantedArr) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, unGrantedStr)) {
                        shouldShowDialog = true;
                        shouldShowDialogArr.add(unGrantedStr);
                    }
                }
                if (shouldShowDialog) {
                    showPermissionTipsDialog(activity, shouldShowDialogArr);
                }
            }

        }
    }

    /**
     * 当系统提醒请求权限的对话框勾选不再提醒时，弹出的自定义对话框
     */
    private void showPermissionTipsDialog(Activity activity, ArrayList<String> permissionList) {
        if (activity == null || permissionList == null || permissionList.size() < 1) {
            return;
        }
        String message = getPermissionDialogMessage(permissionList);
        if (TextUtils.isEmpty(message)) return;
        L.i(TAG, "勾选了不在提醒所以弹出自定义对话框：" + permissionList.toString());
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(activity);
        builder.setTitle(QsHelper.getString(android.R.string.dialog_alert_title))//
                .setMessage(message)//
                .setPositiveButton(QsHelper.getString(android.R.string.ok), new DialogInterface.OnClickListener() {//
                    @Override public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.fromParts("package", QsHelper.getApplication().getPackageName(), null));
                        QsHelper.getApplication().startActivity(intent);
                        dialog.cancel();
                    }
                }).setNegativeButton(QsHelper.getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).show();

    }

    private String getPermissionDialogMessage(ArrayList<String> list) {
        if (list == null || list.size() < 1) return null;
        StringBuilder sb = new StringBuilder();
        sb.append("（");
        for (int i = 0, size = list.size(); i < size; i++) {
            sb.append(getPermissionName(list.get(i)));
            if (i != size - 1) {
                sb.append("，");
            }
        }
        return sb.append("）").append(QsHelper.getString(R.string.request_permission_end)).toString();
    }


    private String getPermissionName(String permission) {
        switch (permission) {
            case Manifest.permission.ACCESS_COARSE_LOCATION:
                return QsHelper.getString(R.string.request_location_permission);
            case Manifest.permission.READ_EXTERNAL_STORAGE:
                return QsHelper.getString(R.string.request_read_external_storage_permission);
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                return QsHelper.getString(R.string.request_write_external_storage_permission);
            case Manifest.permission.READ_CONTACTS:
                return QsHelper.getString(R.string.request_constants_permission);
            case Manifest.permission.CALL_PHONE:
                return QsHelper.getString(R.string.request_call_permission);
            case Manifest.permission.CAMERA:
                return QsHelper.getString(R.string.request_camera_permission);
            case Manifest.permission.RECORD_AUDIO:
                return QsHelper.getString(R.string.request_record_audio_permission);
            case Manifest.permission.READ_PHONE_STATE:
                return QsHelper.getString(R.string.request_read_phone_state_permission);
            default:
                return QsHelper.getString(R.string.request_other_permission);
        }
    }
}
