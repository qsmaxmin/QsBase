package com.qsmaxmin.qsbase.plugin.permission;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.SparseArray;

import com.qsmaxmin.qsbase.LifeCycleCallbacksAdapter;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.utils.QsHelper;
import com.qsmaxmin.qsbase.common.widget.toast.QsToast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * @CreateBy qsmaxmin
 * @Date 2017/3/7 12:36
 * @Description
 */

public class PermissionHelper {
    private static final String                TAG = "PermissionHelper";
    private static       PermissionHelper      helper;
    private              int                   requestCode;
    private              SparseArray<DataItem> maps;

    private PermissionHelper() {
        maps = new SparseArray<>();
    }

    public static PermissionHelper getInstance() {
        if (helper == null) {
            synchronized (PermissionHelper.class) {
                if (helper == null) helper = new PermissionHelper();
            }
        }
        return helper;
    }

    public static void release() {
        if (helper != null) {
            helper.maps = null;
        }
    }

    public static boolean isPermissionGranted(String... permissionArr) {
        if (permissionArr == null) return true;
        for (String permission : permissionArr) {
            if (!(ContextCompat.checkSelfPermission(QsHelper.getApplication(), permission) == PackageManager.PERMISSION_GRANTED)) return false;
        }
        return true;
    }

    public void startRequestPermission(PermissionCallbackListener listener, String... permissions) {
        startRequestPermission(listener, false, null, permissions);
    }

    public void startRequestPermission(PermissionCallbackListener listener, boolean showToastWhenReject, String toastText, String... permissions) {
        Activity activity = QsHelper.getScreenHelper().currentActivity();
        if (permissions == null || permissions.length == 0 || activity == null) return;
        L.i(TAG, "startRequestPermission:" + Arrays.toString(permissions));

        activity.getApplication().registerActivityLifecycleCallbacks(new LifeCycleCallbacksAdapter() {
            @Override public void onActivityDestroyed(Activity activity) {
                activity.getApplication().unregisterActivityLifecycleCallbacks(this);
                if (maps != null && maps.size() > 0) {
                    int size = maps.size();
                    ArrayList<Integer> keyList = new ArrayList<>();
                    for (int index = 0; index < size; index++) {
                        if (maps.valueAt(index).getActivity() == activity) {
                            keyList.add(maps.keyAt(index));
                        }
                    }
                    for (int key : keyList) {
                        maps.remove(key);
                    }
                }
            }
        });

        String[] unGrantedPermissions = getUnGrantedPermissionArr(permissions);
        if (unGrantedPermissions != null && unGrantedPermissions.length > 0) {
            requestCode++;
            L.i(TAG, "start request permission  requestCode:" + requestCode + ", unGrantedPermissions:" + Arrays.toString(unGrantedPermissions));

            DataItem builder = new DataItem();
            builder.setActivity(activity);
            builder.setListener(listener);
            builder.setShowToastWhenReject(showToastWhenReject);
            builder.setToastText(toastText);
            maps.put(requestCode, builder);

            ActivityCompat.requestPermissions(builder.getActivity(), unGrantedPermissions, requestCode);
        } else {
            L.i(TAG, "all permission is granted....");
            if (listener != null) {
                int[] result = new int[permissions.length];
                Arrays.fill(result, PackageManager.PERMISSION_GRANTED);
                listener.onPermissionCallback(true, false, permissions, result);
            }
        }
    }

    private String[] getUnGrantedPermissionArr(String... permissionArr) {
        ArrayList<String> arrayList = null;
        for (String permission : permissionArr) {
            if (!isPermissionGranted(permission)) {
                if (arrayList == null) arrayList = new ArrayList<>();
                arrayList.add(permission);
            }
        }
        return arrayList != null ? arrayList.toArray(new String[0]) : null;
    }


    /**
     * @see com.qsmaxmin.qsbase.mvp.QsActivity#onRequestPermissionsResult(int, String[], int[])
     */
    public static void parsePermissionResultData(int requestCode, String[] permissions, int[] grantResults) {
        if (helper == null || helper.maps == null) return;

        SparseArray<DataItem> maps = helper.maps;
        DataItem builder = maps.get(requestCode);
        if (builder == null) return;
        maps.remove(requestCode);

        boolean grantedAll = true;
        ArrayList<String> unGrantedList = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {//用户不同意，向用户展示该权限作用
                grantedAll = false;
                if (i < permissions.length) {
                    L.i(TAG, "user un granted permission:" + permissions[i]);
                    unGrantedList.add(permissions[i]);
                }
            }
        }

        boolean shouldShowDialog = !grantedAll && shouldShowRequestPermissionRationale(builder.getActivity(), unGrantedList);
        if (!grantedAll && builder.isShowToastWhenReject() && !TextUtils.isEmpty(builder.getToastText())) {
            QsToast.show(builder.getToastText());
        }
        if (builder.getListener() != null) {
            builder.getListener().onPermissionCallback(grantedAll, shouldShowDialog, permissions, grantResults);
        }
    }

    private static boolean shouldShowRequestPermissionRationale(Activity activity, List<String> unGrantedList) {
        try {
            for (String unGrantedStr : unGrantedList) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, unGrantedStr)) {
                    return true;
                }
            }
        } catch (Exception ignored) {
        }
        return false;
    }
}
