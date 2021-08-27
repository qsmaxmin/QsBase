package com.qsmaxmin.qsbase.plugin.route;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.util.Size;
import android.util.SizeF;
import android.util.SparseArray;

import com.qsmaxmin.qsbase.common.log.L;

import java.io.Serializable;
import java.util.ArrayList;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;

/**
 * @CreateBy qsmaxmin
 * @Date 2021/8/26 10:06
 * @Description Activity跳转
 */
public class QsRoute {
    private String                targetPath;
    private Class<?>              targetClass;
    private Bundle                bundle;
    private int                   flag;
    private int                   enterAnim;
    private int                   exitAnim;
    private ActivityOptionsCompat options;

    private QsRoute() {
    }

    public static QsRoute withRoute(String routePath) {
        QsRoute route = new QsRoute();
        route.targetPath = routePath;
        return route;
    }

    public static QsRoute withClass(Class<?> clazz) {
        QsRoute route = new QsRoute();
        route.targetClass = clazz;
        return route;
    }

    public QsRoute setFlag(int flag) {
        this.flag = flag;
        return this;
    }

    public QsRoute addFlag(int flag) {
        this.flag |= flag;
        return this;
    }

    private Class<?> findClass() throws Exception {
        if (targetClass != null) return targetClass;
        Class<?> clazz = RouteDataHolder.findClass(targetPath);
        if (clazz == null) {
            throw new Exception("class not found, path:" + targetPath);
        }
        targetClass = clazz;
        return clazz;
    }

    private Intent createIntent(Context context, Class<?> target) {
        Intent intent = new Intent(context, target);
        if (flag != 0) intent.setFlags(flag);
        if (bundle != null) intent.putExtras(bundle);
        return intent;
    }

    public void start(Activity activity, int requestCode) {
        start(activity, requestCode, null);
    }

    public void start(Activity activity, int requestCode, RouteCallback callback) {
        try {
            Class<?> clazz = findClass();
            Intent intent = createIntent(activity, clazz);
            if (Activity.class.isAssignableFrom(clazz)) {
                if (options == null) {
                    if (requestCode > 0) {
                        activity.startActivityForResult(intent, requestCode);
                    } else {
                        activity.startActivity(intent);
                    }
                    if (enterAnim != 0 || exitAnim != 0) {
                        activity.overridePendingTransition(enterAnim, exitAnim);
                    }
                } else {
                    if (requestCode > 0) {
                        ActivityCompat.startActivityForResult(activity, intent, requestCode, options.toBundle());
                    } else {
                        ActivityCompat.startActivity(activity, intent, options.toBundle());
                    }
                }

            } else if (Service.class.isAssignableFrom(clazz)) {
                activity.startService(intent);
            }

            if (callback != null) {
                callback.onSuccess();
            }
        } catch (Throwable t) {
            if (callback != null) {
                callback.onFailed(t);
            }
        }
    }

    public void start(Context context) {
        start(context, null);
    }

    public void start(Context context, RouteCallback callback) {
        try {
            Class<?> clazz = findClass();
            Intent intent = createIntent(context, clazz);
            if (Activity.class.isAssignableFrom(clazz)) {
                if (options == null) {
                    context.startActivity(intent);
                    if (enterAnim != 0 || exitAnim != 0) {
                        if (context instanceof Activity) {
                            ((Activity) context).overridePendingTransition(enterAnim, exitAnim);
                        } else {
                            if (L.isEnable()) {
                                L.e("QsRoute", "context is not Activity, so cannot override pending transition !!");
                            }
                        }
                    }
                } else {
                    ActivityCompat.startActivity(context, intent, options.toBundle());
                }
                if (callback != null) {
                    callback.onSuccess();
                }
            } else if (Service.class.isAssignableFrom(clazz)) {
                context.startService(intent);
            }
        } catch (Throwable t) {
            if (callback != null) {
                callback.onFailed(t);
            }
        }
    }

    public QsRoute withTransition(int enterAnim, int exitAnim) {
        this.enterAnim = enterAnim;
        this.exitAnim = exitAnim;
        return this;
    }

    public QsRoute withOptions(ActivityOptionsCompat compat) {
        this.options = compat;
        return this;
    }

    public QsRoute putString(String key, String value) {
        getBundle().putString(key, value);
        return this;
    }

    public QsRoute putStringArray(String key, String[] value) {
        getBundle().putStringArray(key, value);
        return this;
    }

    public QsRoute putStringArrayList(String key, ArrayList<String> value) {
        getBundle().putStringArrayList(key, value);
        return this;
    }

    public QsRoute putBoolean(String key, boolean value) {
        getBundle().putBoolean(key, value);
        return this;
    }

    public QsRoute putBooleanArray(String key, boolean[] value) {
        getBundle().putBooleanArray(key, value);
        return this;
    }

    public QsRoute putByte(String key, byte value) {
        getBundle().putByte(key, value);
        return this;
    }

    public QsRoute putByteArray(String key, byte[] value) {
        getBundle().putByteArray(key, value);
        return this;
    }

    public QsRoute putShort(String key, short value) {
        getBundle().putShort(key, value);
        return this;
    }

    public QsRoute putShortArray(String key, short[] value) {
        getBundle().putShortArray(key, value);
        return this;
    }

    public QsRoute putInt(String key, int value) {
        getBundle().putInt(key, value);
        return this;
    }

    public QsRoute putIntArray(String key, int[] value) {
        getBundle().putIntArray(key, value);
        return this;
    }

    public QsRoute putIntegerArrayList(String key, ArrayList<Integer> value) {
        getBundle().putIntegerArrayList(key, value);
        return this;
    }

    public QsRoute putLong(String key, long value) {
        getBundle().putLong(key, value);
        return this;
    }

    public QsRoute putLongArray(String key, long[] value) {
        getBundle().putLongArray(key, value);
        return this;
    }

    public QsRoute putFloat(String key, float value) {
        getBundle().putFloat(key, value);
        return this;
    }

    public QsRoute putFloatArray(String key, float[] value) {
        getBundle().putFloatArray(key, value);
        return this;
    }

    public QsRoute putDouble(String key, double value) {
        getBundle().putDouble(key, value);
        return this;
    }

    public QsRoute putDoubleArray(String key, double[] value) {
        getBundle().putDoubleArray(key, value);
        return this;
    }

    public QsRoute putChar(String key, char value) {
        getBundle().putChar(key, value);
        return this;
    }

    public QsRoute putCharArray(String key, char[] value) {
        getBundle().putCharArray(key, value);
        return this;
    }

    public QsRoute putCharSequence(String key, CharSequence value) {
        getBundle().putCharSequence(key, value);
        return this;
    }

    public QsRoute putCharSequenceArray(String key, CharSequence[] value) {
        getBundle().putCharSequenceArray(key, value);
        return this;
    }

    public QsRoute putCharSequenceArrayList(String key, ArrayList<CharSequence> value) {
        getBundle().putCharSequenceArrayList(key, value);
        return this;
    }

    public QsRoute putSerializable(String key, Serializable value) {
        getBundle().putSerializable(key, value);
        return this;
    }

    public QsRoute putBundle(String key, Bundle value) {
        getBundle().putBundle(key, value);
        return this;
    }

    public QsRoute putParcelable(String key, Parcelable value) {
        getBundle().putParcelable(key, value);
        return this;
    }

    public QsRoute putParcelableArray(String key, Parcelable[] value) {
        getBundle().putParcelableArray(key, value);
        return this;
    }

    public QsRoute putParcelableArrayList(String key, ArrayList<Parcelable> value) {
        getBundle().putParcelableArrayList(key, value);
        return this;
    }

    public QsRoute putSparseParcelableArray(String key, SparseArray<Parcelable> value) {
        getBundle().putSparseParcelableArray(key, value);
        return this;
    }

    public QsRoute putAll(Bundle value) {
        getBundle().putAll(value);
        return this;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public QsRoute putAll(PersistableBundle value) {
        getBundle().putAll(value);
        return this;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public QsRoute putSize(String key, Size value) {
        getBundle().putSize(key, value);
        return this;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public QsRoute putSizeF(String key, SizeF value) {
        getBundle().putSizeF(key, value);
        return this;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public QsRoute putBinder(String key, IBinder value) {
        getBundle().putBinder(key, value);
        return this;
    }

    private Bundle getBundle() {
        if (bundle == null) bundle = new Bundle();
        return bundle;
    }


}
