package com.purehero.module.fragment.common;

import android.app.Activity;
import android.content.DialogInterface;

import com.purehero.module.common.DialogUtils;
import com.purehero.module.common.OnSuccessListener;
import com.purehero.module.common.R;
import com.purehero.module.fragment.filelist.FileListData;

import java.io.File;

/**
 * Created by purehero on 2017-03-28.
 */

public class FunctionRenameSelectedItem {
    final Activity context;
    final File targetFile;
    final OnSuccessListener successListener;

    public FunctionRenameSelectedItem(Activity context, File targetFile, OnSuccessListener successListener) {
        this.context = context;
        this.targetFile = targetFile;
        this.successListener = successListener;
    }

    public void run() {
        String title    = context.getString( R.string.rename);
        String text     = null;
        String hint     = targetFile.getName();

        DialogUtils.no_string_res     = R.string.cancel;
        DialogUtils.yes_string_res    = R.string.rename;
        DialogUtils.TextInputDialog( context,title, text, hint, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch( i ) {
                    case DialogUtils.DIALOG_BUTTON_ID_YES :
                        File destFile = new File( targetFile.getParentFile(), DialogUtils.getTextInputDialogResult());
                        if( targetFile.renameTo( destFile )) {

                            if( successListener != null ) {
                                successListener.OnSuccess();
                            }
                        }
                        break;
                }
            }
        } );
        DialogUtils.no_string_res     = -1;
        DialogUtils.yes_string_res    = -1;
    }
}
