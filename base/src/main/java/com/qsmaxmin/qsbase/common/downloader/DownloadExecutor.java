package com.qsmaxmin.qsbase.common.downloader;

import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.utils.QsHelper;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Set;

import androidx.annotation.NonNull;
import okhttp3.Call;
import okhttp3.Callback;
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
    private       long               initTime;

    DownloadExecutor(QsDownloader<M, K> downloader, M model, String tag) {
        this.downloader = downloader;
        this.model = model;
        this.TAG = tag;
    }

    void start(final Request.Builder builder) {
        initTime = System.currentTimeMillis();
        QsHelper.executeInWorkThread(new Runnable() {
            @Override public void run() {
                startInner(builder);
            }
        });
    }

    private void startInner(final Request.Builder builder) {
        postDownloadStart();
        if (L.isEnable()) L.i(TAG, "download started......id:" + model.getId() + ", time gone:" + getTimeGone());

        final File targetFile = new File(model.getFilePath());
        String name = targetFile.getName();
        File parentFile = targetFile.getParentFile();
        File tempFile = new File(parentFile, name + "_temp");

        if (targetFile.exists()) {
            postDownloadComplete(targetFile);
        } else {
            if (!parentFile.exists() && !parentFile.mkdirs()) {
                L.e(TAG, "create dir failed...dir:" + parentFile.getPath());
                postDownloadFailed("create dir failed, dir:" + parentFile.getPath());
                return;
            }
            if (tempFile.exists() && tempFile.length() > 0) {
                checkFileBeforeDownload(builder, tempFile);
            } else {
                startDownload(builder.build(), tempFile, 0);
            }
        }
    }

    private void checkFileBeforeDownload(final Request.Builder builder, final File tempFile) {
        L.i(TAG, "old file exists....size:" + tempFile.length() + ", path:" + tempFile.getPath());
        downloader.getClient().newCall(builder.build()).enqueue(new Callback() {
            @Override public void onFailure(@NonNull Call call, @NonNull IOException e) {
                postDownloadFailed(e.getMessage());
                e.printStackTrace();
            }

            @Override public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    if (response.isSuccessful()) {
                        ResponseBody body = response.body();
                        if (body == null || body.contentLength() == 0) {
                            L.e(TAG, "download failed, body is empty!");
                            postDownloadFailed("download failed, body is empty!");
                            return;
                        }
                        long existsLength = tempFile.length();
                        if (existsLength < body.contentLength()) {
                            Request request = builder.header("RANGE", "bytes=" + existsLength + "-").build();
                            startDownload(request, tempFile, existsLength);
                        } else if (existsLength == body.contentLength()) {
                            if (L.isEnable()) L.i(TAG, "need not download file, exists file length matched contentLength, id:" + model.getId());
                            postDownloading(existsLength, existsLength);
                            postDownloadComplete(tempFile);
                        }
                    } else {
                        L.e(TAG, "onResponse failed, response code=" + response.code());
                        postDownloadFailed("onResponse failed, response code=" + response.code());
                    }
                } catch (Exception e) {
                    postDownloadFailed(e.getMessage());
                    e.printStackTrace();
                } finally {
                    close(response);
                }
            }
        });
    }

    private void startDownload(Request request, final File tempFile, final long rangStartPoint) {
        downloader.getClient().newCall(request).enqueue(new Callback() {
            @Override public void onFailure(@NonNull Call call, @NonNull IOException e) {
                postDownloadFailed(e.getMessage());
                e.printStackTrace();
            }

            @Override public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (L.isEnable()) printResponseHeader(response);
                RandomAccessFile accessFile = null;
                boolean downloadSuccess = false;
                try {
                    if (response.isSuccessful()) {
                        if (L.isEnable()) L.i(TAG, "response OK.......id:" + model.getId() + ", time gone:" + getTimeGone());
                        ResponseBody body = response.body();

                        if (body != null) {
                            long contentLength = body.contentLength();
                            long startPoint = rangStartPoint;
                            if (startPoint > 0 && response.code() != 206) {
                                startPoint = 0;
                                if (L.isEnable()) L.e(TAG, "server not support resume from break point");
                            }

                            if (contentLength > 0) {
                                long totalLength = startPoint + contentLength;
                                accessFile = new RandomAccessFile(tempFile, "rwd");
                                accessFile.seek(startPoint);
                                if (startPoint > 0) {
                                    postDownloading(startPoint, totalLength);
                                }
                                InputStream is = body.byteStream();
                                byte[] buff = new byte[1024 * 10];
                                int len;
                                long progress;
                                long tempLength = startPoint;
                                long lastProgress = 0;

                                long callbackSize = 100 * 1024;
                                long callbackCount = totalLength / callbackSize;
                                callbackCount = callbackCount < 30 ? 30 : (callbackCount > 100 ? 100 : callbackCount);

                                while ((len = is.read(buff)) != -1) {
                                    accessFile.write(buff, 0, len);
                                    tempLength += len;
                                    progress = tempLength * callbackCount / totalLength;
                                    if (progress != lastProgress) {
                                        lastProgress = progress;
                                        postDownloading(tempLength, totalLength);
                                    }
                                }
                                if (tempLength == totalLength) {
                                    if (L.isEnable()) L.i(TAG, "download success, id:" + model.getId() + ", time gone:" + getTimeGone());
                                    downloadSuccess = true;
                                } else {
                                    if (L.isEnable()) L.e(TAG, "download failed, content length not matched, wanted:" + totalLength + ", but:" + tempLength);
                                    postDownloadFailed("content length not matched, wanted:" + totalLength + ", but:" + tempLength);
                                }
                            } else {
                                L.e(TAG, "download failed, content length is 0");
                                postDownloadFailed("download failed, content length is 0");
                            }
                        } else {
                            L.e(TAG, "download failed, response body is null");
                            postDownloadFailed("download failed, response body is null");
                        }
                    } else {
                        L.e(TAG, "download failed, response code=" + response.code());
                        postDownloadFailed("download failed, response code:" + response.code());
                    }
                } catch (Exception e) {
                    postDownloadFailed(e.getMessage());
                    e.printStackTrace();
                } finally {
                    close(response);
                    close(accessFile);
                    if (downloadSuccess) {
                        postDownloadComplete(tempFile);
                    }
                }
            }
        });
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

    private void postDownloadStart() {
        downloader.postDownloadStart(model);
    }

    private void postDownloading(long size, long totalSize) {
        downloader.postDownloading(model, size, totalSize);
    }

    private void postDownloadFailed(String msg) {
        downloader.removeExecutorFromTask(model);
        downloader.postDownloadFailed(model, msg);
    }

    private void postDownloadComplete(File tempFile) {
        downloader.removeExecutorFromTask(model);

        if (!tempFile.getAbsolutePath().equals(model.getFilePath())) {
            File targetFile = new File(model.getFilePath());
            if (targetFile.exists()) {
                boolean delete = targetFile.delete();
                L.i(TAG, "delete old file(success:" + delete + "):" + targetFile.getAbsolutePath());
            }
            boolean success = tempFile.renameTo(targetFile);
            if (success) {
                downloader.postDownloadComplete(model);
            } else {
                downloader.postDownloadFailed(model, "tempFile(" + tempFile.getAbsolutePath() + ") rename to file(" + model.getFilePath() + ") failed....");
            }
        } else {
            downloader.postDownloadComplete(model);
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

    private String getTimeGone() {
        return (System.currentTimeMillis() - initTime) + "ms";
    }
}
