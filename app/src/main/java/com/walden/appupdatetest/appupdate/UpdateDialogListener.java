package com.walden.appupdatetest.appupdate;

import com.walden.appupdatetest.appupdate.UpdateDialog;

/**
 * Created by walden on 2017/7/27.
 */

public interface UpdateDialogListener {


    void confirmClick(UpdateDialog dialog);

    void cancleClick(UpdateDialog dialog);
}
