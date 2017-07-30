package com.walden.appupdatetest.appupdate;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.walden.appupdatetest.MainActivity;
import com.walden.appupdatetest.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by walden on 2017/7/27.
 * 升级弹框  , 下载进度  ,安装
 */

public class UpdateUtil {

    private static int notifyCode = 12321;
    private static NotificationCompat.Builder builder;
    private static NotificationManager manager;

    public static void update(final Activity context, String tmpurl) {

        HttpUtil.get(context, tmpurl, new HttpUtil.CallBack() {
            @Override
            public void success(String str) {
                try {
                    JSONObject result = new JSONObject(str);
                    String versonCode = result.optString("updatetype");

                    if (versonCode.equals("0")) {
                        String extension = result.optString("extension");
                        Toast.makeText(context, extension, Toast.LENGTH_SHORT).show();
                    } else if (versonCode.equals("1") || versonCode.equals("2")) {
                        JSONObject extension = result.optJSONObject("extension");
                        final String appUrl = result.optString("url");
                        String title = extension.optString("title");
                        final String content = extension.optString("content");
                        Log.d("asdasd", "success: " + appUrl);
                        //Toast.makeText(MainActivity.this, title + "-" + content, Toast.LENGTH_SHORT).show();
                        //弹出升级提示框
                        UpdateDialogUtil.showUpdateDialog(context, title, content, versonCode.equals("2"), new UpdateDialogListener() {
                            @Override
                            public void confirmClick(UpdateDialog dialog) {
                                if (dialog.isForceUpdate()) {  //强制升级
                                    dialog.changeStyle();  //显示下载对话框
                                } else {
                                    dialog.dismiss();
                                    initNotifyBar(context);
                                }
                                downloadApk(context, dialog, appUrl);
                            }

                            @Override
                            public void cancleClick(UpdateDialog dialog) {
                                dialog.dismiss();
                            }
                        });
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void fail(String str) {
                Toast.makeText(context, "fail", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void downloadApk(final Activity context, final UpdateDialog dialog, String url) {
        HttpUtil.getApk(context, url, new HttpUtil.CallBack1() {
            @Override
            public void success(File file) {  //下载完成
                Intent intents = new Intent();
                intents.setAction("android.intent.action.VIEW");
                intents.addCategory("android.intent.category.DEFAULT");
                intents.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //context.startActivity(intents);
            }

            @Override
            public void process(final int a) {  //进度
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //提示框下载进度
                        if (dialog.isForceUpdate()) {
                            dialog.getProgresNum().setText(a + " %");
                            dialog.getProgressBar().setProgress(a);
                        }
                        if (builder != null && manager != null) {//通知栏下载进度
                            builder.setProgress(100, a, false);
                            builder.setContentText(a + "%");
                            manager.notify(notifyCode, builder.build());
                        }

                    }
                });
            }

            @Override
            public void fail(String str) {
                Log.d("asdasd", "fail: " + str);
            }
        });
    }

    private static void initNotifyBar(Activity context) {
        if (manager == null) {
            manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);
        builder = new NotificationCompat.Builder(context)
                .setTicker("版本更新")
                .setSmallIcon(R.mipmap.ic_launcher)
                //.setContentText("正在下载")
                .setContentTitle("下载")
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent);

        builder.setProgress(100, 0, false);
        manager.notify(notifyCode, builder.build());

       /* new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    builder.setProgress(100, i, false);
                    manager.notify(3, builder.build());
                    //下载进度提示
                    builder.setContentText("下载" + i + "%");
                    try {
                        Thread.sleep(50);//演示休眠50毫秒
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //下载完成后更改标题以及提示信息
                builder.setContentTitle("开始安装");
                builder.setContentText("安装中...");
                //设置进度为不确定，用于模拟安装
                builder.setProgress(0, 0, true);
                manager.notify(3, builder.build());
                // manager.cancel(NO_3);//设置关闭通知栏
            }
        }).start();*/

    }
}