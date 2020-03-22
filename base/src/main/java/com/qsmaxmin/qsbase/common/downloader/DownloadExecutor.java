package com.qsmaxmin.qsbase.common.downloader;

import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.utils.QsHelper;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Set;

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
class DownloadExecutor<M extends QsDownloadModel> {
    private final String          TAG;
    private final QsDownloader<M> downloader;
    private final M               model;
    private       long            initTime;

    DownloadExecutor(QsDownloader<M> downloader, M model, String tag) {
        this.downloader = downloader;
        this.model = model;
        this.TAG = tag;
    }

    void start(final Request.Builder builder) {
        initTime = System.currentTimeMillis();
        QsHelper.getThreadHelper().getWorkThreadPoll().execute(new Runnable() {
            @Override public void run() {
                startInner(builder);
            }
        });
    }

    private void startInner(final Request.Builder builder) {
        postDownloadStart();
        if (L.isEnable()) L.i(TAG, "download started......id:" + model.getId() + ", time gone:" + getTimeGone());

        final File targetFile = new File(model.getFilePath());
        if (targetFile.exists()) {
            checkFileBeforeDownload(builder, targetFile);
        } else {
            File parentFile = targetFile.getParentFile();
            if (!parentFile.exists() && !parentFile.mkdirs()) {
                L.e(TAG, "create dir failed...dir:" + parentFile.getPath());
                postDownloadFailed("create dir failed, dir:" + parentFile.getPath());
                return;
            }
            startDownload(builder.build(), targetFile, 0);
        }
    }

    /**
     * 断点续传前，检查文件真实大小
     */
    private void checkFileBeforeDownload(final Request.Builder builder, final File targetFile) {
        L.i(TAG, "old file exists....size:" + targetFile.length() + ", path:" + targetFile.getPath());
        downloader.getClient().newCall(builder.build()).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                postDownloadFailed(e.getMessage());
                e.printStackTrace();
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (response.isSuccessful()) {
                        ResponseBody body = response.body();
                        if (body == null || body.contentLength() == 0) {
                            L.e(TAG, "download failed, body is empty!");
                            postDownloadFailed("download failed, body is empty!");
                            return;
                        }
                        long existsLength = targetFile.length();
                        if (existsLength < body.contentLength()) {
                            Request request = builder.header("RANGE", "bytes=" + existsLength + "-").build();
                            startDownload(request, targetFile, existsLength);
                        } else if (existsLength == body.contentLength()) {
                            if (L.isEnable()) L.i(TAG, "need not download file, exists file length matched contentLength, id:" + model.getId());
                            postDownloading(existsLength, existsLength);
                            postDownloadComplete();
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

    private void startDownload(Request request, final File targetFile, final long rangStartPoint) {
        downloader.getClient().newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                postDownloadFailed(e.getMessage());
                e.printStackTrace();
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                if (L.isEnable()) printResponseHeader(response);

                RandomAccessFile accessFile = null;
                try {
                    if (response.isSuccessful()) {
                        if (L.isEnable()) L.i(TAG, "response OK.......id:" + model.getId() + ", time gone:" + getTimeGone());
                        ResponseBody body = response.body();

                        if (body != null) {
                            long contentLength = body.contentLength();
                            long startPoint = rangStartPoint;
                            if (startPoint > 0 && response.code() != 206) {//不支持断点续传
                                startPoint = 0;
                                if (L.isEnable()) L.e(TAG, "server not support resume from break point");
                            }

                            if (contentLength > 0) {
                                long totalLength = startPoint + contentLength;
                                accessFile = new RandomAccessFile(targetFile, "rwd");
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
                                    postDownloadComplete();
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
                }
            }
        });
    }

    private void printResponseHeader(Response response) {
        if (response == null) return;
        int code = response.code();
        Headers headers = response.headers();
        if (headers == null) return;
        Set<String> names = headers.names();
        StringBuilder sb = new StringBuilder("onResponse......code:" + code);
        for (String name : names) {
            String value = headers.get(name);
            sb.append("\n\t").append(name).append("=").append(value);
        }
        L.i(TAG, sb.toString());
    }

    /**
     * 分成1~5个片段
     */
    private int getPartCount(long contentLength) {
        int mb_10 = 10 * 1024 * 1024;
        int count = (int) (contentLength / mb_10);
        return count < 1 ? 1 : count > 5 ? 5 : count;
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

    private void postDownloadComplete() {
        downloader.removeExecutorFromTask(model);
        downloader.postDownloadComplete(model);
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
