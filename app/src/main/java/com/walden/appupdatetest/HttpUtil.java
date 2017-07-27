package com.walden.appupdatetest;
import android.app.Activity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by wangjt on 2017/7/27.
 *
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
                        InputStream is = connection.getInputStream();
                        final String result = StringStreamUtil.convertStream2String(is);

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                callBack.success(result);
                            }
                        });
                    } else {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                callBack.success("fail");
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callBack.success("IOException");
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

        void success(String str);

        void fail(String str);
    }
}
