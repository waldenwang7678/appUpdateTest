package com.walden.appupdatetest.appupdate;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by walden on 2017/7/27.
 */

public class UpdateDialogUtil {
    public static UpdateDialog showUpdateDialog(Context context, String title, String content, boolean dialogType, UpdateDialogListener listener) {
        UpdateDialog dialog = new UpdateDialog(context, dialogType, title, content, listener);
        initDialog(context, dialogType, dialog, 0);
        return dialog;
    }

    private static void initDialog(Context context, boolean type, Dialog dialog, int upDistance) {
        int tempDistance = 250;
        if (upDistance > 0) {
            tempDistance = upDistance;
        }
        dialog.setCanceledOnTouchOutside(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);            //无标题
        dialog.show();

        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
//        lp.y = DisplayUtil.dip2px(context, tempDistance);

        int displayWidth = DisplayUtil.getDisplayWidth(context);
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        lp.width = (int) (displayWidth - fontScale * (50 * 2));   //dialog.show 之后设置才能生效
//        lp.height = 300; // 高度
//        lp.alpha = 0.7f; // 透明度
        window.setAttributes(lp);
    }

    //设置 dialog 左右边距 (备用)
    public static void setDialogMargin(Context context, Dialog dialog, int margin) {
        if (dialog.isShowing()) {
            int displayWidth = DisplayUtil.getDisplayWidth(context);
            float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
            Window window = dialog.getWindow();
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = (int) (displayWidth - fontScale * (margin * 2));   //dialog.show 之后设置才能生效
            window.setAttributes(lp);
        }
    }

    //dialog 距离顶部距离
    public static void setDialogUpDistance(Context context, Dialog dialog, int distance) {
        if (dialog.isShowing()) {
            Window window = dialog.getWindow();
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.y = DisplayUtil.dip2px(context, distance);
            window.setAttributes(lp);
        }
    }
}
