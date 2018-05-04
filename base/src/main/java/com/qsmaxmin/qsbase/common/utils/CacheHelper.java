package com.qsmaxmin.qsbase.common.utils;


import com.google.gson.Gson;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.model.QsModel;
import com.qsmaxmin.qsbase.mvp.model.QsConstants;

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
 * @Description 缓存处理类 将Model对象序列化到本地的/data/data/<package name>/fileName文件中 或者从file读取内容，转换成Model对象
 */
public class CacheHelper {

    CacheHelper() {
    }

    public <T extends QsModel> void saveObject2File(T model, String fileName) {
        File parentDir = new File(QsHelper.getInstance().getApplication().getFilesDir(), QsConstants.PARENT_FILE_DIR_NAME);
        if (!parentDir.exists()) {
            boolean mkdirs = parentDir.mkdirs();
            L.i("CacheHelper", "create parent dir success:" + mkdirs);
        }

        Gson gson = new Gson();
        String json = gson.toJson(model);
        FileOutputStream fos = null;
        try {
            File targetFile = new File(parentDir, fileName);
            fos = new FileOutputStream(targetFile);
            fos.write(json.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            QsHelper.getInstance().closeStream(fos);
        }
    }

    public <T extends QsModel> T getObjectFromFile(String fileName, Class<T> clazz) {
        File parentDir = new File(QsHelper.getInstance().getApplication().getFilesDir(), QsConstants.PARENT_FILE_DIR_NAME);
        if (!parentDir.exists()) {
            L.i("CacheHelper", "parent dir not exists, dir:" + parentDir.getAbsolutePath());
            return null;
        }
        File targetFile = new File(parentDir, fileName);
        if (!targetFile.exists()) {
            L.i("CacheHelper", "cache file not exists, file:" + targetFile.getAbsolutePath());
            return null;
        }
        FileInputStream fis = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        try {
            fis = new FileInputStream(targetFile);
            inputStreamReader = new InputStreamReader(fis);
            bufferedReader = new BufferedReader(inputStreamReader);
            String json = bufferedReader.readLine();
            Gson gson = new Gson();
            return gson.fromJson(json, clazz);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            QsHelper.getInstance().closeStream(fis, inputStreamReader, bufferedReader);
        }
        return null;
    }
}
