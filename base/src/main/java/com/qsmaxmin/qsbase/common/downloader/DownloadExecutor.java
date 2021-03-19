package com.qsmaxmin.qsbase.common.downloader;

import com.qsmaxmin.qsbase.common.log.L;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Set;

import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * @CreateBy qsmaxmin
 * @Date 2020/3/16 9:30
 * @Description
 */
class DownloadExecutor<M extends QsDownloadModel<K>, K> {
    private final String             TAG;
    private final QsDownloader<M, K> downloader;
    private final M                  model;
    private final long               initTime;
    private final File               tempFile;
    private final File               targetFile;
    private       boolean            canceled;
    private       boolean            isDownloadSuccess;

    DownloadExecutor(QsDownloader<M, K> downloader, M model, String tag) {
        this.initTime = System.currentTimeMillis();
        this.downloader = downloader;
        this.model = model;
        this.TAG = tag;
        this.targetFile = new File(model.getFilePath());
        this.tempFile = new File(model.getTempFilePath());
    }

    void start(final Request.Builder builder) throws Exception {
        checkCancel();
        if (L.isEnable()) L.i(TAG, "download started......id:" + model.getId() + ", time gone:" + getTimeGone());

        if (downloader.isForceDownload() || !targetFile.exists()) {
            File parentFile = targetFile.getParentFile();
            if (!parentFile.exists() && !parentFile.mkdirs()) {
                throw new Exception("create dir failed...dir:" + parentFile.getPath());
            }

            if (downloader.isSupportBreakPointTransmission() && tempFile.exists() && tempFile.length() > 0) {
                checkFileBeforeDownload(builder);
            } else {
                startDownload(builder.build(), 0);
            }
        }
        isDownloadSuccess = true;
    }

    private void checkFileBeforeDownload(Request.Builder builder) throws Exception {
        L.i(TAG, "checkFileBeforeDownload...old file exists, size:" + tempFile.length() + ", path:" + tempFile.getPath());
        Response response = downloader.getClient().newCall(builder.build()).execute();
        checkCancel();
        try {
            if (!response.isSuccessful()) {
                throw new Exception("onResponse failed, response code=" + response.code());
            }
            ResponseBody body = response.body();
            if (body == null || body.contentLength() <= 0) {
                throw new Exception(" body is empty!");
            }
            long existsLength = tempFile.length();
            if (existsLength < body.contentLength()) {
                Request request = builder.header("RANGE", "bytes=" + existsLength + "-").build();
                startDownload(request, existsLength);
            } else if (existsLength == body.contentLength()) {
                if (L.isEnable()) L.i(TAG, "need not download file, exists file length matched contentLength, id:" + model.getId());
                model.update(existsLength, existsLength);
                callbackDownloading(existsLength, existsLength);
            }
        } finally {
            close(response);
        }
    }

    private void startDownload(Request request, long rangStartPoint) throws Exception {
        Response response = downloader.getClient().newCall(request).execute();
        checkCancel();

        if (L.isEnable() && downloader.isPrintResponseHeader()) {
            printResponseHeader(response);
        }
        RandomAccessFile accessFile = null;
        try {
            if (!response.isSuccessful()) {
                throw new Exception("onResponse failed, response code=" + response.code());
            }
            ResponseBody body = response.body();
            if (body == null || body.contentLength() <= 0) {
                throw new Exception(" body is empty!");
            }

            long contentLength = body.contentLength();
            long startPoint = rangStartPoint;
            if (startPoint > 0 && response.code() != 206) {
                startPoint = 0;
                if (L.isEnable()) L.e(TAG, "server not support resume from break point");
            }
            long totalLength = startPoint + contentLength;
            accessFile = new RandomAccessFile(tempFile, "rwd");
            accessFile.seek(startPoint);
            if (startPoint > 0) {
                model.update(startPoint, totalLength);
                callbackDownloading(startPoint, totalLength);
            }
            InputStream is = body.byteStream();
            byte[] buff = new byte[1024 * 10];
            int len;
            long progress;
            long tempLength = startPoint;
            long lastProgress = 0;

            while ((len = is.read(buff)) != -1) {
                accessFile.write(buff, 0, len);
                tempLength += len;
                model.update(tempLength, totalLength);
                progress = model.getDownloadProgress();
                if (progress != lastProgress) {
                    lastProgress = progress;
                    callbackDownloading(tempLength, totalLength);
                }
            }
            if (tempLength != totalLength) {
                throw new Exception("download failed, content length not matched, wanted:" + totalLength + ", but:" + tempLength);
            } else {
                renameFile(tempFile, targetFile);
            }
        } finally {
            close(response);
            close(accessFile);
        }
    }

    private void printResponseHeader(Response response) {
        if (response == null) return;
        int code = response.code();
        Headers headers = response.headers();
        Set<String> names = headers.names();
        StringBuilder sb = new StringBuilder("onResponse......code:" + code);
        for (String name : names) {
            String value = headers.get(name);
            sb.append("\n\t").append(name).append("=").append(value);
        }
        L.i(TAG, sb.toString());
    }

    private void callbackDownloading(long size, long totalSize) {
        downloader.postDownloading(model, size, totalSize);
    }

    private void renameFile(File tempFile, File targetFile) throws Exception {
        if (targetFile.exists()) {
            boolean delete = targetFile.delete();
            L.i(TAG, "delete old file(success:" + delete + "):" + targetFile.getAbsolutePath());
        }
        if (!tempFile.renameTo(targetFile)) {
            throw new Exception("tempFile(" + tempFile.getAbsolutePath() + ") rename to file(" + model.getFilePath() + ") failed....");
        }
    }

    private void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkCancel() throws Exception {
        if (canceled) throw new Exception("current task is canceled....");
    }

    private String getTimeGone() {
        return (System.currentTimeMillis() - initTime) + "ms";
    }

    boolean isDownloadSuccess() {
        return isDownloadSuccess;
    }

    final void cancel() {
        this.canceled = true;
    }

    final void applyWait() {
        try {
            synchronized (this) {
                wait();
            }
        } catch (Exception ignored) {
        }
    }

    final void applyNotify() {
        try {
            synchronized (this) {
                notifyAll();
            }
        } catch (Exception ignored) {
        }
    }

    final M getModel() {
        return model;
    }

    final File getTempFile() {
        return tempFile;
    }

    final File getTargetFile() {
        return targetFile;
    }
}

