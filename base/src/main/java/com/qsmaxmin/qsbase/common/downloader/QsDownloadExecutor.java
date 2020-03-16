package com.qsmaxmin.qsbase.common.downloader;

import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.common.utils.QsHelper;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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
class QsDownloadExecutor {
    private final String          TAG = "QsDownloadExecutor";
    private final QsDownloadModel model;
    private       long            initTime;

    QsDownloadExecutor(QsDownloadModel model) {
        this.model = model;
    }

    void start(final Request request) {
        initTime = System.currentTimeMillis();
        postDownloadStart();

        QsHelper.getThreadHelper().getWorkThreadPoll().execute(new Runnable() {
            @Override public void run() {
                startInner(request);
            }
        });
    }

    private void startInner(Request request) {
        if (L.isEnable()) L.i(TAG, "download started......id:" + model.getId() + ", time gone:" + getTimeGone());
        QsDownloadHelper.getInstance().getClient().newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                postDownloadFailed(e.getMessage());
                e.printStackTrace();
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                FileOutputStream fos = null;
                try {
                    if (response.isSuccessful()) {
                        if (L.isEnable()) L.i(TAG, "downloading.......id:" + model.getId() + ", time gone:" + getTimeGone());
                        ResponseBody body = response.body();
                        if (body != null) {
                            long contentLength = body.contentLength();
                            if (contentLength > 0) {
                                File zipFile = new File(model.getFilePath());
                                fos = new FileOutputStream(zipFile);
                                InputStream is = body.byteStream();
                                byte[] buff = new byte[1024 * 10];
                                int len;
                                long progress;
                                long tempLength = 0;
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
                                    fos.write(buff, 0, len);
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
                    close(fos);
                }
            }
        });
    }

    private void postDownloadStart() {
        QsDownloadHelper.getInstance().postDownloadStart(model);
    }

    private void postDownloading(long size, long totalSize) {
        QsDownloadHelper.getInstance().postDownloading(model, size, totalSize);
    }

    private void postDownloadFailed(String msg) {
        QsDownloadHelper.getInstance().removeExecutorFromTask(model);
        QsDownloadHelper.getInstance().postDownloadFailed(model, msg);
    }

    private void postDownloadComplete() {
        QsDownloadHelper.getInstance().removeExecutorFromTask(model);
        QsDownloadHelper.getInstance().postDownloadComplete(model);
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
