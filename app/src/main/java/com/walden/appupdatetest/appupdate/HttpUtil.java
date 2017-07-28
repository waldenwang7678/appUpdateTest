package com.walden.appupdatetest.appupdate;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by wangjt on 2017/7/27.
 * get 请求  , 版本判断, apk下载
 */

public class HttpUtil {

    public static void get(final Activity activity, final String urlStr, final CallBack callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(urlStr);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Charset", "UTF-8");
                    connection.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
                    if (connection.getResponseCode() == 200) {
                        final InputStream is = connection.getInputStream();
                        final String strJson = StringStreamUtil.convertStream2String(is);
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                callBack.success(strJson);
                            }
                        });
                    } else {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                callBack.fail("fail");
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callBack.fail("IOException");
                        }
                    });

                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();

    }

    public static void getApk(final Activity activity, final String urlStr, final CallBack1 callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(urlStr);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Charset", "UTF-8");
                    connection.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");

                    final int totalLength = connection.getContentLength();
                    if (connection.getResponseCode() == 200) {
                        final InputStream is = connection.getInputStream();
                        //apk 文件放置内部存储
                        //final File file = new File(activity.getApplication().getCacheDir() ,"/app.apk");
                        //文件放置外部存储
                        final File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "/app.apk");
                        Log.d("asdasd", "run: " + file.getAbsolutePath());
                        if (!file.exists()) {
                            file.createNewFile();
                        }
                        FileOutputStream fos = new FileOutputStream(file);
                        byte[] bytes = new byte[1024 * 64];
                        int length;
                        int lengtsh = 0;
                        while ((length = is.read(bytes)) != -1) {
                            fos.write(bytes, 0, length);
                            //获取当前进度值
                            lengtsh += length;
                            callBack.process(lengtsh * 100 / totalLength);
                        }
                        fos.flush();
                        fos.close();
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (totalLength == file.length()) {  //下载完毕
                                    callBack.success(file);
                                }
                            }
                        });

                    } else {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                callBack.fail("fail");
                            }
                        });
                    }
                } catch (final IOException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callBack.fail("IOException : " + e.getMessage());
                        }
                    });

                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();

    }

    interface CallBack {

        void success(String is);

        void fail(String str);
    }

    interface CallBack1 {

        void success(File file);

        void process(int a);

        void fail(String str);
    }
}
