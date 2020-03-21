package com.qsmaxmin.qsbase.common.downloader;

import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.utils.QsHelper;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * @CreateBy qsmaxmin
 * @Date 2020/3/16 9:30
 * @Description
 */
class DownloadExecutor<M extends QsDownloadModel> {
    private final String          TAG = "DownloadExecutor";
    private final QsDownloader<M> downloader;
    private final M               model;
    private       long            initTime;

    DownloadExecutor(QsDownloader<M> downloader, M model) {
        this.downloader = downloader;
        this.model = model;
    }

    void start(final Request.Builder builder) {
        initTime = System.currentTimeMillis();
        postDownloadStart();
        QsHelper.getThreadHelper().getWorkThreadPoll().execute(new Runnable() {
            @Override public void run() {
                startInner(builder);
            }
        });
    }

    private void startInner(Request.Builder builder) {
        final Request request;
        final File targetFile = new File(model.getFilePath());
        long downloadedSize = 0;
        if (targetFile.exists()) {
            downloadedSize = targetFile.length();
            builder.addHeader("Range", downloadedSize + "-");
            L.i(TAG, "old file exists....size:" + downloadedSize + ", path:" + targetFile.getPath());
        } else {
            File parentFile = targetFile.getParentFile();
            if (!parentFile.exists() && !parentFile.mkdirs()) {
                L.e(TAG, "create dir failed...dir:" + parentFile.getPath());
                postDownloadFailed("create dir failed, dir:" + parentFile.getPath());
                return;
            }
        }
        request = builder.build();
        final long finalDownloadedSize = downloadedSize;


        if (L.isEnable()) L.i(TAG, "download started......id:" + model.getId() + ", time gone:" + getTimeGone());
        downloader.getClient().newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                postDownloadFailed(e.getMessage());
                e.printStackTrace();
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                RandomAccessFile accessFile = null;
                try {
                    if (response.isSuccessful()) {
                        if (L.isEnable()) L.i(TAG, "downloading.......id:" + model.getId() + ", time gone:" + getTimeGone());
                        ResponseBody body = response.body();
                        if (body != null) {
                            long contentLength = body.contentLength();
                            if (contentLength > 0) {
                                accessFile = new RandomAccessFile(targetFile, "rwd");
                                accessFile.seek(finalDownloadedSize);
                                if (finalDownloadedSize > 0) {
                                    postDownloading(finalDownloadedSize, contentLength);
                                }
                                InputStream is = body.byteStream();
                                byte[] buff = new byte[1024 * 10];
                                int len;
                                long progress;
                                long tempLength = finalDownloadedSize;
                                long lastProgress = 0;
                                long callbackCount;
                                if (contentLength < 10 * 1024 * 1024) {
                                    callbackCount = 10;
                                } else if (contentLength > 100 * 1024 * 1024) {
                                    callbackCount = 100;
                                } else {
                                    callbackCount = (contentLength / (1024 * 1024));
                                }

                                while ((len = is.read(buff)) != -1) {
                                    accessFile.write(buff, 0, len);
                                    tempLength += len;
                                    progress = tempLength * callbackCount / contentLength;
                                    if (progress != lastProgress) {
                                        lastProgress = progress;
                                        postDownloading(tempLength, contentLength);
                                    }
                                }
                                if (tempLength == contentLength) {
                                    if (L.isEnable()) L.i(TAG, "download complete........id:" + model.getId() + ", time gone:" + getTimeGone());
                                    postDownloadComplete();
                                } else {
                                    if (L.isEnable()) L.e(TAG, "download failed......content length not matched");
                                    postDownloadFailed("content length not matched");
                                }
                            } else {
                                postDownloadFailed("content length is 0");
                            }
                        } else {
                            postDownloadFailed("response body is null");
                        }
                    } else {
                        postDownloadFailed("response code:" + response.code());
                    }
                } catch (Exception e) {
                    postDownloadFailed(e.getMessage());
                } finally {
                    close(response);
                    close(accessFile);
                }
            }
        });
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
