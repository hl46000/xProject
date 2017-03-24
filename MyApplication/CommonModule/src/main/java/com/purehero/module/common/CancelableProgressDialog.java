package com.purehero.module.common;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by purehero on 2017-03-24.
 */

public class CancelableProgressDialog extends ProgressDialog {
    public boolean isCancel() {
        return isCancel;
    }

    public void setCancel(boolean cancel) {
        isCancel = cancel;
    }

    boolean isCancel = false;
    public CancelableProgressDialog(Context context) {
        super(context);
    }
};