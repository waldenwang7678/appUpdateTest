package com.walden.appupdatetest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.walden.appupdatetest.R;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    //submitcode  0 不升级 , 1 , 普通升级  , 2 强制升级
    private String url = "http://walden-wang.cn/API/appupdate.php";  //
    private EditText typeEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        typeEt = (EditText) findViewById(R.id.updatetype);

        findViewById(R.id.bt1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkVerson();
            }
        });
    }

    private UpdateDialog appUpdateDialog;

    //检查更新
    private void checkVerson() {
        //20170727152840_app-debug.apk  文件名
        String type = typeEt.getText().toString();
        String tmpurl = type.length() > 0 ? url + "?updatetype=" + type : url;

        HttpUtil.get(this, tmpurl, new HttpUtil.CallBack() {


            @Override
            public void success(String str) {
                try {
                    JSONObject result = new JSONObject(str);
                    String versonCode = result.optString("updatetype");

                    if (versonCode.equals("0")) {
                        String extension = result.optString("extension");
                        Toast.makeText(MainActivity.this, extension, Toast.LENGTH_SHORT).show();
                    } else if (versonCode.equals("1") || versonCode.equals("2")) {
                        JSONObject extension = result.optJSONObject("extension");
                        String title = extension.optString("title");
                        String content = extension.optString("content");
                        String appUrl = extension.optString("url");
                        //Toast.makeText(MainActivity.this, title + "-" + content, Toast.LENGTH_SHORT).show();
                        //弹出升级提示框
                        appUpdateDialog = UpdateDialogUtil.showUpdateDialog(MainActivity.this, title, content, versonCode.equals("2"), new UpdateDialogListener() {
                            @Override
                            public void confirmClick(UpdateDialog dialog) {
                                dialog.showProgress();
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
                Toast.makeText(MainActivity.this, "fail", Toast.LENGTH_SHORT).show();
            }
        });
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (appUpdateDialog != null && appUpdateDialog.isShowing()) {
                if (appUpdateDialog.isForceUpdate()) {
                    return true;
                }
            }
        }

        return super.onKeyDown(keyCode, event);


    }
}
