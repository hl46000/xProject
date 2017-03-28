package com.purehero.module.filelistfragment;

import android.app.Activity;
import android.content.DialogInterface;

import com.purehero.module.common.DialogUtils;
import com.purehero.module.common.OnSuccessListener;

import java.io.File;

/**
 * Created by purehero on 2017-03-28.
 */

public class FunctionRenameSelectedItem {
    final Activity context;
    final FileListData targetFile;
    final OnSuccessListener successListener;

    public FunctionRenameSelectedItem(Activity context, FileListData targetFile, OnSuccessListener successListener) {
        this.context = context;
        this.targetFile = targetFile;
        this.successListener = successListener;
    }

    public void run() {
        String title    = context.getString( R.string.rename);
        String text     = null;
        String hint     = targetFile.getFilename();

        DialogUtils.no_string_res     = R.string.cancel;
        DialogUtils.yes_string_res    = R.string.rename;
        DialogUtils.TextInputDialog( context,title, text, hint, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch( i ) {
                    case DialogUtils.DIALOG_BUTTON_ID_YES :
                        File destFile = new File( targetFile.getFile().getParentFile(), DialogUtils.getTextInputDialogResult());
                        if( targetFile.getFile().renameTo( destFile )) {
                            targetFile.setFile( destFile );

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
