package com.walden.appupdatetest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.walden.appupdatetest.appupdate.UpdateUtil;

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


    //检查更新
    private void checkVerson() {
        //20170727152840_app-debug.apk  文件名
        String type = typeEt.getText().toString();
        String tmpurl = type.length() > 0 ? url + "?updatetype=" + type : url;

        UpdateUtil.update(this, tmpurl);
        boolean enviroment = BuildConfig.ENVIRONMENT;   //
    }
}
