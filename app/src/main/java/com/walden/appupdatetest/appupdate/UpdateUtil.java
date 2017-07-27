package com.walden.appupdatetest.appupdate;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;

/**
 * Created by walden on 2017/7/27.
 * 升级弹框  , 下载进度  ,安装
 */

public class UpdateUtil {

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
                                dialog.showProgress();
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
                Log.d("asdasd", "success: ");

                Intent intents = new Intent();
                intents.setAction("android.intent.action.VIEW");
                intents.addCategory("android.intent.category.DEFAULT");
                intents.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intents);
            }

            @Override
            public void process(final int a) {  //进度
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.getProgresNum().setText(a + " %");
                        dialog.getProgressBar().setProgress(a);
                    }
                });

            }

            @Override
            public void fail(String str) {
                Log.d("asdasd", "fail: " + str);
            }
        });
    }


}
