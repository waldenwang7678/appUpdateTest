/*
package com.walden.appupdatetest.appupdate;

*/
/**
 * Created by walden on 2017/7/20.
 *//*


import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.walden.appupdatetest.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class AppUpdateService extends IntentService {
    private static final String ACTION_UPDATE = "appupdate";
    private static final String EXTRA_URL = "appurl";
    private static final String EXTRA_FILE_NAME = "appname";
    private boolean isRunning = false;
    private NotificationManager updateNotificationManager;
    private Notification updateNotification;
    private PendingIntent updatePendingIntent;
    private static OnProgressListener mProgressListener;

    public interface OnProgressListener {
        void onProgress(int progress);

        void onSuccess(boolean isSuccess);
    }

    public AppUpdateService() {
        super("AppUpdateService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_URL);
                final String param2 = intent.getStringExtra(EXTRA_FILE_NAME);
                startDownloade(param1, param2);
            }
        }
    }

    @Override
    public void onDestroy() {
        mProgressListener = null;
        super.onDestroy();
    }

    public static void startUpdate(Context context, String param1, String param2, OnProgressListener pregressListener) {
        mProgressListener = pregressListener;
        Intent intent = new Intent(context, AppUpdateService.class);
        intent.setAction(ACTION_UPDATE);
        intent.putExtra(EXTRA_URL, param1);
        intent.putExtra(EXTRA_FILE_NAME, param2);
        context.startService(intent);
    }

    private void startDownloade(String url, String fileName) {
        if (isRunning) {
            return;
        }
        isRunning = true;
        initRemoteView();

        try {
            boolean isSuccess = downloadUpdateFile(url, fileName);
            if (mProgressListener != null) {
                mProgressListener.onSuccess(isSuccess);
            }
            if (isSuccess) {
                Uri uri = Uri.fromFile(new File(fileName));
                Intent installIntent = new Intent(Intent.ACTION_VIEW);
                installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                installIntent.setDataAndType(uri, "application/vnd.android.package-archive");
                startActivity(installIntent);
                try {
                    updateNotificationManager.cancel(0);
                } catch (Exception ex) {
                }
            } else {
                Notification notification = new Notification.Builder(AppUpdateService.this)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText("下载失败")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .build();
                updateNotificationManager.notify(0, notification);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    */
/**
     * 初始化状态栏进度条
     *//*

    private void initRemoteView() {
        try {
            updateNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            //状态栏提醒内容
            updateNotification = new Notification.Builder(this)
                    .setTicker("版本更新下载")
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher).build();
            updatePendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, RemoteViews.class), 0);
            updateNotification.contentIntent = updatePendingIntent;
            //状态栏提醒内容
            updateNotification.contentView = new RemoteViews(getApplication().getPackageName(), R.layout.progress);
            updateNotification.contentView.setProgressBar(R.id.progressBar1, 100, 0, false);
            updateNotification.contentView.setTextViewText(R.id.textView1, "0%");
            // 发出通知
            updateNotificationManager.notify(0, updateNotification);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private boolean downloadUpdateFile(String downloadUrl, String filepath) {
        try {
            int downloadCount = 0;
            int currentSize = 0;
            long totalSize = 0;
            int updateTotalSize = 0;
            boolean result = false;
            HttpURLConnection httpConnection = null;
            InputStream is = null;
            FileOutputStream fos = null;
            File temp = new File(filepath + ".tmp");
            if (temp.getParentFile().isDirectory()) {
                temp.getParentFile().mkdirs();
            }
            try {
                URL url = new URL(downloadUrl);
                httpConnection = (HttpURLConnection) url.openConnection();
                httpConnection.setRequestProperty("User-Agent", "PacificHttpClient");
                if (currentSize > 0) {
                    httpConnection.setRequestProperty("RANGE", "bytes=" + currentSize + "-");
                }
                httpConnection.setConnectTimeout(20000);
                httpConnection.setReadTimeout(120000);
                updateTotalSize = httpConnection.getContentLength();
                if (httpConnection.getResponseCode() == 404) {
                    throw new Exception("fail!");
                }
                is = httpConnection.getInputStream();
                fos = new FileOutputStream(temp, false);
                byte buffer[] = new byte[4096];
                int readsize = 0;
                while ((readsize = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, readsize);
                    totalSize += readsize; // 为了防止频繁的通知导致应用吃紧，百分比增加10才通知一次
                    if ((downloadCount == 0) || (int) (totalSize * 100 / updateTotalSize) - 1 > downloadCount) {
                        downloadCount += 1;
                        try {
                            updateNotificationupdateNotification.contentView.setProgressBar(R.id.progressBar1, 100, (int) totalSize * 100 / updateTotalSize,
                                    false);
                            updateNotification.contentView.setTextViewText(R.id.textView1, (int) totalSize * 100 / updateTotalSize + "%");
                            updateNotification.contentIntent = updatePendingIntent;
                            updateNotificationManager.notify(0, updateNotification);
                            if (mProgressListener != null) {
                                mProgressListener.onProgress((int) totalSize * 100 / updateTotalSize);
                                System.out.println("AppUpdateService.downloadUpdateFile" + (int) totalSize * 100 / updateTotalSize);
                            }
                        } catch (Exception ex) {
                        }
                    }
                }
                temp.renameTo(new File(filepath));
                temp.delete();
            } finally {
                if (httpConnection != null) {
                    httpConnection.disconnect();
                }
                if (is != null) {
                    is.close();
                }
                if (fos != null) {
                    fos.close();
                }
                result = updateTotalSize > 0 && updateTotalSize == totalSize;
                if (!result) {
                    //下载失败或者未下载完成
                    new File(filepath).delete();
                }
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

*/
