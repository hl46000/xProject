package com.purehero.module.common;

import android.app.Activity;
import android.content.DialogInterface;

import com.purehero.module.common.CancelableProgressDialog;
import com.purehero.module.common.DialogUtils;
import com.purehero.module.common.OnSuccessListener;
import com.purehero.module.common.ProgressRunnable;
import com.purehero.module.filelistfragment.*;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by purehero on 2017-03-28.
 */

public class FunctionDeleteSelectedItem {
    final Activity context;
    final List<FileListData> targetFiles;
    final OnSuccessListener successListener;

    public FunctionDeleteSelectedItem(Activity context, List<FileListData> targetFiles, OnSuccessListener successListener) {
        this.context = context;
        this.targetFiles = targetFiles;
        this.successListener = successListener;
    }

    public void run() {
        final int item_count = targetFiles.size();
        String message = String.format( "%d %s\n\n", item_count, context.getString( com.purehero.module.filelistfragment.R.string.delete_message ));

        DialogUtils.no_string_res     = com.purehero.module.filelistfragment.R.string.cancel;
        DialogUtils.yes_string_res    = com.purehero.module.filelistfragment.R.string.delete;
        DialogUtils.confirmDialog( context, com.purehero.module.filelistfragment.R.string.delete, message, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i == DialogUtils.DIALOG_BUTTON_ID_YES ) {       // Clicked Delete
                    DialogUtils.progressDialog( context, com.purehero.module.filelistfragment.R.string.delete_item, "", new ProgressRunnable(){
                        @Override
                        public void run( final CancelableProgressDialog dialog) {

                            dialog.setMax( item_count );

                            int progress_count = 0;
                            for( final FileListData data : targetFiles ) {
                                context.runOnUiThread( new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog.setMessage(data.getFilename());
                                    }
                                });

                                try {
                                    FileUtils.forceDelete( data.getFile() );
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                dialog.setProgress( ++progress_count );
                            }

                            if( successListener != null ) {
                                successListener.OnSuccess();
                            }
                        }
                    });
                }
            }
        });


        DialogUtils.no_string_res     = -1;
        DialogUtils.yes_string_res    = -1;
    }
}
