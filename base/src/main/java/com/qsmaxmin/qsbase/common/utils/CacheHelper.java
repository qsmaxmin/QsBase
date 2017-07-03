package com.qsmaxmin.qsbase.common.utils;


import android.content.Context;

import com.google.gson.Gson;
import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.model.QsModel;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;


/**
 * @CreateBy qsmaxmin
 * @Date 16/8/10  下午6:14
 * @Description 缓存处理类 将Model对象序列化到本地的/data/data/<package name>/fileName文件中 或者从file读取内容，转换成Model对象
 */
public class CacheHelper {

    private static CacheHelper cacheHelper;

    private CacheHelper() {
    }

    public static CacheHelper getInstance() {
        return cacheHelper;
    }

    public <T extends QsModel> void saveObject2File(T model, String fileName) {
        Gson gson = new Gson();
        String json = gson.toJson(model);
        FileOutputStream fs = null;
        try {
            fs = QsHelper.getInstance().getApplication().openFileOutput(fileName, Context.MODE_PRIVATE);
            fs.write(json.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            StreamCloseUtils.close(fs);
        }
    }

    public <T extends QsModel> T getObjectFromFile(String fileName, Class<T> clazz) {
        FileInputStream fileInputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        try {
            fileInputStream = QsHelper.getInstance().getApplication().openFileInput(fileName);
            inputStreamReader = new InputStreamReader(fileInputStream);
            bufferedReader = new BufferedReader(inputStreamReader);
            String json = bufferedReader.readLine();
            Gson gson = new Gson();
            return gson.fromJson(json, clazz);
        } catch (Exception e) {
            L.e("CacheHelper", e.getMessage());
        } finally {
            StreamCloseUtils.close(fileInputStream, inputStreamReader, bufferedReader);
        }
        return null;
    }
}
