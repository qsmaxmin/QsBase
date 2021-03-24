package com.qsmaxmin.qsbase.common.downloader;

import com.qsmaxmin.qsbase.common.log.L;
import com.qsmaxmin.qsbase.plugin.threadpoll.QsThreadPollHelper;

import java.io.Closeable;
import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.annotation.NonNull;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * @CreateBy qsmaxmin
 * @Date 2020/3/16 9:30
 * @Description
 */
class DownloadExecutor<M extends QsDownloadModel<T>, T> {
    private static final int                MAX_THREAD_COUNT = 5;
    private final        String             TAG;
    private final        QsDownloader<M, T> qsDownloader;
    private final        M                  m;
    private final        File               targetFile;
    private final        Object             threadLocker;
    private              File               tempFile;
    private              RandomAccessFile   accessFile;
    private              long               initTime;
    private              boolean            canceled;
    private              boolean            printRespHeader;
    private              boolean            isDownloading;
    private              long[]             lastSeekPoints;

    DownloadExecutor(QsDownloader<M, T> downloader, M model, String tag) {
        if (L.isEnable()) {
            initTime = System.currentTimeMillis();
        }
        this.qsDownloader = downloader;
        this.m = model;
        this.TAG = tag;
        this.targetFile = new File(model.getFilePath());
        this.threadLocker = new Object();
    }

    private String initTag() {
        return TAG;
    }

    final M getModel() {
        return m;
    }

    final boolean isDownloading() {
        return isDownloading;
    }

    final boolean isDownloadSuccess() {
        return getContentLength() > 0 && getDownloadedLength() == getContentLength();
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

    final void cancel() {
        this.canceled = true;
    }

    /**
     * 是否打印响应头信息
     */
    final void setPrintRespHeader(boolean printRespHeader) {
        this.printRespHeader = printRespHeader;
    }

    final void start(Request.Builder builder) throws Exception {
        this.isDownloading = true;
        Response response = null;
        try {
            checkCanceled();
            if (!targetFile.exists()) {
                if (L.isEnable()) L.i(initTag(), "download started......id:" + m.getId() + ", time gone:" + getTimeGone());
                File parentFile = targetFile.getParentFile();
                if (!parentFile.exists()) {
                    boolean success = parentFile.mkdirs();
                    if (L.isEnable()) L.i(initTag(), "onStart.........crate dir(" + success + "):" + parentFile.getAbsolutePath());
                }
                tempFile = new File(m.getDownloadTempFilePath());
                accessFile = new RandomAccessFile(tempFile, "rwd");

                response = getClient().newCall(builder.build()).execute();
                if (!response.isSuccessful()) {
                    throw new Exception("onResponse failed, response code=" + response.code());
                }
                if (L.isEnable() && isPrintRespHeader()) {
                    printResponseHeader(response);
                }
                ResponseBody body = response.body();
                if (body == null) {
                    throw new Exception("download failed, body is empty!");
                }
                long contentLength = body.contentLength();
                if (contentLength <= 0) {
                    throw new Exception("download failed, content length is 0");
                }
                setContentLength(contentLength);
                if (L.isEnable()) {
                    L.i(initTag(), "response ok, contentLength:" + contentLength + ", id:" + m.getId() + ", time gone:" + getTimeGone());
                }

                if (isBigFile(contentLength) && isServerSupportBreakPointTransmission(response)) {
                    if (canBreakPointTransmission(response)) {
                        int threadCount = lastSeekPoints.length;
                        long[][] ranges = new long[threadCount][];
                        long stepLength = contentLength / threadCount;
                        for (int i = 0; i < threadCount; i++) {
                            long start = i * stepLength;
                            long end = (i == threadCount - 1) ? contentLength : (i * stepLength + stepLength);
                            long seekPoint = lastSeekPoints[i];
                            ranges[i] = new long[]{seekPoint, end};
                            addDownloadedLength(seekPoint - start);
                        }
                        postDownloading();
                        onStart(builder, response, ranges);
                    } else {
                        int threadCount = calculateThreadCount(contentLength);
                        long stepLength = contentLength / threadCount;
                        long[][] ranges = new long[threadCount][];
                        for (int i = 0; i < threadCount; i++) {
                            long start = i * stepLength;
                            long end = (i == threadCount - 1) ? contentLength : (i * stepLength + stepLength);
                            ranges[i] = new long[]{start, end};
                        }
                        writeThreadCountAndETag(threadCount, getETag(response));
                        onStart(builder, response, ranges);
                    }
                } else {
                    readDataOnly(response.body());
                }

                if (isDownloadSuccess()) {
                    if (L.isEnable()) {
                        L.i(initTag(), "download complete, id:" + m.getId() + ", time gone:" + getTimeGone());
                    }
                    accessFile.setLength(contentLength);
                    renameFile(tempFile, targetFile);
                } else {
                    throw new Exception("download failed!! downloadedLength(" + getDownloadedLength() +
                            ")are not match contentLength(" + getContentLength() + ") id:" + m.getId());
                }

            } else {
                if (L.isEnable()) L.i(initTag(), "download started...... old file is exists!! id:" + m.getId());
            }
        } finally {
            close(response);
            close(accessFile);
            isDownloading = false;
        }
    }

    private void onStart(@NonNull final Request.Builder builder, @NonNull Response response, @NonNull long[][] ranges) throws Exception {
        if (ranges.length == 1) {
            long[] firstRange = ranges[0];
            if (firstRange[0] == 0) {
                readDataSingleThread(response.body(), 0);
            } else {
                startRangeDownload(0, builder, firstRange[0], firstRange[1], false);
            }
        } else {
            final int threadCount = ranges.length;
            final AtomicInteger completeCount = new AtomicInteger(1);
            for (int i = 1; i < threadCount; i++) {
                final long[] seekRange = ranges[i];
                final int index = i;
                QsThreadPollHelper.runOnHttpThread(new Runnable() {
                    @Override public void run() {
                        try {
                            startRangeDownload(index, builder, seekRange[0], seekRange[1], true);
                        } catch (Exception e) {
                            if (L.isEnable()) L.e(initTag(), e);
                        } finally {
                            if (completeCount.addAndGet(1) == threadCount) {
                                downloadNotify();
                            }
                        }
                    }
                });
            }
            long[] firstRange = ranges[0];
            if (firstRange[0] == 0) {
                readDataMultithreaded(0, response.body(), 0, firstRange[1]);
            } else {
                startRangeDownload(0, builder, firstRange[0], firstRange[1], true);
            }
            if (completeCount.get() < threadCount) {
                downloadWait();
            }
        }
    }

    /**
     * 开始断点下载
     */
    private void startRangeDownload(int index, @NonNull Request.Builder builder, long startPoint, long endPoint, boolean multithreaded) throws Exception {
        if (startPoint >= endPoint) return;
        if (L.isEnable()) L.i(initTag(), "startRangeDownload(" + index + "), range[" + startPoint + "," + endPoint + "], multithreaded:" + multithreaded);
        Request request = builder.header("RANGE", "bytes=" + startPoint + "-" + endPoint).build();
        Response response = getClient().newCall(request).execute();
        checkCanceled();
        try {
            if (!response.isSuccessful()) {
                throw new Exception("download failed, response code:" + response.code());
            }
            ResponseBody body = response.body();
            if (body == null) {
                throw new Exception("download failed, response body is null");
            }
            long contentLength = body.contentLength();
            if (contentLength <= 0) {
                throw new Exception("download failed, content length is 0");
            }
            if (multithreaded) {
                readDataMultithreaded(index, body, startPoint, endPoint);
            } else {
                readDataSingleThread(body, startPoint);
            }
        } finally {
            close(response);
        }
    }

    /**
     * 服务器不支持断点续传时，不记录下载信息
     */
    private void readDataOnly(@NonNull ResponseBody body) throws Exception {
        if (L.isEnable()) L.i(initTag(), "readDataOnly................");
        byte[] buff = new byte[1024_0];
        int len;
        long progress;
        long lastProgress = 0;
        InputStream is = body.byteStream();
        accessFile.seek(0);
        while ((len = is.read(buff)) != -1) {
            accessFile.write(buff, 0, len);
            addDownloadedLength(len);
            progress = m.getDownloadProgress();
            if (progress != lastProgress) {
                lastProgress = progress;
                postDownloading();
            }
        }
    }

    /**
     * 服务器支持断点续传，但文件太小了
     */
    private void readDataSingleThread(@NonNull ResponseBody body, long startPoint) throws Exception {
        if (L.isEnable()) L.i(initTag(), "readDataSingleThread................startPoint:" + startPoint);
        byte[] buff = new byte[1024_0];
        int len;
        long progress;
        long readLength = 0;
        long lastProgress = 0;
        InputStream is = body.byteStream();
        while ((len = is.read(buff)) != -1) {
            accessFile.seek(startPoint + readLength);
            accessFile.write(buff, 0, len);
            readLength += len;
            addDownloadedLength(len);

            progress = m.getDownloadProgress();
            if (progress != lastProgress) {
                lastProgress = progress;
                writeSeekPoint(0, startPoint + readLength);
                postDownloading();
            }
        }
    }

    /**
     * 服务器支持断点续传且多线程下载时，做好同步工作
     */
    private void readDataMultithreaded(int index, @NonNull ResponseBody body, long startPoint, long endPoint) throws Exception {
        if (L.isEnable()) L.i(initTag(), "readDataMultithreaded(" + index + "), range[" + startPoint + "," + endPoint + "].....");
        long targetLength = endPoint - startPoint;
        byte[] buff = new byte[1024_0];
        int whileCount = 0;
        int len;
        long progress;
        long readLength = 0;
        long lastProgress = 0;
        int realLen;
        InputStream is = body.byteStream();
        while ((len = is.read(buff)) != -1 && readLength < targetLength) {
            realLen = (int) Math.min(len, targetLength - readLength);
            synchronized (targetFile) {
                accessFile.seek(startPoint + readLength);
                accessFile.write(buff, 0, realLen);
                readLength += realLen;

                whileCount++;
                if (readLength == targetLength || whileCount % 10 == 0) {
                    writeSeekPoint(index, startPoint + readLength);
                }
            }
            addDownloadedLength(realLen);

            progress = m.getDownloadProgress();
            if (progress != lastProgress) {
                lastProgress = progress;
                postDownloading();
            }
        }
        if (readLength != targetLength) {
            throw new Exception("range(index:" + index + ") download failed!! range not matched, startPoint:" + startPoint + ", endPoint:" + endPoint
                    + ",targetLength:" + targetLength + ", but downloadLength:" + readLength);
        }
    }

    private void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception ignored) {
            }
        }
    }

    private void renameFile(File tempFile, File targetFile) throws Exception {
        if (targetFile.exists()) {
            boolean delete = targetFile.delete();
            L.i(TAG, "delete old file(success:" + delete + "):" + targetFile.getAbsolutePath());
        }
        if (!tempFile.renameTo(targetFile)) {
            throw new Exception("tempFile(" + tempFile.getAbsolutePath() + ") rename to file(" + m.getFilePath() + ") failed....");
        }
    }

    private void checkCanceled() throws Exception {
        if (canceled) {
            throw new Exception("当前任务已取消");
        }
    }

    private boolean isPrintRespHeader() {
        return printRespHeader;
    }

    private OkHttpClient getClient() {
        return qsDownloader.getClient();
    }

    private void postDownloading() {
        qsDownloader.postDownloading(m);
    }

    private long getDownloadedLength() {
        return m.getDownloadedLength();
    }

    private void setContentLength(long len) {
        m.setTotalLength(len);
    }

    private long getContentLength() {
        return m.getTotalLength();
    }

    private void addDownloadedLength(long added) {
        m.addDownloadedLength(added);
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
        L.i(initTag(), sb.toString());
    }

    /**
     * 服务器是否支持断点续传
     */
    private boolean isServerSupportBreakPointTransmission(Response response) {
        String eTag = getETag(response);
        return eTag != null && eTag.length() > 0 &&
                ("bytes".equalsIgnoreCase(response.header("Accept-Ranges")) || response.header("Content-Range") != null);
    }

    private String getETag(Response response) {
        return response.header("ETag");
    }

    /**
     * 检查本地临时文件，判断是否支持断点续传
     */
    private boolean canBreakPointTransmission(Response response) {
        if (tempFile == null || !tempFile.exists() || tempFile.length() <= 0) {
            return false;
        }
        try {
            String eTag = getETag(response);
            if (eTag == null || eTag.length() == 0) return false;

            long fileLength = accessFile.length();
            long contentLength = response.body().contentLength();
            if (fileLength < contentLength) {
                return false;
            }
            accessFile.seek(contentLength);
            int threadCount = accessFile.readInt();
            if (threadCount < 1 || threadCount > MAX_THREAD_COUNT) return false;

            lastSeekPoints = new long[threadCount];
            for (byte i = 0; i < threadCount; i++) {
                lastSeekPoints[i] = accessFile.readLong();
            }

            byte[] eTagBytes = eTag.getBytes();
            byte[] lastETagBytes = new byte[eTagBytes.length];
            int len = accessFile.read(lastETagBytes);
            if (len != lastETagBytes.length) return false;

            for (int i = 0; i < lastETagBytes.length; i++) {
                if (lastETagBytes[i] != eTagBytes[i]) return false;
            }
            if (L.isEnable()) {
                L.i(initTag(), "checked can break point transmission, seekPoints:" + Arrays.toString(lastSeekPoints) + ", ETag:" + eTag);
            }
            return true;
        } catch (Exception ignored) {
        }
        return false;
    }

    /**
     * 记录线程数和ETag
     */
    private void writeThreadCountAndETag(int threadCount, @NonNull String eTag) throws Exception {
        accessFile.seek(getContentLength());
        accessFile.writeInt(threadCount);
        accessFile.seek(getContentLength() + 4 + threadCount * 8);
        accessFile.write(eTag.getBytes());
    }

    private void writeSeekPoint(int index, long seekValue) throws Exception {
        accessFile.seek(getContentLength() + 4 + index * 8);
        accessFile.writeLong(seekValue);
    }

    private String getTimeGone() {
        return (System.currentTimeMillis() - initTime) + "ms";
    }

    /**
     * 根据文件长度决定开启多少个线程同时下载
     */
    private int calculateThreadCount(long contentLength) {
        if (contentLength < 5_120_000) return 1;
        int count = (int) (contentLength / 5_120_000);
        return Math.min(count, MAX_THREAD_COUNT);
    }

    private boolean isBigFile(long contentLength) {
        return contentLength > 500_000;
    }

    private void downloadWait() {
        synchronized (threadLocker) {
            try {
                threadLocker.wait();
            } catch (Exception ignored) {
            }
        }
    }

    private void downloadNotify() {
        synchronized (threadLocker) {
            try {
                threadLocker.notifyAll();
            } catch (Exception ignored) {
            }
        }
    }
}

