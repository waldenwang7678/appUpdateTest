package com.walden.appupdatetest.appupdate;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.walden.appupdatetest.R;

/**
 * Created by wangjt on 2017/7/27.
 * 升级提示框
 */

public class UpdateDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private boolean dialogType;
    private String title;
    private String content;
    private String cancle;
    private String confirm;
    private UpdateDialogListener mListener;
    private TextView title_view;
    private TextView content_view;
    private View confirmLayout;
    private View progressLayout;
    private TextView progress_num;
    private ProgressBar progress_bar;

    UpdateDialog(Context context, boolean dialogType, String title, String content, UpdateDialogListener listener) {
        super(context);
        mContext = context;
        this.dialogType = dialogType;
        this.title = title;
        this.content = content;
        this.cancle = "取消";
        this.confirm = "确定";
        this.mListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(R.drawable.white_corner_bg);
        this.setContentView(R.layout.update_dialog_layout);

        initDialogView();
        ViewTreeObserver vtoTitle = title_view.getViewTreeObserver();
        vtoTitle.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                title_view.getViewTreeObserver().removeOnPreDrawListener(this);
                adjustText(title_view);
                adjustText(content_view);
                return false;
            }
        });
    }

    private void initDialogView() {
        confirmLayout = findViewById(R.id.conform_layout);
        progressLayout = findViewById(R.id.progress_layout);
        progress_num = (TextView) findViewById(R.id.progress_num);
        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);

        title_view = (TextView) findViewById(R.id.update_title);        //标题
        content_view = (TextView) findViewById(R.id.update_content);    //内容

        TextView update_cancle = (TextView) findViewById(R.id.update_cancle);
        TextView update_confirm = (TextView) findViewById(R.id.update_confirm);
        View middle_line = findViewById(R.id.middle_line);

        title_view.setText(title);
        content_view.setText(content);
        update_cancle.setText(cancle);
        update_confirm.setText(confirm);

        update_confirm.setOnClickListener(this);
        update_cancle.setOnClickListener(this);

        if (dialogType) {
            update_cancle.setVisibility(View.GONE);
            middle_line.setVisibility(View.GONE);
            setCancelable(false);
        }
        confirmLayout.setVisibility(View.VISIBLE);
        progressLayout.setVisibility(View.GONE);

    }

    private void adjustText(TextView textView) {
        int titleLineCount = textView.getLineCount();
        if (titleLineCount > 1) {
            textView.setGravity(Gravity.CENTER | Gravity.LEFT);
        } else {
            textView.setGravity(Gravity.CENTER);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.update_cancle:
                mListener.cancleClick(this);  //取消升级
                break;
            case R.id.update_confirm:
                mListener.confirmClick(this);  //升级
                break;
        }
    }

    public void showProgress() {
        confirmLayout.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);
        //progress_bar.setProgress(20);
    }

    public boolean isForceUpdate() {
        return dialogType;
    }
    public ProgressBar getProgressBar(){
        return progress_bar;
    }
    public TextView getProgresNum(){
        return progress_num;
    }
}



