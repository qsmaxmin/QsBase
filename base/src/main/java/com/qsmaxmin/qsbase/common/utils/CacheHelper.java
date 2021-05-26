package com.qsmaxmin.qsbase.common.utils;


import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.qsmaxmin.annotation.QsNotProguard;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.plugin.threadpoll.QsThreadPollHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 * @CreateBy qsmaxmin
 * @Date 16/8/10  下午6:14
 * @Description CacheHelper
 */
public class CacheHelper {

    public static <T extends QsNotProguard> void saveObject2File(final T model, final String key) {
        if (isMainThread()) {
            executeAsync(new Runnable() {
                @Override public void run() {
                    saveObject2FileAsync(model, key);
                }
            });
        } else {
            saveObject2FileAsync(model, key);
        }
    }

    private static <T extends QsNotProguard> void saveObject2FileAsync(T model, String key) {
        if (model == null || TextUtils.isEmpty(key)) return;
        Gson gson = new Gson();
        String json = gson.toJson(model);
        FileOutputStream fos = null;
        try {
            fos = QsHelper.getApplication().openFileOutput(key, Context.MODE_PRIVATE);
            fos.write(json.getBytes());
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            StreamUtil.close(fos);
        }
    }

    public static <T extends QsNotProguard> void saveObject2File(final T model, final File cacheFile) {
        if (isMainThread()) {
            executeAsync(new Runnable() {
                @Override public void run() {
                    saveObject2FileAsync(model, cacheFile);
                }
            });
        } else {
            saveObject2FileAsync(model, cacheFile);
        }
    }

    private static <T extends QsNotProguard> void saveObject2FileAsync(T model, File cacheFile) {
        if (cacheFile == null || model == null) return;
        File parentFile = cacheFile.getParentFile();
        if (!parentFile.exists()) {
            boolean mkdirs = parentFile.mkdirs();
        } else if (cacheFile.exists()) {
            boolean delete = cacheFile.delete();
        }
        Gson gson = new Gson();
        String json = gson.toJson(model);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(cacheFile);
            fos.write(json.getBytes());
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            StreamUtil.close(fos);
        }
    }

    public static <T extends QsNotProguard> T getObjectFromFile(String key, Class<T> clazz) {
        if (clazz == null || TextUtils.isEmpty(key)) return null;
        if (isMainThread()) L.e("CacheHelper", "It is not recommended to execute this method(getObjectFromFile) on the main thread.....");
        FileInputStream fileInputStream = null;
        InputStreamReader inputStreamReader = null;
        try {
            fileInputStream = QsHelper.getApplication().openFileInput(key);
            inputStreamReader = new InputStreamReader(fileInputStream);
            Gson gson = new Gson();
            return gson.fromJson(inputStreamReader, clazz);
        } catch (FileNotFoundException e) {
            L.e("CacheHelper", "getObjectFromFile not exit... key:" + key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            StreamUtil.close(fileInputStream);
            StreamUtil.close(inputStreamReader);
        }
        return null;
    }

    public static <T extends QsNotProguard> T getObjectFromFile(File cacheFile, Class<T> clazz) {
        if (cacheFile == null || !cacheFile.exists() || clazz == null) return null;
        if (isMainThread()) L.e("CacheHelper", "It is not recommended to execute this method(getObjectFromFile) on the main thread.....");
        FileInputStream fileInputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        try {
            fileInputStream = new FileInputStream(cacheFile);
            inputStreamReader = new InputStreamReader(fileInputStream);
            bufferedReader = new BufferedReader(inputStreamReader);
            String json = bufferedReader.readLine();
            Gson gson = new Gson();
            return gson.fromJson(json, clazz);
        } catch (FileNotFoundException e) {
            L.e("CacheHelper", "getObjectFromFile not exit... cacheFile:" + cacheFile.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            StreamUtil.close(fileInputStream);
            StreamUtil.close(inputStreamReader);
            StreamUtil.close(bufferedReader);
        }
        return null;
    }

    public static void deleteCacheFile(String key) {
        try {
            QsHelper.getApplication().deleteFile(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean isMainThread() {
        return QsThreadPollHelper.isMainThread();
    }

    private static void executeAsync(Runnable runnable) {
        QsHelper.executeInWorkThread(runnable);
    }

}
